package com.xonal

package object xonal{
  case class User(firstName: String, lastName: String)
  case class Radio(radioId: Int, station: String, stnid: String, frequency: String, location: String, url: String, likes: Int)
  case class Shows(showsid:Int, Shows: String, showtime: java.sql.Timestamp, showdesc: String, likes: Option[Int])
}
