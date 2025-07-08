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

  // Función principal paralelizada que reconstruye una cadena secreta
  // umbral: controla cuándo usar paralelismo o seguir secuencial
  // n: longitud objetivo de la cadena
  // o: oráculo que valida si una cadena es válida
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

      // Se construye el árbol de sufijos (estructura para validar subsecuencias) → solo funciona con List
      val arbol = arbolDeSufijos(cadenas.toList)

      val longitudActual = cadenas.headOption.map(_.length).getOrElse(0)

      // Si la longitud actual es mayor o igual al umbral → ejecuta secuencialmente (evitar sobrecarga de hilos)
      if (longitudActual >= umbral) {
        for {
          c1 <- cadenas
          c2 <- cadenas
          s = c1 ++ c2
          if particiones(s, cadenas, arbol)
        } yield s
      } else {
        // Si la longitud es menor que el umbral → ejecuta en paralelo para acelerar combinaciones
        val cadenasPar = cadenas.par
        val mitad = cadenasPar.size / 2
        val (left, right) = cadenasPar.splitAt(mitad)

        // Se procesan en paralelo dos mitades de las cadenas
        val (res1, res2) = parallel(
          left.flatMap(c1 => cadenas.collect {
            case c2 if particiones(c1 ++ c2, cadenas, arbol) => c1 ++ c2
          }),
          right.flatMap(c1 => cadenas.collect {
            case c2 if particiones(c1 ++ c2, cadenas, arbol) => c1 ++ c2
          })
        )

        // Se unen los resultados paralelos
        (res1 ++ res2).seq
      }
    }

    // Función recursiva que genera, filtra y verifica combinaciones hasta encontrar la cadena deseada
    def recursivaTurboAceleradaPar(alfa: Seq[Seq[Char]]): Seq[Char] = {
      val combinaciones = filtrar(alfa)

      if (combinaciones.isEmpty) return Seq.empty

      val longitudActual = combinaciones.headOption.map(_.length).getOrElse(0)

      // Si la longitud actual supera el umbral → filtra secuencialmente
      // Si no → filtra en paralelo aplicando el oráculo
      val candidatas =
        if (longitudActual >= umbral) {
          combinaciones.filter(o)
        } else {
          combinaciones.par.filter(o).seq
        }

      // Si encuentra la cadena correcta → la devuelve, si no → continúa recursivamente
      if (candidatas.isEmpty) Seq.empty
      else candidatas.find(_.length == n).getOrElse(recursivaTurboAceleradaPar(candidatas))
    }

    // Se construye el conjunto inicial con las posibles letras (alfabeto)
    val conjuntoInicial = alfabeto.map(Seq(_))

    // Casos base: si la longitud es 1 o 2 → se prueban todas las combinaciones posibles sin recursión
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
      // Para cadenas de longitud mayor → se llama a la función recursiva optimizada
      recursivaTurboAceleradaPar(conjuntoInicial)
    }
  }


}


