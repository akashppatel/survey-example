import _root_.sbtassembly.AssemblyPlugin

lazy val commonSettings = Seq(
  organization := "com.survey",
  version := "0.1.0",
  scalaVersion := "2.11.11",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)

val engineDeps = Seq(
  cache,
  "com.typesafe.play" % "play-slick_2.11" % "2.1.0-M1",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0-M1",
  "mysql" % "mysql-connector-java" % "5.1.38" % Runtime,
  "com.h2database" % "h2" % "1.4.194",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.1",
  filters,
  specs2 % Test
)


lazy val survey = (project in file(".")).
  disablePlugins(AssemblyPlugin).
  settings(commonSettings: _*).
  settings(
    name := "survey-example"
  ).
  aggregate(engine)

lazy val engine = (project in file("engine")).
  enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(
    name := "engine",
    libraryDependencies ++= engineDeps
  )
