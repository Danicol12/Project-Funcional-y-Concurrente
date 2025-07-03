import ReconstCadenas.*
import ReconstCadenasPar.*
import Oraculo.*
import ArbolSufijos.*
import benchmark.*
import org.scalameter.measure

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

sec256.length
sec512.length
sec1024.length
sec16.length

val cadenaAleatoria=contruirCadenaAleatoria(7)
val orac = crearOraculo(1)(cadenaAleatoria)
/*
//val pruebaTurbo= reconstruirCadenaTurbo(128, orac)
val pruebaTurboMejorada= reconstruirCadenaTurboMejorada(128, orac)
val pruebaTurboAcelerada= reconstruirCadenaTurboAcelerada(128,orac)

pruebaTurboMejorada==pruebaTurboAcelerada
pruebaTurboAcelerada==secprueba
*/

promedioComparacion(reconstruirCadenaIngenuo, reconstruirCadenaIngenuoPar(5))(7,5,orac)

//compararAlgoritmos(reconstruirCadenaTurboAcelerada,reconstruirCadenaTurboAceleradaPar(8))(256,orac)
