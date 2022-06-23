package lectures.part4implicits

object OrganizingImplicits extends App {

  //  implicit val revereOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  //  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)
  //  println(List(1, 4, 4, 3, 2).sorted)
  // scala.Predef
  /*
  * Implicits (used as implicits parameters)
  * - val/var
  * - object
  * - accessor methods = defs with no parentheses
  *
  * */

  case class Person(name: String, age: Int)

  val person = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  //  object Person {
  //
  //    implicit val alphabeticOrdering: Ordering[Person] =
  //      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  //  }

  //  implicit val ageOrdering: Ordering[Person] =
  //    Ordering.fromLessThan((a, b) => a.age < b.age)

  //  println(person.sorted)

  /*
  * Implicit scope
  *  - normal scope = Local Scope
  *  - imported scope
  *  - companions of all types involved in the method signature
  *   - list
  *   - Ordering
  *   - all the types involved = A or any super type
  * */
  // def sorted[B>:A](implicit ord: Ordering[B]):List[B]

  // Best practices
  //  when defining an implicit val :
  /*
  * 1- if there is a single possible value for it
  *  and you can edit the code for the type
  * then define the implicit in the companion
  *
  * 2- if there is are many possible values for it
  *   but a single good one
  *   and you can edit the code for the type
  * then define the good implicit in the companion
  * */

  object AlphabeticNameOrdering {
    implicit val alphabeticOrdering: Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit val ageOrdering: Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.age < b.age)
  }

  // use an specific implicit

  import AlphabeticNameOrdering._

  println(person.sorted)

  /*
  * Exercise
  * */
  case class Purchase(nUnits: Int, unitPrice: Double)

  // the most usage
  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }
}
