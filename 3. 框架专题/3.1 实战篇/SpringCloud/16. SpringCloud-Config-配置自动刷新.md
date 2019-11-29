####准备工作
我们首先需要下载rabbitMq（默认4396端口）
>MQ全称为Message Queue,
 [消息队列](https://baike.baidu.com/item/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97)（MQ）是一种应用程序对应用程序的通信方法。应用程序通过读写出入队列的消息（针对应用程序的数据）来通信，而无需专用连接来链接它们。消息传递指的是程序之间通过在消息中发送数据进行通信，而不是通过直接调用彼此来通信，直接调用通常是用于诸如[远程过程调用](https://baike.baidu.com/item/%E8%BF%9C%E7%A8%8B%E8%BF%87%E7%A8%8B%E8%B0%83%E7%94%A8)的技术。排队指的是应用程序通过 队列来通信。队列的使用除去了接收和发送应用程序同时执行的要求。其中较为成熟的MQ产品有IBM WEBSPHERE MQ等等。

[rabbitMq下载](http://www.rabbitmq.com/install-windows.html)
安装它之前我们还需要安装erlang环境（rabbitMq是用该语言写的，因为该语言对并发支持较好）
[Erlang下载](http://www.erlang.org/downloads)
安装好后再安装一个管理工具plugin-management
打开rabbitmq命令行，执行
`rabbitmq-plugins enable rabbitmq_management`
然后重启rabbitmq服务，访问127.0.0.1:15672
![image.png](http://upload-images.jianshu.io/upload_images/5786888-1d8c22ba1e14e046.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
默认账号密码都是  guest

好了，终于到正文了。
####1.pom修改
```
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-bus-amqp</artifactId>
		</dependency>
```
####2.修改配置文件
```
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8080
      profile: dev
      label: master
  application:
    name: spring-cloud-config-client
  rabbitmq:
    host: 127.0.0.1
    port: 15672
    username: guest
    password: guest
```

####3.启动server 和 该bus client 服务
* 访问看服务是否正常启动
![image.png](http://upload-images.jianshu.io/upload_images/5786888-fd5668dea2557297.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* 修改application-dev文件(给配置文件的内容添加后缀bus)并push
![image.png](http://upload-images.jianshu.io/upload_images/5786888-0788a8f0e263e801.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* 发送curl请求
`curl -X POST http://127.0.0.1:8081/bus/refresh`
![curl.png](http://upload-images.jianshu.io/upload_images/5786888-db87426f54b964a8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![后台日志.png](http://upload-images.jianshu.io/upload_images/5786888-5c00915e1710e5f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![重新请求.png](http://upload-images.jianshu.io/upload_images/5786888-a887c79a820eb971.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####有同学看到这里，和手动刷新一样，并没有做到自动刷新啊。别急，进入自己的git仓库的setting，找到webhooks，
![webhooks.png](http://upload-images.jianshu.io/upload_images/5786888-c5e9b0132eae465c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![添加成功.png](http://upload-images.jianshu.io/upload_images/5786888-8018a8e3b4dcb357.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
**注意：也可以每个config server和config client里面都配置SpringCloud Bus。
（添加依赖+修改配置）,这样的话给server或者client 发送post请求都可以达到目的。个人建议发给server，然后server会拉取配置文件统一下发给client。**
