### 1. 本地创建文本文件
```
[root@s166 fantj]# mkdir input
[root@s166 fantj]# cd input/
[root@s166 input]# echo "hello fantj" > file1.txt
[root@s166 input]# echo "hello hadoop" > file2.txt
[root@s166 input]# echo "hello mapreduce" > file3.txt
[root@s166 input]# ls
file1.txt  file2.txt  file3.txt
```
### 2. 将文件上传到hadoop
```
[root@s166 input]# hadoop fs -mkdir /wordcount
[root@s166 input]# hadoop fs -ls /
Found 1 items
drwxr-xr-x   - root supergroup          0 2018-07-27 07:51 /wordcount
[root@s166 input]# cd ..
[root@s166 fantj]# ls
download  hadoop  input  jdk
[root@s166 fantj]# hadoop fs -put input/* /wordcount
[root@s166 fantj]# hadoop fs -ls /wordcount
Found 3 items
-rw-r--r--   3 root supergroup         12 2018-07-27 07:52 /wordcount/file1.txt
-rw-r--r--   3 root supergroup         13 2018-07-27 07:52 /wordcount/file2.txt
-rw-r--r--   3 root supergroup         16 2018-07-27 07:52 /wordcount/file3.txt
```
### 3. 启用hadoop自带单词统计进行处理
```
[root@s166 fantj]# cd /home/fantj/hadoop/share/hadoop/
[root@s166 hadoop]# ls
common  hdfs  httpfs  kms  mapreduce  tools  yarn
[root@s166 hadoop]# cd mapreduce/
[root@s166 mapreduce]# ls
hadoop-mapreduce-client-app-2.7.0.jar     hadoop-mapreduce-client-hs-plugins-2.7.0.jar       hadoop-mapreduce-examples-2.7.0.jar
hadoop-mapreduce-client-common-2.7.0.jar  hadoop-mapreduce-client-jobclient-2.7.0.jar        lib
hadoop-mapreduce-client-core-2.7.0.jar    hadoop-mapreduce-client-jobclient-2.7.0-tests.jar  lib-examples
hadoop-mapreduce-client-hs-2.7.0.jar      hadoop-mapreduce-client-shuffle-2.7.0.jar          sources
[root@s166 mapreduce]# hadoop jar hadoop-mapreduce-examples-2.7.0.jar wordcount /wordcount /print
```

### 查看结果
```
[root@s166 mapreduce]# hadoop fs -ls /print
Found 2 items
-rw-r--r--   3 root supergroup          0 2018-07-27 07:55 /print/_SUCCESS
-rw-r--r--   3 root supergroup         37 2018-07-27 07:55 /print/part-r-00000
[root@s166 mapreduce]# hadoop fs -cat /print/part-r-00000
fantj	1
hadoop	1
hello	3
mapreduce	1
```
