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

    val personForm = 
      Formlet { (x: String) => ((y: String) => ((a: Int) => Person(x,y,a))) } <*>
      Formlet.input.withLabel("Firstname") <*>
      Formlet.input.withLabel("Lastname") <*>
      (Formlet { (x: String) => Integer.parseInt(x) } <*> Formlet.input.withLabel("Age"))

    val (html, func) = personForm.run()

    "#myForm" #> html
  }
  
}