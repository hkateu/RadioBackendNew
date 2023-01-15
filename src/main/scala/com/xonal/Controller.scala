package com.xonal

import Model._
import xonal.{Radio, Shows, User}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.unsafe.IORuntime
import _root_.io.circe._
import _root_.io.circe.literal._
import _root_.io.circe.generic.auto._
import _root_.io.circe.syntax._
import _root_.io.circe.Encoder
import _root_.io.circe.Decoder
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.server.middleware.Logger
import org.http4s.implicits._
import org.http4s.server.middleware._
import org.http4s.server.Router
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._



object Controller extends IOApp {
 implicit val TimestampFormat : Encoder[java.sql.Timestamp] with Decoder[java.sql.Timestamp] =
    new Encoder[java.sql.Timestamp] with Decoder[java.sql.Timestamp] {
    override def apply(a: java.sql.Timestamp): Json = Encoder.encodeString.apply(a.toString)

    override def apply(c: HCursor): Decoder.Result[java.sql.Timestamp] = Decoder.decodeString.map(s => java.sql.Timestamp.valueOf(s)).apply(c)
  }

  implicit val RadioEncoder: Encoder[Radio] = radio => Json.obj(
    "radioId" -> radio.radioId.asJson,
    "station" -> radio.station.asJson,
    "stnid" -> radio.stnid.asJson,
    "frequency" -> radio.frequency.asJson,
    "location" -> radio.location.asJson,
    "url" -> radio.url.asJson,
    "likes" -> radio.likes.asJson
  )

  implicit val ShowsEncoder: Encoder[Shows] = shows => Json.obj(
    "showsid" -> shows.showsid.asJson,
    "shows" -> shows.Shows.asJson,
    "showtime" -> shows.showtime.asJson,
    "showdesc" -> shows.showdesc.asJson,
    "likes" -> shows.likes.asJson
  )

  implicit val UserEncoder: Encoder[User] = user => Json.obj(
    "firstName" -> user.firstName.asJson,
    "lastName" -> user.lastName.asJson
  )

  implicit val UserDecoder = jsonOf[IO, User]
  implicit val RadioDecoder = jsonOf[IO, Radio]
  implicit val ShowsDecoder = jsonOf[IO, Shows]

  val jsonApp = HttpRoutes.of[IO] {
    case req @ GET -> Root / "hello" / name =>
      Ok(s"Hello $name")
  }.orNotFound


import scala.concurrent.ExecutionContext.Implicits.global
  val allRoutes = HttpRoutes.of[IO] {
    case req @ GET -> Root/ "getradios" / "" =>
      getRadios.flatMap(rdios => Ok(rdios.asJson))
    case req @ GET -> Root/ "shows" =>
      getShows(1).flatMap(shows => Ok(shows.asJson))
  }.orNotFound

  import scala.concurrent.duration._

  val methodConfig = CORSConfig(
    anyOrigin = true,
    anyMethod = false,
    allowedMethods = Some(Set("GET", "POST")),
    allowCredentials = true,
    maxAge = 1.day.toSeconds
  )
   val corsMethodSvc = CORS(allRoutes, methodConfig)

  val finalHttpApp = Logger.httpApp(true, true)(corsMethodSvc)

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"9000")
      .withHttpApp(finalHttpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

}
