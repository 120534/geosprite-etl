package cn.cas.geosprite.etl

import java.io.File

import cn.cas.index.caculation.Calculations
import com.typesafe.config.ConfigFactory
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.render.ColorMap
import geotrellis.raster.resample.Bilinear
import geotrellis.raster.{ArrayTile, DoubleConstantNoDataCellType, MultibandTile, Tile}
import geotrellis.spark.{SpatialKey, TileLayerMetadata}
import geotrellis.spark.util.SparkUtils
import geotrellis.spark._
import geotrellis.vector.ProjectedExtent
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import geotrellis.spark.io.hadoop._
import geotrellis.spark.tiling.FloatingLayoutScheme
import org.apache.commons._
import org.apache.commons.lang.time.StopWatch

// 不使用ETL工具进行
object NDVIStitch {
  def main(args: Array[String]): Unit = {
    // stopWatch 计时的一个工具类
    val stopWatch = new StopWatch()
    stopWatch.start()

    implicit val sc = SparkUtils.createSparkContext("testRDD",new SparkConf().setMaster("local[*]"))
    val inputPath = ""
    val inputRdd: RDD[(ProjectedExtent, MultibandTile)] = sc.hadoopMultibandGeoTiffRDD(inputPath)

    val (_, rasterMetaData) = TileLayerMetadata.fromRdd(inputRdd, FloatingLayoutScheme(512))

    val tiled: RDD[(SpatialKey, MultibandTile)] =
          inputRdd
            .tileToLayout(rasterMetaData.cellType, rasterMetaData.layout, Bilinear)
            .repartition(100)

    val ndviTile = tiled.map(myfuncPerElement)

    val resultRdd:TileLayerRDD[SpatialKey] = new ContextRDD(ndviTile, rasterMetaData)

    SinglebandGeoTiff(resultRdd.stitch(),resultRdd.metadata.extent,resultRdd.metadata.crs).write("E://ndvi_tiled.tif")

//    val ndvi =  tiled.map(_._2.convert(DoubleConstantNoDataCellType).combineDouble(0, 2)
//        { (r: Double, ir: Double) =>
//          Calculations.ndvi(r, ir)})

    println("Rendering PNG and saving to disk...")

    stopWatch.stop()
    println(stopWatch.getTime)
  }

  def myfuncPerElement(e:(SpatialKey,MultibandTile)): (SpatialKey,Tile) ={
    val ndvi =
      e._2.convert(DoubleConstantNoDataCellType).combineDouble(0,2){
        (r,ir)=>Calculations.ndvi(r,ir)
      }
    (e._1,ndvi)
  }
}