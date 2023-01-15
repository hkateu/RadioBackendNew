package com.xonal
import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.ExecutionContexts
import xonal.{Radio, Shows, User}

object Model {
  //Transactor with setting for database connection
  //using ip 127.0.0.1
  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "com.mysql.cj.jdbc.Driver",
    //local mysql server
    "jdbc:mysql://localhost/radio",
    //container mysql server
    //"jdbc:mysql://127.0.0.1:3306/radio?enabledTLSProtocols=TLSv1.2", //this works
    //"jdbc:mysql://host.docker.internal:3306/radio?enabledTLSProtocols=TLSv1.2",
    //"jdbc:mysql://xonaldb:3306/radio?autoReconnect=true&useSSL=false",
    //"jdbc:mysql://localhost:6603/radio?user=herb&password=password1",
    //local username
    "herb",
    //container username
    //"root",
    "password"
  )

//getting users in the database
  def getNames(email: String): IO[User] = {
    val query = sql"select firstName, lastName from users where email = $email".query[User]
    val action = query.unique
    action.transact(xa)
  }

  //getting all radio stations
  //IO[List[Radio2]]
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

}
