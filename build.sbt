val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "IndyBusTimeTable",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.jsoup" % "jsoup" % "1.15.3",
    )
  )
