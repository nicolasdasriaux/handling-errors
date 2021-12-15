package pureeffect.effect.list

trait List[+A] extends Product with Serializable { as =>
  import List._

  def ::[B >: A](b: B): List[B] = List.::(b, as)

  def :::[B >: A](bs: List[B]): List[B] = (bs, as) match {
    case (bs, Nil) => bs
    case (Nil, as) => as
    case (b :: bs, as) => b :: bs ::: as
  }

  def map[B](f: A => B): List[B] = as match {
    case Nil => Nil
    case a :: as => f(a) :: as.map(f)
  }

  def flatMap[B](f: A => List[B]): List[B] = as match {
    case Nil => Nil
    case a :: as => f(a) ::: as.flatMap(f)
  }
}

object List {
  final case class ::[+A](head: A, tail: List[A]) extends List[A]
  final case object Nil extends List[Nothing]

  def empty[A]: List[A] = Nil
}
