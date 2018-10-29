package cn.cas.geosprite.etl

import geotrellis.raster.{MultibandTile, Tile}
import geotrellis.spark.SpatialKey
import geotrellis.spark.etl.Etl
import geotrellis.spark.util.SparkUtils
import geotrellis.vector.ProjectedExtent
import org.apache.spark.SparkConf

object ETL {

  def main(args: Array[String]): Unit = {
    var args=new Array[String](6)
    args(0)="--input"
    args(1)="data/input.json"
    args(2)="--output"
    args(3)="data/output.json"
    args(4)="--backend-profiles"
    args(5)="data/backend-profiles.json"
    goETL(args)
  }
  def goETL(args: Array[String])={
    implicit val sc = SparkUtils.createSparkContext("geotrellis",new SparkConf().setMaster("local[*]"))
    try{
      Etl.ingest[ProjectedExtent, SpatialKey, MultibandTile](args)
      println("finished!")
    }finally {
      sc.stop()
    }
  }
}
