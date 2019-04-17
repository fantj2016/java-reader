###1. 什么是RDB
  在第一章有讲过，redis是存储在内存中，并在硬盘里备份一份，硬盘里的数据就是rdb文件。
* 缺点：耗时，时间复杂度大；fork消耗内存；硬盘io性能影响；宕机save不成功导致数据丢失
###2. 命令
######1. 手动生成RDB
* save #生成rdb文件(同步命令，会造成redis阻塞)
* bgsave #生成rdb文件(异步命令，利用系统线程fork来处理)
######2. 自动生成RDB
  redis自动会在一个标准内(可根据需求选择更改)，自动调用bgsave生成RDB文件dump.rdb。标准图：
  |配置|seconds|changes|
  |:------:|:------:|:--------:|
  |save|900|1|
  |save|300|10|
  |save|60|10000|
###3. 快照配置

将DB保存到磁盘的规则定义（快照）
格式：save <seconds> <changes>
例子：save 900 1  //在900秒（15分钟）内如果至少有1个键值发生变化  就保存
            save 300 10  //在300秒（6分钟）内如果至少有10个键值发生变化  就保存  
save 900 1                      //每一条表示一个存盘点
save 300 10
save 60 10000

**stop-writes-on-bgsave-error yes**：如果启用如上的快照（RDB），在一个存盘点之后，可能磁盘会坏掉或者权限问题，redis将依然能正常工作
**rdbcompression yes**：是否将字符串用LZF压缩到.rdb 数据库中，如果想节省CPU资源可以将其设置成no，但是字符串存储在磁盘上占用空间会很大，默认是yes

**rdbchecksum yes**：rdb文件的校验，如果校验将避免文件格式坏掉，如果不校验将在每次操作文件时要付出校验过程的资源新能，将此参数设置为no，将跳过校验

**dbfilename dump.rdb**：转储数据的文件名

**dir ./data**：redis的工作目录，它会将转储文件存储到这个目录下，并生成一个附加文件
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ae05a78ebcd8fed7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###1. 什么是AOF
  只要往redis里写入一条命令，它就会把该日志写到aof文件里。
###2. 配置(三种写入策略和AOF重写)
#####1. AOF写入策略
1. always 策略： 只要往redis写入一条命令，就会把该命令写入缓冲区，然后慢慢的写入aof文件。（IO开销大）
2. **everysec策略(常用)**： 每秒一次fsync写入aof文件（可能会丢失一秒数据）
3. no策略： 不用管，系统决定什么时候写入aof文件。（不可控）
#####2. AOF重写
 >为什么要重写呢？我们说过AOF是记录每一条命令，那如果我set hello world 之后又set hello fant，AOF文件里还是会有两条记录，但是我们只需要知道最后一条有用记录即可。或者说多条单个命令合成一条批量命令。
通俗的说：重写是为了对原生aof日志进行精简优化。

######重写两种方式
1. bgrewriteaof 类似bgsave一样的机制。
2. AOF重写配置

  |配置名|含义|
  |:-----:|:-----:|
  |auto-aof-rewrite-percentage 100|AOF文件增长率|
  |auto-aof-rewrite-min-size 64mb|AOF文件重写需要的尺寸|
* 增长率和尺寸是什么意思呢？
尺寸：内容打到一定大小才触发aof重写
增长率：第一次100Mb触发，增长率是2，下次就是200Mb触发
* 那怎样获取当前配置尺寸(条件大小)呢？
有两个统计名：
1. aof_current_size : 当前尺寸
2. aof_base_size 上次启动和重写的尺寸

#####3. AOF配置详情
```
appendonly yes #改为yes，打开AOF
# 只增文件的文件名称。（默认是appendonly.aof）
appendfilename appendonly.aof
#AOF写入策略
appendfsync always
#目录
dir ./bigdiskpath
#是否允许丢失数据
no-appendfsync-on-rewrite yes
# AOF重写配置尺寸和增长率
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
```
###3. AOF实战
![image.png](http://upload-images.jianshu.io/upload_images/5786888-26e96212506166ac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后我们去/data目录看一下
![image.png](http://upload-images.jianshu.io/upload_images/5786888-fc9d65a7c959f805.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
cat appendonly.aof
![image.png](http://upload-images.jianshu.io/upload_images/5786888-06cb3a1acec13352.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看出来，world和fantj都存在，然而只有fantj是有效数据。然后我们手动重写aof(bgrewriteaof)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7b477cda0a44fda3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后再cat一下
![image.png](http://upload-images.jianshu.io/upload_images/5786888-1472dfe9ff32a3db.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##RDB与AOF对比区别与选择
|场景|RDB|AOF|
|:-----:|:-----:|:-----:|
|启动优先级|低(相对不全)|高(日志全)|
|体积|小(有压缩)|大(类日志)|
|恢复速度|快|慢|
|数据安全性|丢数据|策略决定|
|重量级|重|轻|

二者可以同时开启，一般也是同时开启，根据自己项目需求，选择最适合自己的策略。
