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
      the environment to pass to the function upon form submit.

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

object Types {
  type Env = Map[String, String]
  type Names = List[String]
  type Error = String
}

/**
 * Something that can provide unique names for form elements.
 */
trait NameProvider {
  def uniqueName(): String
}

trait HandlerRegister {
  import Types.{ Env, Names, Error }
  def addHandler(name: String, f: () => Unit): Unit
}

trait FormletConfig { 
  val nameProvider: NameProvider
  val handlerRegister: HandlerRegister
  val environmentProducer: EnvironmentProducer
}

trait EnvironmentProducer {
  import Types.{ Env, Names}
  
  /**
   * Given the names used for the input elements construct the
   * environment. This is of course only possible when dealing
   * with the POST request produced by executing the form produced
   * by the formlet.  
   */
  def createEnviornment(names: Names): Env 
}

/**
 * @author mads379
 */
trait Formlet[A] { that =>

  import Types.{ Env, Names, Error }
  
  val config: FormletConfig
  val value: () => (NodeSeq, Env => Either[Error, A], Names)
  
  private lazy val formName = this.config.nameProvider.uniqueName()
  
  private trait InnerFormlet[A] extends Formlet[A] { 
    val config: FormletConfig = config
  }

  /**
   * TODO: Write documentation
   */
  def <*>[X,Y](x: Formlet[X])(implicit ev: A <:< (X => Y)): Formlet[Y] = new InnerFormlet[Y] {
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
  def label(text: String): Formlet[A] = new InnerFormlet[A] {
    val value = () => {
      val (xml, func, names) = that.value()
      (<label>{text}</label> ++ xml, func, names)
    }
  }

  /**
   * TODO: Write documentation
   */
  def validate( f: A => Either[Error, A]): Formlet[A] = new InnerFormlet[A] {
    val value = () => {
      val (html, func, names) = that.value()
      val g = (env: Env) => func(env).right.flatMap( f )
      (html, g, names)
    }
  }
  
  /**
   * Map over the value extracted by the formlet.
   */
  def map[B]( f: A => B): Formlet[B] = new InnerFormlet[B] {
    val value = () => {
      val (html, func, names) = that.value()
      val g = (env: Env) => func(env).right.map(f)
      (html, g, names)
    }
  }

  /**
   * TODO: Write documentation
   */
  def process( f: A => Unit): Unit = (new InnerFormlet[A => Unit] {
    val value = () => (NodeSeq.Empty, (_: Env) => Right(f), Nil)
  }) <*> this

  /**
   * TODO: Write documentation
   */
  def xhtml: NodeSeq = {

    val (html, func, names) = this.value()

    <form>
      { html ++ <button type="submit" class="btn" name={{this.formName}}>Submit</button> }
    </form>
  }

  /**
   * Register a handler that will get executed when the form is 
   * submitted.
   */
  def handler(f: Either[Error, A] => Unit) = {
    
    val (html, func, names) = this.value()
    
    config.handlerRegister.addHandler(this.formName, () => {
      val env = config.environmentProducer.createEnviornment(names)
      f(func(env))
    })
  }

}

/**
 * Mixin for an object that can create Formlets. 
 */
trait BaseFormlet { that => 
  
  import Types._
  
  val config: FormletConfig
  
  private trait InnerFormlet[A] extends Formlet[A] {
    val config: FormletConfig = that.config 
  }

  def apply[A](a: A): Formlet[A] = new InnerFormlet[A] {
    val value = () => (NodeSeq.Empty, (_: Env) => Right(a), Nil)
  }

  /**
   * Creates an input field of type text
   */
  def input: Formlet[String] = new InnerFormlet[String] {
    val value = () => {
      val name = this.config.nameProvider.uniqueName()
      val func = (env: Env) => {
        env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
      }
      (<input name={{ name }} type="text"/>, func, List(name))
    }
  }

  /**
   * TODO: Write documentation
   */
  def textarea: Formlet[String] = new InnerFormlet[String] {
    val value = () => {
      val name = config.nameProvider.uniqueName()

      val func = (env: Env) => {
        env.get(name).map( Right(_) ).getOrElse( Left("Missing field") )
      }

      (<textarea name={{ name }}></textarea>, func, List(name))
    }
  }

  /**
   * TODO: Write documentation
   */
  def checkbox: Formlet[Boolean] = new InnerFormlet[Boolean] {
    val value = () => {

      val name = config.nameProvider.uniqueName()

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
  def select(options: Map[String, String]): Formlet[String] = new InnerFormlet[String] {
    val value = () => {

      val name = config.nameProvider.uniqueName()

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
   * Options is a list of tuples where the first component is the value
   * associated with the radio button and the second is a label.
   */
  def radio(options: List[(String, String)]): Formlet[Option[String]] = new InnerFormlet[Option[String]] {
    val value = () => {

      val name = config.nameProvider.uniqueName()

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
