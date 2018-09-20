package com.chattapp.client

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}
import com.chattapp.client.ClientMessage.SendMessage

object ClientMain extends App {
  val port = 18573

  val system = ActorSystem("ClientMain")
  val clientConnection = system.actorOf(Props(new Client(new InetSocketAddress("localhost",port), system)))
  val bufferedReader = io.Source.stdin.bufferedReader()
  loop("")

  def loop(message: String): Boolean = message match {
    case "~quit" =>
      system.terminate()
      false
    case _ =>
      val message = bufferedReader.readLine()
      clientConnection ! SendMessage(message)
      loop(message)
  }

}
