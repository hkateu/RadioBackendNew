name := "radio-backend"

version := "0.1"

scalaVersion := "2.13.7"

fork := true

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"
val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.0-M5"

libraryDependencies ++= Seq(
    "org.tpolecat" %% "doobie-core"           % DoobieVersion,
    "org.tpolecat" %% "doobie-postgres"       % DoobieVersion,
    "org.tpolecat" %% "doobie-hikari"         % DoobieVersion,
    "io.estatico"  %% "newtype"               % NewTypeVersion,
    "mysql"         % "mysql-connector-java"  % "8.0.31",
    "org.http4s"   %% "http4s-circe"          % Http4sVersion,
    "org.http4s"   %% "http4s-dsl"            % Http4sVersion,
    "io.circe"     %% "circe-core"         % CirceVersion,
    "io.circe"     %% "circe-generic"         % CirceVersion,
    "io.circe"     %% "circe-literal"         % CirceVersion,
    "io.circe"     %% "circe-parser"         % CirceVersion,
    "org.http4s"   %% "http4s-ember-server"   % Http4sVersion,
    "org.http4s"   %% "http4s-ember-client"   % Http4sVersion,
    "ch.qos.logback" %  "logback-classic"     % "1.2.10",
    "org.scalatest" %% "scalatest" % "3.2.14" % "test"
)


lazy val app = (project in file("."))
  .settings(
    assembly / mainClass := Some("com.xonal.Controller")
  )
