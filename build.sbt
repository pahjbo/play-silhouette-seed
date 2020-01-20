import com.typesafe.sbt.SbtScalariform._
import play.sbt.routes.RoutesKeys.routesImport
import scalariform.formatter.preferences._

version in ThisBuild := "6.0.0"

scalaVersion in ThisBuild := "2.12.8"
resolvers in ThisBuild += Resolver.jcenterRepo
resolvers in ThisBuild += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

scalacOptions in ThisBuild  ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  //"-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  // Play has a lot of issues with unused imports and unsued params
  // https://github.com/playframework/playframework/issues/6690
  // https://github.com/playframework/twirl/issues/105
  "-Xlint:-unused,_"
)

lazy val api = (project in file("modules/api")).settings(
  libraryDependencies ++= Seq(
    guice,
    "com.mohiva" %% "play-silhouette" % "6.1.0",
  )
)

lazy val slick = (project in file("modules/slick")).settings(
  libraryDependencies ++= Seq(
    guice,
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "com.typesafe.play" %% "play-slick" % "5.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "6.1.0",
    "com.mohiva" %% "play-silhouette-totp" % "6.1.0",
    "com.h2database" % "h2" % "1.4.199"
  )
).dependsOn(api)

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  name := "play-silhouette-seed",
    libraryDependencies ++= Seq(
    "com.mohiva" %% "play-silhouette" % "6.1.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "6.1.0",
    "com.mohiva" %% "play-silhouette-persistence" % "6.1.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "6.1.0",
    "com.mohiva" %% "play-silhouette-totp" % "6.1.0",
    "org.webjars" %% "webjars-play" % "2.8.0",
    "org.webjars" % "bootstrap" % "4.4.1" exclude("org.webjars", "jquery"),
    "org.webjars" % "jquery" % "3.2.1",
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "com.iheart" %% "ficus" % "1.4.3",
    "com.typesafe.play" %% "play-mailer" % "7.0.0",
    "com.typesafe.play" %% "play-mailer-guice" % "7.0.0",
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.2-akka-2.6.x",
    "com.adrianhurt" %% "play-bootstrap" % "1.5.1-P27-B4",
    "com.mohiva" %% "play-silhouette-testkit" % "6.1.0" % "test",
    specs2 % Test,
    ehcache,
    guice,
    filters
  ),
  routesImport += "utils.route.Binders._",

  // https://github.com/playframework/twirl/issues/105
  TwirlKeys.templateImports := Seq(),
).dependsOn(api, slick).aggregate(api, slick)

//********************************************************
// Scalariform settings
//********************************************************

scalariformAutoformat := true

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentConstructorArguments, false)
  .setPreference(DanglingCloseParenthesis, Preserve)
