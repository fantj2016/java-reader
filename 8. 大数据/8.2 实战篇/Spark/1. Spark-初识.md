>Apache Spark](https://spark.apache.org/)是一个围绕速度、易用性和复杂分析构建的大数据处理框架。最初在2009年由加州大学伯克利分校的AMPLab开发，并于2010年成为Apache的开源项目之一。

Spark是MapReduce的替代方案，而且兼容HDFS、Hive，可融入Hadoop的生态系统，以弥补MapReduce的不足。

### 1. 特性
参考：http://spark.apache.org/

1. Spark可以将Hadoop集群中的应用在内存中的运行速度提升100倍，甚至能够将应用在磁盘上的运行速度提升10倍。

![](https://user-gold-cdn.xitu.io/2018/8/17/16545e20d7232649?w=880&h=219&f=png&s=28857)

2. Spark让开发者可以快速的用Java、Scala或Python编写程序。它本身自带了一个超过80个高阶操作符集合。而且还可以用它在shell中以交互式地查询数据。

![](https://user-gold-cdn.xitu.io/2018/8/17/16545e20d762abfc?w=860&h=205&f=png&s=31113)

3. 除了Map和Reduce操作之外，它还支持SQL查询，流数据，机器学习和图表数据处理。开发者可以在一个数据管道用例中单独使用某一能力或者将这些能力结合在一起使用。

![](https://user-gold-cdn.xitu.io/2018/8/17/16545e20d74a11d2?w=899&h=205&f=png&s=41697)

4. 到处运行，可以跑在Hadoop、k8s或者单机、云服务，可以接受各种不同的数据。

![](https://user-gold-cdn.xitu.io/2018/8/17/16545e20d774a7d2?w=867&h=318&f=png&s=71732)

### 2. 内置库
#### 2.1 Spark SQL
>Spark SQL是Apache Spark用于处理结构化数据的模块。

##### 特性
1. 将SQL查询与Spark程序无缝混合。
```
results = spark.sql(
  "SELECT * FROM people")
names = results.map(lambda p: p.name)
```
2. 以相同方式连接到任何数据源。
```
spark.read.json("s3n://...")
  .registerTempTable("json")
results = spark.sql(
  """SELECT * 
     FROM people
     JOIN json ...""")
```
3. 在现有仓库上运行SQL或HiveQL查询。
4. 通过JDBC或ODBC连接。
5. Spark SQL包括基于成本的优化器，列式存储和代码生成，以快速进行查询。同时，它使用Spark引擎扩展到数千个节点和多小时查询，该引擎提供完整的中间查询容错。不要担心使用不同的引擎来获取历史数据。

#### 2.2 Spark Streaming
>Spark Streaming可以轻松构建可扩展的容错流应用程序。

###### 特性
1. 使用方便。通过API构建应用程序。
2. 容错（开箱即用）。可以恢复之前的工作和操作状态。
3. 将流式传输与批量和交互式查询相结合。


Spark Streaming可以从[HDFS](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsUserGuide.html)， [Flume](https://flume.apache.org/)，[Kafka](https://kafka.apache.org/)， [Twitter](https://dev.twitter.com/)和 [ZeroMQ](http://zeromq.org/)读取数据 。在生产中，Spark Streaming使用[ZooKeeper](https://zookeeper.apache.org/)和[HDFS](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsUserGuide.html)实现高可用性。


#### 2.3 Spark MLlib
>MLlib是Apache Spark的可扩展机器学习库。

##### 特性
1. 使用方便。可用于Java，Scala，Python和R.
2. 高质量算法，比MapReduce快100倍。
3. 随处运行，在Hadoop，Apache Mesos，Kubernetes，独立或云端，可以针对不同的数据源。

MLlib包含许多算法和实用程序。

###### ML算法包括：

**分类**：逻辑回归，朴素贝叶斯，......

**回归**：广义线性回归，生存回归，......

**决策树**，随机森林和梯度提升树

**建议**：交替最小二乘法（ALS）

**聚类**：K均值，高斯混合（GMM），......

**主题建模**：潜在Dirichlet分配（LDA）

**频繁项目集，关联规则和顺序模式挖掘**

###### ML工作流程工具包括：

**特征转换**：标准化，规范化，散列，......

ML管道施工

模型评估和超参数调整

**ML持久性**：保存和加载模型和管道

**其他工具包括**：

分布式线性代数：SVD，PCA，......

统计：汇总统计，假设检验，......

#### 2.4 Spark GraphX
>GraphX是Apache Spark用于图形和图形并行计算的API。

##### 特性
1. 灵活性：无缝地使用图形和集合。有效地使用RDD [转换](http://spark.apache.org/docs/latest/graphx-programming-guide.html#property-operators)和[连接](http://spark.apache.org/docs/latest/graphx-programming-guide.html#join-operators)图形，以及使用[Pregel API](http://spark.apache.org/docs/latest/graphx-programming-guide.html#pregel-api)编写自定义迭代图算法。

2. 速度：与最快的专业图形处理系统相比具有可比性。
3. 图形算法。
除了[高度灵活的API之外](http://spark.apache.org/docs/latest/graphx-programming-guide.html#graph-operators)，GraphX还提供了各种图形算法，其中许多都是用户提供。知名的算法有：
网页排名
连接组件
标签传播
SVD ++
强大的连接组件
三角计数
