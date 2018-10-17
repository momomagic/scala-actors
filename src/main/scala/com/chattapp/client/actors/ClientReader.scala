package com.chattapp.client.actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorSystem, Kill}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import com.chattapp.messages.{Message, ReadMessage}

class ClientReader(address: InetSocketAddress, actorSystem: ActorSystem,
                   callback:Message => Unit) extends Actor{
  IO(Tcp)(actorSystem) ! Connect(address)

  def receive = {
    case CommandFailed(_: Tcp.Command) =>
      self ! Kill
      actorSystem.terminate()
    case Connected(_, _) =>
      val connection = sender()
      connection ! Register(self)
      context become {
        case Received(data) =>
          callback(ReadMessage(data.decodeString("US-ASCII")))
      }
  }

}