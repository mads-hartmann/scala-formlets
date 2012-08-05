package com.sidewayscoding.formlets.lift.snippet

import com.sidewayscoding.formlets.{ Formlet, LiftFormlet }
import net.liftweb.http.S
import net.liftweb.http.SessionVar
import net.liftweb.util._
import net.liftweb.util.Helpers._

object HelloWorld {

  case class Person(name: String, lastname: String)

  object Result extends SessionVar[Option[Person]](None)

  lazy val form: Formlet[Person] =
    LiftFormlet((Person.apply _).curried) <*> LiftFormlet.input <*> LiftFormlet.input

}

class HelloWorld {

  println("Invoking snippet: " + S.session)

  import HelloWorld._

  form.handler { e => {
    println("Executing form handler " + e)
    e match {
      case Left(err) => println(err); Result(None)
      case Right(p) => println("handler"); Result(Some(p))
    }
  }}

  // replace the contents of the element with id "time" with the date
  def howdy =
    "#form" #> form.xhtml &
      "#session" #> Result.is.toString

}
