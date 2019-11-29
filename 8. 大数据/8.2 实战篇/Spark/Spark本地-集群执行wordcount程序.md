

###  在spark shell中编写WordCount程序
.

执行步骤
1. 将spark.txt文件上传到hdfs上 /usr/local  hadoop fs -put
2. 打包maven项目
3. 将打包后的jar包上传到机器
4. 编写 spark-submit 脚本
5. 执行脚本，提交spark应用到集群执行

hadoop fs -put spark.txt /spark.txt
http://spark1:50070  查看

】
如果要在集群上运行，则需要修改代码中的两处：
1. 将 setMaster() 方法删掉，他会自己去连接
2. 将对象是本地文件改成hdfs上的文件
 SparkConf conf = new SparkConf().setAppName("wordCountCluster");
JavaSparkContext sc = new JavaSparkContext(conf);
JavaRDD<String> lines = sc.textFile("hdfs://spark1:9000/spark.txt");
