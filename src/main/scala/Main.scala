object Main {
  def main(args: Array[String]): Unit = {

    if (args.length < 1){
      println("Must run a command")
      return
    }

    args(0) match {
      case "server" => {
        SentenceServer.server()
      }
      case "client" => {
        SentenceClient.client()
      }
      case _ => {
        println("INVALID COMMAND")
      }
    }
  }
}