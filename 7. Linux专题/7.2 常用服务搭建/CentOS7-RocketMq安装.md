首先要安装maven 
 https://www.jianshu.com/p/d41f0504e7a9和jdk 
 https://www.jianshu.com/p/89cd77509a4b

1. wget https://github.com/apache/rocketmq/archive/rocketmq-all-4.2.0.tar.gz
2. tar xvzf rocketmq-all-4.2.0.tar.gz
3. mv rocketmq-rocketmq-all-4.2.0 rocketmq
4. cd rokeetmq
5. mvn -Prelease-all -DskipTests clean install -U
6. cd distribution/target/apache-rocketmq
#####Start Name Server
```
  > nohup sh bin/mqnamesrv &
  > tail -f ~/logs/rocketmqlogs/namesrv.log
The Name Server boot success...
```  
#####Start Broker
```
  > nohup sh bin/mqbroker -n localhost:9876 &
  > tail -f ~/logs/rocketmqlogs/broker.log 
  The broker[%s, 172.30.30.233:10911] boot success...
```
#####Send & Receive Messages
在发送/接收消息之前，我们需要告诉客户名称服务器的位置。 RocketMQ提供了多种方法来实现这一点。 为了简单起见，我们使用环境变量NAMESRV_ADDR
```
 > export NAMESRV_ADDR=localhost:9876
 > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
 SendResult [sendStatus=SEND_OK, msgId= ...

 > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
 ConsumeMessageThread_%d Receive New Messages: [MessageExt...
```
#####Shutdown Servers
```
> sh bin/mqshutdown broker
The mqbroker(36695) is running...
Send shutdown request to mqbroker(36695) OK

> sh bin/mqshutdown namesrv
The mqnamesrv(36664) is running...
Send shutdown request to mqnamesrv(36664) OK
```
