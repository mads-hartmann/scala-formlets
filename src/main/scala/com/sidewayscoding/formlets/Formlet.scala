/*
  Formlets - A Nice Form Abstraction

  Inspired by the paper 

    The Essence of Form Abstraction by 
    Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop
  
  See README for details 
  
*/ 

package com.sidewayscoding.formlets

import scala.xml.{ NodeSeq }
import net.liftweb.util.Helpers.{ nextFuncName }

trait Formlet[A] {

  import Formlet.{ Env }

  val value: (NodeSeq, Map[String, String] => A)

  def <*>[X,Y](x: Formlet[X])(implicit ev: A <:< (X => Y)) = {
    val (xml, func) = this.value
    val (xml2, func2) = x.value
    new Formlet[Y] { 
      val value = 
        (xml ++ xml2, ( (env: Map[String, String]) => func(env)(func2(env) ) ))
    }
  }

  def withLabel(text: String) = {
    val (xml, func) = this.value
    new Formlet[A] {
      val value = 
        (<label>{text}</label> ++ xml, func)
    }
  }

  def run(): (NodeSeq, Map[String, String] => A) = {
    val (xml, g) = this.value
    (xml,g)
  }

}

object Formlet {

  type Env = Map[String, String]

  def apply[A](a: A): Formlet[A] = {
    new Formlet[A] { 
       val value = (NodeSeq.Empty, (_: Map[String, String]) => a)
    }
  }

  def input = {
    val name = nextFuncName
    new Formlet[String] { 
      val value = 
        (<input name={{name}} type="text"/>, (env: Map[String, String]) => env.apply(name))
    }
  }

  def textarea = {
    val name = nextFuncName
    new Formlet[String] {
      val value = 
        (<textarea name={{name}}></textarea>, (env: Env) => env.apply(name))
    }
  }

}
