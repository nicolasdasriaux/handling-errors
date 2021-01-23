package pureeffect.writer

object WriterApp {
  def main(args: Array[String]): Unit = {
    def fib(n: Int): Writer[String, Int] = {
      if (n == 0 || n == 1)
        for {
          result <- Writer.success(1)
          _ <- Writer.log(s"fib(${n}) = 1")
        } yield result
      else
        for {
          _ <- Writer.log(s"fib($n) = fib(${n - 2}) + fib(${n - 1})")
          a <- fib(n - 2)
          b <- fib(n - 1)
          result = a + b
          _ <- Writer.log(s"fib($n) = $result")
        } yield a + b
    }

    val value = for {
      a <- Writer.success(1)
      _ <- Writer.log(s"a=${a}")
      b <- Writer.success(2)
      _ <- Writer.log(s"b=${b}")
      sum = a + b
      - <- Writer.log(s"sum=$sum")
    } yield sum

    println(value)
    println(fib(5))

    fib(5).run match { case (w, _) => w.foreach(println) }
  }
}
