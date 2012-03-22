/*
  Formlets - A Nice Form Abstraction

  Inspired by the paper 

    The Essence of Form Abstraction by 
    Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop
  
  See README for details 
  
*/ 

import scala.xml.{ NodeSeq }

case class Formlet[A](val f: (Int) => (NodeSeq, Map[String, String] => A, Int)) {

  def <*>[X,Y](x: Formlet[X])(implicit ev: A <:< (X => Y)) = {
    Formlet { (i: Int) => 
      val (xml, func, i2) = this.f(i)
      val (xml2, func2, i3) = x.f(i2)
      (xml ++ xml2, ( (env: Map[String, String]) => func(env)(func2(env) ) ), i3)
    }
  }

  def run(i: Int): (NodeSeq, Map[String, String] => A) = {
    val (xml, g, _) = this.f(i) 
    (xml,g)
  }

}

object Formlet {

  def apply[A](a: A): Formlet[A] = {
    Formlet { (x: Int) =>
       (NodeSeq.Empty, (_: Map[String, String]) => a, x)
    }
  }

  def input = Formlet { (i: Int) => {
      val (w, i2) = nextName(i)
      (<input name={{w}} type="text"/>, (env: Map[String, String]) => env.apply(w) ,i2)
    }
  }

  private def nextName(i: Int): (String, Int) = ("input_" + i, i + 1)

}
