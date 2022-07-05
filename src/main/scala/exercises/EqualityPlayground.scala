package exercises

import lectures.part4implicits.TypeClasses.{HTMLEnrichment, HTMLSerializer, User}

object EqualityPlayground extends App {
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean =
      a.name == b.name && a.email == b.email
  }


  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) =
      equalizer.apply(a, b)
  }
  /*
    * Exercise: implement the TC pattern for the equality tc
    * */

  // AD-HOC polymorphism
  val jhon = User("Jhon", 32, "j@mail.com")
  val anotherJhon = User("Jhon", 322, "j2@mail.com")
  println(Equal.apply(jhon, anotherJhon))

  /*
  * Exercise - improve the equal TC with an implicit conversion class
  * === (another value: T)
  * !== (another value: T)
  * */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(other: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(value, other)

    def !==(other: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.apply(value, other)
  }

  println(jhon === anotherJhon)

  /*
  * jhon.===(anotherJhon)
  * new TypeSafeEqual[User](jhon).===(anotherJhon)
  * new TypeSafeEqual[User](jhon).===(anotherJhon)(NameEquality)
  * */
  // without context bounds
  def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

  // context bounds
  def htmlSugar[T: HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // use serializer
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

  // implicitly
  case class Permissions(mask: String)

  implicit val defaultPermissions: Permissions = Permissions("0744")
  // in some other part of the code
  val standardPerms = implicitly[Permissions]

}
