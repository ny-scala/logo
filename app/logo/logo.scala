package logo

case class Turtle(x: Int, y: Int, id: Char) {
  def up   (board: Board): Turtle = if ((y-1) < 1           ) copy(y=board.height) else copy(y=y-1)
  def down (board: Board): Turtle = if ((y+1) > board.height) copy(y=1           ) else copy(y=y+1)
  def left (board: Board): Turtle = if ((x-1) < 1           ) copy(x=board.width ) else copy(x=x-1)
  def right(board: Board): Turtle = if ((x+1) > board.width ) copy(x=1           ) else copy(x=x+1)
  def occupies(xp: Int, yp: Int) = x == xp && y == yp
}

case class Board(width: Int, height: Int) {
  def pretty(turtles: Traversable[Turtle]): String = {
    plot(turtles).map( line =>
      line.mkString
    ).mkString("\n")
  }
  def plot(turtles: Traversable[Turtle]): Seq[Seq[Char]] = {
    for (y <- 1 to height) yield {
      for (x <- 1 to width) yield {
        turtles.find( _.occupies(x, y) ).map(_.id).getOrElse('.')
      }
    }
  }
}

object Logo {
  def main(args: Array[String]) {
    println("logo");
  }
  val b = Board(10, 10)
  val t1 = Turtle(2, 5, 'n')
}
