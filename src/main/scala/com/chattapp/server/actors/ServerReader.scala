package com.chattapp.server.actors

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.chattapp.messages.{Message, _}

class ServerReader(actorSystem: ActorSystem, portGiven: Int, callback:Message => Unit) extends ServerActor(actorSystem,callback) {

  val server = "localhost"
  IO(Tcp)(actorSystem) ! Bind(self, new InetSocketAddress(this.server, this.portGiven))

  override def receive: Receive = {
    case CommandFailed(_: Bind) =>
      println("Failed to start listening on " + server + ":" + portGiven)
      context stop self
      actorSystem.terminate()

    case Bound(localAddress: InetSocketAddress) =>
      println("Started listening on " + localAddress)

    case Connected(_, _) =>
      callback(NewClientSpeaking(sender.path.name, sender))
      sender ! Register(self)
    case Received(data) =>
      val text = data.decodeString("US-ASCII")
      val clientActorName = sender.path.name
      if (isCommand(text)) {
        getCommand(text) match {
          case "quit" => callback(QuitClientMessage(clientActorName))
          case "online" => callback(OnlineMessage())
          case _ => callback(ErrorMessage(clientActorName, "Unknown command!"))
        }
      } else {
        callback(SendClientMessageToAll(text, serverMessage = false))
      }
  }

}
