import ArbolSufijos._
import Oraculo._

package object ReconstCadenas {
/*
  def reconstruirCadenaIngenuo(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    ???
  }
*/
  def reconstruirCadenaMejorado(n: Int, o: Oraculo): Seq[Char] = {

    /**
     * Función recursiva auxiliar que construye posibles cadenas válidas de forma incremental.
     *
     * @param paso       número de caracteres que estamos intentando generar en esta iteración
     * @param acumuladas conjunto de secuencias válidas encontradas hasta el momento
     * @return una cadena válida de longitud n si se encuentra, o secuencia vacía si no
     */
    def construirCadena(paso: Int, acumuladas: Seq[Seq[Char]]): Seq[Char] = {

      // Para cada secuencia acumulada, intenta agregar cada letra del alfabeto al final
      // y se queda con aquellas nuevas secuencias que el oráculo acepta como válidas
      val nuevas = acumuladas.flatMap { parcial =>
        alfabeto.map(letra => parcial :+ letra) // concatena una letra al final de la secuencia
      }.filter(seq => o(seq)) // filtra solo las que el oráculo dice que son válidas

      // Busca entre las nuevas secuencias alguna que ya tenga longitud n (es decir, la completa)
      val coincidencias = nuevas.filter(_.length == n)

      if (coincidencias.nonEmpty) {
        // Si se encontró al menos una secuencia completa, la devuelve
        coincidencias.head
      } else if (paso > n) {
        // Si ya se superó el límite de pasos sin encontrar nada, se devuelve vacío
        Seq.empty
      } else {
        // Si no se ha llegado aún a la longitud n, se sigue construyendo en la siguiente iteración
        construirCadena(paso + 1, nuevas)
      }
    }

    // Llamada inicial a la función recursiva con paso 1 y una secuencia vacía como punto de partida
    construirCadena(1, Seq(Seq.empty))
  }

/*
  def reconstruirCadenaTurbo(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    ???
  }

  def reconstruirCadenaTurboMejorada(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa el filtro para ir más rápido
    ???
  }

  def reconstruirCadenaTurboAcelerada(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa el filtro para ir más rápido
    // Usa árboles de sufijos para guardar Seq[Seq[Char]]
    ???
  }*/

}