package pureerror.cats

import cats.data.WriterT
import cats.implicits._

object WriterTApp {
  def main(args: Array[String]): Unit = {
    def pure(v: Boolean): WriterT[Option, List[String], Boolean] = v.pure[WriterT[Option, List[String], ?]]
    def apply(v: Option[(List[String], Double)]): WriterT[Option, List[String], Double] = WriterT(v)
    def liftF(v: Option[Double]): WriterT[Option, List[String], Double] = WriterT.liftF(v)

    def tell(l: List[String]): WriterT[Option, List[String], Unit] = WriterT.tell(l)

    def put(v: Int): WriterT[Option, List[String], Int] = WriterT.put(v)(List(s"Put $v"))
    def putT(v: Option[Int]): WriterT[Option, List[String], Int] = WriterT.putT(v)(List(s"Put $v"))
    def value(v: String): WriterT[Option, List[String], String] = WriterT.value(v)
    def valueT(v: Option[String]): WriterT[Option, List[String], String] = WriterT.valueT(v)

    val unit = for {
      pu <- pure(true)
      a <- apply((List(s"Apply"), 30.0).some)
      l <- liftF(40.0.some)
      _ <- tell(List("Tell"))
      p <- put(50)
      pT <- putT(51.some)
      v <- value("60")
      vT <- valueT("61".some)
    } yield (pu, a, l, p, pT, v, vT)

    println(unit.run)
  }
}
