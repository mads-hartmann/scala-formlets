name := "formlets"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases",
                  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/")

libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.5-SNAPSHOT" % "compile->default"

libraryDependencies += "net.liftweb" %% "lift-testkit" % "2.5-SNAPSHOT" % "test"

libraryDependencies += "org.specs2" %% "specs2" % "1.11" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.26" % "test"
