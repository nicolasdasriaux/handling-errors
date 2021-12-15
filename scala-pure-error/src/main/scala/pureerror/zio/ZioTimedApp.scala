package pureerror.zio
import zio._
import zio.console.Console
import zio.duration.durationInt

case class Counter(name: String, private val state: Ref[Int]) {
  def inc: UIO[Unit] = state.update(_ + 1)
  def current: UIO[Int] = state.get
}

object ZioTimedApp extends zio.App {
  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    def managedCounter(name: String): ZManaged[Console, Nothing, Counter] = ZManaged.make(
      console.putStrLn(s"Initialize $name").orDie *> Ref.make(0).map(Counter(name, _))
    )(
      _ => console.putStrLn(s"Finalize $name").orDie
    )

    val counters = ZManaged.foreachParN(5)(1 to 10)(i => managedCounter(s"counter${i}"))

    ZIO.foreachParN(5)((1 to 10).toList) { i =>
      managedCounter(s"counter$i").use { counter: Counter =>
        {
          for {
            _ <- counter.inc
            current <- counter.current
            _ <- console.putStrLn(s"${counter.name}=($current)").orDie
          } yield ()
        }.repeat(Schedule.spaced(1.second).jittered)
      }.timeout(10.second)
    }.exitCode
  }
}
