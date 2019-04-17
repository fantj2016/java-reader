>上次的那个springboot和dubbo的整合版本中，dubbo的版本是2.5.3，它的Service注解和事务不能同时使用，会造成扫描失效，2.6.2的dubbo版本已经纠正了此不便，官方也给出了与springboot整合的quick start ，但是又缺少与zk的整合部分，所以我在这里只讲述在dubbo-spring-boot-starter依赖里，需要添加的zk依赖，只拿服务提供者来举例。

### 1. 导入依赖
```
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

`dubbo-spring-boot-starter`版本和springboot版本的相关性：

|versions|	Java	|Spring Boot	|Dubbo|
|:------:|:------:|:-------:|:-----:|
|0.2.0	|1.8+	|2.0.x|	2.6.2 +|
|0.1.1	|1.7+	|1.5.x|	2.6.2 +|

当然，这只是dubbo的，我们还需要添加 zookeeper的依赖
```
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.2</version>
        </dependency>

```
### application.properties
```
# Spring boot application
spring.application.name = user-server
management.port = 9091

# Base packages to scan Dubbo Components (e.g., @Service, @Reference)
# 需要扫描的包
dubbo.scan.basePackages  = com.tyut.user.service
# Dubbo Config properties
## ApplicationConfig Bean
dubbo.application.id = user-server
dubbo.application.name = user-server

## ProtocolConfig Bean
dubbo.protocol.id = dubbo
dubbo.protocol.name = dubbo
dubbo.protocol.port = 20880

## RegistryConfig Bean
dubbo.registry.id = my-registry
# 这里是zk的连接配置
dubbo.registry.address = zookeeper://47.xxx.2xx.xx:2181

```

### 其它照旧,旧文章链接： [SpringBoot 整合 Dubbo&Zookeeper 实现分布式（特别完整）](https://www.jianshu.com/p/78835f740404)

### apache 官方文档：https://github.com/apache/incubator-dubbo-spring-boot-project
