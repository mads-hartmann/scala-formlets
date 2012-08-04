# Formlets - A Nice Form Abstraction

Inspired by the paper

The Essence of Form Abstraction by
Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop

[Formlets site](http://groups.inf.ed.ac.uk/links/formlets/) and
[the paper](http://groups.inf.ed.ac.uk/links/papers/formlets-essence.pdf).

This project contains the very first parts of a Formlet implementation
in Scala for Lift-based web projects - **It's not very usable yet**; I'm
simply exploring the idea at the moment.

## Using Formlets in your project

Currently the artifacts aren't published anywhere at the momemt so if
you want to try it out you have to depend on the source directly from
this git repository. You can do so by putting the following in your
`project/Build.scala` file.

    import sbt._

    object MyProject extends Build {

      lazy val root = Project("project", file(".")) dependsOn(formlet)

      lazy val formlet = uri("git://github.com/mads379/scala-formlets")
    }

## Working on the project

### Lift implementation


    > test:run

Then select `com.sidewayscding.formlets.lift.RunWebApp` and point your
browser to `http://localhost:8080`.
