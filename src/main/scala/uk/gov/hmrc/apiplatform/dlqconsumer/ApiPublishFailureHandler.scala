package uk.gov.hmrc.apiplatform.dlqconsumer
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import uk.gov.hmrc.apiplatform.dlqconsumer.SnsServiceProvider.dlqSnsService

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

class ApiPublishFailureHandler(snsService: SnsService)  extends RequestHandler[SQSEvent, Unit] {
  def this() {
    this(dlqSnsService)
  }

  override def handleRequest(input: SQSEvent, context: Context): Unit = {
    Console.println(s"Entering handleRequest with message of type ${input.getClass.getName}")
    input.getRecords.foreach { sqsMsg =>
       sendEventBody(sqsMsg.getBody)
    }

    def sendEventBody(msgBody: String): Unit = {
      val responseMsgId = snsService.sendMessage(s"""{"AlarmDescription": "AWS API Gateway publishing failure", "NewStateValue": "ALARM", "detail": $msgBody}""", context).messageId
      Console.println(s"PublishResponse message ID is $responseMsgId")
      }
    }
  }
