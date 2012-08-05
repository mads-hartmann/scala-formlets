package com.sidewayscoding.formlets

import net.liftweb.common.{ Box }
import net.liftweb.util.Helpers.{ nextFuncName }
import net.liftweb.http.{ S }

object LiftNameProvider extends NameProvider {
  import net.liftweb.util.Helpers.{ nextFuncName }
  def uniqueName(): String = nextFuncName
}

object LiftHandlerRegister extends HandlerRegister {
  import net.liftweb.http.{ S }
  def addHandler(name: String, f: () => Unit): Unit = {
    println("adding handler: " + S.session)
    S.addFunctionMap(name, f)
  }
}

object LiftEnvironmentProducer extends EnvironmentProducer {
  import Types.{ Names, Env }
  def createEnviornment(names: Names): Env = {

    val empty = Map[String, String]()

    val mapping: Seq[(String, Box[String])] =
      names zip (names map { S.param _ })

    println(mapping)

    mapping.foldLeft(empty) {
      case (e: Env, (key: String, box: Box[String])) =>
        box map { value => e + (key -> value) } openOr e
    }
  }
}

object LiftFormletConfig extends FormletConfig {
  val nameProvider: NameProvider = LiftNameProvider
  val handlerRegister: HandlerRegister = LiftHandlerRegister
  val environmentProducer: EnvironmentProducer = LiftEnvironmentProducer
}

trait LiftFormlet[A] extends Formlet[A] {
  val config: FormletConfig = LiftFormletConfig
}

object LiftFormlet extends BaseFormlet {
  val config: FormletConfig = LiftFormletConfig
}
