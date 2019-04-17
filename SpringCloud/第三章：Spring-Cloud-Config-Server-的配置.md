###1.为什么要使用config
1.  集中管理
2. 不通环境不通配置
3. **运行期间动态调整配置**
4. 自动刷新
###2.用法入门
1. 导入pom
```
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>
```
2. 启动类添加注解
`@EnableConfigServer`
3. 修改application.yml
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/jiaofanting/spring-cloud-config-repo/
server:
  port: 8080

```
这个uri是我git目录放的一个application.yml 路径，打开是404，因为它不是一个有效的链接，它只是说明了 application.yml文件放在哪里。
4. 看官方文档说明
```
The HTTP service has resources in the form:

/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties

where the "application" is injected as the spring.config.name in the SpringApplication (i.e. what is normally "application" in a regular Spring Boot app), "profile" is an active profile (or comma-separated list of properties), and "label" is an optional git label (**defaults to "master".**)
```
截图更清晰
![
](http://upload-images.jianshu.io/upload_images/5786888-863fb2fc0da1bcee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
注意中间有个- ，对照文档看效果
![image.png](http://upload-images.jianshu.io/upload_images/5786888-e2181f13c48f8d92.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
