package com.sidewayscoding.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb._
import util._
import common._
import Helpers._
import com.sidewayscoding.formlets.{ Formlet }

import net.liftweb.http.{ S }

case class Person(
  firstname: String,
  lastname: String,
  town: String,
  age: Int,
  gender: Option[String],
  agreed: Boolean
)

object MyForms {

  val towns = List("Albertslund", "Copenhagen")
  val townsMap = Map(towns.zip(towns) :_*)

  val genders = List("Male","Female")
  val gendersMap = genders.zip(genders)

  val mkPerson = (Person.apply _).curried

  val validateFirstname = (firstname: String) =>
    if (firstname.size > 0) Right(firstname) else Left("Sorry, the Firstname field is required")

  val personForm =
    Formlet { mkPerson } <*>
    Formlet.input.label("Firstname").validate(validateFirstname) <*>
    Formlet.input.label("Lastname") <*>
    Formlet.select( townsMap ).label("Town") <*>
    Formlet.input.label("Age").transform( toInt _ ) <*>
    Formlet.radio( gendersMap ).label("Gender") <*>
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