package com.sidewayscoding.formlets

import bootstrap.liftweb.Boot
import com.sidewayscoding.formlets.lift.snippet.HelloWorld._
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.mocks.MockHttpServletRequest
import net.liftweb.mockweb.MockWeb
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._

class LiftBackendSpec extends WebSpec2(new Boot().boot _) {

  "LiftBackendSpec" should {

    sequential

    val (_, _, names) = form.value
    val formName      = form.formName
    val values        = List("Mads", "Hartmann")
    val tuples        = (formName, "a") :: (names zip values)
    val testUrl       = "/index"
    val purl          = tuples.reverse.map { case (k, v) => "%s=%s".format(k, v) }.mkString("&")
    val fullurl       = "/?" + purl

    val preq = new MockHttpServletRequest(fullurl, "")
    preq.parameters = tuples
    preq.method = "POST"
    preq.contentType = "application/x-www-form-urlencoded"

    val testSession = MockWeb.testS(testUrl) { S.session }

    ("Rendering a formlet should add the handler to the functionMap of the session"
    withTemplateFor (testUrl, testSession)) in {
      req => {
        S.functionMap.contains(formName) must_== (true)
      }
    }

    // This is perhabs more of a test of my WebSpec but regardless it's still
    // important. Especially because it currently fails!
    ("When reundering an unrelevant page the functionMap should be unchanged"
    withTemplateFor ("/other", testSession)) in { req => {
      S.functionMap.contains(formName) must_== (true)
    }}

    ("When submitting a formlet it should invoke the handler"
     withSFor(preq, testSession)) in {
       Result.is must_== Person("Mads", "Hartmann")
     }

  }

}
