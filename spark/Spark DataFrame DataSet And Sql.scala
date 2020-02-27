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


import org.apache.spark.sql._
import org.apache.spark.sql.functions._

// COMMAND ----------

// DBTITLE 1,Spark Session
val spark = SparkSession
      .builder
      .appName("RTRMovies")
      .master("local[*]")
      .getOrCreate()

// COMMAND ----------

// val lines = sc.textFile("/FileStore/tables/u.data")
val lines = spark.sparkContext.textFile("/FileStore/tables/u.data")

// COMMAND ----------

lines.take(5).foreach(println)

// COMMAND ----------

val movies = lines.map(x => (x.split("\t"))).map(m => (m(1).toInt ,m(2).toFloat))

// COMMAND ----------

movies.take(5).foreach(println)

// COMMAND ----------

// DBTITLE 1,DataFrame Column Names
val colNames = Seq("movieId","ratings")

// COMMAND ----------

val movieDataFrame = spark.createDataFrame(movies).toDF(colNames: _*)


// COMMAND ----------

// DBTITLE 1,DataFrame
movieDataFrame.take(5).foreach(println)

// COMMAND ----------

movieDataFrame.printSchema()

// COMMAND ----------

df.withColumnRename("oldName", "newName")

// COMMAND ----------

val averageRatings = movieDataFrame.groupBy("movieId").avg("ratings")


// COMMAND ----------

averageRatings.take(5).foreach(println)

// COMMAND ----------

val movieCounts = movieDataFrame.groupBy("movieID").count()

// COMMAND ----------

movieCounts.take(5).foreach(println)

// COMMAND ----------

// # Join the two together ( movieID, avg(rating), and count columns)
val averagesAndCounts = movieCounts.join(averageRatings, "movieID")

// COMMAND ----------

averagesAndCounts.printSchema()

// COMMAND ----------

averagesAndCounts.take(5).foreach(println)

// COMMAND ----------

val filterlowCountMovies = averagesAndCounts.filter($"count" > 10)

// COMMAND ----------

val topTenMovies = filterlowCountMovies.orderBy("avg(ratings)")

// COMMAND ----------

topTenMovies.take(10).foreach(println)

// COMMAND ----------

import org.apache.spark.sql.functions._
filterlowCountMovies.orderBy(desc("avg(ratings)")).take(10).foreach(println)

// COMMAND ----------

import java.nio.charset.CodingErrorAction
import scala.io.Codec

implicit val codec = Codec("UTF-8")
codec.onMalformedInput(CodingErrorAction.REPLACE)
codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

// COMMAND ----------

val movieNamesRawLines = spark.sparkContext.textFile("/FileStore/tables/u.item")

// COMMAND ----------

movieNamesRawLines.take(3).foreach(println)

// COMMAND ----------

val movieNames = movieNamesRawLines.map(x => (x.split('|'))).map(m => (m(0).toInt ,m(1)))

// COMMAND ----------

movieNames.take(3).foreach(println)

// COMMAND ----------

val movieNamesCol = Seq("movieId" , "movieName")

// COMMAND ----------

val movieNamesDf = spark.createDataFrame(movieNames).toDF(movieNamesCol: _*)

// COMMAND ----------

movieNamesDf.printSchema()

// COMMAND ----------

val topTenMoviesWithName = movieNamesDf.join(filterlowCountMovies, "movieID")

// COMMAND ----------

topTenMoviesWithName.printSchema()

// COMMAND ----------

topTenMoviesWithName.orderBy(desc("avg(ratings)")).take(20).foreach(println)

// COMMAND ----------

// DBTITLE 1,Data Sets
import spark.implicits._
val movieDatSet = movies.toDS()

// COMMAND ----------

movieDatSet.take(5).foreach(println)

// COMMAND ----------

//       case _:ByteType => "Byte"
//       case _:ShortType => "Short"
//       case _:IntegerType => "Int"
//       case _:LongType => "Long"
//       case _:FloatType => "Float"
//       case _:DoubleType => "Double"
//       case _:DecimalType => "java.math.BigDecimal"
//       case _:StringType => "String"
//       case _:BinaryType => "Array[Byte]"
//       case _:BooleanType => "Boolean"
//       case _:TimestampType => "java.sql.Timestamp"
//       case _:DateType => "java.sql.Date"
//       case _:ArrayType => "scala.collection.Seq"
//       case _:MapType => "scala.collection.Map"
//       case _:StructType => "org.apache.spark.sql.Row"
//       case _ => "String"

// COMMAND ----------

 // Case class so we can get a column name for our movie ID
final case class Movies(movieId: Int , movieRating : Float)

// COMMAND ----------

movieDatSet.printSchema()

// COMMAND ----------

val movieColumnDatSet = movieDatSet.map(colName => Movies(colName._1, colName._2))

// COMMAND ----------

movieColumnDatSet.printSchema()

// COMMAND ----------

// DBTITLE 1,Determine the class of a Scala object 
println(movieColumnDatSet.getClass)

// COMMAND ----------

movieColumnDatSet.take(5).foreach(println)

// COMMAND ----------

val averageRatingsDs = movieColumnDatSet.groupBy("movieId").avg("movieRating")

// COMMAND ----------

averageRatingsDs.take(5).foreach(println)

// COMMAND ----------

val movieCountsDs = movieColumnDatSet.groupBy("movieID").count()

// COMMAND ----------

movieCountsDs.take(5).foreach(println)

// COMMAND ----------

val averagesAndCountsDs = movieCountsDs.join(averageRatingsDs, "movieID")

// COMMAND ----------

averagesAndCountsDs.take(5).foreach(println)

// COMMAND ----------

averagesAndCountsDs.printSchema()

// COMMAND ----------

val filterlowCountMoviesDs = averagesAndCountsDs.filter($"count" > 10)

// COMMAND ----------

filterlowCountMoviesDs.orderBy(desc("avg(movieRating)")).take(10).foreach(println)

// COMMAND ----------

val topTenMoviesWithNameDs = movieNamesDf.join(filterlowCountMoviesDs, "movieID")

// COMMAND ----------

topTenMoviesWithNameDs.orderBy(desc("avg(movieRating)")).take(10).foreach(println)

// COMMAND ----------

println(topTenMoviesWithNameDs.getClass())

// COMMAND ----------

topTenMoviesWithNameDs.printSchema()

// COMMAND ----------

val TopColNames = Seq("movieId","movieName","count","rating")

// COMMAND ----------

val topTenMoviesDstoDf = topTenMoviesWithNameDs.toDF(TopColNames: _*)

// COMMAND ----------

// DBTITLE 1,SQL
topTenMoviesDstoDf.createOrReplaceTempView("topTenMoviesView")

// COMMAND ----------

val topTenSql = spark.sqlContext.sql("select movieName, rating from topTenMoviesView order by count desc")
      
    
topTenSql.show()
      
