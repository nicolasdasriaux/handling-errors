lazy val root = (project in file("."))
  .settings(
    name := "handling-errors",
    version := "0.1",
    scalaVersion := "2.12.8",
    
    scalacOptions += "-Ypartial-unification",
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),

    resolvers += Resolver.sonatypeRepo("releases"),
    
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.6.0"
    )
  )
