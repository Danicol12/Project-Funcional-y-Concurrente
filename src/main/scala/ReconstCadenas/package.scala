import ArbolSufijos._
import Oraculo._

package object ReconstCadenas {
/*
// Definimos el alfabeto sobre el que trabaja el genoma
val alfabeto: Seq[Char] = Seq('a', 'c', 'g', 't')

// Tipo para representar el oráculo
type Oraculo = Seq[Char] => Boolean

// Función auxiliar que genera todas las combinaciones posibles de longitud `n`
def generarCadenas(n: Int): Seq[Seq[Char]] = {
  if (n == 0) Seq(Seq.empty)
  else for {
    prefijo <- generarCadenas(n - 1)
    letra <- alfabeto
  } yield prefijo :+ letra
}

// Implementación funcional y pura del algoritmo ingenuo
def reconstruirCadenaIngenuo(n: Int, o: Oraculo): Seq[Char] = {
  generarCadenas(n).find(o).getOrElse(Seq.empty)
}


  def reconstruirCadenaMejorado(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    ???
  }

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