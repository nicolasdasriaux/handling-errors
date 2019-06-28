import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

case class Customer(id: Int, firstName: String, lastName: String)

object Customer {
  def validateTrimmedAndNonEmpty(s: String): Either[String, String] =
    Either.cond(s != "" && s.trim == s, s, "Expected to be trimmed and non empty")

  def validatePositiveInt(i: Int): Either[String, Int] =
    Either.cond(i > 0, i, "Negative int")

  def validateCustomer(id: Int, firstName: String, lastName: String): ValidatedNel[String, Customer] = {
    (
      validatePositiveInt(id).toValidatedNel[String].leftMap(l => l.map(s => s"id: $s")),
      validateTrimmedAndNonEmpty(firstName).toValidatedNel[String].leftMap((l: NonEmptyList[String]) => l.map(s => s"firstName: $s")),
      validateTrimmedAndNonEmpty(lastName).toValidatedNel.leftMap((l: NonEmptyList[String]) => l.map(s => s"lastName: $s"))
    ).mapN(Customer.apply)
  }
}

object ValidatedApp {
  def main(args: Array[String]): Unit = {
    val valid: Validated[Nothing, Int] = Validated.valid(1)
    Validated.invalid("error")
    println(Customer.validateCustomer(0, "", " Peter "))


    val list = (1 to 100).map(i => if(i % 10 != 0) i.validNel[String] else s"error$i".invalidNel[Int]).toList
    val sequence = list.sequence
    println(sequence)
  }
}
