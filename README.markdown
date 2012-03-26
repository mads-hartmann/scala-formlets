# Formlets - A Nice Form Abstraction

Inspired by the paper

The Essence of Form Abstraction by
Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop

[Formlets site](http://groups.inf.ed.ac.uk/links/formlets/) and
[the paper](http://groups.inf.ed.ac.uk/links/papers/formlets-essence.pdf).

This project contains the very first parts of a Formlet implementation
in Scala for Lift-based web projects - **It's not very usable yet**; I'm
simply exploring the idea at the moment

If you want to try it out then create put the following in your
`project/Build.scala` file.

    import sbt._

    object Visoble extends Build {

      lazy val root = Project("project", file(".")) dependsOn(formlet)

      lazy val formlet = uri("git://github.com/mads379/scala-formlets#b8f704e758")
    }

I have an example Lift project online [here](http://lift-formlets.mads379.cloudbees.net/)
and the source is [here](https://github.com/mads379/scala-formlets-example)