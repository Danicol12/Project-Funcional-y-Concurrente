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
  /*

  def reconstruirCadenaMejoradoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa paralelismo de tareas y/o datos
    ???
  }

  def reconstruirCadenaTurboPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {


    // Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa paralelismo de tareas y/o datos
    ???
  }


  def reconstruirCadenaTurboMejoradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa paralelismo de tareas y/o datos
    ???
  }
  
   */

  def reconstruirCadenaTurboAceleradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {

    def particiones(caracteres: Seq[Char], conjInicial: Seq[Seq[Char]], arbol: Trie): Boolean = {
      if (conjInicial.isEmpty) return false
      val tam = conjInicial.head.length
      val part = caracteres.sliding(tam).toList
      part.forall(w => pertenece(w, arbol))
    }

    def filtrar(cadenas: Seq[Seq[Char]]): Seq[Seq[Char]] = {
      if (cadenas.isEmpty) return Seq.empty

      val arbol = arbolDeSufijos(cadenas.toList)  // Solo aquí usamos toList porque el árbol lo necesita

      val longitudActual = cadenas.headOption.map(_.length).getOrElse(0)

      if (longitudActual >= umbral) {
        for {
          c1 <- cadenas
          c2 <- cadenas
          s = c1 ++ c2
          if particiones(s, cadenas, arbol)
        } yield s
      } else {
        val cadenasPar = cadenas.par
        val mitad = cadenasPar.size / 2
        val (left, right) = cadenasPar.splitAt(mitad)

        val (res1, res2) = parallel(
          left.flatMap(c1 => cadenas.collect {
            case c2 if particiones(c1 ++ c2, cadenas, arbol) => c1 ++ c2
          }),
          right.flatMap(c1 => cadenas.collect {
            case c2 if particiones(c1 ++ c2, cadenas, arbol) => c1 ++ c2
          })
        )

        (res1 ++ res2).seq
      }
    }

    def recursivaTurboAceleradaPar(alfa: Seq[Seq[Char]]): Seq[Char] = {
      val combinaciones = filtrar(alfa)

      if (combinaciones.isEmpty) return Seq.empty

      val longitudActual = combinaciones.headOption.map(_.length).getOrElse(0)

      val candidatas =
        if (longitudActual >= umbral) {
          combinaciones.filter(o)
        } else {
          combinaciones.par.filter(o).seq
        }

      if (candidatas.isEmpty) Seq.empty
      else candidatas.find(_.length == n).getOrElse(recursivaTurboAceleradaPar(candidatas))
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


