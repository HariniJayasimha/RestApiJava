name := """Go-vs-Play"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.7"

PlayKeys.devSettings := Seq("play.server.http.port" -> "8080")

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "mysql" % "mysql-connector-java" % "5.1.38",
  "com.github.fge" % "json-schema-validator" % "2.2.6"
)
