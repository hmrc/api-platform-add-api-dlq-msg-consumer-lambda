package uk.gov.hmrc.apiplatform.dlqconsumer

import scala.jdk.CollectionConverters._

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import uk.gov.hmrc.apiplatform.dlqconsumer.SnsServiceProvider.dlqSnsService

class ApiPublishFailureHandler(snsService: SnsService) extends RequestHandler[SQSEvent, Unit] {
  def this() {
    this(dlqSnsService)
  }

  override def handleRequest(input: SQSEvent, context: Context): Unit = {
    input.getRecords.asScala.foreach { sqsMsg =>
      Console.println(s"DLQ message: ${sqsMsg.getBody}")
      sendEventBody(sqsMsg.getBody)
    }

    def sendEventBody(msgBody: String): Unit = {
      snsService.sendMessage(s"""{"AlarmDescription": "AWS API Gateway publishing failure", "NewStateValue": "ALARM", "detail": $msgBody}""").messageId
    }
  }
}
