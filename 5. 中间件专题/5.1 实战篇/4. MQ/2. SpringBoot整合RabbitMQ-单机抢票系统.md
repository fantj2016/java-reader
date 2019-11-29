>上一章节讲了基本的整合和各种Exchange的使用，这章主要来实现一个单机的简单的抢票系统，麻雀虽小但五脏俱全，为什么用它做抢票系统大家应该也懂，为了削峰和异步处理。

在这个项目里我用的是`springboot`的2版本，ORM选用`JPA`快速开发，JSON工具使用阿里的`fastjson`，当然，mq用的是`rabbitMQ`。导入的是`springboot`集成的依赖。
### 1. 配置部分
##### 1.1 pom.xml

```
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>RELEASE</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
```
##### 1.2 application.properties
```
server.port=10000

spring.datasource.url=jdbc:mysql://xxxxx/xxxxx?characterEncoding=utf-8
spring.datasource.username=xxx
spring.datasource.password=xxxx
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.show-sql=true

spring.rabbitmq.host=localhost
spring.rabbitmq.username=root
spring.rabbitmq.password=root
spring.rabbitmq.port=5672
```
我只是很有针对性的对`mq`和`datasource`进行了配置。

##### 1.3 数据表
```
create table if not result
(
	id int auto_increment primary key,
	ticket_id int null,
	user_id int null
);

create table if not exists ticket
(
	id int auto_increment primary key,
	name varchar(255) null,
	content varchar(255) null,
	user_name varchar(20) null,
	count int default '6666' not null
);
```
根据数据表可以Generate出JavaBean，不贴JavaBean了。
 ##### 1.4 项目架构
```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── fantj
│   │   │           └── springbootjpa
│   │   │               ├── AMQP.java
│   │   │               ├── controller
│   │   │               │   └── TicketController.java
│   │   │               ├── mq
│   │   │               │   ├── Message.java
│   │   │               │   ├── MQConstants.java
│   │   │               │   ├── MQReceiver.java
│   │   │               │   └── MQSender.java
│   │   │               ├── pojo
│   │   │               │   ├── Result.java
│   │   │               │   └── Ticket.java
│   │   │               ├── repostory
│   │   │               │   ├── ResultRepository.java
│   │   │               │   └── TicketRepository.java
│   │   │               └── service
│   │   │                   ├── ResultServiceImpl.java
│   │   │                   ├── ResultService.java
│   │   │                   ├── TicketServiceImpl.java
│   │   │                   └── TicketService.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── rebel.xml
```
### 2. 启动类
```
@SpringBootApplication
@EntityScan("com.fantj.springbootjpa.pojo")
@EnableRabbit
public class AMQP {
    public static void main(String[] args) {
        SpringApplication.run(AMQP.class, args);
    }
}
```
注意这个`@EnableRabbit`注解，它会开启对rabbit注解的支持。
### 3. controller
>很简单的一个controller类，实现查询和抢票功能。
```
@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/get/{id}")
    public Ticket getByid(@PathVariable Integer id){
        return ticketService.findById(id);
    }

    @RequestMapping("/reduce/{id}/{userId}")
    public String reduceCount(@PathVariable Integer id,
                              @PathVariable Integer userId){
        Message message = new Message(id,userId);

        ticketService.reduceCount(id);
        mqSender.sendMessage(new Message(message.getTicketId(),message.getUserId()));
        return "抢票成功!";
    }
}
```
注意`private MQSender mqSender;`这是我的`rabbit`发送消息的类。

### 4. Service
>接口我就不再这里贴出，直接贴实现类。
##### 4.1 ResultServiceImpl.java
```
@Service
public class ResultServiceImpl implements ResultService{

    @Autowired
    private ResultRepository resultRepository;
    @Override
    public void add(Result result) {
        resultRepository.add(result.getTicketId(), result.getUserId());

    }

    @Override
    public Result findOneByUserId(Integer userId) {
        return resultRepository.findByUserId(userId);
    }
}
```

##### 4.2 TicketServiceImpl.java
```
@Service
public class TicketServiceImpl implements TicketService{

    @Autowired
    private TicketRepository repository;
    @Override
    public Ticket findById(Integer id) {
        return repository.findTicketById(id);
    }

    @Override
    public Ticket reduceCount(Integer id) {
        repository.reduceCount(id);
        return findById(id);
    }
}
```
这两个都是很普通的service实现类，没有新加入的东西。

### 5. Dao
##### 5.1 ResultRepository.java
```
@Repository
public interface ResultRepository extends JpaRepository<Result,Integer> {

    @Transactional
    @Modifying
    @Query(value = "insert into result(ticket_id,user_id) values(?1,?2) ",nativeQuery = true)
    void add(@Param("ticketId") Integer ticketId,@Param("userId") Integer userId);

    Result findByUserId(Integer userId);
}
```
##### 5.2 TicketRepository.java
```
@Repository
public interface TicketRepository extends JpaRepository<Ticket,Integer>{
    /**
     *  减少库存
     */
    @Transactional
    @Modifying
    @Query(value = "update ticket t set t.count=t.count+(-1) where id=?1",nativeQuery = true)
    int reduceCount(Integer id);
    /**
     * 查询信息
     */
    Ticket findTicketById(Integer id);
}
```
到了这里，你会发现，md哪里有用mq的痕迹...

### 6. MQ
>剩下的全是mq的处理。
###### 6.1 Message.java
>这个类用来封装mq传输的消息对象，我们使用它来对传输的byte进行编解码，得到我们想要的数据。
```
@Data
public class Message implements Serializable {
    private Integer ticketId;
    private Integer userId;

    public Message() {
    }
    public Message(Integer ticketId, Integer userId) {
        this.ticketId = ticketId;
        this.userId = userId;
    }
}
```
###### 6.2 MQConstants.java
>这是一个常量类，用来定义和保存`queue`的名字，虽然里面只有一个常量，好习惯要从小事做起。
```
public class MQConstants {
    public static final String QUEUE= "qiangpiao";
}
```
###### 6.3 MQSender.java
>这是消息发送类，用来给queue发送数据。
```
@Service
@Slf4j
public class MQSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(Message message){
        String msg = JSONObject.toJSONString(message);
        log.info("send message : "+msg);
        amqpTemplate.convertAndSend(MQConstants.QUEUE,msg);
    }
}
```
`AmqpTemplate`是springboot框架提供给我们使用的amqp操作模板，利用它我们能更方便的调用和处理业务。
我们在Controller层调用它，来完成消息入队的操作，完成削峰和异步处理，大大增加了系统并发和强健性。
###### 6.4 MQReceiver.java
>这是消息接收类，用来从queue里获取数据。
```
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private ResultService resultService;

    @RabbitListener(queues = MQConstants.QUEUE)
    public void receive(String message){
        log.info("receive msg : "+message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        System.out.println(jsonObject);
        Message msg = JSONObject.toJavaObject(jsonObject, Message.class);
        Integer ticketId = msg.getTicketId();
        Integer userId = msg.getUserId();
        // 减库存
        Ticket ticket = ticketService.reduceCount(ticketId);
        if (ticket.getCount() <= 0){
            return;
        }
        // 判断是否已经抢过
        Result oneByUserId = resultService.findOneByUserId(userId);
        if (oneByUserId != null){
            return;
        }
        resultService.add(new Result(ticketId,userId));
    }
}
```
在这个类中，`@RabbitListener(queues = MQConstants.QUEUE)`标记的是监听方法，该方法会从queue中获取到String数据。

之后我们需要将其复原为JavaBean，取出我们该要的属性，继续处理业务: `查询票剩余量`->`判断是否已抢到过`-> `减库存` -> `增加抢票数据`。 (我这里写的有点草率，应该先查余量...，不过不重要，本章重点在过一遍springboot与rabbitmq的整合)。

### 运行效果
>我对该抢票功能做了一个9999请求，我本来做3k并发，电脑没那么多句柄，实现不了，最后做了1k并发的压测。

这是rabbitMQ 自带Managerment模板上的截图:
>![](https://upload-images.jianshu.io/upload_images/5786888-7f737b2d43b0f4d7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-1b335be0c2ed841e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

压测报告:
```
Server Software:        
Server Hostname:        127.0.0.1
Server Port:            10000

Document Path:          /ticket/reduce/1/10
Document Length:        13 bytes

Concurrency Level:      1000
Time taken for tests:   423.101 seconds
Complete requests:      9999
Failed requests:        0
Total transferred:      1459854 bytes
HTML transferred:       129987 bytes
Requests per second:    23.63 [#/sec] (mean)
Time per request:       42314.334 [ms] (mean)
Time per request:       42.314 [ms] (mean, across all concurrent requests)
Transfer rate:          3.37 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    2   6.8      0      29
Processing:   217 40197 7390.7  41984   58488
Waiting:      217 40197 7390.8  41984   58488
Total:        246 40199 7384.8  41985   58488

Percentage of the requests served within a certain time (ms)
  50%  41984
  66%  42670
  75%  42744
  80%  42758
  90%  42801
  95%  42828
  98%  42850
  99%  42868
 100%  58488 (longest request)
```

### 注意
1. 本项目没有考虑线程安全的问题，事实上线程是不安全的，线程安全问题后面会说。
2. 本项目只是为了mq的削峰和异步处理，最直观的就是数据库可以称住高并发,一般来讲，数据库连接这块是称不住的。
3. mq在分布式下的问题后面会说。
