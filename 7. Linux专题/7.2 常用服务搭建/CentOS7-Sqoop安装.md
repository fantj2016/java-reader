>sqoop是apache旗下一款“Hadoop和关系数据库服务器之间传送数据”的工具。



###1. 下载并解压

### 2. 修改配置文件
##### 2.1 进入`/sqoop/conf`目录
`mv sqoop-env-template.sh sqoop-env.sh`
```
export HADOOP_COMMON_HOME=/home/fantj/hadoop/ 
export HADOOP_MAPRED_HOME=/home/fantj/hadoop/
export HIVE_HOME=/home/fantj/hive
```

##### 2.2 配置`/etc/profile`

```
export SQOOP_HOME=/xxx/sqoop
export PATH=$PATH:$SQOOP_HOME/bin
```
### 3. 加入mysql的jdbc驱动包
`/hive/lib/mysql-connector-java-5.1.28.jar`

### 4. 验证环境
`sqoop-version`
```
[root@s166 ~]# sqoop-version
Sqoop 1.4.2
git commit id 
Compiled by ag on Tue Aug 14 18:38:15 IST 2012

```
