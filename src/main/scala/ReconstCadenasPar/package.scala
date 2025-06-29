import common._
import scala.collection.parallel.CollectionConverters._
import Oraculo._
import ArbolSufijos._

package object ReconstCadenasPar {

  // Ahora versiones paralelas
  /*
  def reconstruirCadenaIngenuoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa paralelismo de tareas
    ???
  }

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

  def reconstruirCadenaTurboAceleradaPar(n: Int, o: Oraculo, umbral: Int): Seq[Char] = {
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa árboles de sufijos para guardar Seq[Seq[Char]]
    // Paraleliza según umbral

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
