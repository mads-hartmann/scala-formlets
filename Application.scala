object Application {
  def main(args: Array[String]): Unit = {
    
    case class Person(firstname: String, lastname: String, age: Int)
    case class Relationship(p1: Person, p2: Person)

    // A formlet for a person
    val personForm = 
      Formlet { (x: String) => ((y: String) => ((a: Int) => Person(x,y,a))) } <*>
      Formlet.input <*>
      Formlet.input <*>
      (Formlet { (x: String) => Integer.parseInt(x) } <*> Formlet.input)

    // A formlet for a relationship. The nice thing is that we can
    // reuse the personForm previously defined to build an even bigger
    // form. If we in the future change the personForm then that's OK,
    // we don't have to make any changes here! :)

    val relationshipForm = 
      Formlet { (p: Person) => ((p2: Person) => Relationship(p,p2))} <*>
      personForm <*>
      personForm

    val (xml, func) = relationshipForm.run(0)

    // Now imagine that we have rendered the XML and 
    // the user has typed in some info 

    val env = Map(
      ("input_0" -> "Mads"),
      ("input_1" -> "Hartmann"),
      ("input_2" -> "22"),
      ("input_3" -> "Eva"),
      ("input_4" -> "Paus Regnar"),
      ("input_5" -> "22")
    )

    println(func(env))

  }
}