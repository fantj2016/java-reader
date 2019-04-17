###1. 什么是发布订阅
Redis 发布订阅(pub/sub)是一种消息通信模式：发送者(pub)发送消息，订阅者(sub)接收消息。

Redis 客户端可以订阅任意数量的频道。

下图展示了频道 channel1 ， 以及订阅这个频道的三个客户端 —— client2 、 client5 和 client1 之间的关系：![image.png](http://upload-images.jianshu.io/upload_images/5786888-0d41cae20e882e26.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
当有新消息通过 PUBLISH 命令发送给频道 channel1 时， 这个消息就会被发送给订阅它的三个客户端：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-dff24637650291b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###2. 实例
以下实例演示了发布订阅是如何工作的。在我们实例中我们创建了订阅频道名为 redisChat:
```
redis 127.0.0.1:6379> SUBSCRIBE redisChat

Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "redisChat"
3) (integer) 1
```
现在，我们先重新开启个 redis 客户端，然后在同一个频道 redisChat 发布两次消息，订阅者就能接收到消息。
```
redis 127.0.0.1:6379> PUBLISH redisChat "Redis is a great caching technique"

(integer) 1

redis 127.0.0.1:6379> PUBLISH redisChat "Learn redis by runoob.com"

(integer) 1

# 订阅者的客户端会显示如下消息
1) "message"
2) "redisChat"
3) "Redis is a great caching technique"
1) "message"
2) "redisChat"
3) "Learn redis by runoob.com"
```


### Redis 发布订阅常用命令

下表列出了 redis 发布订阅常用命令：

| 序号 | 命令及描述 |
|:----:|:----:|
| 1 | PSUBSCRIBE pattern订阅一个或多个符合给定模式的频道。 |
| 2 | PUBSUB subcommand 查看订阅与发布系统状态。 |
| 3 | PUBLISH channel message]将信息发送到指定的频道。 |
| 4 | PUNSUBSCRIBE 退订所有给定模式的频道。 |
| 5 | SUBSCRIBE channel [channel ...]订阅给定的一个或多个频道的信息。 |
| 6 | UNSUBSCRIBE [channel [channel ...]]指退订给定的频道。 |


本文借鉴 runoob.com，觉得本网站给的思路比较清晰。

###1. 什么是GEO
  geo就是地理信息定位，存储经纬度，计算两地距离、范围等
###2. api介绍
* geo key longitude latitude member [longitude latitude member ...] #增加地里位置信息
`geoadd cities:locations 111.111.111.11 beijing`
* geopos key member [member ...] #获取地理位置信息
`geopos cities:location beijing`
* geodist key member1 member2 [unit] #获取两个地里位置的距离 unit:m. km. mi(英里) .ft(尺)
* georadius 命令百度查看详情，这里不做介绍，用到的也很少
`georadiusbymember cities:locations beijing 150 km`查找距离北京150km以内的城市


