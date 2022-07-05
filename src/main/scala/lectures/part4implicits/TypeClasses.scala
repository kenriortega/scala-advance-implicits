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


  /*Equality
  * */


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

  // part 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(jhon.toHTML) // println(new HTMLEnrichment[User](jhon).toHTML(UserSerializer)
  // COOL!
  /*
  *  - extend to new types
  *  - choose implementation
  * */
  println(2.toHTML)
  println(jhon.toHTML(PartialUserSerializer))
  /*
  * type class itself {...}
  * type class instances (some of which are implicit) PartialUserSerializer,UserSerializer, ...
  *  conversion with implicit classes  HTMLEnrichment */

}
