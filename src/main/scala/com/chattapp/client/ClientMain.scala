package com.chattapp.client

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}
import com.chattapp.client.actors.{ClientReader, ClientSender}
import com.chattapp.messages._

object ClientMain extends App {
  val portSpeaking = 18573
  val portListening = 18572

  val system = ActorSystem("ClientMain")
  val clientSpeaking = system.actorOf(Props(new ClientSender(new InetSocketAddress("localhost",portSpeaking), system,callbackFromSender)))
  val clientListening = system.actorOf(Props(new ClientReader(new InetSocketAddress("localhost",portListening),system,callbackFromReader)))

  val bufferedReader = io.Source.stdin.bufferedReader()
  loop(EmptyMessage())

  def loop(message: Message): Boolean = message match {
    case OrderMessage(keyword) if keyword == "~quit" =>
      system.terminate()
      false
    case EmptyMessage() =>
      val message = bufferedReader.readLine()
      clientSpeaking ! SendMessage(message)
      loop(EmptyMessage())
  }

  def callbackFromReader(message: Message): Unit= {
    message match {
      case ReadMessage(message: String) =>
        print(message)

    }
  }

  def callbackFromSender(message: Message): Unit = {
    message match {
      case MessageSentSuccess() =>
        print("message successfully sent")
    }
  }

}



