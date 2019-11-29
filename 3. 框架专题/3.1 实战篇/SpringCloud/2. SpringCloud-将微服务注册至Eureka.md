####1.创建eureka模块
![image.png](http://upload-images.jianshu.io/upload_images/5786888-50f4d947656c8a25.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####2.添加pom依赖
```
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka-server</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-netflix-eureka-server</artifactId>
		</dependency>
```
并且加入父依赖管理。
```
	<parent>
		<groupId>com.laojiao</groupId>
		<artifactId>fantj-parent</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

```
同样在父类版本控制器里添加上这个模块的pom路径。
```
<modules>
		<module>../fantj-consumer-movie</module>
		<module>../fantj-provider-user</module>
		<module>../fantj-discovery-eureka</module>
	</modules>
```
####3.配置application.yml
```
server:
  port: 9000
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://127.0.0.1:9000/eureka
```
如果想给eureka服务上添加身份验证功能，需要补充
```
#加入密码验证
security:
  basic:
    enabled: true
  user:
    name: laojiao
    password: laojiao

```
并且还需要添加springsecurity的依赖包。
>我在这里不用身份验证。所以忽略该步骤

####4.在启动类上加注解
`@EnableEurekaServer`表示这个springboot是一个eureka服务
启动该模块。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-26bdfec67f697241.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####5.将服务注册至Eureka服务
![image.png](http://upload-images.jianshu.io/upload_images/5786888-192e49e6821b49a2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
比如我要将这个服务注册至eureka。
1. 加依赖
```
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```
第一个是eureka的依赖。第二个是监控与管理模块，它可以将该模块运行时的信息暴露出来。详细查看官方文档。
2. 在启动类里加注解
`@EnableEurekaClient`标明它是个eureka客户端
3. 配置application
在原有基础上添加
```
eureka:
  client:
    service-url:
      defaultZone:  http://127.0.0.1:9000/eureka      #这个是eureka模块配置的默认空间
  instance:
    prefer-ip-address: true  #将主机名改成ip(192.168.0.1)
    instance-id: ${spring.application.name}:${spring.application.instance_id}:${server.port}
```
instance是修改注册到eureka的ip地址信息。详细查看文档。
启动项目
![image.png](http://upload-images.jianshu.io/upload_images/5786888-3d9e8834855cacda.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我们就看到了user注册到了eureka列表里。


