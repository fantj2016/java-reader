>项目一旦放到生产环境，就需要不停机更改配置。比如更改一些线程池连接数什么的。或者是配置文件，这里我演示手动刷新git仓库上的配置文件
###1. 添加pom
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```
为什么一定要加actuator依赖呢？
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7160a0831039fccc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
有了actuator这个依赖，就可以使用/refresh 这个节点来刷新带有`@RefreshScope`注解服务的bean
###2. 修改启动类
添加注解`@RefreshScope`
注解`@RefreshScope`拓展：
>Refresh Scope
A Spring @Bean that is marked as @RefreshScope will get special treatment when there is a configuration change. This addresses the problem of stateful beans that only get their configuration injected when they are initialized. For instance if a DataSource has open connections when the database URL is changed via the Environment, we probably want the holders of those connections to be able to complete what they are doing. Then the next time someone borrows a connection from the pool he gets one with the new URL.
###3. 在贴一下server 和 client 的配置代码
算了  不贴了，和以前一样的。
server：
[Spring Cloud Config Server](http://www.jianshu.com/p/19aa816ef99f)
client：
[SpringCloud Config Client](http://www.jianshu.com/p/98fbae039974)
### 启动config server和 client 服务
1. 先来访问/profile 看服务是否正常运行
![](http://upload-images.jianshu.io/upload_images/5786888-5edbb12dbed7de7a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
2. 修改git上的application-dev.yml配置
![image.png](http://upload-images.jianshu.io/upload_images/5786888-aadfaf6575507ac7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ac6a07891da4b358.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
3. 再次访问/profile
![image.png](http://upload-images.jianshu.io/upload_images/5786888-467cc0da83edad0d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我们发现它并没有发生变化。这需要我们的一个触发操作
发送一个`post`请求给 `/refresh`
![image.png](http://upload-images.jianshu.io/upload_images/5786888-5ff0bcbfd882b594.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-4f9c3b84ceec8529.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
再来看看 client端 控制台打印：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-3a27a5d82432f0b8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
意思是得到刷新指令后，重新从server获取 配置仓库的内容。

**注意！@RefreshScope和 @Configuration在同一类上使用有冲突**：
>	@RefreshScope works (technically) on an @Configuration class, but it might lead to surprising behaviour: e.g. it does not mean that all the @Beans defined in that class are themselves @RefreshScope. Specifically, anything that depends on those beans cannot rely on them being updated when a refresh is initiated, unless it is itself in @RefreshScope (in which it will be rebuilt on a refresh and its dependencies re-injected, at which point they will be re-initialized from the refreshed @Configuration).这并不意味着在该类中定义的所有@Beans本身都是@RefreshScope。 具体来说，任何依赖这些bean的东西都不能依赖于它们在刷新时被更新，除非它本身在@RefreshScope中（在刷新时它将被重建，并且它的依赖关系被重新注入，在这一点上它们将被 从刷新的@Configuration重新初始化）。

