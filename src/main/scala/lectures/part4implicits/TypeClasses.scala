package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div> $name ($age) email: $email</div>"
  }

  User("Jhon", 32, "j@mail.com").toHtml
  /* Disadvantage
  * 1- for the types WE write
  * 2- ONE implementation out of quite a number
  * */

  // Option 2
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(_, _, _) =>
      case _ =>
    }
  }
  /*
  * 1- lost type safety
  * 2- need to modify the code every time
  * 3- still one implementation
  * */

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(u: User): String =
      s"<div> ${u.name} (${u.age}) email: ${u.email}</div>"
  }

  val jhon = User("Jhon", 32, "j@mail.com")
  println(UserSerializer.serialize(jhon))
  /*
  * 1 we can define serializers for other types
  *
  * */

  import java.util.Date

  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(value: Date): String = s"{${value.toString}"
  }

  /*
  * 2- we can define multiple serializers*/
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(value: User): String = s"${value.name}"
  }

  // Type Class
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
  }

  /*Equality
  * */
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

  // part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"$value"
  }

  println(HTMLSerializer.serialize(43))
  println(HTMLSerializer.serialize(jhon))

  // access to the entire type interface
  println(HTMLSerializer[User].serialize(jhon))

  /*
  * Exercise: implement the TC pattern for the equality tc
  * */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) =
      equalizer.apply(a, b)
  }

  val anotherJhon = User("Jhon", 322, "j2@mail.com")
  println(Equal.apply(jhon, anotherJhon))

  // AD-HOC polymorphism
}
