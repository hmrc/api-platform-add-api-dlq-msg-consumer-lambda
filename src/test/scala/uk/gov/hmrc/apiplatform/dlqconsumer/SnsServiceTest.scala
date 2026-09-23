package uk.gov.hmrc.apiplatform.dlqconsumer

import com.amazonaws.services.lambda.runtime.Context
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{ListTopicsRequest, ListTopicsResponse, PublishRequest, PublishResponse, Topic}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global

class SnsServiceTest  extends AnyWordSpec with Matchers with MockitoSugar {

  trait Setup {
    val mockSnsClient: SnsClient = mock[SnsClient]
    val mockContext: Context = mock[Context]
    val snsService = new SnsService(mockSnsClient)
  }

  "send message" should {
    "successfully send a message" in new Setup {
      val request = ListTopicsRequest.builder.build
      val topic: Topic = Topic.builder().topicArn("arn:aws:sns:eu-west-2:618259438944:protected-api-gateway-notifications").build()
      val listTopicsResponse = ListTopicsResponse.builder().topics(topic).build
      val publishResponse = PublishResponse.builder().build()
      when(mockSnsClient.listTopics(request)).thenReturn(listTopicsResponse)
      when(mockSnsClient.publish(any[PublishRequest])).thenReturn(publishResponse)

      val result = snsService.sendMessage("message", mockContext)

      result shouldEqual publishResponse
    }
  }
}
