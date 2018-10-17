package com.chattapp.server.actors

import akka.actor.{Actor, ActorSystem}

abstract class ServerActor(actorSystem: ActorSystem, callback: com.chattapp.messages.Message => Unit) extends Actor {
  val CommandCharacter = "~"


  def isCommand(message: String): Boolean = {
    message.startsWith(CommandCharacter)
  }

  def getCommand(message: String): String = {
    val split = message.split(" ")
    val command = split(0)
    val actualCommand = command.substring(1, command.length())
    actualCommand
  }


}
