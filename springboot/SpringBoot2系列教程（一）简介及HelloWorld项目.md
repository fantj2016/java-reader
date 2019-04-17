>Spring Boot 2.0.0.RELEASE 需要[Java 8 or 9](https://www.java.com/) and [Spring Framework 5.0.4.RELEASE](https://docs.spring.io/spring/docs/5.0.4.RELEASE/spring-framework-reference/) 或者更高版本. 支持Maven 3.2+和Gradle 4 版本

#####支持的Servlet容器
Tomcat 8.5、Jetty 9.4、Jetty 9.4

####官方版本迁移wiki
https://github.com/spring-projects/spring-boot/wiki

####HelloWorld异同项目分析

######1. 修改 pom.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>springboot-mybatis</groupId>
	<artifactId>springboot-mybatis</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>springboot-mybatis</name>
	<description>Demo project for Spring Boot</description>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
  </parent>

	<!-- Add typical dependencies for a web application -->
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-test</artifactId>
			<version>5.0.4.RELEASE</version>
		</dependency>
	</dependencies>

	<!-- Package as an executable jar -->
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
######2. 修改启动类
```
package springbootmybatis.springbootmybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class MybatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisApplication.class, args);
	}

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

}

```
![运行结果](http://upload-images.jianshu.io/upload_images/5786888-40a06e86999c36d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

######代码中三个注解分析
两个MVC注解：
1. @RestController 是Spring处理web请求时用到的注解。告诉Spring将结果字符串直接呈现给调用者。
2. @RequestMapping 提供了“路由”信息，将`/`请求映射给home()方法。

SpringBoot注解：
3. @EnableAutoConfiguration 这个注解告诉Springboot 根据你添加的jar包来猜测配置Spring 。@SpringBootApplication一样，也可以使用exclude属性来禁用不需要自动配置的应用。例子：`@EnableAutoConfiguration（exclude = {DataSourceAutoConfiguration.class}）` 那么，它和@SpringBootApplication注解有什么关系呢？
######我们来看下@SpringBootApplication注解源码
   ![](http://upload-images.jianshu.io/upload_images/5786888-e30be488a4d492cf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
所以，@SpringBootApplication注释是相当于使用@Configuration， @EnableAutoConfiguration以及@ComponentScan与他们的**默认属性**。

通俗的讲：@SpringBootApplication = @Configuration+@EnableAutoConfiguration+@ComponentScan 。**前提**是默认配置，当然，如果你想要覆盖默认配置，你就需要重写该注解了，重写也很简单，给注解加上参数就好。



