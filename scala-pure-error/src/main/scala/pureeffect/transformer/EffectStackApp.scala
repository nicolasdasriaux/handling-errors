package pureeffect.transformer

import cats._
import cats.data._
import cats.implicits._

object EffectStackApp {
  type Id = Int
  type Name = String
  type Error = String

  def main(args: Array[String]): Unit = {

    def inverse(x: Double): Reader[Int, Either[Error, Double]] = Reader { scale =>
      if (x != 0) Either.right(scale / x) else Either.left("Error")
    }

    def inverseT(x: Double): ReaderT[Either[Error, *], Int, Double] = ReaderT { scale =>
      if (x != 0) Either.right(scale / x) else Either.left("Error")
    }

    def findName(id: Id): Either[Error, Option[Name]] =
      id match {
        case _ if id < 0 => Either.left("Invalid ID")
        case _ if List(1, 5, 9).contains(id) => Right(Some(s"Name $id"))
        case _ => Right(None)
      }

    def findNameT(id: Id): OptionT[Either[Error, *], Name] = OptionT {
      id match {
        case _ if id < 0 => Either.left("Invalid ID")
        case _ if List(1, 5, 9).contains(id) => Right(Some(s"Name $id"))
        case _ => Right(None)
      }
    }

    println(inverseT(4.0).run(8))
    println(inverseT(0).run(-5))

    val program: ReaderT[Either[Error, *], Int, Double] = for {
      a <- inverseT(4)
      b <- inverseT(8)
    } yield a + b

    println(program.run(32))

    val program2: OptionT[Either[Error, *], (Name, Name)] = for {
      name1 <- findNameT(1)
      name2 <- findNameT(5)
    } yield (name1, name2)

    println(program2.value)
  }
}
