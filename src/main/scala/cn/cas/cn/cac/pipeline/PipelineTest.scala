package cn.cas.cn.cac.pipeline

import geotrellis.spark.util.SparkUtils
import org.apache.spark.SparkConf


object PipelineTest {
  def `JSON example` = {
    import geotrellis.spark._
    import geotrellis.spark.pipeline._
    import geotrellis.spark.pipeline.ast._
    import geotrellis.spark.pipeline.ast.untyped.ErasedNode

    import org.apache.spark.SparkContext

    import scala.util._

    implicit val sc: SparkContext = SparkUtils.createSparkContext("PipelineJson",new SparkConf().setMaster("local[*]"))

    // pipeline json example
    val maskJson =
      """
        |[
        |  {
        |    "uri" : "E://catalog//LC08_L1TP_090082_20180518_20180604_01_T1_B3.TIF",
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
        |    "name" : "mask",
        |    "uri" : "E://catalog//catalog",
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
        node.eval.foreach { case (zoom, rdd) =>
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

  def `Scala AST example` = {
    import geotrellis.spark._
    import geotrellis.spark.tiling._
    import geotrellis.spark.pipeline._
    import geotrellis.spark.pipeline.json._
    import geotrellis.spark.pipeline.json.read._
    import geotrellis.spark.pipeline.json.transform._
    import geotrellis.spark.pipeline.json.write._
    import geotrellis.spark.pipeline.ast._
    import geotrellis.spark.pipeline.ast.untyped.ErasedNode
    // the same example as above but written via Scala DSL
    import org.apache.spark.SparkContext

    import scala.util._

    implicit val sc: SparkContext = SparkUtils.createSparkContext("PipelineAst",new SparkConf().setMaster("local[*]"))

    val scheme = Left[LayoutScheme, LayoutDefinition](FloatingLayoutScheme(512))
    val jsonRead = JsonRead("s3://geotrellis-test/", `type` = ReadTypes.SpatialS3Type)
    val jsonTileToLayout = TileToLayout(`type` = TransformTypes.SpatialTileToLayoutType)
    val jsonReproject = Reproject("EPSG:3857", scheme, `type` = TransformTypes.SpatialBufferedReprojectType)
    val jsonPyramid = Pyramid(`type` = TransformTypes.SpatialPyramidType)
    val jsonWrite = JsonWrite("mask", "s3://geotrellis-test/pipeline/", PipelineKeyIndexMethod("zorder"), scheme, `type` = WriteTypes.SpatialType)

    val list: List[PipelineExpr] = jsonRead ~ jsonTileToLayout ~ jsonReproject ~ jsonPyramid ~ jsonWrite

    // typed way, as in the JSON example above
    val typedAst: Node[Stream[(Int, TileLayerRDD[SpatialKey])]] =
      list
        .node[Stream[(Int, TileLayerRDD[SpatialKey])]]
    val result: Stream[(Int, TileLayerRDD[SpatialKey])] = typedAst.eval

    // in some cases you may want just to evaluate the pipeline
    // to add some flexibility we can do parsing and avaluation steps manually
    // erasedNode function would parse JSON into an ErasedNode type that can be evaluated
    val untypedAst: ErasedNode = list.erasedNode

    // it would be an untyped result, just some evaluation
    // but you still have a chance to catch and handle some types of exceptions
    val untypedResult: Any =
      Try { untypedAst.unsafeEval } match {
        case Success(_) =>
        case Failure(e) =>
      }

    // typed result
    val typedResult: Option[Stream[(Int, TileLayerRDD[SpatialKey])]] =
      Try { untypedAst.eval } match {
        case Success(stream) => Some(stream)
        case Failure(e) => None
      }
  }

  def main(args: Array[String]): Unit = {
  `JSON example`
  }
}
