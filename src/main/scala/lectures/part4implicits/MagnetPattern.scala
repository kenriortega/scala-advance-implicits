package lectures.part4implicits

import javax.naming.spi.DirStateFactory.Result
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MagnetPattern extends App {

  class Serializer[T]

  class P2PRequest

  class P2PResponse

  // method overloading
  trait Actor {
    def receive(statusCode: Int): Int

    def receive(request: P2PRequest): Int

    def receive(response: P2PResponse): Int

    def receive[T: Serializer](message: T): Int

    def receive[T: Serializer](message: T, statusCode: Int): Int

    def receive(future: Future[P2PRequest]): Int
    //    def receive(future: Future[P2PResponse]): Int // not possible
    // lots of overloads
  }

  /* Problems
  * 1- type erasure
  * 2- lifting doesn`t work for all overloads
  *   val receiveFV = receive _ // ?!
  * 3- code duplications
  * 4- type inferrence and deault args
  *   actor.receive(?!)
  * */

  /*
  * Best practice to solve this problems is called magnet pattern
  *
  * */
  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet.apply()

  implicit class FromP2PRequest(req: P2PRequest) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling a P2PRequest
      println("handling a P2PRequest")
      42
    }
  }

  implicit class FromP2PResponse(req: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling a P2PResponse
      println("handling a P2PResponse")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  // benefits
  /*
  * 1- no more type problems*/
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 1
  }


  receive(Future(new P2PRequest))
  receive(Future(new P2PResponse))

  // 2- lifting works
  trait MathLib {
    def add1(x: Int) = x + 1

    def add1(s: String) = s.toInt + 1
  }

  // magnetize
  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet.apply()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFv = add1 _
  println(addFv(1))
  println(addFv("2"))

  /*Drawbacks
  * 1- verbose
  * 2- harder to read
  * 3- you can`t name or replace default args
  * 4- call by name doesn`t work correctly
  *   (hint: side effects)
  * */

  class Handler {
    def handle(s: => String): Unit = {
      println(s)
      println(s)
    }
    // other...
  }

  trait HandlerMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandlerMagnet) = magnet.apply()

  implicit class StringHandle(s: => String) extends HandlerMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("hello, scala")
    "scala side effect"
  }

//  handle(sideEffectMethod())
  handle{
    println("hello, scala")
   new StringHandle( "scala side effect")
  }

}
