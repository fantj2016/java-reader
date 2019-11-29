>有时候想了想Flume框架的原理，其实也是对文件或者文件夹进行监控，那我自己也可以写一个脚本来简单的实现监控我们想监控的文件，然后对其进行上传。

### 1. 引入环境变量
```
#!/bin/bash

#set java env
export JAVA_HOME=/home/fantj/jdk/
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH

#set hadoop env
export HADOOP_HOME=/home/fantj/hadoop/
export PATH=${HADOOP_HOME}/bin:${HADOOP_HOME}/sbin:$PATH
```
### 2. 声明路径变量
```
#日志文件存放的目录
log_src_dir=/home/fantj/log/

#待上传文件存放的目录
log_toupload_dir=/home/fantj/toupload/


#日志文件上传到hdfs的根路径
hdfs_root_dir=/data/log/
```

### 3. 扫描文件
```
ls $log_src_dir | while read fileName
do
        if [[ "$fileName" == access.log ]]; then
                date=`date +%Y_%m_%d_%H_%M_%S`
                #将文件移动到待上传目录并重命名
                #打印信息
                echo "moving $log_src_dir$fileName to $log_toupload_dir"fantj_log_$fileName"$date"
                mv $log_src_dir$fileName $log_toupload_dir"fantj_log_$fileName"$date
                #将待上传的文件path写入一个列表文件willDoing
                echo $log_toupload_dir"fantj_log_$fileName"$date >> $log_toupload_dir"willDoing."$date
        fi

done
```
把已经扫描到的日志文件重命名，然后mv到待上传目录，然后打印日志，并对mv成功的日志文件加上willDoing做未完成上传标记。

### 4. 开始上传
```
#找到列表文件willDoing
ls $log_toupload_dir | grep will |grep -v "_COPY_" | grep -v "_DONE_" | while read line
do
        #打印信息
        echo "toupload is in file:"$line
        #将待上传文件列表willDoing改名为willDoing_COPY_
        mv $log_toupload_dir$line $log_toupload_dir$line"_COPY_"
        #读列表文件willDoing_COPY_的内容（一个一个的待上传文件名）  ,此处的line 就是列表中的一个待上传文件的path
        cat $log_toupload_dir$line"_COPY_" |while read line
        do
                #打印信息
                echo "puting...$line to hdfs path.....$hdfs_root_dir"
                hadoop fs -put $line $hdfs_root_dir
        done
        mv $log_toupload_dir$line"_COPY_"  $log_toupload_dir$line"_DONE_"
done
```

### 5. 测试
我在`/home/fantj/log`目录下放一个`access.log`文件，然后执行脚本。
```
[root@s166 fantj]# ./upload2hdfs.sh

envs: hadoop_home: /home/fantj/hadoop/
log_src_dir:/home/fantj/log/
moving /home/fantj/log/access.log to /home/fantj/toupload/fantj_log_access.log2018_07_29_12_49_03
toupload is in file:willDoing.2018_07_29_12_49_03
puting.../home/fantj/toupload/fantj_log_access.log2018_07_29_12_49_03 to hdfs path...../data/log/
```
```
[root@s166 fantj]# hadoop fs -ls -R /data/
drwxr-xr-x   - root supergroup          0 2018-07-29 00:49 /data/log
-rw-r--r--   3 root supergroup      14340 2018-07-29 00:49 /data/log/fantj_log_access.log2018_07_29_12_49_03
-rw-r--r--   3 root supergroup      14340 2018-07-28 13:53 
```
