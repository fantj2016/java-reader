>flume是分布式的日志收集系统，它将各个服务器中的数据收集起来并送到指定的地方去，可以是文件、可以是hdfs。

### 安装
```
tar -zxvf apache-flume-1.6.0-bin.tar.gz
```
### 配置环境变量
```
export FLUME_HOME=/xxx/flume
export PATH=$PATH:$FLUME_HOME/bin
```
###### 修改conf下的flume-env.sh,在里面配置JAVA_HOME
### 验证
```
flume-ng  version


[root@s166 log]# flume-ng version
Flume 1.6.0
Source code repository: https://git-wip-us.apache.org/repos/asf/flume.git
Revision: 2561a23240a71ba20bf288c7c2cda88f443c2080
Compiled by hshreedharan on Mon May 11 11:15:44 PDT 2015
From source with checksum b29e416802ce9ece3269d34233baf43f
```
好了，到这里我们环境就配置好了。

### 实例1：监听一个指定的网络端口
##### 1.1 配置文件
```
flume官网中NetCat Source描述：

Property Name Default     Description
channels       –     
type           –     The component type name, needs to be netcat
bind           –  日志需要发送到的主机名或者Ip地址，该主机运行着netcat类型的source在监听          
port           –  日志需要发送到的端口号，该端口号要有netcat类型的source在监听   
```
然后在`flume/conf`目录下创建一个配置文件`netcat-logger.conf`
```
# 定义这个agent中各组件的名字
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# 描述和配置source组件：r1
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

# 描述和配置sink组件：k1
a1.sinks.k1.type = logger

# 描述和配置channel组件，此处使用是内存缓存的方式
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# 描述和配置source  channel   sink之间的连接关系
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```
表示的是监听44444端口
###### 1.2 启动收集
`flume-ng agent -c conf -f conf/netcat-logger.conf -n a1  -Dflume.root.logger=INFO,console`
```
-c conf   指定flume自身的配置文件所在目录
-f conf/netcat-logger.con  指定我们所描述的采集方案
-n a1  指定我们这个agent的名字
```
###### 1.3 测试
在另一个终端上执行`nc localhost 44444`（没有nc的yum install nmap-ncat.x86_64,如果没有该包，请更新成阿里yum源:[Redhat7.x 修改阿里云yum源](https://www.jianshu.com/p/fd00c304ae5d)
）
```
[root@s166 log]# nc localhost 44444
hello
OK
fantj
OK
```
然后看flume服务端的响应：
```
 18:48:48 INFO sink.LoggerSink: Event: { headers:{} body: 68 65 6C 6C 6F                                  hello }
 18:48:49 INFO sink.LoggerSink: Event: { headers:{} body: 66 61 6E 74 6A                                  fantj }
```

### 实例2. 监听一个指定的目录，每当有新文件出现，就需要把文件采集到HDFS中去
```
sources.type:  spooldir
sinks.type: hdfs
```
##### 2.1 配置文件
在`flume/conf`目录下创建一个配置文件`spooldir.conf`
```
#定义三大组件的名称
agent1.sources = source1
agent1.sinks = sink1
agent1.channels = channel1

# 配置source组件(监听的文件不能重复)

agent1.sources.source1.type = spooldir
agent1.sources.source1.spoolDir = /home/fantj/log/
agent1.sources.source1.fileHeader = false

#配置拦截器
agent1.sources.source1.interceptors = i1
agent1.sources.source1.interceptors.i1.type = host
agent1.sources.source1.interceptors.i1.hostHeader = hostname

# 配置sink组件
agent1.sinks.sink1.type = hdfs
agent1.sinks.sink1.hdfs.path =hdfs://s166/weblog/flume-collection/%y-%m-%d/
agent1.sinks.sink1.hdfs.filePrefix = access_log
agent1.sinks.sink1.hdfs.maxOpenFiles = 5000
agent1.sinks.sink1.hdfs.batchSize= 100
agent1.sinks.sink1.hdfs.fileType = DataStream
agent1.sinks.sink1.hdfs.writeFormat =Text
agent1.sinks.sink1.hdfs.rollSize = 102400
agent1.sinks.sink1.hdfs.rollCount = 1000000
agent1.sinks.sink1.hdfs.rollInterval = 60
#agent1.sinks.sink1.hdfs.round = true
#agent1.sinks.sink1.hdfs.roundValue = 10
#agent1.sinks.sink1.hdfs.roundUnit = minute
agent1.sinks.sink1.hdfs.useLocalTimeStamp = true
# Use a channel which buffers events in memory
agent1.channels.channel1.type = memory
agent1.channels.channel1.keep-alive = 120
agent1.channels.channel1.capacity = 500000
agent1.channels.channel1.transactionCapacity = 600

# Bind the source and sink to the channel
agent1.sources.source1.channels = channel1
agent1.sinks.sink1.channel = channel1
```
大概意思是：监听`/home/fantj/log/`这个文件，并把它上传到`hdfs://s166/weblog/flume-collection/%y-%m-%d/`这个路径下。

##### 2.2 启用收集

`flume-ng agent -c conf -f ../conf/spoordir.conf -n agent1  -Dflume.root.logger=INFO,console`
##### 2.3  测试
我在`/home/fantj/log`目录下创建一个文本文件。

```
test.txt

this is a spoordir agent test
```
然后看flume服务端响应:
```shell
 19:00:24 INFO avro.ReliableSpoolingFileEventReader: Preparing to move file /home/fantj/log/test.txt to /home/fantj/log/test.txt.COMPLETED
 19:00:24 INFO hdfs.HDFSDataStream: Serializer = TEXT, UseRawLocalFileSystem = false
 19:00:24 INFO hdfs.BucketWriter: Creating hdfs://s166/weblog/flume-collection/18-07-27//access_log.1532732424184.tmp
```
上传完成后，它会给这个文件加个后缀变成`test.txt.COMPLETED`来表示成功。

我们打开hadoop的管理页:http://192.168.27.166:50070
![](https://upload-images.jianshu.io/upload_images/5786888-d8414a27cd1be075.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-78049fadb9c90c3b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![打开文件查看](https://upload-images.jianshu.io/upload_images/5786888-3efbfa43077ffe33.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 实例3：监听一个指定的文件，每当有新更改，就需要把文件采集到HDFS中去
```
sources.type: exec
sink.type: hdfs 
```
##### 3.1 配置文件
同样，我们创建`exec.conf`文件
```
agent1.sources = source1
agent1.sinks = sink1
agent1.channels = channel1

# Describe/configure tail -F source1
agent1.sources.source1.type = exec
agent1.sources.source1.command = tail -F /home/fantj/log/web_log.log
agent1.sources.source1.channels = channel1

#configure host for source
agent1.sources.source1.interceptors = i1
agent1.sources.source1.interceptors.i1.type = host
agent1.sources.source1.interceptors.i1.hostHeader = hostname

# Describe sink1
agent1.sinks.sink1.type = hdfs
#a1.sinks.k1.channel = c1
agent1.sinks.sink1.hdfs.path =hdfs://s166/weblog/flume/%y-%m-%d/
agent1.sinks.sink1.hdfs.filePrefix = access_log
agent1.sinks.sink1.hdfs.maxOpenFiles = 5000
agent1.sinks.sink1.hdfs.batchSize= 100
agent1.sinks.sink1.hdfs.fileType = DataStream
agent1.sinks.sink1.hdfs.writeFormat =Text
agent1.sinks.sink1.hdfs.rollSize = 102400
agent1.sinks.sink1.hdfs.rollCount = 1000000
agent1.sinks.sink1.hdfs.rollInterval = 60
#agent1.sinks.sink1.hdfs.round = true
#agent1.sinks.sink1.hdfs.roundValue = 10
#agent1.sinks.sink1.hdfs.roundUnit = minute
agent1.sinks.sink1.hdfs.useLocalTimeStamp = true

# Use a channel which buffers events in memory
agent1.channels.channel1.type = memory
agent1.channels.channel1.keep-alive = 120
agent1.channels.channel1.capacity = 500000
agent1.channels.channel1.transactionCapacity = 600

# Bind the source and sink to the channel
agent1.sources.source1.channels = channel1
agent1.sinks.sink1.channel = channel1
```
监听`/home/fantj/log/web_log.log`这个文件，上传到`hdfs://s166/weblog/flume/%y-%m-%d/`

##### 3.2 启动
`flume-ng agent -c conf -f ../conf/exec.conf -n agent1  -Dflume.root.logger=INFO,console`
##### 3.3 测试
我在这个文件里新添：
```
test
test
test
```
然后看flume服务端的响应：
```shell
19:15:54 INFO hdfs.BucketWriter: Creating hdfs://s166/weblog/flume/18-07-27//access_log.1532733353751.tmp
19:16:56 INFO hdfs.BucketWriter: Closing hdfs://s166/weblog/flume/18-07-27//access_log.1532733353751.tmp
19:16:56 INFO hdfs.BucketWriter: Renaming hdfs://s166/weblog/flume/18-07-27/access_log.1532733353751.tmp to hdfs://s166/weblog/flume/18-07-27/access_log.1532733353751
```
Creating(刚启动日志) ->Closing ->Renaming (修改文件后日志)
同理我把文件下载下来打开：
![](https://upload-images.jianshu.io/upload_images/5786888-dd8808565e9dfd81.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
