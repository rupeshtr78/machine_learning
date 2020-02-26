**HDFS Pig**

- At command prompt type pig → this will get into Grunt Shell

- Pwd → hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev ( run in the hdfs dir)

- Ls

- sh → shell command parameters → sh ls , sh pwd

- Dump → Shows Results

- quit 

- **clear, help, history, quit, and set; and commands such as exec, kill, and run** to control Pig from the Grunt shell

- ```
  - exec /home/user/maria_dev/movies.pig
  - run /home/user/maria_dev/movies.pig
  ```

  

- Dump → STDOUT

- STORE writes to file system

```c++
STORE oldestFiveStarMovies INTO '/user/maria_dev/oldestFiveStarMovies.txt' USING PigStorage (',');
```



```c++
ratings = LOAD '/user/maria_dev/ml-100k/u.data' AS(userID: int, movieID: int, rating: int, ratingTime: int);

metadata = LOAD '/user/maria_dev/ml-100k/u.item' USING PigStorage('|')
AS(movieID: int, movieTitle: chararray, releaseDate: chararray, videoRealese: chararray, imdblink: chararray);

nameLookup = FOREACH metadata GENERATE movieID, movieTitle,
ToUnixTime(ToDate(releaseDate, 'dd-MMM-yyyy')) AS releaseTime;

ratingsByMovie = GROUP ratings BY movieID;

avgRatings = FOREACH ratingsByMovie GENERATE group as movieID, AVG(ratings.rating) as avgRating;

fiveStarMovies = FILTER avgRatings BY avgRating > 4.0;

fiveStarsWithData = JOIN fiveStarMovies BY movieID, nameLookup BY movieID;

oldestFiveStarMovies = ORDER fiveStarsWithData BY nameLookup::releaseTime;

DUMP oldestFiveStarMovies

STORE oldestFiveStarMovies INTO '/user/maria_dev/oldestFiveStarMovies.txt' USING PigStorage (',');


```

