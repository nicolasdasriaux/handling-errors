package pureerror.zio

import java.util.concurrent.Executors

import zio.console._
import zio.duration._
import zio.stream._
import zio._

object ZioApp extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    for {
      _ <- putStrLn("What's your name?")
      name <- getStrLn
      _ <- putStrLn(s"Hello $name!")
    } yield ()

    object Tick

    val ticks = ZStream.repeatEffectWith(IO.succeed(Tick), Schedule.spaced(1.second))
    val numbers = Stream.fromIterable(1 to 10)

    val tickedNumbers = ticks.zip(numbers).map(_._2)

    val sum = tickedNumbers
      .map(i => i * i)
      .takeWhile(_ < 80)
      .tap(i => putStrLn(s"... $i"))
      .fold(0)(_ + _)

    val value = tickedNumbers.transduce(ZTransducer.foldUntil[Int, Int](0, 3)(_ + _))
    val value1 = value.foreach(l => putStrLn(l.toString))

    val program = for {
      queue <- Queue.bounded[Int](5)
      _ <- putStrLn("Start")
      _ <- queue.offer(10).fork
      n <- queue.take
      _ <- putStrLn(s"n=$n")
      _ <- sum.map(s => putStrLn(s"s=$s"))
      _ <- value1
    } yield ()

    val prog = for {
      l <- Queue.bounded[Int](3)
      r <- Queue.bounded[Int](3)
      ls = Stream.fromQueue(l)
      rs = Stream.fromQueue(r)

      zip = ls.map(Option(_)).zipAllWith(rs.map(Option(_)))(_ => Option.empty[String], _ => Option.empty[String]) {
        case (Some(a), Some(b)) => Some(s"($a, $b)")
        case (Some(a), None) => Some(s"($a, _)")
        case (None, Some(b)) => Some(s"(_, $b)")
        case (None, None) => None
      }

      fiber <- zip.foreach(i => putStrLn(i.toString)).fork
      shutdownQueues = putStrLn("Shutting dow") *> l.shutdown.zipPar(r.shutdown) *> putStrLn("Shut down")
      s <- l.offer(1) *> r.offer(10) *> r.offer(2) *> shutdownQueues.delay(3.seconds)
      _ <- fiber.join
    } yield s

    val executor = Executors.newScheduledThreadPool(5)
    ???
  }
}
