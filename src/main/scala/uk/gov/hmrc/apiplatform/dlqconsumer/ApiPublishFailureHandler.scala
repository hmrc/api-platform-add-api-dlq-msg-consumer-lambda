package uk.gov.hmrc.apiplatform.dlqconsumer
import scala.concurrent.ExecutionContext.Implicits.global

class ApiPublishFailureHandler  {
  private def getSnsService() = {
    new SnsService()
  }

  def handleRequest(event: String)= {
    val snsService = getSnsService()
    snsService.sendMessage(event)
  }

}
