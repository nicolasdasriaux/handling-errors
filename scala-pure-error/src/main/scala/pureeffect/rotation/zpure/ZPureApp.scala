package pureeffect.rotation.zpure

import zio.prelude.{EState, EWriter, State}
import zio.prelude.fx.ZPure

object ZPureExtension {
  implicit class ZPureObjectExt(val self: ZPure.type) {
    def updateEither[S1, S2, E](f: S1 => Either[E, S2]): ZPure[Nothing, S1, S2, Any, E, Unit] =
      self.modifyEither[S1, S2, E, Unit](s1 => f(s1).map(s2 => (s2, ())))

    def _succeed[S]: PartialSucceed[S] = new PartialSucceed[S](true)
    def __succeed[A](value: A): ZPure[Nothing, Any, Nothing, Any, Nothing, A] = ???
  }

  class PartialSucceed[S](private val dummy: Boolean) extends AnyVal {
    def apply[A](value: A): ZPure[Nothing, S, S, Any, Nothing, A] = ZPure.succeed[S, A](value)
  }

  implicit class ZPureExt[W, R, E, A](val self: ZPure[W, Unit, Unit, R, E, A]) {
    def pipeState[S]: ZPure[W, S, S, R, E, A] = ZPure.get[S].flatMap(s => self.contramapState[S](_ => ()).asState(s))
  }

  implicit class ZPureExt2[W, R, E, A](val self: ZPure[W, Any, Nothing, R, E, A]) {
    def pipeState[S]: ZPure[W, S, S, R, E, A] = ZPure.get[S].flatMap(s => self.contramapState[S](_ => ()).asState(s))
  }

  val test: ZPure[Nothing, String, String, Any, Nothing, Int] = ZPure.succeed[Unit, Int](5).pipeState[String]
}

import ZPureExtension._

sealed trait Direction extends Product with Serializable

object Direction {
  final case object North extends Direction
  final case object East extends Direction
  final case object South extends Direction
  final case object West extends Direction
}

case class Point(x: Int, y: Int) {
  import Direction._

  def move(direction: Direction): Point = direction match {
    case North => this.copy(y = y - 1)
    case East => this.copy(x = x + 1)
    case South => this.copy(y = y + 1)
    case West => this.copy(x = x - 1)
  }
}

object Point {
  def move(direction: Direction): ZPure[String, Point, Point, Any, Nothing, Unit] = ZPure.log(direction.toString) *> ZPure.update[Point, Point](_.move(direction))
}

case class Rectangle(p1: Point, p2: Point) {
  def contains(point: Point): Boolean =
    p1.x <= point.x && point.x < p2.x &&
      p1.y <= point.y && point.y < p2.y
}

case class Board(position: Point, fuel: Int) {
  val area: Rectangle = Rectangle(Point(1, 1), Point(4, 3))
  val trap: Point = Point(2, 2)
  val goal: Point = Point(3, 2)

  def move(direction: Direction): Either[String, Board] = {
    if (fuel > 0) {
      val newPosition = position.move(direction)

      if (area.contains(newPosition))
        Right(this.copy(position = newPosition, fuel = fuel - 1))
      else
        Left("Illegal move")
    } else
      Left("Out of fuel")
  }

  def _move(direction: Direction):  EWriter[String, String, Board] = {
    if (fuel > 0) {
      val newPosition = position.move(direction)

      if (area.contains(newPosition))
        ZPure.succeed(this.copy(position = newPosition, fuel = fuel - 1))
      else
        ZPure.fail("Illegal move")
    } else
      ZPure.fail("Out of fuel")
  }
}

object Board {
  def move(direction: Direction): EState[Board, String, Unit] =
    ZPure.updateEither(_.move(direction))

  def _move(direction: Direction): ZPure[String, Board, Board, Any, String, Unit] =
    ZPure.log(direction.toString) *> ZPure.updateEither(_.move(direction))
}

object ZPureApp {
  def main(args: Array[String]): Unit = {
    val success: ZPure[Nothing, Nothing, Nothing, Any, Nothing, Int] = ZPure.succeed(1)
    val failure: ZPure[Nothing, Any, Nothing, Any, String, Nothing] = ZPure.fail("Error")
    val getCurrentId = ZPure.get[Int]
    val incId = for {
      _ <- ZPure.log("Next ID")
      _ <- ZPure.update[Int, Int](_ + 1)
      id <- ZPure.get
      _ <- ZPure.log(s"Next ID will by $id")
    } yield ()
    ZPure.access[Int]
    val test: ZPure[Nothing, Unit, Unit, Any, NumberFormatException, Int] =
      ZPure.attempt[Unit, Int]("5".toInt).refineToOrDie[NumberFormatException]

    val program = for {
      _ <- incId
      _ <- incId
      _ <- incId
      _ <- incId
      _ <- ZPure._succeed("Here")
      _ <- incId
      id <- getCurrentId
      result <- ZPure.tupledPar(
        if (id < 3) ZPure.log("OK") *> ZPure.__succeed(id).pipeState[Int] else ZPure.fail("Too Large"),
        if (id < 4) ZPure.log("OK") *> ZPure.succeed[Int, Int](id) else ZPure.fail("Much too large")
      )
      success <- ZPure._succeed[Int](1) // ZPure.succeed[Int, Int](1)
      _ <- incId
    } yield result

    import Direction._

    def log[W, S1, S2, R, E, A](effect: ZPure[String, S1, S2, R, E, A]): ZPure[String, S1, S2, R, E, A] =
      for {
        a <- effect
        s2 <- ZPure.get[S2]
        _ <- ZPure.log[S2, String](s2.toString)
      } yield a

    val round = for {
      _ <- log(Board.move(South))
      _ <- log(Board.move(North))
      _ <- Board._move(South)
      _ <- Board._move(East)
      board <- EState.get[Board]
    } yield board

    println(round.runAll(Board(position = Point(1, 1), fuel = 3)))
  }
}
