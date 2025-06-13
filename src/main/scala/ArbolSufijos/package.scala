package object ArbolSufijos {

  // Definiendo otra estructura para manipular Seq[Seq[Char]]
  abstract class Trie

  case class Nodo(car: Char, marcada: Boolean, hijos: List[Trie]) extends Trie

  case class Hoja(car: Char, marcada: Boolean) extends Trie

  def raiz(t: Trie): Char = {
    //le entra solo un arbol o una hoja
    t match {
      case Nodo(c, _, _) => c
      case Hoja(c, _) => c
    }
  }

  def cabezas(t: Trie): Seq[Char] = {
    t match {
      case Nodo(_, _, lt) => lt.map(t => raiz(t))
      case Hoja(c, _) => Seq[Char](c)
    }
  }

  def pertenece(s: Seq[Char], t: Trie): Boolean = {

    s match {
      case Nil => t match {
        case Nodo(_, marcada, _) => marcada
        case Hoja(_, marcada) => marcada
      }
      case x :: y => t match {
        case Nodo(_, _, hijos) =>
          val sec = hijos.filter(l => raiz(l) == x)
          if (sec == Nil) false
          else pertenece(y, sec.head)

        case Hoja(_, _) => false
      }
    }
  }

  def adicionar(s: Seq[Char], t: Trie): Trie = {
    def construirRama(xs: Seq[Char]): Trie = xs match {
      case h :: Nil => Hoja(h, true)
      case h :: tail => Nodo(h, false, List(construirRama(tail)))
    }

    s match {
      case Nil => t match {
        case Nodo(car, _, hijos) => Nodo(car, true, hijos)
        case Hoja(car, _) => Hoja(car, true)
      }
      case x :: y => t match {

        // Si estamos en un nodo intermedio
        case Nodo(car, marcada, hijos) =>
          val hijosConX = hijos.filter(h => raiz(h) == x)

          if (hijosConX.isEmpty) {
            // No existe un hijo con raíz x → crear nueva rama
            val nuevaRama = construirRama(x +: y)
            Nodo(car, marcada, nuevaRama :: hijos)
          } else {
            // Ya existe → modificar recursivamente ese hijo
            val hijoExistente = hijosConX.head
            val hijoModificado = adicionar(y, hijoExistente)
            val hijosActualizados = hijoModificado :: hijos.filterNot(h => raiz(h) == x)
            Nodo(car, marcada, hijosActualizados)
          }

        // Si estamos en una hoja pero aún hay más caracteres por insertar
        case Hoja(car, marcada) =>
          val nuevaRama = construirRama(x +: y)
          Nodo(car, marcada, List(nuevaRama))
      }
    }
  }

  def arbolDeSufijos(ss: Seq[Seq[Char]]): Trie = {

    // Paso 1: generar todos los sufijos de todas las secuencias
    def generarSufijos(seqs: Seq[Seq[Char]]): Seq[Seq[Char]] = seqs match {
      case Nil => Nil
      case seq :: tail =>
        val sufijos = for (i <- 0 until seq.length) yield seq.drop(i)
        sufijos.toList ++ generarSufijos(tail)
    }

    // Paso 2: insertar todos los sufijos recursivamente
    def insertarSufijos(sufijos: Seq[Seq[Char]], t: Trie): Trie = sufijos match {
      case Nil => t
      case x :: xs =>
        val nuevoTrie = adicionar(x, t)
        insertarSufijos(xs, nuevoTrie)
    }

    val sufijos = generarSufijos(ss)
    insertarSufijos(sufijos, Nodo('_', false, Nil))
  }
}







