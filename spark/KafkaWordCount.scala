package com.forsynet.ss245

import java.util.UUID

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder

import Utilities._

object KafkaWordCount {
  def main(args: Array[String]): Unit = {

    setupLogging()
//  val Kafka related
    val checkpointLocation = "C:/checkpoint/"

    val bootstrapServers = "192.168.1.200:9092"
    val subscribeType = "subscribe"
    val topics = "kafka-words-01"

    val spark = SparkSession.builder
      .config("spark.master", "local")
      .appName("StructuredKafkaWordCount")
      .getOrCreate()

    import spark.implicits._

    import org.apache.log4j.{Level, Logger}

    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)

    // Create DataSet representing the stream of input lines from kafka
    val lines = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option(subscribeType, topics)
      .option("failOnDataLoss", "false")
      .load()
      .selectExpr("CAST(value AS STRING)")
      .as[String]

    // Generate running word count
    val wordCounts = lines.flatMap(_.split(" ")).groupBy("value").count()

    // Start running the query that prints the running counts to the console
    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .option("checkpointLocation", checkpointLocation)
      .start()

    query.awaitTermination()

    spark.stop()
  }

}
// scalastyle:on println
