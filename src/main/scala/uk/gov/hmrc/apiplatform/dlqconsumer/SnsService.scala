package uk.gov.hmrc.apiplatform.dlqconsumer

import com.amazonaws.services.lambda.runtime.LambdaLogger
import org.slf4j.{Logger, LoggerFactory}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{ListTopicsRequest, PublishRequest, PublishResponse}
import org.slf4j.event.Level

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala


class SnsService()(implicit ec:ExecutionContext) extends LambdaLogger{
  val logger:Logger = LoggerFactory.getLogger(this.getClass)
  override def log(message: String): Unit = logger.info(message)

  override def log(message: Array[Byte]): Unit = logger.info(new String(message))

  val client: SnsClient = SnsClient.builder()
    .region(Region.EU_WEST_2)
    .build()

  private def getSNSTopicArn()(implicit ec:ExecutionContext) = {
    val topicName = "protected-api-gateway-notifications"
    val request = ListTopicsRequest.builder.build

    Future(client.listTopics(request).topics().asScala.toList.find(_.topicArn().contains(topicName)))
  }

  def sendMessage(message:String): Future[PublishResponse] = {
    for {
      maybeArn <- getSNSTopicArn()
      arn = maybeArn.getOrElse(throw new Exception("Unable to get topic ARN"))
      _ <- log.info(s"SNS Topic is $arn and topicArn is ${arn.topicArn}")
      publishRequest = PublishRequest.builder().topicArn(arn.topicArn()).message(message).build()
      publishResponse = client.publish(publishRequest)
      _ <- log.info(s"Publish response is $publishResponse")
    } yield publishResponse
  }
}
