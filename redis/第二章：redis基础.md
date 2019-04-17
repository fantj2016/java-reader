### 1. Redis可执行文件说明
1. redis-server  redis服务器
2. redis-cli    Redis命令行客户端
  redis-cli -h 10.10.10.10 -p 6384  连接ip为x上的redis
3. redis-benchmark    性能测试工具
4. redis-check-aof    AOF文件修复工具
5. redis-check-dump    RDB文件检查工具
6. redis-sentinel    高可用
### 2. 启动方法
1. 最简启动
  redis-server
2. 带参数启动
  redis-server --port xxxx
3. 配置文件启动（首选）
  redis-server configpath
### 3. 常用配置
1. daemonize    是否守护进程，默认no建议 yes
2. port  默认6379
3. logfile  
4. dir  
### 4. 常用命令

1. keys *   计算所有的键
2. dbsize     计算数据库大小
3. exists key
4. del key [key]
5. expire key seconds  设置key过期时间    ttl key 查看剩余过期时间   persist key 去掉key过期时间
6. type key
7. info  查看redis基本信息，（查看工作空间详情）
8. select 1 转到1工作空间
9. save 手动触发备份,该命令将在 redis 安装目录中创建dump.rdb文件。
10. bgsave 该命令在后台执行save
12. expire a 10  给a设置10秒生存期
13. ttl a   查看a还有多久过期
14. rename a b 把a的key 更名为b，b存在的话会被覆盖
15. renamenx a b 把把a的key 更名为b，如果b存在，则不修改（nx后缀就是具有判断逻辑）


### 5. Redis的五大数据结构
###### 5.1 字符串(String)

```
1	SET key value 
设置指定 key 的值
2	GET key 
获取指定 key 的值。
3	GETRANGE key start end 
返回 key 中字符串值的子字符
4	GETSET key value
将给定 key 的值设为 value ，并返回 key 的旧值(old value)。
5	GETBIT key offset
对 key 所储存的字符串值，获取指定偏移量上的位(bit)。
6	MGET key1 [key2..]
获取所有(一个或多个)给定 key 的值。
7	SETBIT key offset value
对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。
8	SETEX key seconds value
将值 value 关联到 key ，并将 key 的过期时间设为 seconds (以秒为单位)。
9	SETNX key value
只有在 key 不存在时设置 key 的值。
10	SETRANGE key offset value
用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始。
11	STRLEN key
返回 key 所储存的字符串值的长度。
12	MSET key value [key value ...]
同时设置一个或多个 key-value 对。
13	MSETNX key value [key value ...] 
同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
14	PSETEX key milliseconds value
这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 SETEX 命令那样，以秒为单位。
15	INCR key
将 key 中储存的数字值增一。
16	INCRBY key increment
将 key 所储存的值加上给定的增量值（increment） 。
17	INCRBYFLOAT key increment
将 key 所储存的值加上给定的浮点增量值（increment） 。
18	DECR key
将 key 中储存的数字值减一。
19	DECRBY key decrement
key 所储存的值减去给定的减量值（decrement） 。
20	APPEND key value
如果 key 已经存在并且是一个字符串， APPEND 命令将 指定value 追加到改 key 原来的值（value）的末尾。
```


###### 5.2  哈希(Hash)
>Redis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。
```

1	HDEL key field1 [field2] 
删除一个或多个哈希表字段
2	HEXISTS key field 
查看哈希表 key 中，指定的字段是否存在。
3	HGET key field 
获取存储在哈希表中指定字段的值。
4	HGETALL key 
获取在哈希表中指定 key 的所有字段和值
5	HINCRBY key field increment 
为哈希表 key 中的指定字段的整数值加上增量 increment 。
6	HINCRBYFLOAT key field increment 
为哈希表 key 中的指定字段的浮点数值加上增量 increment 。
7	HKEYS key 
获取所有哈希表中的字段
8	HLEN key 
获取哈希表中字段的数量
9	HMGET key field1 [field2] 
获取所有给定字段的值
10	HMSET key field1 value1 [field2 value2 ] 
同时将多个 field-value (域-值)对设置到哈希表 key 中。
11	HSET key field value 
将哈希表 key 中的字段 field 的值设为 value 。
12	HSETNX key field value 
只有在字段 field 不存在时，设置哈希表字段的值。
13	HVALS key 
获取哈希表中所有值
14	HSCAN key cursor [MATCH pattern] [COUNT count] 
迭代哈希表中的键值对。
```
###### 5.3  列表(List)
```
1	BLPOP key1 [key2 ] timeout 
移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
2	BRPOP key1 [key2 ] timeout 
移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
3	BRPOPLPUSH source destination timeout 
从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
4	LINDEX key index 
通过索引获取列表中的元素
5	LINSERT key BEFORE|AFTER pivot value 
在列表的元素前或者后插入元素
6	LLEN key 
获取列表长度
7	LPOP key 
移出并获取列表的第一个元素
8	LPUSH key value1 [value2] 
将一个或多个值插入到列表头部
9	LPUSHX key value 
将一个值插入到已存在的列表头部
10	LRANGE key start stop 
获取列表指定范围内的元素
11	LREM key count value 
移除列表元素
12	LSET key index value 
通过索引设置列表元素的值
13	LTRIM key start stop 
对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
14	RPOP key 
移除并获取列表最后一个元素
15	RPOPLPUSH source destination 
移除列表的最后一个元素，并将该元素添加到另一个列表并返回
16	RPUSH key value1 [value2] 
在列表中添加一个或多个值
17	RPUSHX key value 
为已存在的列表添加值
```
###### 5.4 集合(Set)
>Redis 的 Set 是 String 类型的无序集合。集合成员是唯一的，这就意味着集合中不能出现重复的数据。
Redis 中集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是 O(1)。集合中最大的成员数为 232 - 1 (4294967295, 每个集合可存储40多亿个成员)。
```
1	SADD key member1 [member2] 
向集合添加一个或多个成员
2	SCARD key 
获取集合的成员数
3	SDIFF key1 [key2] 
返回给定所有集合的差集
4	SDIFFSTORE destination key1 [key2] 
返回给定所有集合的差集并存储在 destination 中
5	SINTER key1 [key2] 
返回给定所有集合的交集
6	SINTERSTORE destination key1 [key2] 
返回给定所有集合的交集并存储在 destination 中
7	SISMEMBER key member 
判断 member 元素是否是集合 key 的成员
8	SMEMBERS key 
返回集合中的所有成员
9	SMOVE source destination member 
将 member 元素从 source 集合移动到 destination 集合
10	SPOP key 
移除并返回集合中的一个随机元素
11	SRANDMEMBER key [count] 
返回集合中一个或多个随机数
12	SREM key member1 [member2] 
移除集合中一个或多个成员
13	SUNION key1 [key2] 
返回所有给定集合的并集
14	SUNIONSTORE destination key1 [key2] 
所有给定集合的并集存储在 destination 集合中
15	SSCAN key cursor [MATCH pattern] [COUNT count] 
迭代集合中的元素
```
###### 5.5 有序集合(sorted set)
```
1	ZADD key score1 member1 [score2 member2] 
向有序集合添加一个或多个成员，或者更新已存在成员的分数
2	ZCARD key 
获取有序集合的成员数
3	ZCOUNT key min max 
计算在有序集合中指定区间分数的成员数
4	ZINCRBY key increment member 
有序集合中对指定成员的分数加上增量 increment
5	ZINTERSTORE destination numkeys key [key ...] 
计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
6	ZLEXCOUNT key min max 
在有序集合中计算指定字典区间内成员数量
7	ZRANGE key start stop [WITHSCORES] 
通过索引区间返回有序集合成指定区间内的成员
8	ZRANGEBYLEX key min max [LIMIT offset count] 
通过字典区间返回有序集合的成员
9	ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT] 
通过分数返回有序集合指定区间内的成员
10	ZRANK key member 
返回有序集合中指定成员的索引
11	ZREM key member [member ...] 
移除有序集合中的一个或多个成员
12	ZREMRANGEBYLEX key min max 
移除有序集合中给定的字典区间的所有成员
13	ZREMRANGEBYRANK key start stop 
移除有序集合中给定的排名区间的所有成员
14	ZREMRANGEBYSCORE key min max 
移除有序集合中给定的分数区间的所有成员
15	ZREVRANGE key start stop [WITHSCORES] 
返回有序集中指定区间内的成员，通过索引，分数从高到底
16	ZREVRANGEBYSCORE key max min [WITHSCORES] 
返回有序集中指定分数区间内的成员，分数从高到低排序
17	ZREVRANK key member 
返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
18	ZSCORE key member 
返回有序集中，成员的分数值
19	ZUNIONSTORE destination numkeys key [key ...] 
计算给定的一个或多个有序集的并集，并存储在新的 key 中
20	ZSCAN key cursor [MATCH pattern] [COUNT count] 
迭代有序集合中的元素（包括元素成员和元素分值）
```
