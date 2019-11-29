### 实现一：spark shell
>主要用于测试，在部署到集群之前，自己使用集合测试数据来测试流程是否通顺。

#### 1.1 文件上传hdfs
首先先得把文本文件上传到HDFS上的spark目录下
文本内容：
```
[root@s166 fantj]# cat spark.txt 
What is “version control”, and why should you care? Version control is a system that records changes to a file or set of files over time so that you can recall specific versions later. For the examples in this book, you will use software source code as the files being version controlled, though in reality you can do this with nearly any type of file on a computer.

If you are a graphic or web designer and want to keep every version of an image or layout (which you would most certainly want to), a Version Control System (VCS) is a very wise thing to use. It allows you to revert selected files back to a previous state, revert the entire project back to a previous state, compare changes over time, see who last modified something that might be causing a problem, who introduced an issue and when, and more. Using a VCS also generally means that if you screw things up or lose files, you can easily recover. In addition, you get all this for very little overhead.

Local Version Control Systems
Many people’s version-control method of choice is to copy files into another directory (perhaps a time-stamped directory, if they’re clever). This approach is very common because it is so simple, but it is also incredibly error prone. It is easy to forget which directory you’re in and accidentally write to the wrong file or copy over files you don’t mean to.

To deal with this issue, programmers long ago developed local VCSs that had a simple database that kept all the changes to files under revision control.
```
```
[root@s166 fantj]# vim spark.txt
[root@s166 fantj]# hadoop fs -mkdir -p /spark
[root@s166 fantj]# hadoop fs -put spark.txt /spark
[root@s166 fantj]# hadoop fs -ls -R /spark
-rw-r--r--   3 root supergroup       1527 2018-07-30 23:12 /spark/spark.txt
```
#### 1.2 开启shell
```
[root@s166 fantj]# spark-shell 
18/07/31 04:53:52 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
18/07/31 04:53:55 INFO spark.SecurityManager: Changing view acls to: root
18/07/31 04:53:55 INFO spark.SecurityManager: Changing modify acls to: root
18/07/31 04:53:55 INFO spark.SecurityManager: SecurityManager: authentication disabled; ui acls disabled; users with view permissions: Set(root); users with modify permissions: Set(root)
18/07/31 04:53:58 INFO spark.HttpServer: Starting HTTP Server
18/07/31 04:53:59 INFO server.Server: jetty-8.y.z-SNAPSHOT
18/07/31 04:53:59 INFO server.AbstractConnector: Started SocketConnector@0.0.0.0:36422
18/07/31 04:53:59 INFO util.Utils: Successfully started service 'HTTP class server' on port 36422.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 1.5.1
      /_/

...
...
18/07/31 04:57:28 INFO session.SessionState: Created HDFS directory: /tmp/hive/root/6814e7a5-b896-49ac-bcd8-0b94e1a4b165/_tmp_space.db
18/07/31 04:57:30 INFO repl.SparkILoop: Created sql context (with Hive support)..
SQL context available as sqlContext.

scala> 
```
#### 1.3 执行scala程序
`sc.textFile("/spark/spark.txt").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).saveAsTextFile("/spark/out")`
```
sc是SparkContext对象，该对象是提交spark程序的入口
textFile("/spark/spark.txt")是hdfs中读取数据
flatMap(_.split(" "))先map再压平
map((_,1))将单词和1构成元组
reduceByKey(_+_)按照key进行reduce，并将value累加
saveAsTextFile("/spark/out")将结果写入到hdfs中
```
```
scala> sc.textFile("/spark/spark.txt").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).saveAsTextFile("/spark/out")
18/07/31 04:59:40 INFO storage.MemoryStore: ensureFreeSpace(57160) called with curMem=0, maxMem=560497950
18/07/31 04:59:40 INFO storage.MemoryStore: Block broadcast_0 stored as values in memory (estimated size 55.8 KB, free 534.5 MB)
18/07/31 04:59:44 INFO storage.MemoryStore: ensureFreeSpace(17347) called with curMem=57160, maxMem=560497950
18/07/31 04:59:44 INFO storage.MemoryStore: Block broadcast_0_piece0 stored as bytes in memory (estimated size 16.9 KB, free 534.5 MB)
18/07/31 04:59:44 INFO storage.BlockManagerInfo: Added broadcast_0_piece0 in memory on localhost:37951 (size: 16.9 KB, free: 534.5 MB)
18/07/31 04:59:44 INFO spark.SparkContext: Created broadcast 0 from textFile at <console>:22
18/07/31 04:59:49 INFO mapred.FileInputFormat: Total input paths to process : 1
18/07/31 04:59:53 INFO Configuration.deprecation: mapred.tip.id is deprecated. Instead, use mapreduce.task.id
18/07/31 04:59:53 INFO Configuration.deprecation: mapred.task.id is deprecated. Instead, use mapreduce.task.attempt.id
18/07/31 04:59:53 INFO Configuration.deprecation: mapred.task.is.map is deprecated. Instead, use mapreduce.task.ismap
18/07/31 04:59:53 INFO Configuration.deprecation: mapred.task.partition is deprecated. Instead, use mapreduce.task.partition
18/07/31 04:59:53 INFO Configuration.deprecation: mapred.job.id is deprecated. Instead, use mapreduce.job.id
18/07/31 05:00:51 INFO executor.Executor: Finished task 0.0 in stage 1.0 (TID 1). 1165 bytes result sent to driver
18/07/31 05:00:51 INFO scheduler.TaskSetManager: Finished task 0.0 in stage 1.0 (TID 1) in 4730 ms on localhost (1/1)
18/07/31 05:00:51 INFO scheduler.TaskSchedulerImpl: Removed TaskSet 1.0, whose tasks have all completed, from pool 
18/07/31 05:00:51 INFO scheduler.DAGScheduler: ResultStage 1 (saveAsTextFile at <console>:22) finished in 4.733 s
18/07/31 05:00:52 INFO scheduler.DAGScheduler: Job 0 finished: saveAsTextFile at <console>:22, took 15.399221 s
```
#### 1.4 查看执行结果
```shell
[root@s166 ~]# hadoop fs -cat /spark/out/p*
(simple,,1)
(nearly,1)
(For,1)
(back,2)
(this,4)
(under,1)
(it,2)
(means,1)
(introduced,1)
(revision,1)
(when,,1)
...
...
(To,1)
((which,1)
...
(prone.,1)
(an,2)
(time,,1)
(things,1)
(they’re,1)
...
(might,1)
(would,1)
(issue,,1)
(state,,2)
(Systems,1)
(System,1)
(write,1)
(being,1)
(programmers,1)
```

### 实现二：java 本地执行处理
>主要用于临时性的处理。

`pom.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fantj</groupId>
    <artifactId>bigdata</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>bigdata</name>
    <description>Demo project for Spring Boot</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!--<dependency>-->
            <!--<groupId>org.apache.hive</groupId>-->
            <!--<artifactId>hive-jdbc</artifactId>-->
            <!--<version>2.1.0</version>-->
        <!--</dependency>-->
        <!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common -->
        <!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common -->
        <dependency>
            <groupId>ch.cern.hadoop</groupId>
            <artifactId>hadoop-common</artifactId>
            <version>2.7.5.1</version>
            <classifier>sources</classifier>
            <type>java-source</type>
        </dependency>
        <dependency>
            <groupId>ai.h2o</groupId>
            <artifactId>sparkling-water-core_2.10</artifactId>
            <version>1.6.1</version>
            <type>pom</type>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!-- 去除内嵌tomcat -->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--添加servlet的依赖-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    <build>
        <finalName>wordcount</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>2.4</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.fantj.bigdata.WordCountCluster</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>


</project>
```
我用的springboot搭建的环境，所以pom中需要将springboot内置的tomcat移除，我们不需要容器来执行java脚本。最后打成jar包将main方法的路径告诉hadoop即可，不需要容器。然后就是导入hadoop spark的相关依赖。没maven基础的先学习maven。
`WordCountLocal`
```
package com.fantj.bigdata;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by Fant.J.
 */
public class WordCountLocal {
    public static void main(String[] args) {
        /**
         * 创建sparkConfig对象，设置spark应用配置信息,
            setMaster设置spark应用程序要连接的spark集群的master节点的url, 
            local则代表在本地运行
         */
        SparkConf conf = new SparkConf().setAppName("WordCountLocal").setMaster("local");
        /**
         * 创建JavaSparkContext 对象（最重要的对象）
         *
         */
        JavaSparkContext sc = new JavaSparkContext(conf);
        /**
         * 针对输入源（hdfs文件、本地文件等）创建一个初始的RDD,这里是本地测试，所以就针对本地文件
         * textFile（）方法用于根据文件类型的输入源创建RDD
         * RDD的概念：如果是hdfs或者本地文件，创建的RDD每个元素就相当于是文件里的一行。
         */
        JavaRDD<String> lines = sc.textFile("C:\\Users\\84407\\Desktop\\spark.txt");
        /**
         * 对初始RDD进行transformation 计算操作
         * 通常操作会通过创建function，并配合RDD的map、flatMap等算子来执行
         * function通常，如果比较简单，则创建指定function的匿名内部类
         * 如果function比较复杂，则会单独创建一个类，作为实现这个function接口的类
         * 现将每一行拆分成单个的单词
         * FlatMapFunction，有两个泛型阐述，分别代表了输入和输出类型
         * 这里只用FLatMap算子的作用，其实就是讲RDD的一个元素，给拆分成一个或多个元素。
         */
        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(String s) throws Exception {
                //分割
                return Arrays.asList(s.split(" "));
            }
        });
        /**
         * 接着，需要将每一个单词，映射为（单词，1）的这种格式来进行每个单词的出现次数的累加
         * mapTopair其实就是将每个元素以后干涉为一个（v1,v2）这样的Tuple2类型的元素
         * 如果还记得scala的tuple，那么没错，这里的tuple2就是scala类型，包含了两个值
         * mapToPair这个算子，要求的是与PairFunction配合使用，第一给泛型参数代表了 输入类型
         * 第二个和第三个泛型参数，代表的输出的Tuple2的第一给值和第二个值的类型
         * JavaPairRdd的两个泛型参数，分别代表了tuple元素的第一给值和第二个值的类型
         */
        JavaPairRDD<String,Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String,Integer>(word,1);
            }
        });
        /**
         * 然后需要以单词作为key，统计每个单词出现的次数
         * 这里要使用reduceBykey这个算子对每个key对应的value都进行reduce操作
         * 比如JavaPairRDD中有几个元素，假设分别为（hello，1）（hello，1）（hello，1）
         * reduce操作相当于是吧第一个值和第二个值进行计算，然后再讲结果与第三个至进行计算
         * 比如这里的helo，那么就相当于是，1+1=2，然后2+1=3
         * 最后返回的JavaPairRdd中的元素，也是tuple，但是第一个值就是每个key，第二个值就是key的value，也就是次数
         */
        JavaPairRDD<String,Integer> wordCounts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1+v2;
            }
        });
        /**
         * 我们已经统计出了单词的次数
         * 但是，之前我们使用的flatMap、mapToPair、reduceByKey这种操作，都叫做transformation操作
         * 一个Spark应用中，只有transformation操作是不行的，我用foreach来触发程序的执行
         */
        wordCounts.foreach(new VoidFunction<Tuple2<String, Integer>>() {
            @Override
            public void call(Tuple2<String, Integer> wordCount) throws Exception {
                System.out.println(wordCount._1 + "appeared "+ wordCount._2 );
            }
        });
    }
}
```
我们可以看到里面有很多的匿名内部类，我们可以用lambda将它代替，使代码更简洁。
```
    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("WordCountLocal").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("C:\\Users\\84407\\Desktop\\spark.txt");

        JavaRDD<String> words = lines.flatMap((FlatMapFunction<String, String>) s -> {
            //分割
            return Arrays.asList(s.split(" "));
        });

        JavaPairRDD<String, Integer> pairs = words.mapToPair((PairFunction<String, String, Integer>) word -> new Tuple2<>(word, 1));

        JavaPairRDD<String, Integer> wordCounts = pairs.reduceByKey((Integer v1, Integer v2) -> {
            return v1 + v2;
        });

        wordCounts.foreach((Tuple2<String, Integer> wordCount) -> {
            System.out.println(wordCount._1 + "appeared " + wordCount._2);
        });
    }
```
然后执行该main方法：
```
控制台打印：
Systemsappeared 1
examplesappeared 1
withappeared 2
inappeared 3
specificappeared 1
versionsappeared 1
recallappeared 1
copyappeared 2
Inappeared 1
VCSsappeared 1
controlled,appeared 1
Whatappeared 1
directory,appeared 1
Manyappeared 1
setappeared 1
loseappeared 1
...
...
systemappeared 1
Systemappeared 1
writeappeared 1
beingappeared 1
programmersappeared 1
```
### 实现三：集群执行
>最常用，主要可以针对HDFS上存储的大数据并进行离线批处理。

准备工作：
在这之前，需要将spark.txt文本上传到hdfs上。

##### 3.1 修改代码

如果要在集群上执行，需要修改两个地方的代码：
```
        SparkConf conf = new SparkConf().setAppName("WordCountCluster");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("hdfs://s166/spark/spark.txt");
```
setAppName和java类名相一致。然后把路径改成hdfs的文件路径。


##### 3.2 Maven打包
>需要将第二种实现方式的java项目打包成jar，然后放到集群中，通过脚本执行。

![](https://upload-images.jianshu.io/upload_images/5786888-8a7e824989c4da72.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-b6f0128187daa727.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.3 上传到集群
![](https://upload-images.jianshu.io/upload_images/5786888-20d9c815d6e3dd05.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.4 写执行脚本`wordcount.sh `
```
[root@s166 fantj]# cat wordcount.sh 

/home/fantj/spark/bin/spark-submit \
--class com.fantj.bigdata.WordCountCluster \
s--num-executors 1 \
--driver-memory 100m \
--executor-cores 1 \
/home/fantj/worldcount.jar \
```
##### 3.5 执行脚本
`./wordcount.sh `
```
[root@s166 fantj]# ./wordcount.sh 
18/07/31 09:43:49 INFO spark.SparkContext: Running Spark version 1.5.1
18/07/31 09:43:51 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
18/07/31 09:43:52 INFO spark.SecurityManager: Changing view acls to: root
18/07/31 09:43:52 INFO spark.SecurityManager: Changing modify acls to: root
18/07/31 09:43:52 INFO spark.SecurityManager: SecurityManager: authentication disabled; ui acls disabled; users with view permissions: Set(root); users with modify permissions: Set(root)
18/07/31 09:43:54 INFO slf4j.Slf4jLogger: Slf4jLogger started
18/07/31 09:43:54 INFO Remoting: Starting remoting
18/07/31 09:43:55 INFO util.Utils: Successfully started service 'sparkDriver' on port 41710.
18/07/31 09:43:55 INFO Remoting: Remoting started; listening on addresses :[akka.tcp://sparkDriver@192.168.27.166:41710]
18/07/31 09:43:55 INFO spark.SparkEnv: Registering MapOutputTracker
18/07/31 09:43:55 INFO spark.SparkEnv: Registering BlockManagerMaster
18/07/31 09:43:55 INFO storage.DiskBlockManager: Created local directory at /tmp/blockmgr-96c433c9-8f43-40fa-ba4f-1dc888608140
18/07/31 09:43:55 INFO storage.MemoryStore: MemoryStore started with capacity 52.2 MB
18/07/31 09:43:55 INFO spark.HttpFileServer: HTTP File server directory is /tmp/spark-5d613c5d-e9c3-416f-8d8b-d87bc5e03e02/httpd-3609b712-55f4-4140-9e05-2ecee834b18c
18/07/31 09:43:55 INFO spark.HttpServer: Starting HTTP Server
..
...
18/07/31 09:44:12 INFO storage.ShuffleBlockFetcherIterator: Started 0 remote fetches in 4 ms
simple,appeared 1
nearlyappeared 1
Forappeared 1
backappeared 2
thisappeared 4
underappeared 1
itappeared 2
meansappeared 1
introducedappeared 1
revisionappeared 1
when,appeared 1
previousappeared 2
realityappeared 1
typeappeared 1
developedappeared 1
Localappeared 1
simpleappeared 1
...
causingappeared 1
changesappeared 3
andappeared 5
designerappeared 1
approachappeared 1
modifiedappeared 1
systemappeared 1
Systemappeared 1
writeappeared 1
beingappeared 1
programmersappeared 1
18/07/31 09:44:12 INFO executor.Executor: Finished task 0.0 in stage 1.0 (TID 1). 1165 bytes result sent to driver
18/07/31 09:44:12 INFO scheduler.TaskSetManager: Finished task 0.0 in stage 1.0 (TID 1) in 200 ms on localhost (1/1)
18/07/31 09:44:12 INFO scheduler.TaskSchedulerImpl: Removed TaskSet 1.0, whose tasks have all completed, from pool 
18/07/31 09:44:12 INFO scheduler.DAGScheduler: ResultStage 1 (foreach at WordCountCluster.java:44) finished in 0.209 s
18/07/31 09:44:12 INFO scheduler.DAGScheduler: Job 0 finished: foreach at WordCountCluster.java:44, took 2.938418 s
18/07/31 09:44:12 INFO spark.SparkContext: Invoking stop() from shutdown hook
...
..
18/07/31 09:44:13 INFO remote.RemoteActorRefProvider$RemotingTerminator: Remoting shut down.
```
