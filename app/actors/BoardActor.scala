package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.actor.ActorRef
import akka.actor.Terminated
import play.libs.Akka
import akka.actor.Props
import logo._

class BoardActor extends Actor with ActorLogging {
  var users = Set[ActorRef]()

  val board = Board(10, 10)

  var turtles: Map[Int, Turtle] = Map.empty

  def receive = LoggingReceive {

    case Move(uid, turtle) => {
      turtles = turtles.updated(uid, turtle)
      val boardString = board.pretty(turtles.values)
      users map { user => user ! Field(boardString) }
    }

    case m: Message => users map { _ ! m}

    case Subscribe => {
      users += sender
      context watch sender
    }

    case Terminated(user) => users -= user
  }
}

object BoardActor {
  lazy val board = Akka.system().actorOf(Props[BoardActor])
  def apply() = board
}

case class Message(uuid: Int, s: String)
case class Field(s: String)
object Subscribe
