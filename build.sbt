
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"
val meta = """META.INF(.)*"""
ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs @_*) =>
    xs match {
      case "MANIFEST.MF" :: Nil => MergeStrategy.discard
      case "services" :: _ => MergeStrategy.concat
      case _ => MergeStrategy.discard
    }
  case "reference.conf" => MergeStrategy.concat
  case x if x.endsWith(".proto") => MergeStrategy.rename
  case x if x.contains("hadoop") => MergeStrategy.first
  case x => MergeStrategy.first
}
lazy val root = (project in file("."))
  .settings(
    name := "AI-prompt" ,
    assembly / assemblyJarName := "cs441hw3.jar"

  )

// https://mvnrepository.com/artifact/software.amazon.awssdk/lambda
libraryDependencies += "software.amazon.awssdk" % "lambda" % "2.29.17"

// https://mvnrepository.com/artifact/software.amazon.awssdk/regions
libraryDependencies += "software.amazon.awssdk" % "regions" % "2.29.17"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20240303"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.3"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "3.14.0"
// https://mvnrepository.com/artifact/software.amazon.awssdk/bedrockruntime
libraryDependencies += "software.amazon.awssdk" % "bedrockruntime" % "2.29.17"
libraryDependencies += "org.slf4j"%"slf4j-api"%"2.0.16"
libraryDependencies += "ch.qos.logback"%"logback-classic"% "1.4.6"
libraryDependencies += "com.typesafe" % "config"% "1.4.3"
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
enablePlugins(AkkaGrpcPlugin)