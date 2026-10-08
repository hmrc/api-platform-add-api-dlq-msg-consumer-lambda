package uk.gov.hmrc.apiplatform.dlqconsumer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import software.amazon.awssdk.services.sns.model._

import java.util.Collections

class ApiPublisherFailureHandlerTest  extends AnyWordSpec with Matchers with MockitoSugar {
val sqsMessageBody = """{"paths":{"/{name}":{"get":{"parameters":[{"name":"name","required":true,"type":"string","description":"","in":"path"}],"responses":{"200":{"description":"OK"}},"x-auth-type":"None","x-throttling-tier":"Unlimited"}},"/{year}":{"get":{"parameters":[{"name":"year","required":true,"type":"string","description":"","in":"path"}],"responses":{"200":{"description":"OK"}},"x-auth-type":"None","x-throttling-tier":"Unlimited"}}},"info":{"title":"hello--3.0","version":"3.0"},"swagger":"2.0","basePath":"/hello","host":"api-example-microservice.protected.mdtp"}"""
  val sqsMessage = new SQSMessage
  sqsMessage.setMessageId("8f696342-757f-4940-bf55-eb474ec5a1ca")
  sqsMessage.setBody(sqsMessageBody)
  sqsMessage.setEventSource("aws:sqs")
  sqsMessage.setEventSourceArn("arn:aws:sqs:eu-west-2:618259438944:api_platform_admin_api_add_dead")
  sqsMessage.setMd5OfBody("bed885ab28c90a09db05fb8cabb418a7")
  sqsMessage.setAwsRegion("eu-west-2")
  val sqsEvent: SQSEvent = new SQSEvent()
  sqsEvent.setRecords(Collections.singletonList(sqsMessage))
  val expectedSnsMsgBody = s"""{"NewStateValue": "ALARM", "detail": $sqsMessageBody}"""
  trait Setup {
    val mockSnsService: SnsService = mock[SnsService]
    val mockContext: Context = mock[Context]
    val subject = new ApiPublishFailureHandler(mockSnsService)
  }

  "send message" should {
    "successfully send a message" in new Setup {
      val publishResponse = PublishResponse.builder().build()
      when(mockSnsService.sendMessage(any[String],any[Context])).thenReturn(publishResponse)

      val result = subject.handleRequest(sqsEvent, mockContext)

      verify(mockSnsService).sendMessage(expectedSnsMsgBody, mockContext)
      result shouldEqual ()
    }
  }
}
