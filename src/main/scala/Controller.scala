package src.main.scala

//Controller imports
//import cats._
import cats.effect._
import cats.implicits._
import _root_.io.circe._
import _root_.io.circe.literal._
import _root_.io.circe.generic.auto._
import _root_.io.circe.syntax._
import _root_.io.circe.Encoder
import _root_.io.circe.Decoder
import cats.effect.unsafe.implicits.global
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
//import org.http4s.dsl.impl._
//import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server.middleware._
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._


//model imports
import doobie._
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.ExecutionContexts
import cats.effect.unsafe.IORuntime
import org.http4s.server.Router


object Controller extends IOApp {
  case class Hello(name: String)
  //case class User(name: String)
  case class User(firstName: String, lastName: String)
  case class Radio(radioId: Int, station: String, stnid: String, frequency: String, location: String, url: String, likes: Int)
  case class Shows(showsid:Int, Shows: String, showtime: java.sql.Timestamp, showdesc: String, likes: Option[Int])

  //implicit val HelloEncoder: Encoder[Hello] =
  //  Encoder.instance { (hello: Hello) =>
  //    json""" {"Bye": ${hello.name}} """
  //  }

  //Transactor with setting for database connection
  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "com.mysql.cj.jdbc.Driver",
    "jdbc:mysql://localhost/radio",
    "herb",
    "password"
  )

  //getting users in the database
  def getNames(email: String): IO[User] = {
    val query = sql"select firstName, lastName from users where email = $email".query[User]
    val action = query.unique
    action.transact(xa)
  }

  //getting all radio stations
  def getRadios: IO[List[Radio]] = {
    val query = sql"select radioId, station, stnid, frequency, location, url, likes from radio".query[Radio]
    val action = query.to[List]
    action.transact(xa)
  }

  //getting radio station by id
  def getRadio(id: Int): IO[Radio] = {
    val query = sql"select radioId, station, stnid, frequency, location, url, likes from radio where radioId = $id".query[Radio]
    val action = query.unique
    action.transact(xa)
  }

  //getting all shows
  def getShows(id: Int): IO[List[Shows]] = {
    val query = sql"select showsId, shows, showTime, showDesc, likes from shows where radioId = $id".query[Shows]
    val action = query.to[List]
    action.transact(xa)
  }

  //validate login
  def validateLogin(email: String, pass: String): IO[List[User]] ={
    val query = sql"select firstName, lastName from users where email = $email and password = $pass".query[User]
    val action = query.to[List]
    action.transact(xa)
  }

  def checkIfUserExists(email: String): IO[List[User]] ={
    val query = sql"select firstName, lastName from users where email = $email".query[User]
    val action = query.to[List]
    action.transact(xa)
  }

  def regUser(fname:String, lname: String, email: String, pass: String, bday: java.sql.Date, gender: String): IO[Int] = {
    val currTime = new java.sql.Timestamp(System.currentTimeMillis())
    val query = sql"insert into users (password, firstName, lastName, email, birthday, gender, createdOn, lastLogIn) values ($pass, $fname, $lname, $email, $bday, $gender, $currTime, $currTime)".update.run
    query.transact(xa)
  }

  implicit val HelloEncoder: Encoder[Hello] = hello => Json.obj(
    "name" -> hello.name.asJson
  )

  implicit val UserEncoder: Encoder[User] = user => Json.obj(
    "firstName" -> user.firstName.asJson,
    "lastName" -> user.lastName.asJson
  )

   //implicit def TimestampFormat: Encoder[java.sql.Timestamp] with Decoder[java.sql.Timestamp] =
    //new Encoder[java.sql.Timestamp] with Decoder[java.sql.Timestamp]{
      //override def apply(a: java.sql.Timestamp): Json = Encoder.encodeString.apply(a.toString)
      //override def apply(c: HCursor) = Decoder.decodeString.map(s => java.sql.Date.valueOf(s)).apply(c)
    //}


  //implicit def TimestampFormat: Encoder[java.sql.Timestamp]  =
    //new Encoder[java.sql.Timestamp] with Decoder[java.sql.Timestamp]{
      //override def apply(a: java.sql.Timestamp): Json = Encoder.encodeString.apply(a.toString)
    //}

  import _root_.io.circe.Encoder
  import _root_.io.circe.Decoder

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

  implicit val UserDecoder = jsonOf[IO, User]
  implicit val RadioDecoder = jsonOf[IO, Radio]
  implicit val ShowsDecoder = jsonOf[IO, Shows]

  val jsonApp = HttpRoutes.of[IO] {
    case req @ GET -> Root / "hello" / name =>
      Ok(s"Hello $name")
  }.orNotFound

  // import model
  import scala.concurrent.ExecutionContext.Implicits.global
  //val radioRoutes = HttpRoutes.of[IO] {
  //}.orNotFound

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

 // implicit override val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
 // val cookieName = "csrf-token"
 // val key = CSRF.generateSigningKey[IO].unsafeRunSync()
 // val defaultOriginCheck: Request[IO] => Boolean = CSRF.defaultOriginCheck[IO](_, "localhost:3000", Uri.Scheme.http, None)
 // val csrfBuilder = CSRF[IO, IO](key,defaultOriginCheck)

//  val csrf = csrfBuilder
  //          .withCookieName(cookieName)
   //         .withCookieDomain(Some("http://localhost:3000"))
     //       .withCookiePath(Some("/"))
       //     .build
 // val resp = csrf.validate()(allRoutes)


  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"9000")
      .withHttpApp(corsMethodSvc)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

}
