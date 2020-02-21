// Databricks notebook source
val input = sc.textFile("/FileStore/tables/news.txt")


// COMMAND ----------

input.top()

// COMMAND ----------

val words = input.flatMap(line => line.split(' '))

// COMMAND ----------

val loweCaseWords = words.map(word => word.toLowerCase())

// COMMAND ----------

val wordCount = loweCaseWords.countByValue()

// COMMAND ----------

val sampleWords = wordCount.take(20)

// COMMAND ----------

for ((word, count) <- sampleWords) {
  println(word+ " "+count)
}

// COMMAND ----------

// DBTITLE 1,Create Tuple and Add 1 as value and word as Key
val wordtuple = loweCaseWords.map(x => (x ,1))

// COMMAND ----------

wordtuple.take(5)

// COMMAND ----------

// DBTITLE 1,Add Tuples by key to get count
val wordCounter = wordtuple.reduceByKey((x,y) => x +y)

// COMMAND ----------

wordCounter.take(10)

// COMMAND ----------

// DBTITLE 1,Flip the Tuple so we can Sort by key
val wordCountsByVal = wordCounter.map(x => (x._2 , x._1) )

// COMMAND ----------

wordCountsByVal.take(5)

// COMMAND ----------

val wordCountsSorted = wordCountsByVal.sortByKey()

// COMMAND ----------

println("Total Words",wordCountsSorted.count())
// wordCountsSorted.take(10)

// COMMAND ----------

// DBTITLE 1,Sorted Top 10
wordCountsSorted.top(10)

// COMMAND ----------

val wordLength = loweCaseWords.map(word => (word, word.length()))

// COMMAND ----------

wordLength.take(5)

// COMMAND ----------

loweCaseWords.take(5)

// COMMAND ----------

// DBTITLE 1,Filter Small Words 
val smallWordsFilter = loweCaseWords.filter(word => word.length()>3)

// COMMAND ----------

smallWordsFilter.take(10)

// COMMAND ----------

val wordCountLarge = smallWordsFilter.map(x => (x,1)).reduceByKey((x,y) => x + y)

// COMMAND ----------

val LargeWordCountsSorted = wordCountLarge.map( x => (x._2, x._1) ).sortByKey()

// COMMAND ----------

LargeWordCountsSorted.top(20)

// COMMAND ----------


