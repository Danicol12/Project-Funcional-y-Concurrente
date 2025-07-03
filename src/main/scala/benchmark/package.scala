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

  def promedioComparacion(a1: Algoritmo, a2: Algoritmo)(tamaño:Int, repe: Int, oraculo: Oraculo
                         ): ((Double, Double, Double), Vector[(Double, Double, Double)]) = {

    val resultados: Vector[(Double, Double, Double)] = (1 to repe).map { _ =>
      compararAlgoritmos(a1, a2)(tamaño, oraculo)
    }.toVector

    val promedioTiempo1 = resultados.map(_._1).sum / repe
    val promedioTiempo2 = resultados.map(_._2).sum / repe
    val promedioSpeedup = resultados.map(_._3).sum / repe

    ((promedioTiempo1, promedioTiempo2, promedioSpeedup), resultados)
  }



}
