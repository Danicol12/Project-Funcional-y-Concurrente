import ReconstCadenas.*
import ReconstCadenasPar.*
import Oraculo.*
import ArbolSufijos.*
import benchmark.*
import org.scalameter.measure

val sec = List('g','a')
val sec4 = Seq('a', 'c', 'g', 't')
val sec8 = Seq('g', 'a', 't', 'c', 'c', 'a', 'g', 't')


//Algunos ejemplos simples de las funciones:

val cadenaAleatoria=contruirCadenaAleatoria(5)
val cadenaAleatoria2=contruirCadenaAleatoria(128)
val cadenaAleatoria3=contruirCadenaAleatoria(64)


val orac = crearOraculo(1)(cadenaAleatoria)

val orac2= crearOraculo(1)(cadenaAleatoria2)

val orac3=crearOraculo(1)(cadenaAleatoria3)
/*
val prueba0= compararAlgoritmos(reconstruirCadenaTurbo,reconstruirCadenaTurboMejorada)(128,orac3)

 */
/*
val prueba1=reconstruirCadenaIngenuo(5,orac)
val prueba2=reconstruirCadenaIngenuoPar(3)(5,orac)

prueba1==prueba2
promedioComparacion(reconstruirCadenaTurboMejorada,reconstruirCadenaTurboMejoradaPar(3))(5,3,orac)

 */

val prueba3=reconstruirCadenaTurboAcelerada(64,orac3)
val prueba4=reconstruirCadenaTurboAceleradaPar(16)(64,orac3)

prueba3==prueba4
val resultados = promedioComparacion(reconstruirCadenaTurboMejorada,reconstruirCadenaTurboMejoradaPar(16))(64,2,orac3)





