import Dependencies._
import sbt.taskKey

import scala.util.Try

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

val gitDescription = taskKey[String]("Git description of working dir")

gitDescription := Try(Process("git describe --all --always --dirty --long").lines.head
  .replace("heads/","").replace("-0-g","-")).getOrElse("unknown")

// note you don't want the jar name to collide with the non-assembly jar, otherwise confusion abounds.
assemblyJarName in assembly := s"${name.value}-${version.value}-${gitDescription.value}${jgitVersionOverride.map("-jgit-" + _).mkString}.jar"

buildInfoKeys := Seq[BuildInfoKey](version, scalaVersion, gitDescription)

buildInfoPackage := "com.madgag.git.bfg"

crossPaths := false

publishArtifact in (Compile, packageBin) := false

// replace the conventional main artifact with an uber-jar
addArtifact(artifact in (Compile, packageBin), assembly)

libraryDependencies ++= Seq(
  scopt,
  scalaGitTest % "test"
)

fork in Test := true // JGit uses static (ie JVM-wide) config
