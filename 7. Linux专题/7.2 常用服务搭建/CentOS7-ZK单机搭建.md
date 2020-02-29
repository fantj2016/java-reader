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