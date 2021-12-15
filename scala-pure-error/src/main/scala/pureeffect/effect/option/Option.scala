package pureeffect.effect.option

sealed trait Option[+A] extends Product with Serializable { va =>
  import Option._

  def map[B](f: A => B): Option[B] = va match {
    case Some(a) => Some(f(a))
    case None => None
  }

  def flatMap[B](f: A => Option[B]): Option[B] = va match {
    case Some(a) => f(a)
    case None => None
  }
}

object Option {
  final case class Some[A](value: A) extends Option[A]
  final case object None extends Option[Nothing]

  def some[A](a: A): Option[A] = Some(a)
  val none: Option[Nothing] = None

  def empty[A]: Option[A] = None

  def fromEither[A](value: Either[_, A]): Option[A] = value match {
    case Right(a) => Some(a)
    case Left(_) => None
  }
}
