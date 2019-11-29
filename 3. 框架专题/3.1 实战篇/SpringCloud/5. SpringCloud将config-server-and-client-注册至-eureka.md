##1.将config server注册至eureka
* pom不变
* 启动类添加注解`@EnableDiscoveryClient`（不用引入eurekaclient，因为得导入依赖）
*  application
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/jiaofanting/spring-cloud-config-repo/
  application:
    name: config-server-eureka
server:
  port: 8080
eureka:
  client:
    service-url:
      defaultZone:  http://127.0.0.1:9000/eureka
```
可以说就是正常注册
##2.将config client注册至eureka
* pom不变
* 启动类加注解`@EnableDiscoveryClient`（需要引入eureka client依赖）
* bootstrap
```
spring:
  cloud:
    config:
      discovery:
        enabled: true
        service-id: config-server-eureka
  application:
    name: config-client-eureka
eureka:
  client:
    service-url:
      defaultZone:  http://127.0.0.1:9000/eureka
```
application只设置了端口，这里我就不贴代码了
这段配置有个重点就是
```
spring:
  cloud:
    config:
      discovery:
        enabled: true
        service-id: config-server-eureka
```
连接config server方法变了。我们来看看这个`enabled`是什么
![image.png](http://upload-images.jianshu.io/upload_images/5786888-3d004dc4c3f77922.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>Flag to indicate that config server discovery is enabled (config server URL will be looked up via discovery).

意思是 `enabled`这个标记表明server 服务发现是开启的（server url将被发现）
意思就是 会从eureka发现列表里找到config server 的url
![image.png](http://upload-images.jianshu.io/upload_images/5786888-908de444343edd5d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
访问config client 的controller 的 /profile方法
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7057da0f7a07bc71.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
