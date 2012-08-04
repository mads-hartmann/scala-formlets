package com.sidewayscoding.formlets

import org.specs2.mutable._
import org.junit.runner.RunWith
import scala.xml.{ NodeSeq }

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

/**
  * Tests that the formlets produce the right HTML. This is
  * the HTML that is generated regardless of the specific
  * formlet backend implementation.
  */
class FormletsTest extends SpecificationWithJUnit {

  "Markup produced by basic formlets" should {

    "input" in {

      val formlet = TestFormlet.input

      val html = (
        <form>
          <input type="text" name="unique-name"></input>
          <button name="unique-name" type="submit" class="btn">Submit</button>
        </form>
      )

      html must beEqualToIgnoringSpace(formlet.xhtml)
    }

    "textarea" in {

      val formlet = TestFormlet.textarea

      val html = (
        <form>
          <textarea name="unique-name"></textarea>
          <button name="unique-name" type="submit" class="btn">Submit</button>
        </form>
      )

      html must beEqualToIgnoringSpace(formlet.xhtml)
    }

    "checkbox" in {

      val formlet = TestFormlet.checkbox

      val html = (
        <form>
          <input name="unique-name" type="checkbox" />
          <button name="unique-name" type="submit" class="btn">Submit</button>
        </form>
      )

      html must beEqualToIgnoringSpace(formlet.xhtml)
    }

    "select" in {
      val formlet = TestFormlet.select(List(
        ("one","one"),
        ("two","two")
      ))

      val html = (
        <form>
          <select name="unique-name">
            <option value="one">one</option>
            <option value="two">two</option>
          </select>
          <button name="unique-name" type="submit" class="btn">Submit</button>
        </form>
      )

      html must beEqualToIgnoringSpace(formlet.xhtml)
    }

    "radio" in {
      val formlet = TestFormlet.radio(List(
        ("one","one"),
        ("two","two")
      ))

      val html = (
        <form>
          <label class="radio">
            <input name="unique-name" type="radio" value="one"></input>
            one
          </label>
          <label class="radio">
            <input name="unique-name" type="radio" value="two"></input>
            two
          </label>
          <button name="unique-name" type="submit" class="btn">Submit</button>
        </form>
      )

      html must beEqualToIgnoringSpace(formlet.xhtml)
    }
  }

}
