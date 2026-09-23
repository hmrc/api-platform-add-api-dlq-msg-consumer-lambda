package uk.gov.hmrc.apiplatform.dlqconsumer

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

object SnsClientProvider {
    lazy val dlqSnsClient:SnsClient = SnsClient.builder()
      .region(Region.EU_WEST_2)
      .build()
  }
