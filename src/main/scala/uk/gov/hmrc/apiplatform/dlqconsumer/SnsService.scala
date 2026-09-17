package uk.gov.hmrc.apiplatform.dlqconsumer

import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger}
import org.slf4j.{Logger, LoggerFactory}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{ListTopicsRequest, PublishRequest, PublishResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala


class SnsService()(implicit ec: ExecutionContext) {
  val client: SnsClient = SnsClient.builder()
    .region(Region.EU_WEST_2)
    .build()

  def sendMessage(message: String, context: Context): Future[PublishResponse] = {
    Console.println(s"Entering SNSService.sendMessage with message $message" )
    val logger: LambdaLogger = context.getLogger
    for {
      maybeTopic <- getSNSTopic()
      topic = maybeTopic.getOrElse(throw new Exception("Unable to get topic ARN"))
      _ = log(this, logger, s"SNS Topic is $topic and topicArn is ${topic.topicArn}")
      publishRequest = PublishRequest.builder().topicArn(topic.topicArn()).message(message).build()
      publishResponse = client.publish(publishRequest)
      _ = log(this, logger, s"Publish response is $publishResponse")
    } yield publishResponse
  }

  private def getSNSTopic()(implicit ec: ExecutionContext) = {
    val topicName = "protected-api-gateway-notifications"
    val request = ListTopicsRequest.builder.build

    Future(client.listTopics(request).topics().asScala.toList.find(_.topicArn().contains(topicName)))
  }

  private def log[T](instance: T, logger: LambdaLogger, message: String) = {
    logger.log(message)
    instance
  }
}
