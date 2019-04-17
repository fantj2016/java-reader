### 1. 安装 Zookeeper 环境
[Zookeeper 环境搭建&zk命令详解](https://www.jianshu.com/p/c984e5b4dc02)

### 2. 服务提供者

因为用了父工程的版本管理，所以这里没有显示版本，我把用到的版本给大家分享下。

1. jdk 1.8
2. springboot 1.5.8
3. spring-boot-starter-dubbo 1.0.0

闲余之际我会把demo源码分享。

##### 2.1 pom依赖
```
    <dependencies>
        <!-- Spring Boot Web 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Test 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--spring data jpa -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.8-dmr</version>
            <scope>compile</scope>
        </dependency>
        <!--dubbo 依赖-->
        <dependency>
            <groupId>io.dubbo.springboot</groupId>
            <artifactId>spring-boot-starter-dubbo</artifactId>
        </dependency>
        <!--junit 测试工具-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>

```
##### 2.2. Service填写
大概给下项目架构:
![](https://upload-images.jianshu.io/upload_images/5786888-a05bc3bb67e3a211.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
为了方便，我用的springboot data jpa做持久性框架。如果不会jpa，请先看我[springboot jpa整合](https://www.jianshu.com/p/3b31270a44b1)。
1. SchoolRepository .java
```
/**
 * Created by Fant.J.
 */
@Repository
public interface SchoolRepository extends JpaRepository<School,Integer> {

}
```
2. SchoolService.java 略(一个正常的借口)
3. 实现类SchoolServiceImpl .java核心代码
```
@Service(version = "2.0.1")
public class SchoolServiceImpl implements SchoolService {
    @Autowired
    private SchoolRepository schoolRepository;

```
这里的@Service注解是dubbo的注解，不是springframework下的注解。该注解就是向zk注册服务。
##### 2.3. application.properties
```
server.port=9002

## Dubbo 服务提供者配置

spring.dubbo.application.name=school-server
spring.dubbo.registry.address=zookeeper://xxx.xxx.xxx.xxx:2181
spring.dubbo.protocol.name=dubbo
spring.dubbo.protocol.port=20882
spring.dubbo.scan=com.xxx.school.service

spring.datasource.url=jdbc:mysql://xxxxxxxxxx
spring.datasource.username=xxxx
spring.datasource.password=xxxx
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```
注意： 每一个服务都需要一个未被使用的dubbo端口 。
### 3. 服务消费
为了和服务提供者解耦，我们需要把Service接口类单独拿出来放到client模块里，这里不贴详细代码了。
![](https://upload-images.jianshu.io/upload_images/5786888-a4bca55f35dceb46.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.1 pom.xml
```
这里略，根据controller类里的需要来填充相关依赖
```
##### 3.2 SchoolController
```
@RestController
@RequestMapping("/sch")
public class SchoolController {

    @Reference(version = "2.0.1")
    private SchoolService schoolService;

    @RequestMapping("/all")
    public ServerResponse getAll(){
        return schoolService.selectAll();
    }
}
```
注意与@Service注解的version属性值一一对应。
##### 3.3 application.properties
```
## Dubbo 服务消费者配置
spring.dubbo.application.name=xxx
spring.dubbo.registry.address=zookeeper://xxxx.xxx.xxx.xxx
spring.dubbo.scan=com.xxx.web.controller
```

成功截图：

![](https://upload-images.jianshu.io/upload_images/5786888-67aa4b93fba140f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

有疑问请在下面留言。
