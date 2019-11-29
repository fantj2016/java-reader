在使用ribbon的过程中，难免会遇到对不同服务的负载均衡策略不同。
我举个例子。
A服务开了两个端口
B服务开了两个端口
如果使用默认配置。ribbon使用的是轮询策略。
但是如果业务需要A轮询，B随机。
我们该怎么做呢。（提示：ribbon默认扫描包是启动类所在包下com.fantj.ribbon）
####方案一：
 * 写一段配置，扔到该包外面com.fantj下
```

/**
 * Created by Fant.J.
 * 2017/11/30 18:56
 */
@SuppressWarnings("ALL")
@Configuration
//@RibbonClient(name = "provider-user",configuration = Config.class)
public class Config {
    @Autowired
    IClientConfig clientConfig;
    @Bean
    @ConditionalOnMissingBean
    public IRule ribbonRule(IClientConfig clientConfig) {
        return new RandomRule();   //随机访问策略
    }
}

```
####方案二：
自定义注解，并加载到启动类
自定义注解：
```
package com.fantj.fantjconsumermovieribbon;

/**
 * Created by Fant.J.
 * 2017/12/1 17:06
 */
public @interface ExcludeFromComponentScan {
}
```
启动项添加注解@ComponentScan
![image.png](http://upload-images.jianshu.io/upload_images/5786888-99aa42b72f0f47ea.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

其中
@RibbonClient(name = "provider-user",configuration = Config.class)
这个注解的作用是，告诉ribbon  ，`provider-user`这个服务的策略配置在`Config.class`类里面。所以我在Config.java里把`//@RibbonClient(name = "provider-user",configuration = Config.class)
`注释掉了。两者作用相同。
最后，在Config.java里添加注解`@ExcludeFromComponentScan`
重启，便会看到`provider-user`这个服务端口访问策略是随机，别的服务访问策略是轮询。
####方案三
写入配置文件application.yml（**ribbon读取配置文件的优先级最高**）
看下官方的文档：
```
users:    #这里修改成自己的微服务id
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule  #这里是Rule
```
修改成自己的
```
provider-user:  #微服务id
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule  #Rule，这个是权重响应事件Rule
```
