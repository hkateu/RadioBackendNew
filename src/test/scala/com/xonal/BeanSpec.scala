package com.xonal

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._
import org.scalatest.Inspectors._
import com.xonal.xonal.User
import com.xonal.xonal.Radio
import com.xonal.xonal.Shows

class BeanSpec extends AnyFlatSpec with Matchers{
  val fixture = new {
    val myUser = User("Kateu", "Herbert")
    val myRadio = Radio(1, "Capital", "cap", "91.3", "kampala", "@capital", 34)
    val myShows = Shows(1, "Breakfast", new java.sql.Timestamp(new java.util.Date().getTime()), "morning show",Some(50))
  }
  "User" should "contain the right types" in {
    fixture.myUser.firstName shouldBe an[String]
    fixture.myUser.lastName shouldBe an[String]
  }
  "Radio" should "contain the right types" in {
      fixture.myRadio match {
        case Radio(id,_,_,_,_,_,url) => forAll(List(id,url)){_ shouldBe an[Int]}
        case _ => s"Strings"
      }
      fixture.myRadio match {
        case Radio(_, u,v,w,x,y,z) => forAll(List(u,v,w,x,y)){_ shouldBe an[String]}
        case _ => s"Ints"
      }
  }
  "Shows" should "contain the right types" in {
    fixture.myShows.showsid shouldBe an[Int]
    fixture.myShows.Shows shouldBe an[String]
    fixture.myShows.showtime shouldBe an[java.sql.Timestamp]
    fixture.myShows.showdesc shouldBe an[String]
    fixture.myShows.likes shouldBe an[Option[Int]]
  }

}
