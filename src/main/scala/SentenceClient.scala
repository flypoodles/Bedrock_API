import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, parameter, path}
import myapp.Sentence.grpc.{SentenceReply, SentenceRequest, SentenceServiceClient}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}


object SentenceClient {
  val logger = LoggerFactory.getLogger(this.getClass)
  val myConf = ConfigFactory.load()
  val clientConfig = myConf.getConfig("grpcClient")
  val serverConfig = myConf.getConfig("grpcServer")
  def client():Unit = {

    implicit val sys = ActorSystem(clientConfig.getString("name"))

    implicit val ec = sys.dispatcher

    val route = path(clientConfig.getString("endpoint")) {
      get {
        parameter(clientConfig.getString("parameter").?) {
          sentence => {
            val responseText:String = sentence match {
              case Some(n) => {
                // get the request string and sent it to the grpc backend.
                val clientSettings = GrpcClientSettings.connectToServiceAt(serverConfig.getString("address"), serverConfig.getInt("port")).withTls(false)
                val client:SentenceServiceClient = SentenceServiceClient(clientSettings)
                val reply = client.sendPrompt(SentenceRequest(sentence=n))

                try {
                  logger.info("successfully responded")
                  val result = Await.result(reply, 1.minute)
                  s"<h1>${result.message}</h1>"

                } catch {
                  case error :Exception => {
                    logger.info("unsuccessfully responded")
                    s"<h1>Error : ${error}</h1>"}
                }
              }
              case None => "<h1>Hello, world</h1>"
            }
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, responseText))
          }
        }
      }
    }

    val bindingFuture = Http().newServerAt(clientConfig.getString("address"), clientConfig.getInt("port")).bind(route)

    logger.info("Server now online. Please navigate to {}:{}/{} Press RETURN to stop", clientConfig.getString("address"),clientConfig.getInt("port"),clientConfig.getString("endpoint"))

    StdIn.readLine()

    bindingFuture.flatMap(_.unbind()).onComplete(_=>sys.terminate())
  }
}
