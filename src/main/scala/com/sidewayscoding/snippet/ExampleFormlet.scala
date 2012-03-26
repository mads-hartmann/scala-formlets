package com.sidewayscoding.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb._
import util._
import common._
import Helpers._
import com.sidewayscoding.formlets.{ Formlet }

import net.liftweb.http.{ S }

case class Person(firstname: String, lastname: String, age: Int, agreed: Boolean)

object MyForms {

  val mkPerson = (Person.apply _).curried

  val validateFirstname = (firstname: String) =>
    if (firstname.size > 0) Right(firstname) else Left("Sorry, the Firstname field is required")

  val personForm =
    Formlet { mkPerson } <*>
    Formlet.input.label("Firstname").validate(validateFirstname) <*>
    Formlet.input.label("Lastname") <*>
    Formlet.input.label("Age").transform( toInt _ ) <*>
    Formlet.checkbox.label("Agree to some terms?")

  private def toInt(x: String): Either[String, Int] = {
    try {
      Right(Integer.parseInt(x))
    } catch {
      case e: Exception => Left("Please enter a valid integer")
    }
  }

}

class ExampleFormlet {

  def render = {

    import MyForms._

    "#myForm" #> personForm.process( (p: Person) => S.notice(p.toString) ).form
  }

}