import play.PlayImport.PlayKeys

name := "play2-slow-request-logging"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

scalacOptions += "-target:jvm-1.7"

resolvers += "Kamon Repository Snapshots" at "http://snapshots.kamon.io"

val kamonVersion = "0.3.6-1b508ff95ea477387a0b27eae41ee7a76861928e"

libraryDependencies += "io.kamon" %% "kamon-play" % kamonVersion

lazy val main = project.in(file(".")).enablePlugins(PlayScala)
