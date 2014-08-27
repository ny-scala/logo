package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import akka.actor.ActorRef
import akka.actor.Props
import scala.xml.Utility
import play.api.libs.json._
import logo._

case class Move(uid: Int, turtle: Turtle)

class UserActor(uid: Int, board: ActorRef, out: ActorRef) extends Actor with ActorLogging {

  var turtle = Turtle(0, 0, uid.toString.head)

  override def preStart() = {
    BoardActor() ! Subscribe
  }

  private def isValidLogoCommand(msg: JsValue): Boolean = {
    true // TODO
  }

  def receive = LoggingReceive {
    case Message(muid, s) if sender == board => {
      val js = Json.obj("type" -> "message", "uid" -> muid, "msg" -> s)
      out ! js
    }

    case js: JsValue if isValidLogoCommand((js \ "move")) => {
      // Move this turtle
      (js \ "move") match {
        case JsString("u") => turtle = turtle.up
        // case "d" => turtle = turtle.down
        // case "r" => turtle = turtle.right
        // case "l" => turtle = turtle.left
      }
      board ! Move(uid, turtle)
      (js \ "move").validate[String] map { Utility.escape(_) }  map { board ! Message(uid, _ ) }
    }

    case js: JsValue => {
      (js \ "msg").validate[String] map { Utility.escape(_) }  map { board ! Message(uid, _ ) }
    }

    case other => log.error("unhandled: " + other)
  }
}

object UserActor {
  def props(uid: Int)(out: ActorRef) = Props(new UserActor(uid, BoardActor(), out))
}
