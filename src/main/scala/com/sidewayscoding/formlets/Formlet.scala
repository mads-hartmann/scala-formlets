/*
  Formlets - A Nice Form Abstraction

  Inspired by the paper

    The Essence of Form Abstraction by
    Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop

  See README for details

  It differs from the the formlets described in the paper in
  the followings ways

    - We don't have about name sources as Lift has built-in
      functionality for generating unique names for fields; This
      has the advantage of also being more safe against hackers etc.

    - We need to keep track of all of the names used so we can construct
      the enviornment to pass to the function upon form submit.

    - We want to deal with possible failures; either because someone has
      messed with the markup or because some user defined validation
      fails.

  So rather than being a Int => (NodeSeq, Env => A, Int) we now have
  (NodeSeq, Env => Either[Error, A], Names).

*/

package com.sidewayscoding.formlets

import scala.xml.{ NodeSeq }

import net.liftweb.util.Helpers.{ nextFuncName }
import net.liftweb.http.{ S }
import net.liftweb.common.{ Box }

/**
 * TODO: Write documentation
 */
trait Formlet[A] {

  import Formlet.{ Env, Names, Error }

  val value: (NodeSeq, Env => Either[Error, A], Names)

  /**
   * TODO: Write documentation
   */
  def <*>[X,Y](x: Formlet[X])(implicit ev: A <:< (X => Y)) = {

    val (xml,  func,  names)  = this.value
    val (xml2, func2, names2) = x.value

    // Fail fast - btw, where are the flatMap/map methods in Either? :-(
    val f = (env: Env) => func2(env).fold(
      (fail) => Left(fail),
      (success) => func(env).fold(
        (fail) => Left(fail),
        (g) => Right(g(success))
      )
    )

    new Formlet[Y] {
      val value = (xml ++ xml2, f, names ::: names2)
    }
  }

  def withLabel(text: String) = {
    val (xml, func, names) = this.value
    new Formlet[A] {
      val value =
        (<label>{text}</label> ++ xml, func, names)
    }
  }

  /**
   * TODO: Write documentation
   */
  def form: NodeSeq = {

    val (html, func, names) = this.value

    val name = nextFuncName

    // Function that constructs an enviornment based on the information
    // entered by the user and hands the enviornment to the form handler
    // function.
    val collector = () => {

      val empty = Map[String,String]()

      val mapping = names zip ( names map { S.param _ } )

      val env = mapping.foldLeft (empty) {
        case (map: Env, (key: String, box: Box[String])) =>
          box map { value => map + (key -> value) } openOr map
      }

      val result = func(env)

      if (result.isLeft) {
        S.error(result.left.get)
      }

    }

    // Tell lift about the form handler
    S.addFunctionMap(name, collector)

    <form> { html ++ <input type="submit" name={{name}} value="Submit" /> } </form>
  }

}

/**
 * TODO: Write documentation
 */
object Formlet {

  type Env = Map[String, String]
  type Names = List[String]
  type Error = String

  def apply[A](a: A): Formlet[A] = {
    new Formlet[A] {
       val value = (NodeSeq.Empty, (_: Env) => Right(a), Nil)
    }
  }

  /**
   * Creates an input field of type text
   */
  def input = {
    new Formlet[String] {

      val name = nextFuncName

      val func = (env: Env) => {
        env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
      }

      val value = (<input name={{ name }} type="text"/>, func, List(name))
    }
  }

  def textarea = {

    val name = nextFuncName

    val func = (env: Env) => {
      env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
    }

    new Formlet[String] {
      val value =
        (<textarea name={{ name }}></textarea>, func, List(name))
    }
  }

}
