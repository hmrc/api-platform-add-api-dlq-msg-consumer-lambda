package uk.gov.hmrc.apiplatform.dlqconsumer

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{ListTopicsRequest, PublishRequest, PublishResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala


class SnsService()(implicit ec:ExecutionContext) {
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
      publishRequest = PublishRequest.builder().topicArn(arn.topicArn()).message(message).build()
      publishResponse = client.publish(publishRequest)
    } yield publishResponse
  }
}
