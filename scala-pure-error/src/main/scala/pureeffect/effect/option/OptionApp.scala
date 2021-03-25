package pureeffect.effect.option

case class User(id: Int, name: String)

object OptionApp {
  def program(maybeId: Option[Int], maybeName: Option[String]): Option[User] =
    for {
      id <- maybeId
      name <- maybeName
    } yield User(id, name)

  def main(args: Array[String]): Unit = {
    val maybeUser = program(Option.some(1), Option.some("Peter"))
    println(maybeUser)
  }
}
