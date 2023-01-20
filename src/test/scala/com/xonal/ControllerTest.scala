package com.xonal

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._
import com.xonal.Helpers._
import org.http4s.client.Client
//import org.http4s.{Method, Request}
//import org.http4s._
//import org.http4s.implicits._
//import cats.effect.unsafe.IORuntime
//import cats.implicits._
//import cats.syntax.all._
//import cats.effect._
//import cats.effect.IO
//import _root_.io.circe._
//import _root_.io.circe.Json
//import _root_.io.circe.literal._
//import _root_.io.circe.generic.auto._
//import _root_.io.circe.syntax._
//import com.xonal.xonal._

//import Model._
import xonal.{Radio, Shows, User}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import _root_.io.circe.Json
import _root_.io.circe.literal._
import _root_.io.circe.generic.semiauto._
import _root_.io.circe.syntax._
import _root_.io.circe.Encoder
import _root_.io.circe.Decoder._
import _root_.io.circe._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import org.http4s.server.middleware.Logger
import org.http4s.implicits._
import org.http4s.server.middleware._
import org.http4s.server.Router
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class ControllerTest extends AnyFlatSpec with Matchers with AppRoutes{
  val radio1 = Radio(1, "CapitalFM", "capital", "91.2", "Kampala", "capital.ug", 2)
  val radio2 = Radio(2, "CroozeFM", "crooze", "92.3", "Mbarara", "crooze.ug", 4)
  val radios = List(radio1, radio2)

  def getRadiosNew: IO[List[Radio]] = radios.pure[IO]

  val expectedJson = """
  {
    "radioId" : 1,
    "station" : "CapitalFM",
    "stnid" : "capital",
    "frequency" : "91.2",
    "location" : "Kampala",
    "url" : "capital.ug",
    "likes" : 2
  },
  {
    "radioId" : 2,
    "station" : "CroozeFM",
    "stnid" : "crooze",
    "frequency" : "92.3",
    "location" : "Mbarara",
    "url" : "crooze.ug",
    "likes" : 4
  }
  """

  implicit val runtime: IORuntime = IORuntime.global

  override val allRoutes = HttpRoutes.of[IO] {
    case req @ GET -> Root/ "getradios" / "" =>
      getRadiosNew.flatMap(rdios => Ok(rdios.asJson))
    //case req @ GET -> Root/ "shows" =>
      //getShows(1).flatMap(shows => Ok(shows.asJson))
  }.orNotFound
  override val finalHttpApp = Logger.httpApp(true, true)(corMethodSvc(allRoutes))

  val request: Request[IO] = Request(method = Method.GET, uri = uri"/getradios/")
  val client: Client[IO] = Client.fromHttpApp(finalHttpApp)

  val resp:IO[Json] = client.expect[Json](request)
  "get radio" should "return a json object" in {
    resp.unsafeRunSync().noSpaces.filter(_ == '[').filter(_ == ']') should equal(expectedJson.asJson.noSpaces.filter(_ == '[').filter(_ == ']'))
  }
}
