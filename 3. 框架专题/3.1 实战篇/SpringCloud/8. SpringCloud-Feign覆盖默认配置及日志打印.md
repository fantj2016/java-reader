>Spring Cloud的Feign支持的中心概念是指定的客户端。每个假装客户端都是组合的组件的一部分，它们一起工作以根据需要联系远程服务器，并且该集合具有您将其作为应用程序开发人员使用@FeignClient注释的名称。Spring Cloud根据需要，使用FeignClientsConfiguration为每个已命名的客户端创建一个新的集合ApplicationContext。这包含（除其他外）feign.Decoder，feign.Encoder和feign.Contract。

Spring Cloud可以通过使用@FeignClient声明额外的配置（FeignClientsConfiguration）来完全控制假客户端。例：
```
@FeignClient(name = "stores", configuration = FooConfiguration.class)
public interface StoreClient {
    //..
}
```
```
@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface StoreClient {
    //..
}
```
**注意**:
* FooConfiguration不需要使用@Configuration注释。但是，如果是，则请注意将其从任何@ComponentScan中排除，否则将包含此配置，因为它将成为feign.Decoder，feign.Encoder，feign.Contract等的默认来源，指定时。这可以通过将其放置在任何@ComponentScan或@SpringBootApplication的单独的不重叠的包中，或者可以在@ComponentScan中明确排除。
* 只要用到url属性，就必须要有name。


创建一个类型的bean并将其放置在@FeignClient配置（例如上面的FooConfiguration）中）允许您覆盖所描述的每个bean。例：
```
@Configuration
public class FooConfiguration {
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }
}
```
###2.日志
1. application.yml添加
`logging.level.project.user.UserClient: DEBUG`
2. 可以给每个feignclient创建不同的日志级别
```
@Configuration
public class FooConfiguration {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```
