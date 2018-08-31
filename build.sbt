import sbt.StdoutOutput

name := "geosprite-etl"

version := "0.1"

scalaVersion := "2.11.12"

organization := "cn.cas.geosprtie"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Yinline-warnings",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:existentials",
  "-feature")

fork in run := true
outputStrategy in run := Some(StdoutOutput)
connectInput in run := true
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"

resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases"
)

libraryDependencies ++= Seq(
  "org.locationtech.geotrellis" %% "geotrellis-spark" % "2.0.0-RC2",
  "org.apache.spark" %% "spark-core" % "2.2.0" ,
  "org.locationtech.geotrellis" %% "geotrellis-spark-etl" % "2.0.0-RC2",
  "org.locationtech.geotrellis" %% "geotrellis-spark-pipeline" % "2.0.0-RC2"
  //  "com.amazonaws" % "aws-java-sdk" % "1.11.268",
  //  "org.gdal" % "gdal" % "2.3.0"
)
// https://mvnrepository.com/artifact/io.netty/netty-all

