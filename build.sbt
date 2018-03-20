name := "scope"

def info = Seq(
  version := "0.1",
  scalaVersion := "2.11.12",
  organization := "yjs"
)

def scalaMacroAndMeta = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
)

def options = Seq(
  javacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-feature", "-language:_")
  , scalacOptions ++= Seq("-Xlint", "-unchecked")
  , scalacOptions ++= Seq("-deprecation")
)

def testLib = Seq(libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % Test)

def unPublish = Seq(publishArtifact := false, publish := {})


lazy val `scope-macro` = (project in file("./scope-macro"))
  .settings(info)
  .settings(scalaMacroAndMeta)
  .settings(options)

lazy val root = (project in file("."))
  .settings(name := "root")
  .settings(info ++ scalaMacroAndMeta ++ options ++ testLib)
  .dependsOn(`scope-macro`)
  .aggregate(`scope-macro`)