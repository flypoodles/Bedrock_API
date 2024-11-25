import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.typesafe.config.ConfigFactory
import myapp.Sentence.grpc.SentenceServiceHandler
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object SentenceServer {


  val logger = LoggerFactory.getLogger(this.getClass)
  val myConf = ConfigFactory.load()
  val serverConfig = myConf.getConfig("grpcServer")
  def server(): Unit = {

    val conf =
      ConfigFactory.parseString("akka.http.server.enable-http2 = on").withFallback(ConfigFactory.defaultApplication())
    val system = ActorSystem(serverConfig.getString("name"), conf)
    val bindingFuture = new SentenceServer(system).run()

    StdIn.readLine()
    system.terminate()

    // ActorSystem threads will keep the app alive until `system.terminate()` is called
  }

  class SentenceServer(system: ActorSystem) {
    def run(): Future[Http.ServerBinding] = {
      logger.info("server started")
      // Akka boot up code
      implicit val sys: ActorSystem = system
      implicit val ec: ExecutionContext = sys.dispatcher

      // Create service handlers
      val service: HttpRequest => Future[HttpResponse] =
        SentenceServiceHandler(new SentenceImp())

      // Bind service handler servers to localhost:8080/8081
      val binding = Http().newServerAt(serverConfig.getString("address"), serverConfig.getInt("port")).bind(service)

      // report successful binding
      binding.foreach { binding => logger.info(s"gRPC server bound to: ${binding.localAddress}") }

      binding
   //   binding.flatMap(_.unbind()).onComplete(_=>system.terminate())

    }
  }
}