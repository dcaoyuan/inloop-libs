import sbt._
import sbt.Keys._
import bintray.Plugin._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.osgi.SbtOsgi._
import com.typesafe.sbt.osgi.OsgiKeys._

object Build extends sbt.Build {

  lazy val root = Project("inloopio-libs-root", file("."))
    .aggregate(inloopio_util, inloopio_math, inloopio_indicator)
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)

  lazy val inloopio_util = Project("inloopio-util", file("inloopio-util"))
    //.settings(defaultOsgiSettings: _*)
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    //.settings(releaseSettings: _*)
    .settings(bintrayPublishSettings ++ sbtBintraySettings: _*)
    .settings(libraryDependencies ++= (Dependencies.all ++ Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)))

  lazy val inloopio_math = Project("inloopio-math", file("inloopio-math"))
    .dependsOn(inloopio_util)
    //.settings(defaultOsgiSettings: _*)
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    //.settings(releaseSettings: _*)
    .settings(bintrayPublishSettings ++ sbtBintraySettings: _*)
    .settings(libraryDependencies ++= Dependencies.all)

  lazy val inloopio_indicator = Project("inloopio-indicator", file("inloopio-indicator"))
    .dependsOn(inloopio_util, inloopio_math)
    //.settings(defaultOsgiSettings: _*)
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    //.settings(releaseSettings: _*)
    .settings(bintrayPublishSettings ++ sbtBintraySettings: _*)
    .settings(libraryDependencies ++= Dependencies.all)



  lazy val basicSettings = Seq(
    organization := "io.inloop",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.11.4",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    resolvers ++= Seq(
      "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
      "spray" at "http://repo.spray.io",
      "spray nightly" at "http://nightlies.spray.io/"))


  lazy val releaseSettings = Seq(
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { (repo: MavenRepository) => false },
    pomExtra := pomXml)

  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := (),
    // required until these tickets are closed https://github.com/sbt/sbt-pgp/issues/42,
    // https://github.com/sbt/sbt-pgp/issues/36
    publishTo := None)

  lazy val sbtBintraySettings = Seq(
    licenses += ("BSD New", url("http://opensource.org/licenses/BSD-3-Clause")),
    publishMavenStyle := true,
    pomExtra := pomXml,
    bintray.Keys.repository in bintray.Keys.bintray := "snapshots",
    publishArtifact in Test := false)

  lazy val pomXml = (<url>https://github.com/dcaoyuan/inloopio-libs</url>
      <licenses>
        <license>
          <name>The BSD 3-Clause License</name>
          <url>http://opensource.org/licenses/BSD-3-Clause</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:dcaoyuan/inloopio-libs.git</url>
        <connection>scm:git:git@github.com:dcaoyuan/inloopio-libs.git</connection>
      </scm>)

  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test := formattingPreferences)

  import scalariform.formatter.preferences._
  def formattingPreferences =
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(IndentSpaces, 2)

}

object Dependencies {
  val AKKA_VERSION = "2.3.7"

  val scala_xml = "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
  val akka_actor = "com.typesafe.akka" %% "akka-actor" % AKKA_VERSION
  val akka_contrib = "com.typesafe.akka" %% "akka-contrib" % AKKA_VERSION
  val akka_testkit = "com.typesafe.akka" %% "akka-testkit" % AKKA_VERSION % "test"
  val scalatest = "org.scalatest" %% "scalatest" % "2.1.3" % "test"
  val specs2 = "org.specs2" %% "specs2" % "2.3.11" % "test"

  val all = Seq(scala_xml, akka_actor, akka_contrib, scalatest, akka_testkit, specs2)

}
