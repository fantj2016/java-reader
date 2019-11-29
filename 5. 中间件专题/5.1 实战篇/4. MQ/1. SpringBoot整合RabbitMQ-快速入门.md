>MQ全称为Message Queue, 消息队列（MQ）是一种应用程序对应用程序的通信方法。MQ是消费-生产者模型的一个典型的代表，一端往消息队列中不断写入消息，而另一端则可以读取队列中的消息。消息中间件最主要的作用是解耦，中间件最标准的用法是生产者生产消息传送到队列，消费者从队列中拿取消息并处理，生产者不用关心是谁来消费，消费者不用关心谁在生产消息，从而达到解耦的目的。在分布式的系统中，消息队列也会被用在很多其它的方面，比如：分布式事务的支持，RPC的调用等等。


### RabbitMQ介绍

RabbitMQ是实现AMQP（高级消息队列协议）的消息中间件的一种，最初起源于金融系统，用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。RabbitMQ主要是为了实现系统之间的双向解耦而实现的。当生产者大量产生数据时，消费者无法快速消费，那么需要一个中间层。保存这个数据。

AMQP，即Advanced Message Queuing Protocol，高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。消息中间件主要用于组件之间的解耦，消息的发送者无需知道消息使用者的存在，反之亦然。AMQP的主要特征是面向消息、队列、路由（包括点对点和发布/订阅）、可靠性、安全。

RabbitMQ是一个开源的AMQP实现，服务器端用Erlang语言编写，支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP等，支持AJAX。用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。

### 相关概念

通常我们谈到队列服务, 会有三个概念： 发消息者、队列、收消息者，RabbitMQ 在这个基本概念之上, 多做了一层抽象, 在发消息者和 队列之间, 加入了交换器 (Exchange). 这样发消息者和队列就没有直接联系, 转而变成发消息者把消息给交换器, 交换器根据调度策略再把消息再给队列。

那么，其中比较重要的概念有 4 个，分别为：虚拟主机，交换机，队列，和绑定。

* 虚拟主机：一个虚拟主机持有一组交换机、队列和绑定。为什么需要多个虚拟主机呢？很简单，RabbitMQ当中，用户只能在虚拟主机的粒度进行权限控制。 因此，如果需要禁止A组访问B组的交换机/队列/绑定，必须为A和B分别创建一个虚拟主机。每一个RabbitMQ服务器都有一个默认的虚拟主机“/”。
* 交换机：Exchange 用于转发消息，但是它不会做存储 ，如果没有 Queue bind 到 Exchange 的话，它会直接丢弃掉 Producer 发送过来的消息。
这里有一个比较重要的概念：路由键 。消息到交换机的时候，交互机会转发到对应的队列中，那么究竟转发到哪个队列，就要根据该路由键。
* 绑定：也就是交换机需要和队列相绑定，这其中如上图所示，是多对多的关系。


### 四种交换机(Exchange)
交换机的功能主要是接收消息并且转发到绑定的队列，交换机不存储消息，在启用ack模式后，交换机找不到队列会返回错误。交换机有四种类型：Direct, topic, Headers and Fanout

##### 1. Direct Exchange
direct 类型的行为是"先匹配, 再投送". 即在绑定时设定一个 routing_key, 消息的routing_key 匹配时, 才会被交换器投送到绑定的队列中去.是RabbitMQ默认的交换机模式，也是最简单的模式，根据key全文匹配去寻找队列。

配置：设置一个路由键
```
    public  static  final String QUEUE="queue";
    /**
     * direct 交换机模式
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

```
发送服务：
```
@Service
@Slf4j
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message){
        String msg = (String) message;
        log.info("send msg"+message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }
}
```
接收服务：
```
@Service
@Slf4j
public class MQReceiver {

    //监听的queue
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String msg){
        log.info("receive msg "+msg);
    }
}
```
测试：
```
    @Autowired
    private MQSender sender;

    sender.send("hello direct Exchange");
```
![](https://upload-images.jianshu.io/upload_images/5786888-6171d87272630cb8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 2. Topic Exchange
按规则转发消息（最灵活） 转发消息主要是根据通配符。 在这种交换机下，队列和交换机的绑定会定义一种路由模式，那么，通配符就要在这种路由模式和路由键之间匹配后交换机才能转发消息。

路由键必须是一串字符，用句号（.） 隔开，

路由模式必须包含一个 星号（*），主要用于匹配路由键指定位置的一个单词， 井号（#）就表示相当于一个或者多个单词

配置类：
```
    public  static  final String TOPIC_QUEUE1="topic.queue1";
    public  static  final String TOPIC_QUEUE2="topic.queue2";
    public  static  final String ROUTING_KEY1="topic.key1";
    public  static  final String ROUTING_KEY2="topic.#";
    /**
     * Topic 交换机模式  可以用通配符
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }
```

发送类：
```
    public void sendTopic(Object message){
        String msg = (String) message;
        log.info("send topic message"+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
    }
```

接收类：
```
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String msg){
        log.info("receive topic1 msg "+msg);
    }
```
测试：
```
    @Autowired
    private MQSender sender;

    sender.sendTopic("hello topic Exchange");
```
![](https://upload-images.jianshu.io/upload_images/5786888-33b545961a3f24a9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3. Headers Exchange
设置header attribute参数类型的交换机，相较于 direct 和 topic 固定地使用 routing_key , headers 则是一个自定义匹配规则的类型. 在队列与交换器绑定时, 会设定一组键值对规则, 消息中也包括一组键值对( headers 属性), 当这些键值对有一对, 或全部匹配时, 消息被投送到对应队列.
```
    public static final String HEADER_EXCHANGE="headerExchange";
    /**
     * Header 交换机模式
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADER_EXCHANGE);
    }
    @Bean
    public Queue headerQueue(){
        return new Queue(HEADER_QUEUE2,true);
    }
    // 绑定需要指定header，如果不匹配 则不能使用
    @Bean
    public Binding headerBinding(){
        Map<String,Object> map = new HashMap();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }
```

```
    public void sendHeader(Object massage){
        String msg = (String) massage;
        log.info("send fanout message: "+msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADER_EXCHANGE,"",obj);
    }
```
用MessageProperties来添加Header信息，然后与接收者的header比对。我都设置的是"header1","value1"；"header2","value2"
```
    //监听 header模式的queue
    @RabbitListener(queues = MQConfig.HEADER_QUEUE2)
    //因为发送的是 byte 类型，所以接受也是该数据类型
    public void receiveHeader(byte[] message){
        log.info("header queue message"+new String(message));
    }
```
测试：
```
    @Autowired
    private MQSender sender;

    sender.sendHeader("hello header Exchange");
```
![](https://upload-images.jianshu.io/upload_images/5786888-cef02b64fb407919.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 4. Fanout Exchange
转发消息到所有绑定队列，消息广播的模式，不管路由键或者是路由模式，会把消息发给绑定给它的全部队列，如果配置了routing_key会被忽略。
```
    public static final String FANOUT_EXCHANGE="fanoutExchange";
    /**
     * Fanout 交换机模式（广播模式）,不用绑定key
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

```

```
    public void sendFanout(Object massage){
        String msg = (String) massage;
        log.info("send fanout message: "+msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
    }
```
测试：
```
    @Autowired
    private MQSender sender;

    sender.sendFanout("hello fanout Exchange");
```
![](https://upload-images.jianshu.io/upload_images/5786888-642d5294ada00874.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 补充

这个示例是基于springboot的示例。

pom依赖
```
		<!--rabbbitMQ相关依赖-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-amqp</artifactId>
		</dependency>
```
配置文件
```
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    # 这个账号密码只能连接本地的mq，远程的话需要配置
    virtual-host: /
    listener:
      simple:
        concurrency: 10
        max-concurrency: 10
        prefetch: 1 # 从队列每次取一个
        auto-startup: true
        default-requeue-rejected: true # 失败后重试
```
