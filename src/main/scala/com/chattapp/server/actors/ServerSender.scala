package com.chattapp.server.actors

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.chattapp.messages._
import com.chattapp.messages.Message

class ServerSender(actorSystem: ActorSystem,portGiven: Int,callback:Message => Unit) extends ServerActor(actorSystem,callback) {

  val server = "localhost"
  IO(Tcp)(actorSystem) ! Bind(self, new InetSocketAddress(this.server, this.portGiven))
  override def receive: Receive = {
    case CommandFailed(_: Bind) =>
      context stop self
      actorSystem.terminate()

    case Bound(localAddress: InetSocketAddress) =>
      println("Started listening on " + localAddress)

    case Connected(_, _) =>
      callback(NewClientListening(sender.path.name, sender))
      sender ! Register(self)
      context become {
        case SendMessageToAll(actors: List[ActorRef],message: String,serverMessage: Boolean) =>
          send(actors,message,serverMessage)
      }
  }

  def send(actor: ActorRef,message: String,serverMessage: Boolean): Unit = {
    if (serverMessage) {
      actor ! Write(ByteString("[SERVER]: " + message))
    } else {
      actor ! Write(ByteString(message))
    }
  }

  def send(actors: List[ActorRef],message: String,serverMessage: Boolean): Unit = {
    actors.foreach(f => send(f, message, serverMessage))
  }
}
