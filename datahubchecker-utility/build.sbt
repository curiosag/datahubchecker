name := "datahubchecker"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq("org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6")
libraryDependencies ++= Seq("org.scalatest" % "scalatest_2.12" % "3.0.1")
libraryDependencies ++= Seq("org.cg.springstuff" % "spel.dot" % "0.1")

