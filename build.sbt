name := "geosprite-etl"

version := "0.1"

scalaVersion := "2.11.12"

organization := "cn.cas.geosprtie"

resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases"
)

libraryDependencies ++= Seq(
  "org.locationtech.geotrellis" %% "geotrellis-spark" % "2.0.0-RC2",
  "org.locationtech.geotrellis" %% "geotrellis-spark-etl" % "2.0.0-RC2",
  "org.apache.spark" %% "spark-core" % "2.2.0" % Provided
  //  "com.amazonaws" % "aws-java-sdk" % "1.11.268",
  //  "org.gdal" % "gdal" % "2.3.0"
)
