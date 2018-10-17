package com.chattapp.messages

import akka.actor.ActorRef

abstract class Message
case class MessageSentSuccess() extends Message
case class NewClientSubscribed(name: String,actor: ActorRef) extends Message
case class NewClientListening(name: String,actor: ActorRef) extends Message
case class NewClientSpeaking(name: String,actor: ActorRef) extends Message

case class SendClientMessage(actor: ActorRef,message: String,serverMessage: Boolean) extends Message
case class QuitClientMessage(name: String) extends Message
case class OnlineMessage() extends Message
case class ErrorMessage(actor: String,errorMessage: String) extends Message
case class SendClientMessageToAll(message: String,serverMessage: Boolean) extends Message
case class SendMessageToAll(actors: List[ActorRef],message: String,serverMessage: Boolean)

abstract class ClientMessage extends Message
case class SendMessage(message: String) extends ClientMessage
case class OrderMessage(message: String) extends ClientMessage
case class ReadMessage(message: String) extends ClientMessage
case class EmptyMessage() extends ClientMessage