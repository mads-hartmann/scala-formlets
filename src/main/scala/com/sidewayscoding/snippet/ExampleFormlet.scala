package com.sidewayscoding.snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb._
import util._
import common._
import Helpers._
import com.sidewayscoding.formlets.{ Formlet }

class ExampleFormlet {

  def render = {

    case class Person(firstname: String, lastname: String, age: Int)

    val mkPerson = (Person.apply _).curried

    val personForm =
      Formlet { (p: Person) => println("created person " + p) } <*>
      (Formlet { mkPerson } <*>
      Formlet.input.withLabel("Firstname") <*>
      Formlet.input.withLabel("Lastname") <*>
      (Formlet { (x: String) => Integer.parseInt(x) } <*> Formlet.input.withLabel("Age")))

    "#myForm" #> personForm.form
  }

}