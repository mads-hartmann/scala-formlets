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

  val (_, _, names) = form.value
  val values  = List("Mads","Hartmann")
  val tuples  = names zip values
  val jfields = tuples map { case (k,v) => JField(k,v) }
  val jobj    = JObject(JField(form.formName, "") :: jfields)
  val testUrl = "http://localhost:8080"

  val testSession = MockWeb.testS(testUrl) {
    S.session
  }

  "When submitting the form it invokes the function with the input" withReqFor(testUrl) withPost(jobj) in {
    req => {
      Result.is must_== Person("Mads","Hartmann")
    }
  }


}
