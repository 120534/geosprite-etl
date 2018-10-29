package cn.cas.geosprite.etl

import java.io.File

import geotrellis.proj4.WebMercator
import geotrellis.raster.MultibandTile
import geotrellis.raster.io.geotiff.compression.NoCompression
import geotrellis.raster.resample.Bilinear
import geotrellis.spark.io.cog.COGLayer
import geotrellis.spark.io.file.FileAttributeStore
import geotrellis.spark.io.file.cog.FileCOGLayerWriter
import geotrellis.spark.io.index.ZCurveKeyIndexMethod
import geotrellis.spark.{Metadata, MultibandTileLayerRDD, SpatialKey, TileLayerMetadata}
import geotrellis.spark.tiling.{FloatingLayoutScheme, ZoomedLayoutScheme}
import geotrellis.vector.ProjectedExtent
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object COGtest {
  val inputPath = new File("/data/r-g-nir.tif").getAbsolutePath
  val outputPath = "E:\\geosprite-etl\\data"

  def main(args: Array[String]): Unit = {
    // Setup Spark to use Kryo serializer.
    val conf =
      new SparkConf()
        .setMaster("local[*]")
        .setAppName("Spark Tiler")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")

    val sc = new SparkContext(conf)
    try {
      run(sc)
    } finally {
      sc.stop()
    }
  }

  def run(implicit sc: SparkContext) = {

//    val inputRdd: RDD[(ProjectedExtent, MultibandTile)] =
//      sc.hadoopMultibandGeoTiffRDD(inputPath)
//
//    val (_, rasterMetaData) =
//      TileLayerMetadata.fromRdd(inputRdd, FloatingLayoutScheme(256))
//
//    val tiled: RDD[(SpatialKey, MultibandTile)] =
//      inputRdd
//        .tileToLayout(rasterMetaData.cellType, rasterMetaData.layout, Bilinear)
//        .repartition(100)
//
//    val layoutScheme = ZoomedLayoutScheme(WebMercator, tileSize = 256)
//
//    val (zoom, reprojected): (Int, RDD[(SpatialKey, MultibandTile)] with Metadata[TileLayerMetadata[SpatialKey]]) =
//      MultibandTileLayerRDD(tiled, rasterMetaData)
//        .reproject(WebMercator, layoutScheme, Bilinear)
//
//    val attributeStore = FileAttributeStore(outputPath)
//
//    val writer = FileCOGLayerWriter(attributeStore)
//
//    val layerName = "COGlayer"
//
//    val cogLayer =
//      COGLayer.fromLayerRDD(
//        reprojected,
//        zoom,
//        compression = NoCompression,
//        maxTileSize = 4096
//      )
//
//    val keyIndexes =
//      cogLayer.metadata.zoomRangeInfos.
//        map { case (zr, bounds) => zr -> ZCurveKeyIndexMethod.createIndex(bounds) }.
//        toMap
//
//    writer.writeCOGLayer(layerName, cogLayer, keyIndexes)
  }
}
