
### 1. 问题发现
在1.0版本中，我们配置redis的cacheManager是这种方式：
```
    //缓存管理器
    @Bean
    public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间
        cacheManager.setDefaultExpiration(10000);
        return cacheManager;
    }    //缓存管理器
```
然而在2.0版本中，这个代码直接报错，原因是RedisCacheManager取消了1.0版本中的`public RedisCacheManager(RedisOperations redisOperations)`的这个构造方法，所以我们无法再用`RedisTemplate`作为参数来自定义`CacheManager`。

下面看一看两个版本的差别：

###### 1.0 版本的CacheManager构造器
![](https://upload-images.jianshu.io/upload_images/5786888-4b9cd30262f4cec9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 2.0 版本的CacheManager构造器
![](https://upload-images.jianshu.io/upload_images/5786888-0b8cd322165452c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

`RedisCacheWriter`提供了对Redis的set、setnx、get等命令的访问权限，可以由多个缓存实现共享，并负责写/读来自Redis的二进制数据。

`RedisCacheConfiguration`根据名字都能想到它是提供redis的配置。


### 2. springboot2.0 中 CacheManager自定义配置
```
    /**
     * 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置CacheManager的值序列化方式为json序列化
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
                                                    .fromSerializer(jsonSerializer);
        RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig()
                                                    .serializeValuesWith(pair);
        //设置默认超过期时间是30秒
        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }
```
上面的代码中，还设置了`CacheManager`的值序列化方式，所以有了这个配置，可以直接在注解的形式中实现json的redis存储而不用再去多写配置。
