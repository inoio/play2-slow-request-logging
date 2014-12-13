import play.PlayImport.PlayKeys

name := "play2-slow-request-logging"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

scalacOptions += "-target:jvm-1.7"

lazy val main = project.in(file(".")).enablePlugins(PlayScala)
