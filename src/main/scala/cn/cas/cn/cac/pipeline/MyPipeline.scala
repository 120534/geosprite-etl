package cn.cas.cn.cac.pipeline

import geotrellis.spark.util.SparkUtils
import org.apache.spark.SparkConf
import geotrellis.spark._
import geotrellis.spark.pipeline._
import geotrellis.spark.pipeline.ast._
import geotrellis.spark.pipeline.ast.untyped.ErasedNode

import org.apache.spark.SparkContext

import scala.util._

import scala.util.{Failure, Success, Try}

object MyPipeline {
  def jsonPipeline={
    //第一步创建sc
    implicit val sc: SparkContext = SparkUtils.createSparkContext("PipelineJson",new SparkConf().setMaster("local[*]"))
    //第二步生成json
    val json =
      """[
 |     {
 |    "type": "singleband.spatial.read.hadoop",
 |    "path": "E:\\catalog\\LC81070352015218LGN00_B3.TIF",
 |    },
      {
 |    "type": "singleband.spatial.transform.buffered-reproject",
 |    "crs": "EPSG:3857"
 |    },
 |    {
 |    "type": "singleband.spatial.transform.pyramid",
 |    "resampleMethod": "nearest-neighbor",
 |    "layoutScheme": "zoomed",
 |    "tileSize": 256,
 |    "cellSize": {
 |      "width": 30,
 |      "height": 30
 |    },
 |    "partitions": 100
 |    },
 |    {
 |    "type": "singleband.spatial.write",
 |    "name": "landsat",
 |    "uri": "E:\\catalog\\mask\\",
 |    "pyramid": "true",
 |    "keyIndexMethod": {
 |      "type": "zorder",|
 |    },
 |    "maxZoom": 13
 |    }
      ]""".stripMargin
    val maskJson =
      """
        |[
        |  {
        |    "uri" : "E:\\aws\\LC08_L1TP_122030_20171226_20180103_01_T1_B3.TIF",
        |    "type" : "singleband.spatial.read.hadoop"
        |  },
        |  {
        |    "resample_method" : "nearest-neighbor",
        |    "type" : "singleband.spatial.transform.tile-to-layout"
        |  },
        |  {
        |    "crs" : "EPSG:3857",
        |    "scheme" : {
        |      "crs" : "epsg:3857",
        |      "tileSize" : 256,
        |      "resolutionThreshold" : 0.1
        |    },
        |    "resample_method" : "nearest-neighbor",
        |    "type" : "singleband.spatial.transform.buffered-reproject"
        |  },
        |  {
        |    "end_zoom" : 0,
        |    "resample_method" : "nearest-neighbor",
        |    "type" : "singleband.spatial.transform.pyramid"
        |  },
        |  {
        |    "name" : "landsat",
        |    "uri" : "s3://chd-landsat/landsat",
        |    "key_index_method" : {
        |      "type" : "zorder"
        |    },
        |    "scheme" : {
        |      "crs" : "epsg:3857",
        |      "tileSize" : 256,
        |      "resolutionThreshold" : 0.1
        |    },
        |    "type" : "singleband.spatial.write"
        |  }
        |]
      """.stripMargin


    // parse the JSON above
    val list: Option[Node[Stream[(Int, TileLayerRDD[SpatialKey])]]] = maskJson.node

    list match {
      case None => println("Couldn't parse the JSON")
      case Some(node) => {
        // eval evaluates the pipeline
        // the result type of evaluation in this case would ben Stream[(Int, TileLayerRDD[SpatialKey])]
        node.eval
          .foreach { case (zoom, rdd) =>
          println(s"ZOOM: ${zoom}")
          println(s"COUNT: ${rdd.count}")
        }
      }
    }

    // in some cases you may want just to evaluate the pipeline
    // to add some flexibility we can do parsing and avaluation steps manually
    // erasedNode function would parse JSON into an ErasedNode type that can be evaluated
    val untypedAst: Option[ErasedNode] = maskJson.erasedNode

    // it would be an untyped result, just some evaluation
    // but you still have a chance to catch and handle some types of exceptions
    val untypedResult: Option[Any] = untypedAst.map { en =>
      Try { en.unsafeEval } match {
        case Success(_) =>
        case Failure(e) =>
      }
    }

    // typed result
    val typedResult: Option[Stream[(Int, TileLayerRDD[SpatialKey])]] = untypedAst.flatMap { en =>
      Try { en.eval[Stream[(Int, TileLayerRDD[SpatialKey])]] } match {
        case Success(stream) => Some(stream)
        case Failure(e) => None
      }
    }
  }

  def main(args: Array[String]): Unit = {
    jsonPipeline
  }
}
