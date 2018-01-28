package io.rob

import akka.actor.{ActorRef, ActorSystem, Props}
import com.paddypowerbetfair.rabbitmq.{BasicPublish, RabbitMqActor, RegisterPublisher}
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Address
import akka.pattern.ask

import scala.util.{Failure, Success}

object MyApp extends App {
  val addresses: List[com.rabbitmq.client.Address] = List(new Address("localhost", 5672))

  implicit val system: ActorSystem = ActorSystem()

  val connectionFactory = new com.rabbitmq.client.ConnectionFactory()

  val rabbitActor: ActorRef = system.actorOf(Props(new RabbitMqActor(connectionFactory, addresses)), "rabbitActor")

  val myPublishingActor: ActorRef = system.actorOf(MyPublishingActor.props)

  rabbitActor ! RegisterPublisher(myPublishingActor, declarations = Nil, publisherConfirms = false)

  myPublishingActor ? "" onComplete {
    case Success(_) =>
      system.terminate()
    case Failure(_) =>
      ()
  }

}
