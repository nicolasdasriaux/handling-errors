package pureerror.eitherext

import java.io.IOException

import EitherExtension._
import org.scalatest.{EitherValues, FunSpec, Matchers}

class EitherExtensionTest extends FunSpec with Matchers with EitherValues {
  describe("Either Extension") {
    it("succeed") {
      Either.succeed(1) should be(Right(1))
    }

    it("fail") {
      Either.fail("ERROR") should be(Left("ERROR"))
    }

    it("die") {
      the[IOException] thrownBy {
        Either.die(new IOException("Error"))
      } should have message "Error"
    }

    it("dieMessage") {
      the[RuntimeException] thrownBy {
        Either.dieMessage("Error")
      } should have message "Error"
    }

    it("mapError") {
      (Either.succeed(1): Either[String, Int]).mapError(_.toUpperCase) should be(Right(1))
      (Either.fail("Error"): Either[String, Int]).mapError(_.toUpperCase) should be(Left("ERROR"))
    }

    it("flatMapError") {
      (Either.succeed(1): Either[String, Int]).flatMapError(_ => Either.succeed(2)) should be(Right(1))
      (Either.succeed(1): Either[String, Int]).flatMapError(_ => Either.fail("B")) should be(Right(1))
      (Either.fail("A"): Either[String, Int]).flatMapError(_ => Either.fail("B")) should be(Left("B"))
      (Either.fail("B"): Either[String, Int]).flatMapError(_ => Either.succeed(2)) should be(Right(2))
    }

    describe("attempt") {
      it("success") {
        val either = Either.attempt("1".toInt)
        either.right.value should be(1)
      }

      it("failure") {
        val either = Either.attempt("abc".toInt)
        either.left.value should be(a[NumberFormatException])
        either.left.value should have message """For input string: "abc""""
      }
    }

    describe("orDie") {
      it("success") {
        val either = Either.succeed(1): Either[Throwable, Int]
        either.orDie.right.value should be(1)
      }

      it("failure") {
        val either = Either.fail(new NumberFormatException("Error")): Either[NumberFormatException, Int]

        the[NumberFormatException] thrownBy {
          either.orDie
        } should have message "Error"
      }
    }

    describe("orDieWith") {
      it("success") {
        val either = (Either.succeed(1): Either[String, Int])
        either.orDieWith(new NumberFormatException(_)).right.value should be(1)
      }

      it("failure") {
        val either = Either.fail("Error"): Either[String, Int]

        the[NumberFormatException] thrownBy {
          either.orDieWith(new NumberFormatException(_))
        } should have message "Error"
      }
    }

    it("orElse") {
      val value1 = Either.attempt("abc".toInt)
      value1.left.value should be (a[NumberFormatException])
      value1.left.value should have message("""For input string: "abc"""")
      val value2 = Either.attempt("abc".toInt).refineToOrDie[NumberFormatException]
      value2.mapError(_.getMessage) should be(Left("""For input string: "abc"""".stripMargin))
    }
  }
}
