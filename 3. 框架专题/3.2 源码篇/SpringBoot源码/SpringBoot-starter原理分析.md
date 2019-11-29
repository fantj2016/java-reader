### start包解析原理
1. 首先，SpringBoot在启动时会去依赖的starter包中寻找 `resources/META-INF/spring.factories` 文件，然后根据文件中配置的Jar包去扫描项目所依赖的Jar包，这类似于 Java 的 SPI 机制。

2. 根据 `spring.factories`配置加载`AutoConfigure`类。

3. 根据 `@Conditional`注解的条件，进行自动配置并将`Bean`注入`Spring Context`上下文当中。

我们也可以使用`@ImportAutoConfiguration({MyServiceAutoConfiguration.class}) `指定自动配置哪些类。


### 手写一个starter
项目结构：
>![](https://upload-images.jianshu.io/upload_images/5786888-ad75e17a45356eb4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.fantj</groupId>
    <artifactId>simple-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>simple-spring-boot-starter</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!--编译时生成 spring-configuration-metadata.json ，此文件主要给IDE使用。如当配置此jar相关配置属性在 application.yml ，
        你可以用ctlr+鼠标左键点击属性名，IDE会跳转到你配置此属性的类中。-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-configuration-processor</artifactId>
                <optional>true</optional>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-autoconfigure</artifactId>
            </dependency>
        </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

##### SimpleAutoConfigure.java
>自动配置类。
```
@Configuration
@ConditionalOnClass(SimpleService.class)
@EnableConfigurationProperties(SimpleServiceProperties.class)
public class SimpleAutoConfigure {

    @Autowired
    private SimpleServiceProperties properties;

    @Bean
    // 当容器中没有指定Bean的情况下
    @ConditionalOnMissingBean
    // 指定的属性是否有指定的值
    @ConditionalOnProperty(prefix = "simple.service", value = "enabled", havingValue = "true")
    SimpleService simpleService(){
        return new SimpleService(properties.getConfig());
    }
}
```

##### SimpleServiceProperties.java
>配置文件的属性注入。
```
@ConfigurationProperties("simple.service")
public class SimpleServiceProperties {
    private String config;

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }
}
```

##### SimpleService.java
>使用。
```
public class SimpleService {
    private String config;

    public SimpleService(String config) {
        this.config = config;
    }

    public String[] split(String separatorChar) {
        return StringUtils.split(this.config, separatorChar);
    }
}
```

##### spring.factories
>在src/main/resources新建文件夹META-INF，然后新建一个spring.factories文件
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.fantj.ss.config.SimpleAutoConfigure
```


然后进行maven打包。

### 调用
>项目结构：
![](https://upload-images.jianshu.io/upload_images/5786888-9c11738863f84727.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### pom.xml
```
<dependency>
    <groupId>com.fantj</groupId>
    <artifactId>simple-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

##### application.yml
```
simple:
  service:
    config: fantj,java-reader
    enabled: true
```

##### Test
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleSpringBootCustomerApplicationTests {

    @Autowired
    private SimpleService simpleService;
    @Test
    public void contextLoads() {
        String[] splitArray = simpleService.split(",");
        System.out.println(Arrays.toString(splitArray));
    }

}
```

##### 结果
![](https://upload-images.jianshu.io/upload_images/5786888-6471d890c6c74555.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)