###Jedis是什么？
那jedis就是集成了redis的一些命令操作，封装了redis的java客户端。提供了连接池管理。一般不直接使用jedis，而是在其上在封装一层，作为业务的使用。如果用spring的话，可以看看spring 封装的 redis [Spring Data Redis](https://link.zhihu.com/?target=http%3A//projects.spring.io/spring-data-redis/)
·###Jedis基本使用
```
//生成一个Jedis对象，这个对象负责和指定Redis节点进行通信
Jedis jedis = new Jedis("127.0.0.1",6379);
//jedis执行set操作
jedis.set("hello","world");
//jedis执行get操作
String value = jedis.get("hello");
//incr key 自增1，如果key不存在，自增后get(key)=1
jedis.incr("counter");
//2.hash
jedis.hset("myhash","f1","v1");
jedis.hset("myhash","f2","v2");
//hash输出结果
jedis.hgetAll("myhash");
//3.list
jedis.rpush("mylist","1");
jedis.rpush("mylist","2");
jedis.rpush("mylist","3");
//输出结果[1,2,3]
jedis.lrange("mylist",0,-1);
//4.set
jedis.sadd("myset","a");
jedis.sadd("myset","b");
jedis.sadd("myset","c");
//输出结果
jedis.smembers("myset");
//5.zset
jedis.zadd("myzset",1,"jiao");
jedis.zadd("myzset",2,"fant");
jedis.zadd("myzset",3,"j");
//输出结果
jedis.zrangeWithScores("myzset",0,-1);
```
###Jedis 连接池
1. 直接连接
  * 优点：简单方便，适用于少量长期连接
  * 缺点：存在每次新建/关闭TCP开销；资源无法控制，存在连接泄露可能；线程不安全
2. Jedis连接池
  * 优点：Jedis预先生成，降低开销使用；连接池的形式保护和控制资源的使用
  * 缺点：相对于直连，使用相对麻烦，尤其在资源的管理上需要很多参数来保证，一旦规划不合理也会出现问题。

Redis连接池工具类 `RedisPool.java`
```
package com.answer.admin.util.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis连接池工具
 * Created by Fant.J.
 * 2017/10/24 20:55
 */
public class RedisPool {

    private static JedisPool jedisPool = null;
    private static Jedis jedis;
    static {
        jedis = getJedisPool().getResource();
    }
    /**
     * 构建redis连接池
     */
    public static JedisPool getJedisPool(){
        if (jedisPool == null){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(1024);//可用连接实例的最大数目,如果赋值为-1,表示不限制
            config.setMaxIdle(5);// 控制一个Pool最多有多少个状态为idle(空闲的)jedis实例,默认值也是8
            config.setMaxWaitMillis(1000*100);// 等待可用连接的最大时间,单位毫秒,默认值为-1,表示永不超时/如果超过等待时间,则直接抛出异常
            config.setTestOnBorrow(true);// 在borrow一个jedis实例时,是否提前进行validate操作,如果为true,则得到的jedis实例均是可用的
            jedisPool = new JedisPool(config, "192.168.218.129", 6379);
        }
        return jedisPool;
    }
    /**
     * 释放redis资源
     */
    public static void returnResource(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }
}

```
使用
```
package com.answer.admin.util.redis;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Fant.J.
 * 2017/10/24 20:48
 */

public class RedisTest {
    /** 普通用法 */
    @Test
    public void redisLearn(){
        Jedis jedis = new Jedis("192.168.218.129",6379);
        String result = jedis.get("hello");
        System.out.println(result);
    }
    /** 连接池用法,切记操作结束要施放资源 */
    @Test
    public void redisPoolLearn(){
        String value = "hello";
        String result = null;
        Jedis jedis = null;
        try {
            JedisPool jedisPool = RedisPool.getJedisPool();
            jedis = jedisPool.getResource();
            result = jedis.get(value);
        }catch (Exception e){
            RedisPool.returnResource(jedis);//施放资源
            e.printStackTrace();
        }finally {
            RedisPool.returnResource(jedis);
        }
        System.out.println(result);
    }
}

```
