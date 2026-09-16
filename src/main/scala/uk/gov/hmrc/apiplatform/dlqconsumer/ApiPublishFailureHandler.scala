package uk.gov.hmrc.apiplatform.dlqconsumer
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestHandler}

import scala.concurrent.ExecutionContext.Implicits.global

class ApiPublishFailureHandler  extends RequestHandler[Object,String] {
  private def getSnsService() = {
    new SnsService()
  }

  override def handleRequest(input: Object, context: Context): String = {
    val snsService = getSnsService()
    snsService.sendMessage(input.toString, context)
    "sent"
    }
  }
