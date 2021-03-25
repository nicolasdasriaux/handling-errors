addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    organization := "pureerror",
    name := "scala-pure-error",
    version := "1.0-SNAPSHOT",

    scalaVersion := "2.13.5",

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.3.1",
      "org.typelevel" %% "cats-effect" % "2.3.1",

      "dev.zio" %% "zio" % "1.0.5",
      "dev.zio" %% "zio-streams" % "1.0.5",
      "dev.zio" %% "zio-prelude" % "1.0.0-RC3+16-936a5218-SNAPSHOT",

      "org.scalactic" %% "scalactic" % "3.0.8" % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),

    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    )
  )
