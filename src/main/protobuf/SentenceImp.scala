import com.typesafe.config.ConfigFactory
import myapp.Sentence.grpc._
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.lambda.LambdaClient
import software.amazon.awssdk.services.lambda.model.{InvokeRequest, InvokeResponse}

import scala.concurrent.Future

class SentenceImp extends SentenceService{
  val myConf = ConfigFactory.load()
  val serverConfig = myConf.getConfig("grpcServer")
  val lambdaConfig = myConf.getConfig("LambdaConfig")
  override def sendPrompt(in: SentenceRequest): Future[SentenceReply] = {

    println(s"sendPrompt : |${in.sentence}|")

    if(in.sentence.isBlank){
      Future.successful(SentenceReply(s"Sentence must not be empty"))
    } else {
      val lambdaFunctionName =serverConfig.getString("lambdaAddress")
      val payload = s""" {"${lambdaConfig.getString("key")}": "${in.sentence}"}"""
      println(payload)
      val lambdaClient = LambdaClient.builder()
        .region(Region.US_EAST_1) // Specify your region
        .build()

      val request = InvokeRequest.builder()
        .functionName(lambdaFunctionName)

        .payload(SdkBytes.fromUtf8String(payload))
        .build()

      // Invoke the Lambda function
      val response: InvokeResponse = lambdaClient.invoke(request)

      // Process the response
      val responsePayload = response.payload().asUtf8String()
      println(s"Lambda response: $responsePayload")
      Future.successful(SentenceReply(s"${responsePayload}"))
  //    Future.successful(SentenceReply(s"wassup"))
    }

  }
}
