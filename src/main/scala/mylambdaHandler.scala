
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import org.json.{JSONObject, JSONPointer}
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions._
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest

import scala.jdk.CollectionConverters._

class myLambdaHandler extends RequestHandler[java.util.Map[String, String], String]{
  val logger = LoggerFactory.getLogger(this.getClass)
  val myConf = ConfigFactory.load()
  val generateConfig = myConf.getConfig("LambdaConfig")
  override def handleRequest(input: java.util.Map[String, String], context: Context): String = {

    val sentence = input.asScala.getOrElse(generateConfig.getString("key"), "Not a sentence")
    logger.info("handling request")
    logger.info("info {}",sentence);

    val Model_ID :String = generateConfig.getString("bedrockArn")
    val nativeRequestTemplate =  generateConfig.getString("nativeRequestTemplate")
    val prompt :String = s"""${generateConfig.getString("promptStarter")} ${sentence}"""

    val nativeRequest = nativeRequestTemplate.replace(generateConfig.getString("prompt"), prompt);
    val bedCLient : BedrockRuntimeClient = BedrockRuntimeClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).region(Region.US_EAST_1).build()

    try {

      // trying to invoke the bedrock
      val invokeRequest : InvokeModelRequest = InvokeModelRequest.builder().body(SdkBytes.fromUtf8String(nativeRequest)).modelId(Model_ID).build()
      val response =  bedCLient.invokeModel(invokeRequest)
      val responseBody = new JSONObject(response.body().asUtf8String());
      val text = new JSONPointer(generateConfig.getString("textLocation")).queryFrom(responseBody).toString;

      logger.info("generated text {}",text);
      s"Generated Sentence : ${text}"
    } catch {
      case error: Exception => s"cannot not invoke the bedrock model ${Model_ID} Reason ${error.getMessage}"
    }
  }

}