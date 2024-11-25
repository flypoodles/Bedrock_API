import org.scalatest.flatspec.AnyFlatSpec
import com.typesafe.config.{Config, ConfigFactory}
class Tests  extends AnyFlatSpec  {



  val config =  ConfigFactory.load()

  behavior of "grpc server config"
  val grpcServerConfig = config.getConfig("grpcServer")
  it should "have a name" in {
    assert(grpcServerConfig.hasPath("name"))
  }
  it should "contain a port" in {
    assert(grpcServerConfig.hasPath("port"))
  }
  it should "contain the lambda function arn" in {
    assert(grpcServerConfig.hasPath("lambdaAddress"))
  }

  it should "contain the grpc server address" in {
    assert(grpcServerConfig.hasPath("address"))
  }


  behavior of "grpc client config"
  val grpcClientConfig = config.getConfig("grpcClient")
  it should "have a name" in {
    assert(grpcClientConfig.hasPath("name"))
  }

  it should "contain the grpc server address" in {
    assert(grpcClientConfig.hasPath("address"))
  }

  it should "contain a port" in {
    assert(grpcClientConfig.hasPath("port"))
  }

  it should "specify a endpoint" in {
    assert(grpcClientConfig.hasPath("endpoint"))
  }
  it should "specify a parameter" in {
    assert(grpcClientConfig.hasPath("parameter"))
  }

  behavior of "lambda config"
  val lambdaConfig = config.getConfig("LambdaConfig")

  it should "contain the bedrock arn" in {
    assert(lambdaConfig.hasPath("bedrockArn"))
  }

  it should "contain the ai template" in {
    assert(lambdaConfig.hasPath("nativeRequestTemplate"))
  }

  it should "contain the prompt starter" in {
    assert(lambdaConfig.hasPath("promptStarter"))
  }
  it should "contain the prompt location " in {
    assert(lambdaConfig.hasPath("prompt"))
  }

  it should "contain text location" in {
    assert(lambdaConfig.hasPath("textLocation"))
  }
  it should "contain the key" in {
    assert(lambdaConfig.hasPath("key"))
  }
}
