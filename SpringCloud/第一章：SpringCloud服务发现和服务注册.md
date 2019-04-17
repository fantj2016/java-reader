#服务提供者
####1. 解决硬编码问题
1.  修改一个服务ip后关联到好几个服务的配置修改

####2. 服务发现组件
1. 服务注册表
2. 服务注册
3. 健康检查  默认30s

####3. 服务发现方式
1. 客户端  eureka 【zk】
2. 服务端  consul+nginx

####4. 代码片
######pom.xml
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
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
```
######配置文件application.yml
```
server:
  port: 7900
spring:
  jpa:
    generate-ddl: false
    show-sql: true
    hibernate:
      ddl-auto: none
  datasource:
    url: jdbc:mysql://127.0.0.1/springcloud??characterEncoding=UTF8&useSSL=true&allowMultiQueries=true
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.Driver
    schema: classpath:schema.sql   #建表
    data: classpath:data.sql #数据
logging:
  level: info
```
######创建数据库
1. schema.sql
```
DROP table user if exists;
CREATE TABLE user(
    id bigint,
    username varchar(40),
    name varchar(20),
    age int(3),
    balance decimal(10,2),
    PRIMARY KEY(id)
);
```
2. data.sql
```
insert into user(id,username,name,age,balance) VALUES(1,'user1','老焦1',200,100.00);
insert into user(id,username,name,age,balance) VALUES(2,'user2','老焦2',200,100.00);
insert into user(id,username,name,age,balance) VALUES(3,'user3','老焦3',200,100.00);
insert into user(id,username,name,age,balance) VALUES(4,'user4','老焦4',200,100.00);
insert into user(id,username,name,age,balance) VALUES(5,'user5','老焦5',200,100.00);

```
######创建Bean和dao
User.java
```
/**
 * Created by Fant.J.
 * 2017/11/11 13:53
 */
@Entity
@Data
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String username;
    @Column
    private String name;
    @Column
    private Short age;
    @Column
    private BigDecimal balance;
}
```
UserReposiry.java
```
@Repository
public interface UserReposiry extends JpaRepository<User,Long>{
}

```
######创建视图层

```
@RestController
public class UserController {

    @Autowired
    private UserReposiry userReposiry;

    @GetMapping("/simple/id")
    public User findById(@PathVariable Long id){
        return this.userReposiry.findOne(id);
    }
}
```
######启动项目
![image.png](http://upload-images.jianshu.io/upload_images/5786888-8f64ef197eec648b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#服务消费者
####创建模块　fantj-consumer-movie
######只需要添加User bean和controller
```
server.port= 7901
```
```
/**
 * Created by Fant.J.
 * 2017/11/11 13:53
 */
@Data
public class User implements Serializable{
    private Long id;
    private String username;
    private String name;
    private Short age;
    private BigDecimal balance;
}
```

```
@RestController
public class MovieController {
    @Autowired
    private RestTemplate template;

    @RequestMapping("/movie/{id}")
    public User findById(@PathVariable Long id){
        return this.template.getForObject("http://127.0.0.1:7900/simple/"+id,User.class);
    }
}
```
######启动springboot时候创建RestTemplate类
```
@SpringBootApplication
public class FantjConsumerMovieApplication {

	@Bean  //实例化resttemplate
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(FantjConsumerMovieApplication.class, args);
	}
}

```
######启动项目
![image.png](http://upload-images.jianshu.io/upload_images/5786888-67dc757c528e3130.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这就证明了服务被消费
