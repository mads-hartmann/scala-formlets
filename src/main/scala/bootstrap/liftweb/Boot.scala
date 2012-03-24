package bootstrap.liftweb

import net.liftweb._
import http.{LiftRules, NotFoundAsTemplate, ParsePath}
import sitemap.{SiteMap, Menu, Loc}
import util.{ NamedPF }
import net.liftweb.http.{ Req, Html5Properties }



class Boot {
  def boot {

    // where to search snippet
    LiftRules.addToPackages("com.sidewayscoding")

    // build sitemap
    val entries = List(
      Menu("Home") / "index",
      Menu("Example") / "example")

    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })

    LiftRules.setSiteMap(SiteMap(entries:_*))

    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // HTML5 Rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

  }
}