>Spark SQL执行引擎的一个实例，它与存储在Hive中的数据集成在一起。从类路径上的hive-site.xml读取Hive的配置。

### 1. java本地执行

##### 1.1 json文件：
```
{"id":1,"name":"FantJ","age":18}
{"id":2,"name":"FantJ2","age":18}
{"id":3,"name":"FantJ3","age":18}
{"id":4,"name":"FantJ4","age":18}
{"id":5,"name":"FantJ5","age":18}
{"id":6,"name":"FantJ6","age":18}
```
##### 1.2 `DataFormCreate.java `
```
public class DataFormCreate {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("DataFormCreate").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);

        SQLContext sqlContext = new SQLContext(sc);

        DataFrame df = sqlContext.read().json("C:\\Users\\84407\\Desktop\\spark.json");
        //打印所有数据
        df.show();
        //打印元数据
        df.printSchema();
        //查询某列数据
        df.select("id").show();
        //查询多个列兵对列进行计算
        df.select(df.col("name"),df.col("age").plus(1)).show();
        //过滤
        df.filter(String.valueOf(df.col("name").equals("Fantj"))).show();
        //按照组进行统计
        df.groupBy(df.col("age")).count().show();
    }
}
```
##### 1.3 控制台输出：
```

+---+---+------+
|age| id|  name|
+---+---+------+
| 18|  1| FantJ|
| 18|  2|FantJ2|
| 18|  3|FantJ3|
| 18|  4|FantJ4|
| 18|  5|FantJ5|
| 18|  6|FantJ6|
+---+---+------+

root
 |-- age: long (nullable = true)
 |-- id: long (nullable = true)
 |-- name: string (nullable = true)


+---+
| id|
+---+
|  1|
|  2|
|  3|
|  4|
|  5|
|  6|
+---+


+------+---------+
|  name|(age + 1)|
+------+---------+
| FantJ|       19|
|FantJ2|       19|
|FantJ3|       19|
|FantJ4|       19|
|FantJ5|       19|
|FantJ6|       19|
+------+---------+

+---+---+----+
|age| id|name|
+---+---+----+
+---+---+----+


+---+-----+
|age|count|
+---+-----+
| 18|    6|
+---+-----+
```

### 2. 集群脚本执行
##### 2.1 写执行脚本：
```
 1010  vim hiveContext.sh
```
```
/home/fantj/spark/bin/spark-submit \
--class com.fantj.bigdata.DataFormCreateCluster \
--num-executors 1 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 3 \
--files /home/fantj/hive/conf/hive-site.xml \
--driver-class-path /home/fantj/hive/lib/mysql-connector-java-5.1.17.jar \
/home/fantj/wordcount.jar \
```
##### 2.2 写json文件：
```
 1012  vim spark.json
```
```
{"id":1,"name":"FantJ","age":18}
{"id":2,"name":"FantJ2","age":18}
{"id":3,"name":"FantJ3","age":18}
{"id":4,"name":"FantJ4","age":18}
{"id":5,"name":"FantJ5","age":18}
{"id":6,"name":"FantJ6","age":18}
```
##### 2.3 上传到HDFS：
```
 1013  hadoop fs -put spark.json /spark
 1014  hadoop fs -ls -R /spark
```
```
drwxr-xr-x   - root supergroup          0 2018-07-31 05:00 /spark/out
-rw-r--r--   3 root supergroup          0 2018-07-31 05:00 /spark/out/_SUCCESS
-rw-r--r--   3 root supergroup       1818 2018-07-31 05:00 /spark/out/part-00000
-rw-r--r--   3 root supergroup        203 2018-07-31 19:34 /spark/spark.json
-rw-r--r--   3 root supergroup       1527 2018-07-30 23:12 /spark/spark.txt
```
##### 2.4 执行脚本：
```
 1016  chmod +x hiveContext.sh 
 1017  ./hiveContext.sh 
```
```
......
18/07/31 19:45:05 INFO scheduler.TaskSchedulerImpl: Removed TaskSet 1.0, whose tasks have all completed, from pool 
18/07/31 19:45:05 INFO scheduler.DAGScheduler: ResultStage 1 (show at DataFormCreateCluster.java:22) finished in 0.059 s
18/07/31 19:45:05 INFO scheduler.DAGScheduler: Job 1 finished: show at DataFormCreateCluster.java:22, took 0.134718 s
+---+---+------+
|age| id|  name|
+---+---+------+
| 18|  1| FantJ|
| 18|  2|FantJ2|
| 18|  3|FantJ3|
| 18|  4|FantJ4|
| 18|  5|FantJ5|
| 18|  6|FantJ6|
+---+---+------+

root
 |-- age: long (nullable = true)
 |-- id: long (nullable = true)
 |-- name: string (nullable = true)

18/07/31 19:45:05 INFO spark.SparkContext: Invoking stop() from shutdown hook
18/07/31 19:45:05 INFO handler.ContextHandler: stopped o.s.j.s.ServletContextHandler{/static/sql,null}
18/07/31 19:45:05 INFO handler.ContextHandler: stopped o.s.j.s.ServletContextHandler{/SQL/execution/json,null}
......
```
