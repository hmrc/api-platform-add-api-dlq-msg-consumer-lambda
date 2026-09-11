package uk.gov.hmrc.apiplatform.dlqconsumer
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestHandler}
import software.amazon.awssdk.utils.Logger

import scala.concurrent.ExecutionContext.Implicits.global

class ApiPublishFailureHandler  extends RequestHandler[Object,String] with Logger{
  private def getSnsService() = {
    new SnsService()
  }

  override def handleRequest(input: Object, context: Context): String = {
    val log:LambdaLogger = context.getLogger
    val snsService = getSnsService()
    log.log(s"Sending message to topic ${snsService.client.}")
    snsService.sendMessage(input.toString)
    "sent"
    }
  }
