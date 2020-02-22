package com.forsynet.sparkstreaming

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext._
import Utilities._

/** Listens to a stream of Tweets and keeps track of the most popular
  *  hashtags over a 5 minute window.
  */
object MostTweetCountry {

  /** Our main function where the action happens */
  def main(args: Array[String]) {

    // Configure Twitter credentials using twitter.txt
    setupTwitter()

    // Set up a Spark streaming context named "PopularHashtags" that runs locally using
    // all CPU cores and one-second batches of data
    val ssc = new StreamingContext("local[*]", "PopularHashtags", Seconds(1))

    // Get rid of log spam (should be called after the context is set up)
    setupLogging()

    // Create a DStream from Twitter using our streaming context
    val tweets = TwitterUtils.createStream(ssc, None)

    // filter english

    //val engTweets = tweets.filter(engTweet => engTweet.getLang() == "en")

    //val tweetsByCountry = tweets.filter(countryTweet => countryTweet.getPlace().getCountry().toString().equalsIgnoreCase("USA"))
    // Now extract the text of each status update into DStreams using map()
    val statuses = tweets.map(status => status.getText())
    val tweetCountry =
      tweets.map(status => Option(status.getPlace).map(_.getCountry).orNull)

    // Blow out each word into a new DStream
    val tweetwords = statuses.flatMap(tweetText => tweetText.split(" "))

    // Map each country to a key/value pair of (hashtag, 1) so we can count them up by adding up the values
    val countryKeyValues = tweetCountry.map(country => (country, 1))

    // Now count them up over a 5 minute window sliding every one second
    val countryCounts = countryKeyValues.reduceByKeyAndWindow(
      _ + _,
      _ - _,
      Seconds(300),
      Seconds(5)
    )

    // Sort the results by the count values
    val sortedResults =
      countryCounts.transform(rdd => rdd.sortBy(x => x._2, false))

    // Print the top 10
    sortedResults.print

    ssc.checkpoint("C:/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }
}
