import ReconstCadenas._
import Oraculo._
import scala.collection.parallel.CollectionConverters._
import org.scalameter._


package object benchmark {

  type Algoritmo = (Int, Oraculo) => Seq[Char]
  
  def compararAlgoritmos(a1: Algoritmo, a2: Algoritmo)
                        (tamano: Int, oraculo: Oraculo): (Double, Double, Double) = {
    val timeA1 = config(
      KeyValue(Key.exec.minWarmupRuns -> 10),
      KeyValue(Key.exec.maxWarmupRuns -> 20),
      KeyValue(Key.verbose -> false)
    ) withWarmer (new Warmer.Default) measure (a1(tamano, oraculo))

    val timeA2 = config(
      KeyValue(Key.exec.minWarmupRuns -> 10),
      KeyValue(Key.exec.maxWarmupRuns -> 20),
      KeyValue(Key.verbose -> false)
    ) withWarmer (new Warmer.Default) measure (a2(tamano, oraculo))

    val speedUp = timeA1.value / timeA2.value
    (timeA1.value, timeA2.value, speedUp)
  }



}
