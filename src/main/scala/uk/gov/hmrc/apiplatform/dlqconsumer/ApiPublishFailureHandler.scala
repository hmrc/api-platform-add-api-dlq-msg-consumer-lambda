package uk.gov.hmrc.apiplatform.dlqconsumer
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger, RequestHandler}

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext.Implicits.global

class ApiPublishFailureHandler  extends RequestHandler[Object,String] {
  private def getSnsService() = {
    new SnsService()
  }

  override def handleRequest(input: Object, context: Context): String = {
    Console.println(s"Entering handleRequest with message of type ${input.getClass.getName}")
    val snsService = getSnsService()
    Console.println(s"SNSService is $snsService")
    snsService.sendMessage(input.toString, context)
    "sent"
    }
  }
