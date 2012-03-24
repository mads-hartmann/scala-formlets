name := "formlets"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.4-M5" % "compile->default"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided->default"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default"

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "test,container"

libraryDependencies += "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"

seq(webSettings :_*)