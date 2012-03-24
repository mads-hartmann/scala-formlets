name := "formlets"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.4-M5" % "compile->default"

libraryDependencies += "junit" % "junit" % "4.5" % "test->default"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided->default"

libraryDependencies += "com.h2database" % "h2" % "1.2.138"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default"



resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "test,container"

libraryDependencies += "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"