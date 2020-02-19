

```scala
import sys.process._
```


```scala
val url = "https://raw.githubusercontent.com/rupeshtr78/machine_learning/master/data/fakefriends.csv"

s"wget $url".!
```

    --2020-02-19 03:04:39--  https://raw.githubusercontent.com/rupeshtr78/machine_learning/master/data/fakefriends.csv
    Resolving raw.githubusercontent.com (raw.githubusercontent.com)... 151.101.48.133
    Connecting to raw.githubusercontent.com (raw.githubusercontent.com)|151.101.48.133|:443... connected.
    HTTP request sent, awaiting response... 200 OK
    Length: 8754 (8.5K) [text/plain]
    Saving to: 'fakefriends.csv'
    
         0K ........                                              100% 13.4M=0.001s
    
    2020-02-19 03:04:39 (13.4 MB/s) - 'fakefriends.csv' saved [8754/8754]
    



    url = https://raw.githubusercontent.com/rupeshtr78/machine_learning/master/data/fakefriends.csv






    0




```scala
"ls"!
```

    fakefriends.csv
    logs
    spark-events
    user-libs



    warning: there was one feature warning; re-run with -feature for details






    0




```scala
sc
```




    org.apache.spark.SparkContext@8ee339af




```scala
sc.version
```




    2.4.3




```scala
val df = sc.textFile("fakefriends.csv")
```


    df = fakefriends.csv MapPartitionsRDD[1] at textFile at <console>:31






    fakefriends.csv MapPartitionsRDD[1] at textFile at <console>:31




```scala
df.count()
```




    500




```scala
df.take(5)
```




    Array(0,Will,33,385, 1,Jean-Luc,26,2, 2,Hugh,55,221, 3,Deanna,40,465, 4,Quark,68,21)




```scala
  def dataPipeLine(line: String) = {
      // Split by commas
      val fields = line.split(",")
      // Extract the age and numFriends fields, and convert to integers
      val age = fields(2).toInt
      val numFriends = fields(3).toInt
      // Create a tuple that is our result.
      (age, numFriends)
  }
```


    dataPipeLine: (line: String)(Int, Int)




```scala
val rdd = df.map(dataPipeLine)
```


    rdd = MapPartitionsRDD[2] at map at <console>:35






    MapPartitionsRDD[2] at map at <console>:35




```scala
rdd.top(5)
```




    Array((69,491), (69,470), (69,431), (69,361), (69,236))




```scala
val rddMap = rdd.mapValues(x => (x, 1))
```


    rddMap = MapPartitionsRDD[7] at mapValues at <console>:37






    MapPartitionsRDD[7] at mapValues at <console>:37




```scala
rddMap.top(5)
```




    Array((69,(491,1)), (69,(470,1)), (69,(431,1)), (69,(361,1)), (69,(236,1)))




```scala
val totalsByAge = rdd.mapValues(x => (x, 1)).reduceByKey( (x,y) => (x._1 + y._1, x._2 + y._2))
```


    totalsByAge = ShuffledRDD[5] at reduceByKey at <console>:37






    ShuffledRDD[5] at reduceByKey at <console>:37




```scala
totalsByAge.top(5)
```




    Array((69,(2352,10)), (68,(2696,10)), (67,(3434,16)), (66,(2488,9)), (65,(1491,5)))




```scala
val averagesByAge = totalsByAge.mapValues(x => x._1 / x._2)
```


    averagesByAge = MapPartitionsRDD[11] at mapValues at <console>:39






    MapPartitionsRDD[11] at mapValues at <console>:39




```scala
averagesByAge.top(5)
```




    Array((69,235), (68,269), (67,214), (66,276), (65,298))




```scala
val results = averagesByAge.collect()
```


    results = Array((34,245), (52,340), (56,306), (66,276), (22,206), (28,209), (54,278), (46,223), (48,281), (30,235), (50,254), (32,207), (36,246), (24,233), (62,220), (64,281), (42,303), (40,250), (18,343), (20,165), (38,193), (58,116), (44,282), (60,202), (26,242), (68,269), (19,213), (39,169), (41,268), (61,256), (21,350), (47,233), (55,295), (53,222), (25,197), (29,215), (59,220), (65,298), (35,211), (27,228), (57,258), (51,302), (33,325), (37,249), (23,246), (45,309), (63,384), (67,214), (69,235), (49,184), (31,267), (43,230))






    Array((34,245), (52,340), (56,306), (66,276), (22,206), (28,209), (54,278), (46,223), (48,281), (30,235), (50,254), (32,207), (36,246), (24,233), (62,220), (64,281), (42,303), (40,250), (18,343), (20,165), (38,193), (58,116), (44,282), (60,202), (26,242), (68,269), (19,213), (39,169), (41,268), (61,256), (21,350), (47,233), (55,295), (53,222), (25,197), (29,215), (59,220), (65,298), (35,211), (27,228), (57,258), (51,302), (33,325), (37,249), (23,246), (45,309), (63,384), (67,214), (69,235), (49,184), (31,267), (43,230))




```scala
results.sorted.foreach(println)
```

    (18,343)
    (19,213)
    (20,165)
    (21,350)
    (22,206)
    (23,246)
    (24,233)
    (25,197)
    (26,242)
    (27,228)
    (28,209)
    (29,215)
    (30,235)
    (31,267)
    (32,207)
    (33,325)
    (34,245)
    (35,211)
    (36,246)
    (37,249)
    (38,193)
    (39,169)
    (40,250)
    (41,268)
    (42,303)
    (43,230)
    (44,282)
    (45,309)
    (46,223)
    (47,233)
    (48,281)
    (49,184)
    (50,254)
    (51,302)
    (52,340)
    (53,222)
    (54,278)
    (55,295)
    (56,306)
    (57,258)
    (58,116)
    (59,220)
    (60,202)
    (61,256)
    (62,220)
    (63,384)
    (64,281)
    (65,298)
    (66,276)
    (67,214)
    (68,269)
    (69,235)

