package lectures.part4implicits

import scala.language.implicitConversions

object ImplicitsIntro extends App {

  val pair = "Daniel" -> "555"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  // "person".greet == println(fromStringToPerson("Peter").greet)
  println("person".greet)

  // with this the compile return error
  //  class A{
  //    def greet:Int = 2
  //  }
  //  implicit def fromStringToA(string: String):A = new A

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount

  implicit val defaultAmount = 10
  increment(2)
  // Not default args


}
