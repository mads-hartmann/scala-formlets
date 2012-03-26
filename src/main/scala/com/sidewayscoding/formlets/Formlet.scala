/*
  Formlets - A Nice Form Abstraction

  Inspired by the paper

    The Essence of Form Abstraction by
    Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop

  See README for details

  It differs from the the formlets described in the paper in
  the followings ways

    - We don't care about name sources as Lift has built-in
      functionality for generating unique names for fields; This
      has the advantage of also being more safe against hackers etc.

    - We need to keep track of all of the names used so we can construct
      the enviornment to pass to the function upon form submit.

    - We want to deal with possible failures; either because someone has
      messed with the markup or because some user defined validation
      fails.

  So rather than being a Int => (NodeSeq, Env => A, Int) we now have
  () => (NodeSeq, Env => Either[Error, A], Names). Notice that it still
  has to be a function because we want to generate new unique names every
  time the form is displayed, otherwise it wouldn't be safe against hackers.

*/

package com.sidewayscoding.formlets

import scala.xml.{ NodeSeq }

import net.liftweb.util.Helpers.{ nextFuncName }
import net.liftweb.http.{ S }
import net.liftweb.common.{ Box }

/**
 * TODO: Write documentation
 */
trait Formlet[A] { that =>

  import Formlet.{ Env, Names, Error }

  val value: () => (NodeSeq, Env => Either[Error, A], Names)

  /**
   * TODO: Write documentation
   */
  def <*>[X,Y](x: Formlet[X])(implicit ev: A <:< (X => Y)) = new Formlet[Y] {
    val value = () => {

      val (xml,  func,  names)  = that.value()
      val (xml2, func2, names2) = x.value()

      val f = (env: Env) => for {
        gFunc  <- func(env).right
        gInput <- func2(env).right
      } yield gFunc(gInput)

      (xml ++ xml2, f, names ::: names2)
    }

  }

  /**
   * TODO: Write documentation
   */
  def label(text: String) = new Formlet[A] {
    val value = () => {
      val (xml, func, names) = that.value()
      (<label>{text}</label> ++ xml, func, names)
    }
  }

  /**
   * TODO: Write documentation
   */
  def validate( f: A => Either[Error, A]) = transform(f)

  /**
   * TODO: Write documentation
   */
  def transform[B]( f: A => Either[Error, B]) = new Formlet[B] {
    val value = () => {
      val (html, func, names) = that.value()
      val g = (env: Env) => func(env).right.flatMap( f )
      (html, g, names)
    }
  }

  /**
   * TODO: Write documentation
   */
  def process( f: A => Unit) = Formlet(f) <*> this

  /**
   * TODO: Write documentation
   */
  def form: NodeSeq = {

    val (html, func, names) = this.value()

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

    <form>
    { html ++ <button type="submit" class="btn" name={{name}}>Submit</button> }
    </form>
  }

}

/**
 * TODO: Write documentation
 */
object Formlet {

  type Env = Map[String, String]
  type Names = List[String]
  type Error = String

  def apply[A](a: A): Formlet[A] = new Formlet[A] {
    val value = () => (NodeSeq.Empty, (_: Env) => Right(a), Nil)
  }

  /**
   * Creates an input field of type text
   */
  def input = new Formlet[String] {
    val value = () => {
      val name = nextFuncName

      val func = (env: Env) => {
        env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
      }

      (<input name={{ name }} type="text"/>, func, List(name))
    }
  }

  /**
   * TODO: Write documentation
   */
  def textarea = new Formlet[String] {
    val value = () => {
      val name = nextFuncName

      val func = (env: Env) => {
        env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
      }

      (<textarea name={{ name }}></textarea>, func, List(name))
    }
  }

  /**
   * TODO: Write documentation
   */
  def checkbox: Formlet[Boolean] = new Formlet[Boolean] {
    val value = () => {

      val name = nextFuncName

      val func = (env: Env) => {
        // If a checkbox isn't checked it doesn't show up in S.param
        env.get(name).map( _ => Right(true) ).getOrElse( Right(false) )
      }

      (<input name={{ name }} type="checkbox"/>, func, List(name))
    }
  }

  /**
   * TODO: Write documentation
   */
  def select(options: Map[String, String]) = new Formlet[String] {
    val value = () => {

      val name = nextFuncName

      val func = (env: Env) => {
        env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
      }

      val optionsHtml = options.map { case (key, value) =>
        <option value={{key}}>{value}</option>
      }

      val html = <select name={{name}}> { optionsHtml } </select>

      (html, func, List(name))

    }
  }

  /**
   * TODO: Write documentation
   *
   * Options is a list of tuples where the first component is the value
   * assosiated with the radio button and the second is a label.
   */
  def radio(options: List[(String, String)]) = new Formlet[Option[String]] {
    val value = () => {

      val name = nextFuncName

      val func = (env: Env) =>
        env.get(name).map( x => Right(Some(x)) ).getOrElse( Right(None) )

      val radioHtml = options.foldLeft (NodeSeq.Empty) { case (acc, (key, label)) =>
        acc ++ (<label class="radio">
          <input type="radio" name={{name}} value={{key}} />{label}
        </label>)
      }

      (radioHtml, func, List(name))
    }
  }

}
