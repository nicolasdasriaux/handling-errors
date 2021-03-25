package pureeffect.effect.writer

object WriterApp {
  def fibonacci(n: Int): Writer[String, Int] = {
    if (n == 0 || n == 1)
      for {
        result <- Writer.success(1)
        _ <- Writer.log(s"fib(${n}) = 1")
      } yield result
    else
      for {
        _ <- Writer.log(s"fib($n) = fib(${n - 2}) + fib(${n - 1})")
        a <- fibonacci(n - 2)
        b <- fibonacci(n - 1)
        result = a + b
        _ <- Writer.log(s"fib($n) = $result")
      } yield result
  }

  def main(args: Array[String]): Unit = {
    val logAnResult: Writer[String, Int] = fibonacci(5)
    println(logAnResult)

    logAnResult.value match {
      case (log, result) =>
        println(result)
        log.foreach(println)
    }
  }
}
