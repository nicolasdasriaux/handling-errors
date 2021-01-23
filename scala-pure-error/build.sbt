addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

lazy val root = (project in file("."))
  .settings(
    organization := "pureerror",
    name := "scala-pure-error",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.12.8",

    scalacOptions += "-Ypartial-unification",
    resolvers += Resolver.sonatypeRepo("releases"),

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.6.1",
      "org.typelevel" %% "cats-effect" % "1.3.1",

      "dev.zio" %% "zio" % "1.0.3",
      "dev.zio" %% "zio-streams" % "1.0.3",
      "dev.zio" %% "zio-prelude" % "1.0.0-RC1",

      "org.scalactic" %% "scalactic" % "3.0.8" % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    )
  )
