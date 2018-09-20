package com.chattapp.server

import akka.actor.{ActorSystem, Props}

object ServerMain extends App {
  val system = ActorSystem("ServerMain")
  val server = system.actorOf(Props(new ServerActor(system)))

}
