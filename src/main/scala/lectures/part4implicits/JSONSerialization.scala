package lectures.part4implicits

import java.sql.Date

object JSONSerialization extends App {

  /*
  * Users,Post,feeds
  * Serialize to JSON
  * */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, post: List[Post])

  /*
  * 1- intermediate data type: Int,String,List,Date
  * 2- type classes for conversion to intermediate data types
  * 3- serialize to json
  * */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(value: List[JSONValue]) extends JSONValue {
    override def stringify: String = value.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(value: Map[String, JSONValue]) extends JSONValue {
    override def stringify: String = value.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }
      .mkString("{", ",", "}")
  }

  val data = JSONObject(
    Map(
      "user" -> JSONString("Kalix"),
      "posts" -> JSONArray(List(JSONString("Scala"), JSONNumber(344)))
    )
  )
  println(data.stringify)
  // type class
  /*
  * 1- type class
  * 2- type class instances (implicit)
  * 3- pimp lib to use type class instances
  * */

  // 2.1
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  //  2.3 conversion
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }

  // 2.2
  // existing data types
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // custom data types
  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email),
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "createdAt" -> JSONString(post.createdAt.toString),
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(feed.post.map(_.toJSON))
    ))
  }


  // call stringify on result
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "j@mail.com")
  val feed = Feed(john, List(
    Post("Hello", now),
    Post("Look at this cute puppy", now)
  ))
  println(feed.toJSON.stringify)
}
