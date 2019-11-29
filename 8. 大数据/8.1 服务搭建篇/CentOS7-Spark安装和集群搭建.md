
### 准备工作
首先得安装scala：[CentOS7.x 安装scala](https://www.jianshu.com/p/1995f34d0054)

### 下载解压

### 配置

##### 1. 配置环境变量
`/etc/profile`
```
export SPARK_HOME=/home/fantj/spark
export PATH=$PATH:$SPARK_HOME/bin
export CLASSPAHT=.:$CLASSPATH:$JAVA_HOME/lib:$JAVA_HOME/jre/lib
```
##### 2. 配置`/conf/spark-env.sh`
` cp spark-env.sh.template spark-env.sh`

给尾部添加环境变量：
```
export JAVA_HOME=/home/fantj/jdk
export SCALA_HOME=/home/fantj/scala
export SPARK_MASTER_IP=s166
export SPARK_WORKER_MEMORY=1g
export HADOOP_CONF_DIR=/home/fantj/hadoop/etc/hadoop
```
##### 3. 配置`/conf/slaves.conf`
`cp slaves.template  slaves.conf`

新添数据：
```
spark2
spark3
spark4
```
### 同步配置到slave节点
将spark和scala 和配置文件拷贝到每个slave节点。
```
 1099  scp -r scala-2.11.7 spark-1.5.1-bin-hadoop2.4/ s168:/home/fantj/download/
 1100  scp -r scala-2.11.7 spark-1.5.1-bin-hadoop2.4/ s169:/home/fantj/download/

 1135  scp /etc/profile s167:/etc/profile
 1136  scp /etc/profile s168:/etc/profile
 1137  scp /etc/profile s169:/etc/profile
```
### 启动spark
1. 首先得启动hadoop或者只启动hdfs。`start-dfs.sh`命令。

2. jps查看并确保主从机的hadoop的dfs都启动后。（主：NameNode，从：DataNode）

3. 在`spark`的根目录下执行`./sbin/start-all.sh`，如果想要slave节点也跟着启动，需要做免密码登录。没有做的话可以用相同的命令一个一个节点去启动。

```
[root@s166 spark]# ./sbin/start-all.sh 
starting org.apache.spark.deploy.master.Master, logging to /home/fantj/download/spark-1.5.1-bin-hadoop2.4/sbin/../logs/spark-root-org.apache.spark.deploy.master.Master-1-s166.out
localhost: starting org.apache.spark.deploy.worker.Worker, logging to /home/fantj/download/spark-1.5.1-bin-hadoop2.4/sbin/../logs/spark-root-org.apache.spark.deploy.worker.Worker-1-s166.out
localhost: starting org.apache.spark.deploy.worker.Worker, logging to /home/fantj/download/spark-1.5.1-bin-hadoop2.4/sbin/../logs/spark-root-org.apache.spark.deploy.worker.Worker-1-s167.out
localhost: starting org.apache.spark.deploy.worker.Worker, logging to /home/fantj/download/spark-1.5.1-bin-hadoop2.4/sbin/../logs/spark-root-org.apache.spark.deploy.worker.Worker-1-s168.out
localhost: starting org.apache.spark.deploy.worker.Worker, logging to /home/fantj/download/spark-1.5.1-bin-hadoop2.4/sbin/../logs/spark-root-org.apache.spark.deploy.worker.Worker-1-s169.out
```
4. 再查看jps
```
-------s166 jps -------
1397 NameNode
52854 Worker
1559 SecondaryNameNode
53671 Jps
52719 Master
-------s167 jps -------
1764 DataNode
29092 Jps
28414 Worker
-------s168 jps -------
33921 Worker
1756 DataNode
34063 Jps
-------s169 jps -------
27384 Jps
1754 DataNode
27242 Worker
```
可以看到，一个`Master`三个`Worker`。
然后再访问主节点ip的8080端口。

![](https://upload-images.jianshu.io/upload_images/5786888-98af72ef76d6462c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 打开Spark-shell
```
[root@s166 bin]# spark-shell 
18/07/30 12:34:16 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
18/07/30 12:34:20 INFO spark.SecurityManager: Changing view acls to: root
18/07/30 12:34:20 INFO spark.SecurityManager: Changing modify acls to: root
18/07/30 12:34:20 INFO spark.SecurityManager: SecurityManager: authentication disabled; ui acls disabled; users with view permissions: Set(root); users with modify permissions: Set(root)
18/07/30 12:34:22 INFO spark.HttpServer: Starting HTTP Server
18/07/30 12:34:23 INFO server.Server: jetty-8.y.z-SNAPSHOT
18/07/30 12:34:23 INFO server.AbstractConnector: Started SocketConnector@0.0.0.0:35005
18/07/30 12:34:23 INFO util.Utils: Successfully started service 'HTTP class server' on port 35005.
...
...
18/07/30 12:38:39 INFO session.SessionState: Created local directory: /tmp/2c350bb0-1297-40d8-a9bd-47446b116bf3_resources
18/07/30 12:38:39 INFO session.SessionState: Created HDFS directory: /tmp/hive/root/2c350bb0-1297-40d8-a9bd-47446b116bf3
18/07/30 12:38:39 INFO session.SessionState: Created local directory: /tmp/root/2c350bb0-1297-40d8-a9bd-47446b116bf3
18/07/30 12:38:40 INFO session.SessionState: Created HDFS directory: /tmp/hive/root/2c350bb0-1297-40d8-a9bd-47446b116bf3/_tmp_space.db
18/07/30 12:38:40 INFO repl.SparkILoop: Created sql context (with Hive support)..
SQL context available as sqlContext.

scala> 
```
这就证明开启成功了，同理访问`4040`端口。

![](https://upload-images.jianshu.io/upload_images/5786888-d662e1f314e4ba0d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
