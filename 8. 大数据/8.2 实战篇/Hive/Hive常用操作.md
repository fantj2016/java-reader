### 1. 绑定数据


###### 1.1 创建表&创建文本文件
```
create table  fantj.t3(id int,name string,age int) row format delimited fields terminated by ','stored as textfile;
```
```shell
hive> create table  fantj.t3(id int,name string,age int) row format delimited fields terminated by ','stored as textfile;
OK
Time taken: 4.467 seconds
hive> select * from fantj.t3;
OK
Time taken: 2.82 seconds
```
表示行格式用逗号来分割字段。


###### 创建文本文件test.txt
我创建在`/home/fantj`目录下。
```
1,jiao,18
2,fantj,20
3,laowang,30
4,laotie,40
```

###### 1.2 从本地导入到hive
`LOAD DATA LOCAL INPATH '/home/fantj/test.txt' OVERWRITE INTO  TABLE  t3;`
```shell
hive> LOAD DATA LOCAL INPATH '/home/fantj/test.txt' OVERWRITE INTO TABLE fantj.t3;
Loading data to table fantj.t3
[Warning] could not update stats.
OK
Time taken: 26.334 seconds
```
`select * from fantj.t3;`
```shell
hive> select * from fantj.t3;
OK
1	jiao	18
2	fantj	20
3	laowang	30
4	laotie	40
Time taken: 2.303 seconds, Fetched: 4 row(s)
```
导入成功！

###### 1.3 从hdfs导入到hive

###### 先将test文件上传到hdfs中
`[root@s166 fantj]# hadoop fs -put test.txt /hdfs2hive`
```
-rw-r--r--   3 root supergroup         46    /hdfs2hive/test.txt
```
###### 进入hive，创建表t5
```
create table  fantj.t5(id int,name string,age int) row format delimited fields terminated by ','stored as textfile;
```
```
hive> create table  fantj.t5(id int,name string,age int) row format delimited fields terminated by ','stored as textfile;
OK
Time taken: 3.214 seconds

```
###### 执行导入

`LOAD DATA  INPATH '/hdfs2hive/test.txt' OVERWRITE INTO  TABLE  fantj.t5;`

```
hive> LOAD DATA INPATH '/hdfs2hive/test.txt' OVERWRITE INTO TABLE fantj.t5;
Loading data to table fantj.t5
[Warning] could not update stats.
OK
Time taken: 25.498 seconds
```

检查是否成功：
```
hive> select * from fantj.t5;
OK
1	jiao	18
2	fantj	20
3	laowang	30
4	laotie	40
Time taken: 5.046 seconds, Fetched: 4 row(s)
```







