# Formlets - A Nice Form Abstraction

Inspired by the paper 

The Essence of Form Abstraction by     
Ezra Cooper, Sam Lindley, Philip Wadler, and Jeremy Yallop

This file contains the very first parts of a Formlet implementation
in Scala - I'm simply exploring the idea at the moment.

A `Formlet[A]` represents a form that, when processed, yields a value
of type A. Sort of. A Formlet is an applicative functor(*) so you
can  combine Formlets using `<*>`. Once you combined smaller forms to
produce the form you want you can then invoke the 'run' method with
an initial 'name source' and it will return a tuple of the HTML
(NodeSeq) that represents the form and a function (`Map[String,
String] => A`) - It is then up to you to use the markup to build the
map that you pass to  the function - I will of course provide such a
hook for Lift.

See `Application.scala` for an example

(*) It's not a Monad because that would require that you could use the 
 result of a Formlet to build another Formlet - This of course isn't 
 possible because we don't have the values entered by the user when we're
 constructing the markup.