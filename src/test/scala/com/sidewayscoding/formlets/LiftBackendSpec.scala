package com.sidewayscoding.formlets

import bootstrap.liftweb.Boot
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.mocks.{ MockHttpServletRequest }
import net.liftweb.mockweb.MockWeb

class LiftBackendSpec extends WebSpec2(new Boot().boot _) {

  // TODO: Figure out how to mock a POST request.

  // TODO: Have to get all the names of the fields required
  //       and provide some values for each expected field.

  val testUrl = "http://localhost:8080"

  val testSession = MockWeb.testS(testUrl) {
    S.session
  }

  "When submitting the form it invokes the function with the input" withSFor(testUrl) in {
    S.param("foo") must_== Full("bar")
  }


}
