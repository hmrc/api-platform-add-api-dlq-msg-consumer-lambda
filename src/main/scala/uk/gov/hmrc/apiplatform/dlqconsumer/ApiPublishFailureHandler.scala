package uk.gov.hmrc.apiplatform.dlqconsumer
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import scala.concurrent.ExecutionContext.Implicits.global

class ApiPublishFailureHandler  extends RequestHandler[String,String]{
  private def getSnsService() = {
    new SnsService()
  }

  override def handleRequest(input: String, context: Context): String = {
    val snsService = getSnsService()
    snsService.sendMessage(input)
    "sent"
    }
  }
