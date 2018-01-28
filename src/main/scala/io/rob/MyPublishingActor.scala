
package io.rob

import akka.actor.{Actor, ActorRef, Props}
import com.paddypowerbetfair.rabbitmq.{BasicPublish, PublisherChannelActor}
import com.rabbitmq.client.AMQP.BasicProperties
import com.paddypowerbetfair.rabbitmq.RegisterOk

object MyPublishingActor {

  def props: Props = Props(new MyPublishingActor())
}



class MyPublishingActor extends Actor {

  def receive: Receive = {
    case PublisherChannelActor(rabbitPublisher) =>
      readyToPublish(rabbitPublisher)
  }


  def readyToPublish(publisher: ActorRef): Receive = {
    case RegisterOk => ()

    case s: String => publisher ! BasicPublish(
      "exchangeName",
      "",
      new BasicProperties(), // reference to BasicProperties which defines AMQP Headers
      """{"message": "hello world"}""".getBytes("UTF-8")
    )
    sender() ! s

  }

}