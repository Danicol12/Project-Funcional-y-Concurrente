import common._
import scala.collection.parallel.CollectionConverters._
import Oraculo._
import ArbolSufijos._

package object  ReconstCadenasPar {

  // Ahora versiones paralelas

  def reconstruirCadenaIngenuoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {

    def generarCadenas(k: Int): Seq[Seq[Char]] = {
      if (k == 0) Seq(Seq.empty)
      else if (k >= umbral) {
        // Paralelizamos por letra
        val part1 = task(generarCadenas(k - 1).map(_ :+ 'a'))
        val part2 = task(generarCadenas(k - 1).map(_ :+ 'c'))
        val part3 = task(generarCadenas(k - 1).map(_ :+ 'g'))
        val part4 = task(generarCadenas(k - 1).map(_ :+ 't'))

        part1.join() ++ part2.join() ++ part3.join() ++ part4.join()
      } else {
        for {
          prefijo <- generarCadenas(k - 1)
          letra <- alfabeto
        } yield prefijo :+ letra
      }
    }

    generarCadenas(n).par.find(o).getOrElse(Seq.empty)
  }
  /*

  def reconstruirCadenaMejoradoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa paralelismo de tareas y/o datos
    ???
  }
  */

  def reconstruirCadenaTurboPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Función auxiliar recursiva
    def turboRec(k: Int, sc_k: Seq[Seq[Char]]): Seq[Char] = {
      if (k >= n) {
        // Caso base: se encontró la cadena
        sc_k.head
      } else {
        // Generación de candidatos y filtrado con paralelismo de tareas
        val sc_2k = if (sc_k.length > umbral) {
          // 1. Dividir el trabajo en trozos (chunks)
          val numeroDeTareas = 4 // Se puede ajustar según la máquina
          val chunks = sc_k.grouped(math.max(1, sc_k.length / numeroDeTareas)).toSeq

          // 2. Crear una tarea para procesar cada trozo
          val tasks = chunks.map(chunk => task {
            val candidatos = for {
              s1 <- chunk
              s2 <- sc_k
            } yield s1 ++ s2
            // 3. Filtrar los candidatos de este trozo con el oráculo
            candidatos.filter(o)
          })

          // 4. Esperar a que todas las tareas terminen y unir sus resultados
          tasks.flatMap(_.join()).toSeq
        } else {
          // Ejecución secuencial si no se supera el umbral
          val candidatos = for {
            s1 <- sc_k
            s2 <- sc_k
          } yield s1 ++ s2
          candidatos.filter(o)
        }

        // Llamada recursiva con el nuevo conjunto de subcadenas
        turboRec(k * 2, sc_2k)
      }
    }

    // Paso inicial: encontrar subcadenas de longitud 1
    val sc_1 = alfabeto.filter(c => o(Seq(c))).map(c => Seq(c))

    // Iniciar el proceso
    turboRec(1, sc_1)
  }


  /**
   * Reconstruye la cadena secreta utilizando una versión paralela del algoritmo Turbo Mejorado.
   *
   * @param umbral La cantidad de elementos a partir de la cual se justifica usar paralelismo.
   * @param n      La longitud de la cadena a reconstruir (potencia de 2).
   * @param o      El oráculo que verifica si una secuencia es subcadena de la secreta.
   * @return La cadena secreta reconstruida.
   */
  def reconstruirCadenaTurboMejoradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {

    // El alfabeto se asume definido en el scope, como en el enunciado.
    val alfabeto = Seq('a', 'c', 'g', 't')

    def turboMejoradaRec(k: Int, sc_k: Seq[Seq[Char]]): Seq[Char] = {
      if (k >= n) {
        sc_k.head
      } else {

        val sc_k_set = sc_k.toSet

        def particiones(s: Seq[Char]): Boolean = {
          s.sliding(k, k).forall(sc_k_set.contains)
        }

        val candidatosPrefiltrados = {
          if (sc_k.length > umbral) {
            (for {
              s1 <- sc_k.par
              s2 <- sc_k
            } yield s1 ++ s2)
              .filter(particiones)
              .seq
          } else {
            val todosLosCandidatos = for {
              s1 <- sc_k
              s2 <- sc_k
            } yield s1 ++ s2
            todosLosCandidatos.filter(particiones)
          }
        }


        val sc_2k = {
          if (candidatosPrefiltrados.length > umbral) {
            val numeroDeTareas = 8
            val chunks = candidatosPrefiltrados.grouped(math.max(1, candidatosPrefiltrados.length / numeroDeTareas)).toSeq

            val tasks = chunks.map(chunk => task {
              chunk.filter(o)
            })
            tasks.flatMap(_.join()).toSeq
          } else {
            candidatosPrefiltrados.filter(o)
          }
        }

        turboMejoradaRec(k * 2, sc_2k)
      }
    }

    // Paso inicial: obtener las subcadenas de longitud 1.
    val sc_1 = alfabeto.par.filter(c => o(Seq(c))).map(c => Seq(c)).seq

    // Iniciar la recursión.
    turboMejoradaRec(1, sc_1)
  }


  def reconstruirCadenaTurboAceleradaPar(umbral:Int)(n: Int, o: Oraculo): Seq[Char] = {

    def particiones(caracteres: Seq[Char], conjInicial: Seq[Seq[Char]], arbol: Trie): Boolean = {
      val tam = conjInicial.head.length
      val part = caracteres.sliding(tam).toList
      part.forall(w => pertenece(w, arbol))
    }

    def filtrar(cadenas: Seq[Seq[Char]]): Seq[Seq[Char]] = {
      val arbol = arbolDeSufijos(cadenas)

      if (n <= umbral) {
        for {
          c1 <- cadenas
          c2 <- cadenas
          s = c1 ++ c2
          if particiones(s, cadenas, arbol)
        } yield s
      } else {
        val cadenasPar = cadenas.par
        val (left, right) = cadenasPar.splitAt(cadenasPar.size / 2)
        val ((l1, l2), (r1, r2)) = (left.splitAt(left.size / 2), right.splitAt(right.size / 2))

        val (res1, res2, res3, res4) = parallel(
          for {
            c1 <- l1
            c2 <- cadenas
            s = c1 ++ c2
            if particiones(s, cadenas, arbol)
          } yield s,
          for {
            c1 <- l2
            c2 <- cadenas
            s = c1 ++ c2
            if particiones(s, cadenas, arbol)
          } yield s,
          for {
            c1 <- r1
            c2 <- cadenas
            s = c1 ++ c2
            if particiones(s, cadenas, arbol)
          } yield s,
          for {
            c1 <- r2
            c2 <- cadenas
            s = c1 ++ c2
            if particiones(s, cadenas, arbol)
          } yield s
        )
        (res1 ++ res2 ++ res3 ++ res4).seq
      }
    }

    def recursivaTurboAceleradaPar(alfa: Seq[Seq[Char]]): Seq[Char] = {
      val combinacionesSec = filtrar(alfa)

      val filtrado = if (n <= umbral) {
        combinacionesSec.filter(x => o(x))
      } else {
        val (left, right) = combinacionesSec.par.splitAt(combinacionesSec.size / 2)
        val ((l1, l2), (r1, r2)) = (left.splitAt(left.size / 2), right.splitAt(right.size / 2))
        val (fl1, fl2, fr1, fr2) = parallel(
          l1.filter(o),
          l2.filter(o),
          r1.filter(o),
          r2.filter(o)
        )
        (fl1 ++ fl2 ++ fr1 ++ fr2).seq
      }

      filtrado.find(_.length == n).getOrElse(recursivaTurboAceleradaPar(filtrado))
    }

    val conjuntoInicial = alfabeto.map(Seq(_))

    if (n == 1) {
      conjuntoInicial.find(c => o(c)).getOrElse(Seq.empty)
    } else if (n == 2) {
      val combinaciones = for {
        c1 <- conjuntoInicial
        c2 <- conjuntoInicial
        s = c1 ++ c2
        if o(s)
      } yield s
      combinaciones.headOption.getOrElse(Seq.empty)
    } else {
      recursivaTurboAceleradaPar(conjuntoInicial)
    }
  }
}
