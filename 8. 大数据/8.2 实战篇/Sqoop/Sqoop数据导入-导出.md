### 1. 从HDFS导出到RDBMS数据库

##### 1.1 准备工作
写一个文件
```
sqoop_export.txt

1201,laojiao, manager,50000, TP
1202,fantj,preader,50000,TP
1203,jiao,dev,30000,AC
1204,laowang,dev,30000,AC
1205,laodu,admin,20000,TP
1206,laop,grp des,20000,GR
```
上传到hdfs：
`hadoop fs -put sqoop_export.txt /sqoop/export/`

创建mysql数据库并增加授权:
```
create database sqoopdb;
grant all privileges on sqoopdb.* to 'sqoop'@'%' identified by 'sqoop';
grant all privileges on sqoopdb.* to 'sqoop'@'localhost' identified by 'sqoop';
grant all privileges on sqoopdb.* to 'sqoop'@'s166' identified by 'sqoop';
flush privileges;
```

创建表：
```
use sqoopdb;
CREATE TABLE employee ( 
   id INT NOT NULL PRIMARY KEY, 
   name VARCHAR(20), 
   deg VARCHAR(20),
   salary INT,
   dept VARCHAR(10));
```
##### 1.2 执行导出命令
```
bin/sqoop export \
--connect jdbc:mysql://s166:3306/sqoopdb \
--username sqoop \
--password sqoop \
--table employee \
--export-dir /sqoop/export/emp/ \
--input-fields-terminated-by ','
```
我执行的时候发现它总在报这个错：
```
 ERROR tool.ExportTool: Encountered IOException running export job: java.io.FileNotFoundException: File does not exist: hdfs://s166/home/fantj/sqoop/lib/avro-mapred-1.5.3.jar
```
然后找了很多解决方案：
1. 替换mysql-java的jar包，换个高版本的。
2. 修改hadoop的`mapred-site.xml`文件（先更名`mv mapred-site.xml.template mapred-site.xml`）
```
<configuration>
	<property>
		<name>mapreduce.framework.name</name>
		<value>yarn</value>
	</property>
</configuration>
```
解决后再执行：
```
	Map-Reduce Framework
		Map input records=6
		Map output records=6
		Input split bytes=107
		Spilled Records=0
		Failed Shuffles=0
		Merged Map outputs=0
		GC time elapsed (ms)=95
		CPU time spent (ms)=1210
		Physical memory (bytes) snapshot=97288192
		Virtual memory (bytes) snapshot=2075623424
		Total committed heap usage (bytes)=17006592
	File Input Format Counters 
		Bytes Read=0
	File Output Format Counters 
		Bytes Written=0
 22:34:37 INFO mapreduce.ExportJobBase: Transferred 274 bytes in 47.346 seconds (5.7872 bytes/sec)
 22:34:37 INFO mapreduce.ExportJobBase: Exported 6 records.
```
说明处理成功！
##### 1.3 验证mysql表
```
mysql> select * from employee;
+------+---------+----------+--------+------+
| id   | name    | deg      | salary | dept |
+------+---------+----------+--------+------+
| 1201 | laojiao |  manager |  50000 | TP   |
| 1202 | fantj   | preader  |  50000 | TP   |
| 1203 | jiao    | dev      |  30000 | AC   |
| 1204 | laowang | dev      |  30000 | AC   |
| 1205 | laodu   | admin    |  20000 | TP   |
| 1206 | laop    | grp des  |  20000 | GR   |
+------+---------+----------+--------+------+
6 rows in set (0.07 sec)
```
### 2. 导入表表数据到HDFS
```
bin/sqoop import \
--connect jdbc:mysql://s166:3306/sqoopdb \
--username sqoop \
--password sqoop \
--table employee --m 1
```

```shell
22:44:26 INFO mapreduce.Job: The url to track the job: http://s166:8088/proxy/application_1532679575794_0002/

	File System Counters
		FILE: Number of bytes read=0
		FILE: Number of bytes written=123111
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=87
		HDFS: Number of bytes written=161
		HDFS: Number of read operations=4
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Other local map tasks=1
		Total time spent by all maps in occupied slots (ms)=5972
		Total time spent by all reduces in occupied slots (ms)=0
		Total time spent by all map tasks (ms)=5972
		Total vcore-seconds taken by all map tasks=5972
		Total megabyte-seconds taken by all map tasks=6115328
	Map-Reduce Framework
		Map input records=6
		Map output records=6
		Input split bytes=87
		Spilled Records=0
		Failed Shuffles=0
		Merged Map outputs=0
		GC time elapsed (ms)=195
		CPU time spent (ms)=970
		Physical memory (bytes) snapshot=99921920
		Virtual memory (bytes) snapshot=2079825920
		Total committed heap usage (bytes)=18358272
	File Input Format Counters 
		Bytes Read=0
	File Output Format Counters 
		Bytes Written=161
 22:44:57 INFO mapreduce.ImportJobBase: Transferred 161 bytes in 34.5879 seconds (4.6548 bytes/sec)
 22:44:57 INFO mapreduce.ImportJobBase: Retrieved 6 records.
```

### 3. 导入关系表到HIVE
```shell
sqoop import --connect jdbc:mysql://s166:3306/sqoopdb --username sqoop --password sqoop --table employee --hive-import --m 1
```
### 4. 导入到HDFS指定目录
```
sqoop import \
--connect jdbc:mysql://s166:3306/sqoopdb \
--username sqoop \
--password sqoop \
--target-dir /queryresult \
--table employee --m 1
```

### 5. 导入表数据子集
>我们可以导入表的使用Sqoop导入工具，"where"子句的一个子集。并将结果存储在HDFS的目标目录。

```
sqoop import \
--connect jdbc:mysql://s166:3306/sqoopdb \
--username sqoop \
--password sqoop \
--where "salary>10000" \
--target-dir /wherequery \
--table employee --m 1
```

