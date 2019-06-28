lazy val root = (project in file("."))
  .settings(
    name := "handling-errors",
    version := "0.1",
    scalaVersion := "2.12.8",
    scalacOptions += "-Ypartial-unification",

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.6.0"
    )
  )
