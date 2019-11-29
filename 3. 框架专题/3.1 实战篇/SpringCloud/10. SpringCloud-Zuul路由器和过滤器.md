>Netflix uses Zuul for the following:     Netflix使用Zuul进行以下操作：
Authentication   认证
Insights洞察
Stress Testing 压力测试
Canary Testing 金丝雀测试
Dynamic Routing 动态路由
Service Migration 服务迁移
Load Shedding 加载脱落
Security 安全
Static Response handling 静态响应处理
Active/Active traffic management 主动/主动流量管理

###1.添加依赖
```
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zuul</artifactId>
		</dependency>
```
zuul也需要注册到eureka
###2.修改启动类
添加注解`@EnableZuulProxy`
我们来看看`@EnableZuulProxy`里面都包含了什么注解
![image.png](http://upload-images.jianshu.io/upload_images/5786888-84a5e90cede0e179.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
有`@EnableCircuitBreaker`实现断路器
`@EnableDiscoveryClient`实现eureka用户端注册
###3.application.yml
和普通eureka客户端配置一样
```
server:
  port: 8040
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:9000/eureka
#加入密码验证
security:
  basic:
    enabled: true
  user:
    name: laojiao
    password: laojiao
spring:
  application:
    name: gateway-zuul
```
###4.启动 查看zuul组件是否添加成功
* 启动eureka
* 启动user3服务
* 启动zuul服务
![image.png](http://upload-images.jianshu.io/upload_images/5786888-f6f1531735a9ce65.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
访问user3服务
![image.png](http://upload-images.jianshu.io/upload_images/5786888-65bb33563588fcf0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
通过zuul访问user3服务
![image.png](http://upload-images.jianshu.io/upload_images/5786888-266a666ab31899b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####5. 自定义访问前缀
```
zuul:
  ignoredPatterns: /**/admin/**    #忽略某个服务的访问
  routes:
    provider-user3: /user/**

或者  serviceId模式
zuul:
  routes:
    users:
      path: /user/**
      serviceId: provider-user3

或者 url 模式(不能实现ribbon的负载均衡和hystrix断路器)
 zuul:
  routes:
    users:
      path: /user/**
      url: http://127.0.0.1:7904
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-81a5cbebc3cf3817.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
**但是要注意一点:**
>These simple url-routes don’t get executed as a HystrixCommand nor can you loadbalance multiple URLs with Ribbon. 
To achieve this, specify a service-route and configure a Ribbon client for the serviceId (this currently requires disabling Eureka support in Ribbon: see above for more information), e.g.
**这些简单的url路由不会作为HystrixCommand执行，也不能使用Ribbon来负载多个URL**。
为此，请**指定服务路由并为serviceId配置功能区客户端**（目前需要在功能区中禁用Eureka支持：有关详细信息，请参阅上文）。
```
zuul:
  routes:
    users:
      path: /user/**
      serviceId: provider-user3
ribbon:
  eureka:
    enabled: false

provider-user3:
  ribbon:
    listOfServers: http://127.0.0.1:7904,http://127.0.0.1:7902
```
这样配置后。我们启动7902，然后访问
![image.png](http://upload-images.jianshu.io/upload_images/5786888-01f401bc7dd2e382.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后看看两个user3服务 日志打印 是否做到负载均衡：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-0fc1ef9208b86511.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image.png](http://upload-images.jianshu.io/upload_images/5786888-8f0f4b52390b1a4e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###6.  /routes  使用
访问zuul服务下的`/routes`，可以查看代理的服务路径列表。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ac103b5151944e94.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###7.Strangulation Patterns and Local Forwards（请求转发）

Zuul代理是一个有用的工具，因为您可以使用它来处理来自旧端点的客户端的所有流量，但将一些请求重定向到新端点。
application.yml
```
 zuul:
  routes:
    first:
      path: /first/**
      url: http://first.example.com
    second:
      path: /second/**
      url: forward:/second
    third:
      path: /third/**
      url: forward:/3rd
    legacy:
      path: /**
      url: http://legacy.example.com
```
解释：
>在这个例子中，我们正在扼杀映射到不匹配其他模式的所有请求的“遗留”应用程序。
 / first / **中的路径已被提取到具有外部URL的新服务中。
 并且/ second / **中的路径被禁止，所以它们可以在本地处理，例如， 与一个正常的Spring @RequestMapping。 
在/ third / **中的路径也被转发，但是具有不同的前缀（即/ third / foo被转发到/ 3rd / foo）。
**注意**：被忽略的模式并不完全被忽略，它们只是不被代理处理（所以它们也被有效地本地转发）。

###7.Uploading Files through Zuul（文件上传）
>如果您使用@EnableZuulProxy，则可以使用代理路径上传文件，只要文件很小，就可以使用。
  **对于大文件**，在“/ zuul / *”中有一条**绕过**Spring DispatcherServlet（避免多部分处理）的替代路径。
**例子：我想通过upload服务上传文件，我在upload的配置文件设置允许的最大文件为2500Mb，但是如果通过zuul来上传`http://super-john:8040/file-upload/upload`的话，如果文件超过10MB,zuul会直接挡掉（因为用的是zuul的默认值）；所以我可以在请求前缀上加个zuul即：`http://super-john:8040/zuul/file-upload/upload`**
servlet路径通过zuul.servletPath进行外部化。
**因为zuul默认通过hystrix和ribbon，所以极大的文件也需要提升超时设置 。**

* hystrix和ribbon的超时设置
application.yml
```
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 60000
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
```
###8.禁用Zuul过滤器
For example to disable `org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter` set `zuul.SendResponseFilter.post.disable=true.`

###9.Providing Hystrix Fallbacks For Routes（提供Hystrix路由回退）
>当Zuul中给定路由的电路被跳闸时，可以通过创建一个类型为ZuulFallbackProvider的bean来提供回退响应。 在这个bean中，**你需要指定fallback的路由ID，并提供一个ClientHttpResponse作为回退**。 这是一个非常简单的ZuulFallbackProvider实现

```
class MyFallbackProvider implements ZuulFallbackProvider {
    @Override
    public String getRoute() {
        return "customers";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("fallback".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```
其中
```
public String getRoute() {
        return "customers";
    }

 public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("fallback".getBytes());
            }
```
`customers`改为自己 微服务的idname `provider-user3`
` new ByteArrayInputStream("fallback".getBytes());`里自定义返回内容

* And here is what the route configuration would look like.
```
zuul:
  routes:
    provider-user3: /user/**
```
