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

  So rather than being a Int => (NodeSeq, Env => A, Int) we now have
  (NodeSeq, Env => A, Names).

*/

package com.sidewayscoding.formlets

import scala.xml.{ NodeSeq }

import net.liftweb.util.Helpers.{ nextFuncName }
import net.liftweb.http.{ S }
import net.liftweb.common.{ Box }

trait Formlet[A] {

  import Formlet.{ Env, Names }

  val value: (NodeSeq, Env => A, Names)

  def <*>[X,Y](x: Formlet[X])(implicit ev: A <:< (X => Y)) = {
    val (xml, func, names) = this.value
    val (xml2, func2, names2) = x.value
    new Formlet[Y] {
      val value =
        (xml ++ xml2, ( (env: Env) => func(env)(func2(env) ) ), names ::: names2)
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
   *
   */
  def form: NodeSeq = {

    val (html, func, names) = this.value

    val name = nextFuncName

    // Function that constructs an enviornment based on the information
    // entered by the user and hands the enviornment to the form handler
    // function.
    val collector = () => {

      val empty = Map[String,String]()

      val env = (names zip (names map { S.param _ })).foldLeft (empty) {
        case (map: Env, (key: String, box: Box[String])) =>
          box map { value => map + (key -> value) } openOr map
      }

      func(env)

    }

    // Tell lift about the form handler
    S.addFunctionMap(name, collector)

    <form>
      {
        html ++ <input type="submit" name={{name}} value="Submit" />
      }
    </form>
  }

}

object Formlet {

  type Env = Map[String, String]
  type Names = List[String]

  def apply[A](a: A): Formlet[A] = {
    new Formlet[A] {
       val value = (NodeSeq.Empty, (_: Env) => a, Nil)
    }
  }

  /**
   * Creates an input field of type text
   */
  def input = {
    new Formlet[String] {
      val name = nextFuncName
      val value =
        (<input name={{ name }} type="text"/>, (env: Env) => env.apply(name), List(name))
    }
  }

  def textarea = {
    val name = nextFuncName
    new Formlet[String] {
      val value =
        (<textarea name={{ name }}></textarea>, (env: Env) => env.apply(name), List(name))
    }
  }

}
