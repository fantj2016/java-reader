>为什么需要给token做持久化，试想如果存储token的服务器宕机后，用户信息也会伴随着失效，用户需要重新登陆来获取token，难免降低了用户体验，所以我们需要像处理session分布式一样，将token持久化。

我的案例是将token存储到redis里。

其实springboot已经帮我们封装了太多的东西了，在[上一章的基础上](https://www.jianshu.com/p/19059060036b)，我们只需要添加不到10行代码，就可以实现redis的持久化。

### 1. 新增 TokenStoreConfig.java
```
/**
 * 把token存到redis
 * Created by Fant.J.
 */
@Configuration
public class TokenStoreConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore redisTokenStore(){
        return new RedisTokenStore(redisConnectionFactory);
    }
}
```
### 2. 新增 application.properties
```
spring.redis.database=0
# Redis服务器地址
spring.redis.host=47.xx4.xx9.xx
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=root
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1
# 连接池中的最大空闲连接
spring.redis.pool.max-idle=8
# 连接池中的最小空闲连接
spring.redis.pool.min-idle=0
# 连接超时时间（毫秒）
spring.redis.timeout=0
```
### 3. 修改 MyAuthorizationServerConfig.java
```
  //新增一个注入
    @Autowired
    private TokenStore tokenStore;

     @Override
     public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore)     //新增这一行
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
  }
```

好了，token在redis里面的存储就完成了。这么简单的吗？对，就是这么简单，因为springboot包装的很好了，如果检测到认证服务，它会先从tokenStore获取对应的token数据，如果tokenStore没有，则新生成一个token并存入redis。



### 拓展
###### 那redis是怎么连接的呢？
我们在第一个类中注入了RedisConnectionFactory 这个工厂，它是个接口，里面包含了关于redis连接的方法。只要你按照spring提供的默认配置属性来配置redis，成功连接是没有问题的。贴一小段连接核心源码证明一下
```
	public RedisConnection getConnection() {

		if (cluster != null) {
			return getClusterConnection();
		}

		Jedis jedis = fetchJedisConnector();
		JedisConnection connection = (usePool ? new JedisConnection(jedis, pool, dbIndex, clientName)
				: new JedisConnection(jedis, null, dbIndex, clientName));
		connection.setConvertPipelineAndTxResults(convertPipelineAndTxResults);
		return postProcessConnection(connection);
	}
```
这是RedisConnectionFactory子类工厂JedisConnectionFactory中的getConnection方法。

###### redis属性如何拿到的呢？
```
package org.springframework.boot.autoconfigure.data.redis;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "spring.redis"
)
public class RedisProperties {
    private int database = 0;
    private String url;
    private String host = "localhost";
    private String password;
    private int port = 6379;
    private boolean ssl;
    private int timeout;
    private RedisProperties.Pool pool;
    private RedisProperties.Sentinel sentinel;
    private RedisProperties.Cluster cluster;

    public RedisProperties() {
    }

```
上面是springboot获取属性配置文件的操作。

（ 顺便也带给大家一个获取application.properties 属性的一个规范的方法。）

中间的装配连接池啥的就不在这说了,有兴趣的可以跟踪源码去看看大世界（不得不称赞springboot的源码写的真是碉堡了，简单粗暴Future 和 Callback用的也是流弊的很）。所以还得自己去看去琢磨。

#### 介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

###### 底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)
