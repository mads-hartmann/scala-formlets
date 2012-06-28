package com.sidewayscoding.formlets

import org.specs2.mutable._
import org.junit.runner.RunWith

object TestNameProvider extends NameProvider {
  def uniqueName(): String = "unique-name"
}

object TestHandlerRegister extends HandlerRegister {
  def addHandler(name: String, f: () => Unit): Unit = {
    Unit
  }
}

object TestEnvironmentProducer extends EnvironmentProducer {
  import Types.{ Names, Env }
  def createEnviornment(names: Names): Env = {
    Map[String,String]()
  }
}

object TestFormletConfig extends FormletConfig {
  val nameProvider: NameProvider = TestNameProvider
  val handlerRegister: HandlerRegister = TestHandlerRegister
  val environmentProducer: EnvironmentProducer = TestEnvironmentProducer
}

trait TestFormlet[A] extends Formlet[A] {
  val config: FormletConfig = TestFormletConfig
}

object TestFormlet extends BaseFormlet {
  val config: FormletConfig = TestFormletConfig
}

class FormletsTest extends SpecificationWithJUnit {

  "Markup produced by basic formlets" should {
    
    "input should produce correct markup" in {
      
      val formlet = TestFormlet.input
      
      val html = (
        <form>
          <input type="text" name="unique-name"></input><button name="unique-name" type="submit" class="btn">Submit</button>
        </form>
      )
      
      html must beEqualToIgnoringSpace(formlet.xhtml)
    }
  }

}