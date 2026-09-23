package uk.gov.hmrc.apiplatform.dlqconsumer

import com.amazonaws.services.lambda.runtime.Context
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{ListTopicsRequest, PublishRequest, PublishResponse}
import uk.gov.hmrc.apiplatform.dlqconsumer.SnsClientProvider.dlqSnsClient

import scala.jdk.CollectionConverters.CollectionHasAsScala


class SnsService(snsClient: SnsClient) {
  def this() {
    this(dlqSnsClient)
  }

  def sendMessage(message: String, context: Context): PublishResponse = {
    Console.println(s"Entering SNSService.sendMessage with message $message")
    val maybeTopic = getSNSTopic()
    val topic = maybeTopic.getOrElse(throw new Exception("Unable to get topic ARN"))
    Console.println(s"SNS Topic is $topic and topicArn is ${topic.topicArn}")
    val publishRequest = PublishRequest.builder().topicArn(topic.topicArn()).message(message).build()
    val publishResponse = snsClient.publish(publishRequest)
    Console.println(s"Publish response is $publishResponse")
    publishResponse
  }

  private def getSNSTopic() = {
    val topicName = "protected-api-gateway-notifications"
    val request = ListTopicsRequest.builder.build
    snsClient.listTopics(request).topics().asScala.toList.find(_.topicArn().contains(topicName))
  }
}
