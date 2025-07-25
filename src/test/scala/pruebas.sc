import ReconstCadenas.*
import ReconstCadenasPar.*
import Oraculo.*
import ArbolSufijos.*
import benchmark.*
import org.scalameter.measure

import scala.util.Random

val sec = List('g','a')
val sec4 = Seq('a', 'c', 'g', 't')
val sec8 = Seq('g', 'a', 't', 'c', 'c', 'a', 'g', 't')

val sec16= Seq('g','a','t','c',
  'a','g','g','t',
  'c','a','t','a',
  't','g','c','c'
)
val sec32= Seq(
  'a','g','c','t','t','a','g','a',
  'c','g','t','c','a','a','t','g',
  'g','t','a','c','c','a','g','t',
  't','c','g','a','g','t','a','c'
)

val sec64 = Seq(
  'a','g','c','t','t','a','g','a',
  'c','g','t','c','a','a','t','g',
  'g','t','a','c','c','a','g','t',
  't','c','g','a','g','t','a','c',
  'c','g','a','t','g','a','c','t',
  'g','t','g','c','a','g','t','c',
  'a','c','g','g','t','a','c','t',
  't','a','g','c','c','g','a','t'
)

val sec128 = Seq(
  'a','g','c','t','t','a','g','a','c','g','t','c','a','a','t','g',
  'g','t','a','c','c','a','g','t','t','c','g','a','g','t','a','c',
  'c','g','a','t','g','a','c','t','g','t','g','c','a','g','t','c',
  'a','c','g','g','t','a','c','t','t','a','g','c','c','g','a','t',
  'c','a','t','g','g','t','g','a','c','t','a','c','c','a','g','t',
  'g','g','a','t','t','c','a','c','g','t','t','a','c','g','g','t',
  'a','g','t','c','c','t','g','a','g','c','t','a','c','a','t','g',
  'g','t','c','g','a','t','c','c','g','a','g','t','a','c','g','a'
)

val sec256 = Seq(
  'a','g','c','t','t','a','g','a','c','g','t','c','a','a','t','g',
  'g','t','a','c','c','a','g','t','t','c','g','a','g','t','a','c',
  'c','g','a','t','g','a','c','t','g','t','g','c','a','g','t','c',
  'a','c','g','g','t','a','c','t','t','a','g','c','c','g','a','t',
  'c','a','t','g','g','t','g','a','c','t','a','c','c','a','g','t',
  'g','g','a','t','t','c','a','c','g','t','t','a','c','g','g','t',
  'a','g','t','c','c','t','g','a','g','c','t','a','c','a','t','g',
  'g','t','c','g','a','t','c','c','g','a','g','t','a','c','g','a',
  't','c','g','g','a','t','a','c','t','g','a','g','c','a','t','c',
  'g','a','c','g','t','t','a','g','c','g','a','a','t','c','c','t',
  'g','t','g','c','a','c','g','g','t','a','c','t','g','a','c','t',
  'a','g','t','g','c','t','g','a','a','t','c','c','g','g','a','c',
  'c','t','a','g','t','c','g','t','a','c','a','g','t','g','a','c',
  'a','c','g','a','g','t','t','g','t','a','c','t','g','a','g','c',
  'g','t','a','c','t','c','a','g','g','t','c','t','g','a','t','g',
  't','a','c','c','g','t','a','g','c','a','t','c','g','t','g','a'
)

val sec512 = Seq(
  'a','c','g','t','g','a','c','t','c','g','t','a','g','a','c','t',
  'g','t','a','c','c','a','g','t','a','g','c','t','c','g','t','g',
  'a','t','c','g','g','a','t','a','c','t','g','a','g','c','a','t',
  'c','g','a','c','g','t','t','a','g','c','g','a','a','t','c','c',
  't','g','t','g','c','a','c','g','g','t','a','c','t','g','a','c',
  't','a','g','t','g','c','t','g','a','a','t','c','c','g','g','a',
  'c','c','t','a','g','t','c','g','t','a','c','a','g','t','g','a',
  'c','a','c','g','a','g','t','t','g','t','a','c','t','g','a','g',
  'c','g','t','a','c','t','c','a','g','g','t','c','t','g','a','t',
  'g','t','a','c','c','g','t','a','g','c','a','t','c','g','t','g',
  'a','t','g','a','t','c','c','g','t','a','c','g','a','g','t','t',
  'a','c','g','t','g','a','c','t','c','g','t','a','g','a','c','t',
  'g','t','a','c','c','a','g','t','a','g','c','t','c','g','t','g',
  'a','t','c','g','g','a','t','a','c','t','g','a','g','c','a','t',
  'c','g','a','c','g','t','t','a','g','c','g','a','a','t','c','c',
  't','g','t','g','c','a','c','g','g','t','a','c','t','g','a','c',
  't','a','g','t','g','c','t','g','a','a','t','c','c','g','g','a',
  'c','c','t','a','g','t','c','g','t','a','c','a','g','t','g','a',
  'c','a','c','g','a','g','t','t','g','t','a','c','t','g','a','g',
  'c','g','t','a','c','t','c','a','g','g','t','c','t','g','a','t',
  'g','t','a','c','c','g','t','a','g','c','a','t','c','g','t','g',
  'a','t','g','a','t','c','c','g','t','a','c','g','a','g','t','t',
  't','g','a','c','t','a','g','c','c','t','g','g','a','t','a','c',
  'c','g','t','a','t','g','a','c','c','t','g','g','a','c','t','g',
  'a','g','t','c','g','t','a','g','t','a','c','c','g','a','g','t',
  'g','c','t','a','c','g','g','t','a','t','g','c','t','g','a','c',
  'g','t','g','a','c','t','c','g','a','g','t','a','c','g','t','c',
  'a','g','a','t','c','g','t','a','g','g','a','c','g','t','a','t',
  'c','a','g','t','g','c','a','t','g','t','g','a','c','t','g','c',
  't','g','a','t','a','g','t','g','c','a','g','c','t','a','g','t',
  'a','c','g','t','t','g','c','a','t','g','a','c','t','g','g','a',
  'a','t','a','g','c','a','t','g','a','c','t','c','g','a','g','t'
)

val sec1024 = Seq(
  'a','c','g','t','g','a','c','t','c','g','t','a','g','a','c','t',
  'g','t','a','c','c','a','g','t','a','g','c','t','c','g','t','g',
  'a','t','c','g','g','a','t','a','c','t','g','a','g','c','a','t',
  'c','g','a','c','g','t','t','a','g','c','g','a','a','t','c','c',
  't','g','t','g','c','a','c','g','g','t','a','c','t','g','a','c',
  't','a','g','t','g','c','t','g','a','a','t','c','c','g','g','a',
  'c','c','t','a','g','t','c','g','t','a','c','a','g','t','g','a',
  'c','a','c','g','a','g','t','t','g','t','a','c','t','g','a','g',
  'c','g','t','a','c','t','c','a','g','g','t','c','t','g','a','t',
  'g','t','a','c','c','g','t','a','g','c','a','t','c','g','t','g',
  'a','t','g','a','t','c','c','g','t','a','c','g','a','g','t','t',
  't','g','a','c','t','a','g','c','c','t','g','g','a','t','a','c',
  'c','g','t','a','t','g','a','c','c','t','g','g','a','c','t','g',
  'a','g','t','c','g','t','a','g','t','a','c','c','g','a','g','t',
  'g','c','t','a','c','g','g','t','a','t','g','c','t','g','a','c',
  'g','t','g','a','c','t','c','g','a','g','t','a','c','g','t','c',
  'a','g','a','t','c','g','t','a','g','g','a','c','g','t','a','t',
  'c','a','g','t','g','c','a','t','g','t','g','a','c','t','g','c',
  't','g','a','t','a','g','t','g','c','a','g','c','t','a','g','t',
  'a','c','g','t','t','g','c','a','t','g','a','c','t','g','g','a',
  'g','t','c','a','g','t','g','a','c','t','g','a','t','g','c','t',
  'g','a','c','t','c','g','t','a','c','g','t','t','a','c','g','t',
  'g','a','c','t','c','g','t','a','g','a','c','t','g','t','a','c',
  'c','a','g','t','a','g','c','t','c','g','t','g','a','t','c','g',
  'g','a','t','a','c','t','g','a','g','c','a','t','c','g','a','c',
  'g','t','t','a','g','c','g','a','a','t','c','c','t','g','t','g',
  'c','a','c','g','g','t','a','c','t','g','a','c','t','a','g','t',
  'g','c','t','g','a','a','t','c','c','g','g','a','c','c','t','a',
  'g','t','c','g','t','a','c','a','g','t','g','a','c','a','c','g',
  'a','g','t','t','g','t','a','c','t','g','a','g','c','g','t','a',
  'c','t','c','a','g','g','t','c','t','g','a','t','g','t','a','c',
  'c','g','t','a','g','c','a','t','c','g','t','g','a','t','g','a',
  't','c','c','g','t','a','c','g','a','g','t','t','a','c','g','t',
  'g','a','c','t','c','g','t','a','g','a','c','t','g','t','a','c',
  'c','a','g','t','a','g','c','t','c','g','t','g','a','t','c','g',
  'g','a','t','a','c','t','g','a','g','c','a','t','c','g','a','c',
  'g','t','t','a','g','c','g','a','a','t','c','c','t','g','t','g',
  'c','a','c','g','g','t','a','c','t','g','a','c','t','a','g','t',
  'g','c','t','g','a','a','t','c','c','g','g','a','c','c','t','a',
  'g','t','c','g','t','a','c','a','g','t','g','a','c','a','c','g',
  'a','g','t','t','g','t','a','c','t','g','a','g','c','g','t','a',
  'c','t','c','a','g','g','t','c','t','g','a','t','g','t','a','c',
  'c','g','t','a','g','c','a','t','c','g','t','g','a','t','g','a',
  't','c','c','g','t','a','c','g','a','g','t','t','t','g','a','c',
  't','a','g','c','c','t','g','g','a','t','a','c','c','g','t','a',
  't','g','a','c','c','t','g','g','a','c','t','g','a','g','t','c',
  'g','t','a','g','t','a','c','c','g','a','g','t','g','c','t','a',
  'c','g','g','t','a','t','g','c','t','g','a','c','g','t','g','a',
  'c','t','c','g','a','g','t','a','c','g','t','c','a','g','a','t',
  'c','g','t','a','g','g','a','c','g','t','a','t','c','a','g','t',
  'g','c','a','t','g','t','g','a','c','t','g','c','t','g','a','t',
  'a','g','t','g','c','a','g','c','t','a','g','t','a','c','g','t',
  't','g','c','a','t','g','a','c','t','g','g','a','a','g','t','c',
  'a','g','t','g','a','c','t','g','a','t','g','c','t','g','a','c',
  'c','g','t','a','g','c','a','t','c','g','t','g','a','t','g','a',
  't','c','c','g','t','a','c','g','a','g','t','t','a','c','g','t',
  'g','a','c','t','c','g','t','a','g','a','c','t','g','t','a','c',
  'c','a','g','t','a','g','c','t','c','g','t','g','a','t','c','g',
  'g','a','t','a','c','t','g','a','g','c','a','t','c','g','a','c',
  'g','t','t','a','g','c','g','a','a','t','c','c','t','g','t','g',
  'c','a','c','g','g','t','a','c','t','g','a','c','t','a','g','t',
  'g','c','t','g','a','a','t','c','c','g','g','a','c','c','t','a',
  'g','t','c','g','t','a','c','a','g','t','g','a','c','a','c','g',
  'a','g','t','t','g','t','a','a','t','c','c','a','a','t','c','c'
)

def generarSecuenciaAleatoria(longitud: Int): Seq[Char] = {
  val random = new Random()
  Seq.fill(longitud)(alfabeto(random.nextInt(alfabeto.length)))
}

val sec2048 = generarSecuenciaAleatoria(2048)
val sec4096 = generarSecuenciaAleatoria(4096)

sec256.length
sec512.length
sec1024.length
sec16.length

val repeticiones = 5

val configuracionesDePrueba = List(
  (2, sec, 1, 10, 20),
  (4, sec4, 2, 10, 20),
  (8, sec8, 4, 10, 20),
  (16, sec16, 8, 10, 15),
  (32, sec32, 16, 8, 12),
  (64, sec64, 32, 5, 10),
  (128, sec128, 64, 3, 5),
  (256, sec256, 128, 2, 4),
  (512, sec512, 256, 1, 3),
  (1024, sec1024, 512, 1, 2),
)

configuracionesDePrueba.foreach { case (longitud, secuencia, umbral, minWarmup, maxWarmup) =>

  println(s"--- Iniciando Benchmark para Longitud = $longitud (Warmups: $minWarmup/$maxWarmup) ---")

  val oraculo = crearOraculo(1)(secuencia)

  val algoSecuencial: benchmark.Algoritmo = (n, o) =>
    ReconstCadenas.reconstruirCadenaTurbo(n, o)

  val algoParalelo: benchmark.Algoritmo = (n, o) =>
    reconstruirCadenaTurboPar(umbral)(n, o)

  println("Ejecutando benchmarks...")

  // AHORA SE PASAN LOS VALORES DE WARMUP A LA FUNCIÓN DE BENCHMARK
  val ((promedioSec, promedioPar, promedioSpeedup), resultados) =
    promedioComparacion(algoSecuencial, algoParalelo)(longitud, repeticiones, oraculo, minWarmup, maxWarmup)

  println("\n" + "="*95)
  println(s"               RESULTADOS PARA EL INFORME: reconstruirCadenaTurbo (n=$longitud)")
  println("="*95)

  println(f"${"Longitud"}%-10s | ${"Intento"}%-10s | ${"Acelerada (ms)"}%-18s | ${"AceleradaPar (ms)"}%-20s | ${"Speedup"}%-10s | ${"Umbral"}%-8s")
  println("-" * 95)

  resultados.zipWithIndex.foreach { case ((tiempoSec, tiempoPar, speedup), i) =>
    println(f"${longitud}%-10d | ${i + 1}%-10d | ${tiempoSec}%-18.4f | ${tiempoPar}%-20.4f | ${speedup}%-10.2f | ${umbral}%-8d")
  }

  println("-" * 95)
  println(f"${""}%-10s | ${"Promedio"}%-10s | ${promedioSec}%-18.4f | ${promedioPar}%-20.4f | ${promedioSpeedup}%-10.2f | ${umbral}%-8d")
  println("="*95 + "\n\n")
}

println("--- Todos los benchmarks han finalizado. ---")

val configuracionesDePruebaMejorada = List(
  (2, sec, 1, 10, 20),
  (4, sec4, 2, 10, 20),
  (8, sec8, 4, 10, 20),
  (16, sec16, 8, 10, 15),
  (32, sec32, 16, 8, 12),
  (64, sec64, 32, 5, 10),
  (128, sec128, 64, 3, 5),
  (256, sec256, 128, 2, 4),
  (512, sec512, 256, 1, 3),
  (1024, sec1024, 512, 1, 2),
  (2048, sec2048, 1024, 1, 1),
  (4096, sec4096, 2048, 1, 1)
)


configuracionesDePruebaMejorada.foreach { case (longitud, secuencia, umbral, minWarmup, maxWarmup) =>

  println(s"--- Iniciando Benchmark para Longitud = $longitud (Warmups: $minWarmup/$maxWarmup) ---")

  val oraculo = crearOraculo(1)(secuencia)

  val algoSecuencial: benchmark.Algoritmo = (n, o) =>
    ReconstCadenas.reconstruirCadenaTurboMejorada(n, o)

  val algoParalelo: benchmark.Algoritmo = (n, o) =>
    reconstruirCadenaTurboMejoradaPar(umbral)(n, o)

  println("Ejecutando benchmarks...")

  // AHORA SE PASAN LOS VALORES DE WARMUP A LA FUNCIÓN DE BENCHMARK
  val ((promedioSec, promedioPar, promedioSpeedup), resultados) =
    promedioComparacion(algoSecuencial, algoParalelo)(longitud, repeticiones, oraculo, minWarmup, maxWarmup)

  println("\n" + "="*95)
  println(s"               RESULTADOS PARA EL INFORME: reconstruirCadenaTurboMejorada (n=$longitud)")
  println("="*95)

  println(f"${"Longitud"}%-10s | ${"Intento"}%-10s | ${"Acelerada (ms)"}%-18s | ${"AceleradaPar (ms)"}%-20s | ${"Speedup"}%-10s | ${"Umbral"}%-8s")
  println("-" * 95)

  resultados.zipWithIndex.foreach { case ((tiempoSec, tiempoPar, speedup), i) =>
    println(f"${longitud}%-10d | ${i + 1}%-10d | ${tiempoSec}%-18.4f | ${tiempoPar}%-20.4f | ${speedup}%-10.2f | ${umbral}%-8d")
  }

  println("-" * 95)
  println(f"${""}%-10s | ${"Promedio"}%-10s | ${promedioSec}%-18.4f | ${promedioPar}%-20.4f | ${promedioSpeedup}%-10.2f | ${umbral}%-8d")
  println("="*95 + "\n\n")
}

println("--- Todos los benchmarks han finalizado. ---")