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
