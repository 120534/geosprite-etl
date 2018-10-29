package cn.cas

import geotrellis.raster._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import akka.actor._
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.hadoop.fs.Path

import scala.concurrent._
import com.typesafe.config.ConfigFactory
import geotrellis.raster.render.{ColorMap, ColorRamp, ColorRamps, Png}
import geotrellis.spark.io.hadoop.{HadoopAttributeStore, HadoopLayerReader}

object Test extends App {
  val catalogPath = new java.io.File("data/ingest").getAbsolutePath
  val fileValueReader = FileValueReader(catalogPath)
  def reader(layerId: LayerId) = fileValueReader.reader[SpatialKey,Tile](layerId)
  val tileOpt: Option[Tile] =
  try {
      Some(reader(LayerId("landsat", 7)).read(104,51))
    } catch {
      case _: ValueNotFoundError =>
        None
    }
    val colormap = ColorRamps.greyscale(10000)
    val png = tileOpt.get.renderPng(colormap).write("E://render0.png")
}
