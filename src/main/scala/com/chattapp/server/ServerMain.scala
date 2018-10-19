package com.chattapp.server

import akka.actor.{ActorRef, ActorSystem, Props}
import com.chattapp.messages._
import com.chattapp.server.actors.{ServerReader, ServerSender}


object ServerMain extends App {
  val activeClientsListening = scala.collection.mutable.HashMap.empty[String, ActorRef]
  val activeClientsSpeaking = scala.collection.mutable.HashMap.empty[String, ActorRef]

  def callbackFromReader(message: Message): Unit = {
    message match {
      case NewClientSpeaking(name: String, actor: ActorRef) =>
        activeClientsSpeaking += (name -> actor)
      case QuitClientMessage(name: String) =>
        quit(name)
      case SendClientMessageToAll(message: String,serverMessage: Boolean) =>
        this.sender ! SendMessageToAll(activeClientsListening.values.toList,message,serverMessage)
    }
  }

  def callbackFromSender(message: Message): Unit = {
    message match {
      case MessageSentSuccess() =>
      case NewClientListening(name: String, actor: ActorRef) =>
        activeClientsListening += (name -> actor)
      case QuitClientMessage(name: String) =>
        quit(name, sender = true)
    }
  }

  val system = ActorSystem("serverActors")
  val sender = system.actorOf(Props(new ServerSender(system,18572,callbackFromSender)))
  val reader = system.actorOf(Props(new ServerReader(system, 18573, callbackFromReader)))



  def getActorRefByName(name: String,sender: Boolean = false): Any = if(sender) activeClientsListening(name) else activeClientsSpeaking(name)

  def quit(clientActorName: String, sender: Boolean = false): Unit = {
    if (sender) {
      if (activeClientsListening.contains(clientActorName)) {
        activeClientsListening -= clientActorName
        //sendToAll("", "<" + ClientIdentities.get(clientActorName).get + "> has left the chatroom.", serverMessage = true)
      }
    } else {
      if (activeClientsSpeaking.contains(clientActorName)) {
        activeClientsSpeaking -= clientActorName
        //sendToAll("", "<" + ClientIdentities.get(clientActorName).get + "> has left the chatroom.", serverMessage = true)
      }
    }
  }

}

