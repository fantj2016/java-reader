### 简介
>ZooKeeper是一个分布式的，开放源码的分布式应用程序协调服务，是Google的Chubby一个开源的实现，是Hadoop和Hbase的重要组件。它是一个为分布式应用提供一致性服务的软件，提供的功能包括：配置维护、域名服务、分布式同步、组服务等。
1. 中间件，提供协调服务
2. 作用于分布式系统
3. 支持java，提供java和c语言的客户端api

### 特性
1. 一致性：数据一致性
2. 原子性：要么成功要么都失败。
3. 单一试图：客户端链接集群中的任意zk节点，数据都一致
4. 可靠性：每次对zk的操作状态都回保存在服务端
5. 实时性：客户端可以读取到zk服务端的最新数据


### 单机安装

#### 目录结构
![](https://upload-images.jianshu.io/upload_images/5786888-30439c11210644fe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
contrib：附加功能

dist-maven:maven编译后产生的目录

recipes:demo案例

lib:依赖的jar包


#### 配置zoo.cfg
```
# 用于计算的时间单元。
tickTime=2000
# The number of ticks that the initial 
# 用于集群，允许从节点连接并同步到master节点的初始化连接时间，以tickTime的倍数来表示
initLimit=10
# 用于集群，主从节点通讯请求和应答时间(心跳机制)
syncLimit=5
# 数据目录
dataDir=/tmp/zookeeper
# 客户端端口(用户客户端连接)
clientPort=2181
                         
```
#### 启动
`./zkServer.sh start`

### 基本数据模型
1. 类似linux的文件目录
2. 每一个节点都称为znode，可以有子节点，也可以有数据
3. 每个节点分为临时节点和永久节点，临时节点在客户端断开后消失
4. 每个zk节点都有各自的版本号，可以通过命令行来显示节点信息
5. 每当节点数据发生变化，那么改节点的版本号会累加(乐观锁)
6. 删除/修改过时节点，版本号不匹配则会报错
7. 每个zk节点存储的数据不宜过大，几k即可(官方推荐)
8. 节点可以设置权限ACL，可以通过权限来限制用户的访问

### 数据模型基本操作
1. 客户端连接
```
./zkCli.sh 
WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] help
ZooKeeper -server host:port cmd args
	stat path [watch]
	set path data [version]
	ls path [watch]
	delquota [-n|-b] path
	ls2 path [watch]
	setAcl path acl
	setquota -n|-b val path
	history 
	redo cmdno
	printwatches on|off
	delete path [version]
	sync path
	listquota path
	rmr path
	get path [watch]
	create [-s] [-e] path data acl
	addauth scheme auth
	quit 
	getAcl path
	close 
	connect host:port
[zk: localhost:2181(CONNECTED) 1] ls
[zk: localhost:2181(CONNECTED) 2] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 3] ls /zookeeper
[quota]
[zk: localhost:2181(CONNECTED) 4] ls /zookeeper/quota
[]

```
2. 查看znode结构
3. 关闭客户端


### ZK作用
1. master节点选举(首脑模式)
2. 统一配置文件管理，只需要部署一台服务器，就可以同步更新到其他服务器。
3. 发布和订阅。类似MQ，dubbo发布者把数据存在znode上，订阅者来读取。
4. 提供分布式锁。
5. 集群管理，保证数据强一致性。

### Session基本原理
1. C/S间存在会话
2. 每个会话都可以设置超时时间
3. 心跳结束，session就过期
4. session过期，临时节点znode就会被抛弃
5. 心跳机制
### 常用操作

1. `zkCli.sh` 进入后台
2. `ls /` 查看目录(节点)信息
3. `ls2 /`
ls2：查看节点状态信息
3. `stat /`查看节点状态信息
4. `get /`当前目录(节点)数据展示出来
```
cZxid = 0x0    #zk为节点分配的id
ctime = Thu Jan 01 08:00:00 CST 1970 #节点创建时间
mZxid = 0x0  # 修改后的id
mtime = Thu Jan 01 08:00:00 CST 1970  #修改后的时间
pZxid = 0x0 #子节点id
cversion = -1 #子节点版本
dataVersion = 0  # 当前数据版本号，修改数据会自增
aclVersion = 0 # 权限版本号
ephemeralOwner = 0x0  #节点类型  0x0为持久节点，0x16903bd7d100000为临时节点
dataLength = 0  # 数据长度
numChildren = 1  # 子节点个数
```
#### znode常用命令
##### 创建节点
5. `create [-s] [-e临时节点] path data acl`
```
[zk: localhost:2181(CONNECTED) 6] create /fantj fantj-data
Created /fantj
[zk: localhost:2181(CONNECTED) 7] get /fantj
fantj-data
cZxid = 0x2
ctime = Tue Feb 19 11:31:14 CST 2019
mZxid = 0x2
mtime = Tue Feb 19 11:31:14 CST 2019
pZxid = 0x2
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 10
numChildren = 0
```
6. `create -e /fantj/temp fantj-data` 创建临时节点
7. `create 命令后面加seq`创建顺序节点
##### 修改节点
8. `set path data [version]` 设置节点的数据值
```
[zk: localhost:2181(CONNECTED) 12] set /fantj 
[zk: localhost:2181(CONNECTED) 13] get /fantj
new-data
cZxid = 0x2
ctime = Tue Feb 19 11:31:14 CST 2019
mZxid = 0x4
mtime = Tue Feb 19 11:38:55 CST 2019
pZxid = 0x3
cversion = 1
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 8
numChildren = 1
```
注意此时我的fantj节点数据版本号是1，如果我`set /fantj new-new-data 1`是可以调用成功的，如果将1变成0或者2等就会报错，这就是乐观锁。
##### 删除节点
9. `delete path [version]`
```
[zk: localhost:2181(CONNECTED) 20] ls /fantj
[node, temp]
[zk: localhost:2181(CONNECTED) 21] get /fantj/node   
seq
cZxid = 0x5
ctime = Tue Feb 19 11:43:51 CST 2019
mZxid = 0x5
mtime = Tue Feb 19 11:43:51 CST 2019
pZxid = 0x5
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 22] delete /fantj/node 1
version No is not valid : /fantj/node
[zk: localhost:2181(CONNECTED) 23] delete /fantj/node 0
[zk: localhost:2181(CONNECTED) 24] 
```
注意删除的时候如果带版本号删除，则版本号需要对应才能删除。也可以不带版本号直接删除。

### watcher机制
>针对每个节点操作，都会有一个监督者wathcer，当监控的某个对象(znode)发生变化，则出发watcher事件。触发是一次性，触发后即销毁。

父子节点的增删改都能触发watcher

#### 创建watcher:NodeCreated
1. `stat path watcher` 声明watcher
```
[zk: localhost:2181(CONNECTED) 28] stat /fantj-watch watch
Node does not exist: /fantj-watch
[zk: localhost:2181(CONNECTED) 29] create /fantj-watch fantj-watch-data

WATCHER::

WatchedEvent state:SyncConnected type:NodeCreated path:/fantj-watch
Created /fantj-watch

```
#### 修改watcher：NodeDataChanged
2. `get /fantj-watch watch`
```
[zk: localhost:2181(CONNECTED) 30] get /fantj-watch watch
[zk: localhost:2181(CONNECTED) 33] set /fantj-watch new-data  

WATCHER::

WatchedEvent state:SyncConnected type:NodeDataChanged path:/fantj-watch

```
#### 删除wathcer：NodeDeleted
3. 先设置(get)后删除(del)watcher
```
[zk: localhost:2181(CONNECTED) 35] get /fantj-watch watch   # 设置wathcer
new-data
cZxid = 0xa
ctime = Tue Feb 19 12:04:06 CST 2019
mZxid = 0xc
mtime = Tue Feb 19 12:06:43 CST 2019
pZxid = 0xa
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 8
numChildren = 0
[zk: localhost:2181(CONNECTED) 36] delete /fantj-watch

WATCHER::

WatchedEvent state:SyncConnected type:NodeDeleted path:/fantj-watch
```
只示例了父节点的操作，子节点操作类似。

#### ls为父节点设置watcher，子节点触发
创建和删除子节点触发：
1. `ls /fantj watch`
2. `crate /fantj/xyz 666`
3. `ls /fantj watch`
4. `delete /fantj/xyz`

#### 修改子节点触发：
>修改子节点watcher事件要用`get /fantj/xyz watch`来声明watcher，用`ls`声明不会触发。

1. `get /fantj/xyz watch`
2. `set /fantj/xyz 888`

### watcher使用场景
1. 统一资源配置


### ACL权限控制
1. 针对节点可以设置相关读写等权限，目的为了保障数据安全性
2. 指定不同的权限范围以及角色

#### scheme
1. world: world下只有一个id，就是anyone
2. auth: 代表认证登录，需要注册用户有权限就可以
3. digest: 需要对密码加密才能访问
4. ip: 限制ip进行访问
5. super: 超级管理员，拥有所有权限
#### 权限字母标识
>crdwa
1. c: create 创建子节点
2. r: read 获取节点/子节点
3. w: write 设置节点数据
4. d: delete 删除子节点
5. a: admin 设置权限
#### 基本命令
1. getAcl 获取某个节点的acl权限信息
```
[zk: localhost:2181(CONNECTED) 5] getAcl /fantj/a
'world,'anyone
: cdrwa

```
2. setAcl 设置某个节点的acl权限信息
3. addauth 认证授权信息

#### ACL命令
1. 默认创建节点的权限
```
[zk: localhost:2181(CONNECTED) 4] create /fantj/a  aaa
Created /fantj/a
[zk: localhost:2181(CONNECTED) 5] getAcl /fantj/a
'world,'anyone
: cdrwa

```
2. 修改节点权限
>我们取消它的删除(d)权限.注意：凡是在该节点下创建的子节点，都会继承父节点的权限。
```
[zk: localhost:2181(CONNECTED) 6] setAcl /fantj/a world:anyone:crwa
cZxid = 0x11
ctime = Tue Feb 19 12:39:52 CST 2019
mZxid = 0x11
mtime = Tue Feb 19 12:39:52 CST 2019
pZxid = 0x11
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 7] getAcl /fantj/a
'world,'anyone
: crwa
```
3. 通过auth设置密码
添加用户名
```
[zk: localhost:2181(CONNECTED) 0] addauth digest root:root
```
```
[zk: localhost:2181(CONNECTED) 3] setAcl /fantj/a auth:root:root:cdraw
cZxid = 0x11
ctime = Tue Feb 19 12:39:52 CST 2019
mZxid = 0x11
mtime = Tue Feb 19 12:39:52 CST 2019
pZxid = 0x11
cversion = 0
dataVersion = 0
aclVersion = 2
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 4] getAcl /fantj/a
'digest,'root:qiTlqPLK7XM2ht3HMn02qRpkKIE=
: cdrwa

```
4. 设置ip权限
```
setAcl /fantj/ip ip:192.168.31.245:cdrwa
[zk: localhost:2181(CONNECTED) 7] getAcl /fantj/ip
'ip,'192.168.31.245
: cdrwa
```
5. super管理员
先拿到密码的hash值
```
String m = DigestAuthenticationProvider.generateDigest("super:admin");
m=super:xQJmxLMiHGwaqBvst5y6rkB6HQs=
```
打开zk目录下的/bin/zkServer.sh服务器脚本文件，找到如下一行：
```
nohup $JAVA "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.logger=${ZOO_LOG4J_PROP}"

```
加一个超管的配置项：
```
"-Dzookeeper.DigestAuthenticationProvider.superDigest=super:xQJmxLMiHGwaqBvst5y6rkB6HQs="

```
之后启动zk集群。

注意：需要说明的是，这个超管只是在这次服务器启动期间管用，如果关闭了服务器，并修改了服务器脚本，取消了超管配置，那么下一次启动就没有这个超管了。

### Acl使用场景
1. 使开发/测试环境分离
2. 控制指定ip服务科访问相关节点，防止混乱

### ZK四字命令
>zk可通过自身提供的简写命令来和服务器交互

需要使用到nc命令，安装`yum install nc`

`echo [commond] | nc [ip] [port]`

1. `stat` 查看zk状态信息，以及是否mode
```
echo stat| nc fantj.top 2181
```
2. `ruok` 查看当前zkserver是否启动，返回imok
3. `dump`列出未经处理的会话和临时节点
4. `conf` 查看zk服务配置
5. `cons` 展示连接到服务端的客户端信息
6. `envi` 环境变量
7. `mntr` zk健康信息
8. `wchs` 展示watcher信息
