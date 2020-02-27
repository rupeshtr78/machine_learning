// Databricks notebook source
import scala.io.Source
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.PrintWriter;
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import org.apache.spark.SparkConf

// COMMAND ----------

sc.getConf.getAll.foreach(println)

// COMMAND ----------

val conf = new SparkConf().setAppName("RDD101").setMaster("local[*]")
val sc = SparkContext.getOrCreate()


// COMMAND ----------

val testx = sc.parallelize(Array((10,5), (20,5), (10, 6),(20,6)))

// COMMAND ----------

val xRating = testx.map(x => (x._1 ,((x._2).toFloat,1)))

// COMMAND ----------

val list_elements = xRating.collect().take(4)

// COMMAND ----------

for (element <- list_elements
    if element._1 == 10 ) {
    println(element)
}

// COMMAND ----------

val testy = xRating.reduceByKey( (x, y) => (x._1 + y._1 , x._2 + y._2))

// COMMAND ----------

testy.take(4).foreach(println)

// COMMAND ----------

for (element <- list_elements2) {
    println(element._2._2)
}

// COMMAND ----------

val averageX = testy.mapValues(totalY => totalY._1 / totalY._2)

// COMMAND ----------

averageX.take(4).foreach(println)

// COMMAND ----------

averageX.lookup(10)
