// Databricks notebook source
// "Lambda functions", "anonymous functions", "function literals"
// You can declare functions inline without even giving them a name
// This happens a lot in Spark.

transformInt(3, x => x * x * x)                 //> res0: Int = 27
  
transformInt(10, x => x / 2)                    //> res1: Int = 5
  
  
transformInt(2, x => {val y = x * 2; y * y})    //> res2: Int = 16

// COMMAND ----------

val dataset = Seq(1, 2, 3).toDS()
dataset.show()


// COMMAND ----------

case class Person(name: String, age: Int)

val personDS = Seq(Person("Max", 33), Person("Adam", 32), Person("Muller", 62)).toDS()
personDS.show()


// COMMAND ----------

val wordsDataset = sc.parallelize(Seq("Spark I am your father", "May the spark be with you", "Spark I am your father")).toDS()
val groupedDataset = wordsDataset.flatMap(_.toLowerCase.split(" "))
                                 .filter(_ != "father")
                                 .groupBy("value")
val countsDataset = groupedDataset.count()
countsDataset.show()


// COMMAND ----------

val hello : String = "Namaskaram"
println(hello)

// COMMAND ----------

// Lists
val shipList = List("Enterprise", "Defiant", "Voyager", "Deep Space Nine")
println(shipList)
println(shipList.head)
println(shipList.tail)

// COMMAND ----------

// Iterating though a list
for (ship <- shipList) 
println(ship)

// COMMAND ----------

 // Let's apply a function literal to a list! map() can be used to apply any function to every item in a collection.
val backwards = shipList.map((ship: String) => {ship.reverse})

for (back <- backwards)
println(back)

// COMMAND ----------

// reduce() can be used to combine together all the items in a collection using some function.
val numberList = List(1, 2, 3, 4, 5) 

val sum = numberList.reduce((x:Int,y:Int) => x + y)

// COMMAND ----------

val filter5 = numberList.filter((x:Int) => x !=5)

// COMMAND ----------

// Concatenating lists
val moreNumbers = numberList.map((x:Int) => x *10)
val extendList = numberList ++ moreNumbers

// COMMAND ----------

// More list Actions
val reversed = numberList.reverse                 
val sorted = reversed.sorted                      
val lotsOfDuplicates = numberList ++ numberList   
val distinctValues = lotsOfDuplicates.distinct    
val maxValue = numberList.max                     
val total = numberList.sum                        
val hasFive = filter5.contains(5) 

// COMMAND ----------

val carMap = Map("RTR" -> "Camry", "Roops" -> "Sienna" , "Rhea" -> "HotWheels")

carMap("RTR")
carMap.contains("Reva")
val ReveCar = util.Try(carMap("Reva")) getOrElse "Car Unassigned"

// COMMAND ----------

// If / else syntax
  if (1 > 3) println("Impossible!") else println("The world makes sense.")

// COMMAND ----------

// Matching - like switch in other languages:
  val number = 5                                  //> number  : Int = 3
  number match {
  	case 1 => println("One")
  	case 2 => println("Two")
  	case 3 => println("Three")
  	case _ => println("Something else")
 	}                                         
 	

// COMMAND ----------

// For loops
 	for (x <- 1 to 4) {
 		val squared = x * x
 		println(squared)
 	}        

// COMMAND ----------

var x = 5                                     //> x  : Int = 10
  while (x >= 0) {
  	println(x)
  	x -= 1
  }      

// COMMAND ----------

x = 0
do { println(x); x+=1 } while (x <= 5)

// COMMAND ----------

// Expressions
// "Returns" the final value in a block automatically
   
{val x = 10; x + 20}                           //> res0: Int = 30
println({val x = 10; x + 20})            //> 30

// COMMAND ----------

// Format is def <function name>(parameter name: type...) : return type = { expression }
// Don't forget the = before the expression!
def squareIt(x: Int) : Int = {
  	x * x
  }      

// COMMAND ----------

def cubeIt(x: Int): Int = {x * x * x}           //> cubeIt: (x: Int)Int
  
println(squareIt(2))                            //> 4
println(cubeIt(2))                              //> 8

// COMMAND ----------

// Functions can take other functions as parameters
  
def transformInt(x: Int, f: Int => Int) : Int = {
  	f(x)
  }                                               //> transformInt: (x: Int, f: Int => Int)Int
  
val result = transformInt(2, cubeIt)            //> result  : Int = 8
println (result)                                //> 8

// COMMAND ----------


