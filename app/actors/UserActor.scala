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

case class Move(uid: Int, dir: String)

class UserActor(uid: Int, board: ActorRef, out: ActorRef) extends Actor with ActorLogging {

  override def preStart() = {
    BoardActor() ! Subscribe
    BoardActor() ! Move(uid, "d") // initial move to cause an initial board state("Field") send to user
  }

  private def isValidLogoCommand(msg: JsValue): Boolean = {
    msg match {
      case JsString("u"|"d"|"r"|"l") => true
      case _ => false
    }
  }

  def receive = LoggingReceive {
    case Field(fieldString) if sender == board => {
      val js = Json.obj("type" -> "field", "msg" -> fieldString)
      out ! js
    }

    case Message(muid, s) if sender == board => {
      val js = Json.obj("type" -> "message", "uid" -> muid, "msg" -> s)
      out ! js
    }

    case js: JsValue if isValidLogoCommand((js \ "move")) => {
      // Move this turtle
      board ! Move(uid, (js \ "move").as[String])
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
