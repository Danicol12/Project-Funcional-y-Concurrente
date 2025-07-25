import common._
import scala.collection.parallel.CollectionConverters._
import Oraculo._
import ArbolSufijos._

package object ReconstCadenasPar {

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


  def reconstruirCadenaMejoradoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {

    def construirCadena(paso: Int, acumuladas: Seq[Seq[Char]]): Seq[Char] = {

      val expandidas = acumuladas.flatMap(parcial => alfabeto.map(letra => parcial :+ letra))

      val nuevas =
        if (expandidas.size <= umbral)
          expandidas.filter(o)
        else {
          val (l1, l2) = expandidas.splitAt(expandidas.size / 2)
          val (fl1, fl2) = parallel(
            l1.filter(o),
            l2.filter(o)
          )
          (fl1 ++ fl2)
        }

      nuevas.find(_.length == n).getOrElse {
        if (paso > n) Seq.empty
        else construirCadena(paso + 1, nuevas)
      }
    }

    construirCadena(1, Seq(Seq.empty))
  }

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


  def reconstruirCadenaTurboAceleradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {

    // Verifica si todas las subsecuencias de una cadena pertenecen al conjunto válido (usando árbol de sufijos)
    def particiones(caracteres: Seq[Char], conjInicial: Seq[Seq[Char]], arbol: Trie): Boolean = {
      if (conjInicial.isEmpty) return false
      val tam = conjInicial.head.length
      val part = caracteres.sliding(tam).toList
      part.forall(w => pertenece(w, arbol))
    }

    // Genera nuevas combinaciones de cadenas y filtra solo las que cumplen con la validación
    def filtrar(cadenas: Seq[Seq[Char]]): Seq[Seq[Char]] = {
      if (cadenas.isEmpty) return Seq.empty

      // Se construye el árbol de sufijos (estructura para validar subsecuencias)
      val arbol = arbolDeSufijos(cadenas.toList)

      // El umbral se aplica aquí al tamaño del conjunto 'cadenas' (SC)
      // Paraleliza la generación de combinaciones si el tamaño de 'cadenas' es menor que el umbral
      if (cadenas.size < umbral) {
        val combinacionesFiltradasPar = for {
          c1 <- cadenas.par // Paraleliza la iteración externa
          c2 <- cadenas
          s = c1 ++ c2
          if particiones(s, cadenas, arbol)
        } yield s
        combinacionesFiltradasPar.seq // Vuelve a una colección secuencial
      } else {
        // Si el tamaño de 'cadenas' es igual o mayor al umbral, ejecuta secuencialmente
        for {
          c1 <- cadenas
          c2 <- cadenas
          s = c1 ++ c2
          if particiones(s, cadenas, arbol)
        } yield s
      }
    }

    // Función recursiva que genera, filtra y verifica combinaciones hasta encontrar la cadena deseada
    def recursivaTurboAceleradaPar(alfa: Seq[Seq[Char]]): Seq[Char] = {
      val combinaciones = filtrar(alfa) // Esta llamada puede ser paralela o secuencial según el umbral

      if (combinaciones.isEmpty) return Seq.empty

      // *** Esta parte SIEMPRE se paraleliza ***
      // El filtro con el oráculo es el cuello de botella principal debido al Thread.sleep.
      // Es crucial paralelizarlo para obtener un buen speedup, independientemente del tamaño de las cadenas.
      val candidatas = combinaciones.par.filter(o).seq

      // Si encuentra la cadena correcta → la devuelve, si no → continúa recursivamente
      if (candidatas.isEmpty) Seq.empty
      else candidatas.find(_.length == n).getOrElse(recursivaTurboAceleradaPar(candidatas))
    }

    // Se construye el conjunto inicial con las posibles letras (alfabeto)
    val conjuntoInicial = alfabeto.map(Seq(_))

    // Casos base: si la longitud es 1 o 2
    if (n == 1) {
      // También se paraleliza la llamada al oráculo en el caso base si es necesario
      conjuntoInicial.par.find(c => o(c)).getOrElse(Seq.empty)
    } else if (n == 2) {
      val combinaciones = for {
        c1 <- conjuntoInicial
        c2 <- conjuntoInicial
        s = c1 ++ c2
      } yield s
      // Paralelizar la comprobación del oráculo también para n=2
      combinaciones.par.find(o).getOrElse(Seq.empty)
    } else {
      // Para cadenas de longitud mayor → se llama a la función recursiva optimizada
      recursivaTurboAceleradaPar(conjuntoInicial)
    }
  }
}
