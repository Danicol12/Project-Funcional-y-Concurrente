import ArbolSufijos._
import Oraculo._
import scala.util.Random

package object ReconstCadenas {
/*
  def reconstruirCadenaIngenuo(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    ???
  }

  def reconstruirCadenaMejorado(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    ???
  }

 */

  def reconstruirCadenaTurbo(n: Int, o: Oraculo): Seq[Char] = {

    val filtrado={
      for {
        cha <- alfabeto
        if o(Seq(cha))
        cor <- Seq(cha)
      } yield cor
    }

    val combinaciones = for {
      c1 <- filtrado
      c2 <- filtrado
    } yield Seq(c1, c2)

    val filtrado2={
      for {
        cha <- combinaciones
        if o(cha)
        cor <- Seq(cha)
      } yield cor
    }

    def funcionrecursivaTurbo(alfa:Seq[Seq[Char]]):Seq[Char]={
      val combinacionesSec = for {
        c1 <- alfa
        c2 <- alfa
      } yield c1 ++ c2
      val filtrado=combinacionesSec.filter(x=> o(x))
      if (filtrado.head.length==n) filtrado.head
        else{
          funcionrecursivaTurbo(filtrado)
        }
    }
    if(n==1){
      filtrado
    }
    else if(n==2){
      filtrado2.head
    }

      else{
        funcionrecursivaTurbo(filtrado2)}
  }



  def reconstruirCadenaTurboMejorada(n: Int, o: Oraculo): Seq[Char] = {

    def particiones(caracteres:Seq[Char], conjInicial:Seq[Seq[Char]]):Boolean={
      val tam= conjInicial.head.length
      val part=caracteres.sliding(tam).toList
      if( part.forall(w => conjInicial.contains(w))) true
      else false
    }

    def filtrar(cadenas: Seq[Seq[Char]]): Seq[Seq[Char]] = {

      for {
        c1 <- cadenas
        c2 <- cadenas
        s=c1++c2
        if(particiones(s,cadenas))
      } yield s

    }

    val filtrado={
      for {
        cha <- alfabeto
        if o(Seq(cha))
        cor <- Seq(cha)
      } yield cor
    }

    val combinaciones = for {
      c1 <- filtrado
      c2 <- filtrado
    } yield Seq(c1, c2)


    def recursivaTurboMejorada(alfa:Seq[Seq[Char]]):Seq[Char]={
      val combinacionesSec = filtrar(alfa)
      val filtrado=combinacionesSec.filter(x=> o(x))
      if (filtrado.head.length==n) filtrado.head
        else{
          recursivaTurboMejorada(filtrado)
        }
    }
    if(n==1){
      filtrado
    }
    else if(n==2){
      val filtrado2 = {
        for {
          cha <- combinaciones
          if o(cha)
          cor <- Seq(cha)
        } yield cor
      }
      filtrado2.head
    }

      else{
        recursivaTurboMejorada(combinaciones)}
  }

  def reconstruirCadenaTurboAcelerada(n: Int, o: Oraculo): Seq[Char] = {
    // Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2), y un oraculo para esa secuencia
    // y devuelve la secuencia reconstruida
    // Usa la propiedad de que si s = s1 ++ s2 entonces s1 y s2 también son subsecuencias de s
    // Usa el filtro para ir más rápido
    // Usa árboles de sufijos para guardar Seq[Seq[Char]]
    def particiones(caracteres: Seq[Char], conjInicial: Seq[Seq[Char]],arbol:Trie): Boolean = {
      val tam = conjInicial.head.length
      val part = caracteres.sliding(tam).toList
      if (part.forall(w => pertenece(w,arbol))) true
      else false
    }

    def filtrar(cadenas: Seq[Seq[Char]]): Seq[Seq[Char]] = {
      val arbol = arbolDeSufijos(cadenas)
      for {
        c1 <- cadenas
        c2 <- cadenas
        s = c1 ++ c2
        if (particiones(s, cadenas,arbol))
      } yield s

    }

    val filtrado = {
      for {
        cha <- alfabeto
        if o(Seq(cha))
        cor <- Seq(cha)
      } yield cor
    }

    val combinaciones = for {
      c1 <- filtrado
      c2 <- filtrado
    } yield Seq(c1, c2)


    def recursivaTurboAcelerada(alfa: Seq[Seq[Char]]): Seq[Char] = {
      val combinacionesSec = filtrar(alfa)
      val filtrado = combinacionesSec.filter(x => o(x))
      if (filtrado.head.length == n) filtrado.head
      else {
        recursivaTurboAcelerada(filtrado)
      }
    }

    if (n == 1) {
      filtrado
    }
    else if (n == 2) {
      val filtrado2 = {
        for {
          cha <- combinaciones
          if o(cha)
          cor <- Seq(cha)
        } yield cor
      }
      filtrado2.head
    }

    else {
      recursivaTurboAcelerada(combinaciones)
    }
  }
  
  
  def contruirCadenaAleatoria(tam:Int):Seq[Char]={
    for{
      i<- 1 to tam
    }yield alfabeto(Random.nextInt(alfabeto.length))
  }
}
