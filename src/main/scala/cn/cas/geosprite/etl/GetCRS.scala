package cn.cas.geosprite.etl

object GetCRS {
  import geotrellis.raster.io.geotiff.SinglebandGeoTiff

    def bandPath(b: String) = s"E:\\geotrellis-landsat-tutorial-master\\data\\landsat\\LC81070352015218LGN00_${b}.TIF"
    def main(args: Array[String]): Unit = {
      val rGeoTiff = SinglebandGeoTiff(bandPath("B4"))

      println(rGeoTiff.cellType)
      println(rGeoTiff.extent)
      println(rGeoTiff.tags.headTags)
      println(rGeoTiff.crs)

  }
}
