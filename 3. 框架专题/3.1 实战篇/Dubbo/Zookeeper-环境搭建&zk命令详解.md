### 1. 安装
##### 1.1 下载zookeeper
下载网址：http://www.apache.org/dyn/closer.cgi/zookeeper

##### 1.2. 解压
`tar zxvf zookeeper-3.4.8.tar.gz`

##### 1.3. 修改配置文件
`cd zookeeper-3.3.6/conf`

`将zoo_sample.cfg 改名为 zoo.cfg`

默认端口2181

##### 1.4. 启动
进入bin目录下
执行`./zkServer.sh start`

### 2. 基本命令
参考文档：http://zookeeper.apache.org/doc/current/zookeeperStarted.html
#### 服务命令
启动重启命令：
1. 启动ZK服务:       `sh bin/zkServer.sh start`
2. 查看ZK服务状态: `sh bin/zkServer.sh status`
3. 停止ZK服务:       `sh bin/zkServer.sh stop`
4. 重启ZK服务:      ` sh bin/zkServer.sh restart`

四字命令：
例如：
```
当然，需要先安装nc 工具。
[root@FantJ ~]# yum -y install nc

[root@FantJ ~]# echo conf | nc 127.0.0.1 2181
clientPort=2181
dataDir=/tmp/zookeeper/version-2
dataLogDir=/tmp/zookeeper/version-2
tickTime=2000
maxClientCnxns=60
minSessionTimeout=4000
maxSessionTimeout=40000
serverId=0

```
与conf相同的，还有这些

|ZooKeeper 四字命令|功能描述   |
|:-------:|:----:|
| conf|输出相关服务配置的详细信息。 |
|cons|列出所有连接到服务器的客户端的完全的连接 / 会话的详细信息。包括“接受 / 发送”的包数量、会话 id 、操作延迟、最后的操作执行等等信息。|
|dump|列出未经处理的会话和临时节点。|
|envi|输出关于服务环境的详细信息（区别于 conf 命令）。|
|reqs|列出未经处理的请求|
|ruok|测试服务是否处于正确状态。如果确实如此，那么服务返回“imok ”，否则不做任何相应。|
|stat|输出关于性能和连接的客户端的列表。|
|wchs|列出服务器 watch 的详细信息。|
|wchc|通过 session 列出服务器 watch 的详细信息，它的输出是一个与watch 相关的会话的列表。|
|wchp|通过路径列出服务器 watch 的详细信息。它输出一个与 session相关的路径。|

传递四个字母的字符串给ZooKeeper，ZooKeeper会返回一些有用的信息。
#### 客户端命令
##### 2.1 连接到server
```
./zkCli.sh -server localhost:2181
```
##### 2.2 命令详解
###### 1. help 
 跟linux的help命令一样，查看所有帮助
###### 2. ls
使用 ls 命令来查看某个目录包含的所有文件，例如：
```
[zk: 127.0.0.1:2181(CONNECTED) 1] ls /
1
```
###### 3. ls2
使用 ls2 命令来查看某个目录包含的所有文件，与ls不同的是它查看到time、version等信息
```
[zk: 127.0.0.1:2181(CONNECTED) 1] ls2 /
1
```
###### 4. create
创建znode，并设置初始内容，例如
```
[zk: 127.0.0.1:2181(CONNECTED) 1] create /test "hello" 
1
```
创建一个新的 znode节点“ test ”以及与它关联的字符串

###### 5. get
获取znode的数据，如下：
```
[zk: 127.0.0.1:2181(CONNECTED) 1] get /test
1
```
###### 6. set
修改znode内容，例如：
```
[zk: 127.0.0.1:2181(CONNECTED) 1] set /test "ricky"
1
```
###### 7. delete
删除znode
```
[zk: 127.0.0.1:2181(CONNECTED) 1] delete /test
1
```
###### 8. quit
退出客户端

参考文档：http://zookeeper.apache.org/doc/current/zookeeperStarted.html
