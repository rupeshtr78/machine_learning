package com.forsynet.sparkstreaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrameWriter
import com.mongodb.spark.sql._
import com.mongodb.spark.config._

import Utilities._



object SparkSql2Json {
    
        /** Case class for converting RDD to DataFrame */
  case class Friends(Id: Int, name:String, age:Int ,numFriends:Int)
    
  def main(args:Array[String]) {
    
//    setupLogging()
    
    import org.apache.log4j.{Level, Logger}

    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.spark-project").setLevel(Level.ERROR)

    
    // Create the context with a 1 second batch size
    val conf = new SparkConf().setAppName("FriendsSQL").setMaster("local[*]").set("spark.sql.warehouse.dir", "file:///C:/tmp")
    val ssc = new StreamingContext(conf, Seconds(1))
    

    
    // Create a socket stream to read log data published via netcat on port 9999 locally
    val lines = ssc.socketTextStream("192.168.1.200", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    
    // Extract the (name , age, friends) we want from each log line
    val people = lines.map(x => x.split(","))

   // Process each RDD from each batch as it comes in
    people.foreachRDD((rdd, time) => {
      // So we'll demonstrate using SparkSQL in order to query each RDD  // using SQL queries.

      val spark = SparkSession
         .builder()
         .appName("FriendsSQL")
         .getOrCreate()
          
      import spark.implicits._

      // SparkSQL can automatically create DataFrames from Scala "case classes".
      // We created the Record case class for this purpose.
      // So we'll convert each RDD of tuple data into an RDD of "Record"
      // objects, which in turn we can convert to a DataFrame using toDF()
      val peopleDataFrame = rdd.map(p => Friends(p(0).trim.toInt, p(1),p(2).trim.toInt,p(3).trim.toInt)).toDF()

      // Create a SQL table from this DataFrame
      peopleDataFrame.createOrReplaceTempView("friendsView")
      
      val teenagers = spark.sqlContext.sql("SELECT name, age ,numFriends FROM friendsView")
      
      teenagers.createOrReplaceTempView("teenagersView")
      
      val avgTeenageFriends = spark.sqlContext.sql("SELECT age ,avg(numFriends) FROM teenagersView group by age order by age")
      println(s"========= $time =========")
      teenagers.show()
      
     
      teenagers.write.mode("append").json("rtr-json")
//      avgTeenageFriends.show()
      
      teenagers.write.mode("append").mongo()
          
      // Count up occurrences of each user agent in this RDD and print the results.
      // The powerful thing is that you can do any SQL you want here!
      // But remember it's only querying the data in this RDD, from this batch.
//      val friendsCountsDataFrame =
//        spark.sqlContext.sql("select age, count(*) as total from requests group by age")
//      println(s"========= $time =========")
//      friendsCountsDataFrame.show()
      
      // If you want to dump data into an external database instead, check out the
      // org.apache.spark.sql.DataFrameWriter class! It can write dataframes via
      // jdbc and many other formats! You can use the "append" save mode to keep
      // adding data from each batch.
      
//        import org.apache.spark.sql.DataFrameWriter

//        val options = Map("path" -> "this is the path to your warehouse") // for me every database has a different warehouse. I am not using the default warehouse. I am using users' directory for warehousing DBs and tables
        //and simply write it!
//        df.write.options(options).saveAsTable("db_name.table_name")
//    })
      
    })
    
    // Kick it off
    ssc.checkpoint("C:/checkpoint/")
    ssc.start()
    ssc.awaitTermination() 
    ssc.stop()
    
  }
}

