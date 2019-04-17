>Redis是一个开源的使用ANSI [C语言](https://baike.baidu.com/item/C%E8%AF%AD%E8%A8%80)编写、支持网络、可基于内存亦可持久化的日志型、Key-Value[数据库](https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E5%BA%93)，并提供多种语言的API。从2010年3月15日起，Redis的开发工作由VMware主持。从2013年5月开始，Redis的开发由Pivotal赞助。

##### [redis中文官方网站](https://www.baidu.com/link?url=wsplsdSflp87xaftpmHvD2mmJcD2pbbl9PtVXtUJxL_&wd=&eqid=cda201f20000682e000000025a65962a)
##### [Redis](http://www.baidu.com/link?url=Q032iEcIHsDA8Rhy3NqlUbFv8cuflLFS9_37K_rE6UG)[官网](http://trust.baidu.com/vstar/official/intro?type=gw)

###Redis八大特性
1. 速度快 
Redis是用**C语言实现**的； 
Redis的所有数据**存储在内存中**。 
2. 持久化 
Redis的所有数据存储在内存中，对数据的更新将**异步地保存**到磁盘上。 
3. 支持多种数据结构 
Redis支持五种数据结构：String、List、Set、Hash、Zset 
4. 支持多种编程语言 
Java、php、Python、Ruby、Lua、Node.js 
5. 功能丰富 
除了支持五种数据结构之外，还支持**事务、流水线、发布/订阅、消息队列等**功能。 
6. 源码简单 
约23000行C语言源代码。 
7. 主从复制 
主服务器（master）执行添加、修改、删除，从服务器执行查询。 
8. 高可用及分布式 
Redis-Sentinel（v2.8）支持高可用 
Redis-Cluster（v3.0）支持分布式
### 安装
```
$ wget http://download.redis.io/releases/redis-4.0.9.tar.gz
$ tar xzf redis-4.0.9.tar.gz
$ cd redis-4.0.9
$ make
```
二进制文件是编译完成后在src目录下，通过下面的命令启动Redis服务：
```
$ src/redis-server
```
你可以使用内置的客户端命令redis-cli进行使用：
```
$ src/redis-cli
redis> set foo bar
OK
redis> get foo
"bar"
```


### 添加密码

```
requirepass root
```

登录查看 keys *
```
auth root

```
### 让外网访问
1. 注释bind 127.0.0.1
2. 修改
```
#bind 127.0.0.1
appendonly no
protected-mode no
```
### 应用场景
1. 缓存系统
2. 计数器
3. 消息队列系统
4. 排行榜
5. 社交网络
6. 实时系统

