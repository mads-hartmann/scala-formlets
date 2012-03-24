package com.sidewayscoding.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb._
import util._
import common._
import Helpers._
import com.sidewayscoding.formlets.{ Formlet }

import net.liftweb.http.{ S }

class ExampleFormlet {

  def render = {

    case class Person(firstname: String, lastname: String, age: Int)

    val mkPerson = (Person.apply _).curried

    val validateFirstname = (firstname: String) =>
      if (firstname.size > 0) Right(firstname) else Left("Sorry, the name field is required")

    val personForm =
      Formlet { mkPerson } <*>
      Formlet.input.withLabel("Firstname").validate(validateFirstname) <*>
      Formlet.input.withLabel("Lastname") <*>
      (Formlet { (x: String) => Integer.parseInt(x) } <*> Formlet.input.withLabel("Age"))

    "#myForm" #> personForm.process( (p: Person) => S.notice(p.toString) ).form
  }

}