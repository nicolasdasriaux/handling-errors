package pureerror.eitherext

import ZEither._

object ZEitherApp {
  def main(args: Array[String]): Unit = {
    val value = Either.attempt("1".toInt)
      .refineToOrDie[NumberFormatException]
      .mapError(_.getMessage)
      ._filterOrElse(_ > 1)(value => Either.fail(s"$value should exceed 1"))

    println(value.fold(identity, value => s"value=$value"))
  }
}
