import ReconstCadenas._
import Oraculo._
import scala.collection.parallel.CollectionConverters._
import org.scalameter._


package object benchmark {

  type Algoritmo = (Int, Oraculo) => Seq[Char]

  def compararAlgoritmos(a1: Algoritmo, a2: Algoritmo)
                        (tamano: Int, oraculo: Oraculo, minWarmup: Int, maxWarmup: Int): (Double, Double, Double) = {

    // Se usa la configuración de calentamiento recibida por parámetro
    val timeA1 = config(
      KeyValue(Key.exec.minWarmupRuns -> minWarmup),
      KeyValue(Key.exec.maxWarmupRuns -> maxWarmup),
      KeyValue(Key.verbose -> false)
    ) withWarmer (new Warmer.Default) measure (a1(tamano, oraculo))

    // Se usa la misma configuración para el segundo algoritmo
    val timeA2 = config(
      KeyValue(Key.exec.minWarmupRuns -> minWarmup),
      KeyValue(Key.exec.maxWarmupRuns -> maxWarmup),
      KeyValue(Key.verbose -> false)
    ) withWarmer (new Warmer.Default) measure (a2(tamano, oraculo))

    val speedUp = timeA1.value / timeA2.value
    (timeA1.value, timeA2.value, speedUp)
  }

  /**
   * Ejecuta la comparación un número de veces y calcula el promedio.
   * AHORA TAMBIÉN RECIBE LOS PARÁMETROS DE CALENTAMIENTO PARA PASARLOS.
   */
  def promedioComparacion(a1: Algoritmo, a2: Algoritmo)
                         (tamaño: Int, repe: Int, oraculo: Oraculo, minWarmup: Int, maxWarmup: Int)
  : ((Double, Double, Double), Vector[(Double, Double, Double)]) = {

    val resultados: Vector[(Double, Double, Double)] = (1 to repe).map { _ =>
      // Pasa los parámetros de calentamiento a la función de comparación
      compararAlgoritmos(a1, a2)(tamaño, oraculo, minWarmup, maxWarmup)
    }.toVector

    val promedioTiempo1 = resultados.map(_._1).sum / repe
    val promedioTiempo2 = resultados.map(_._2).sum / repe
    val promedioSpeedup = resultados.map(_._3).sum / repe

    ((promedioTiempo1, promedioTiempo2, promedioSpeedup), resultados)
  }



}
