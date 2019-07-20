用过Redis的都知道，Redis有两种持久化方式：RDB和AOF，他们的区别大家应该都清楚，所以今天主要想分享一下这两种持久化方式的底层原理以及实现。

如果让你手写一个持久化(架构级)的功能，你没有思路的话，那希望这个文章可以给你灵感。


### 1. RDB持久化

#### 1.1 创建
>简单回顾下RDB文件的创建。

有两种创建方式：
1. save.阻塞进程去处理(期间不处理别的请求)
2. bgsave.派生一个子进程去处理

#### 1.2 载入
>在redis服务启动时，如果检测到RDB文件，会进行自动载入。

##### 如果RDB文件和AOF都存在，优先载入谁？
如果开启了AOF，则会优先AOF

#### 1.3 save的底层实现

```
save 900 1  
save 300 10  
save 60 10000  
```
这是`redis.conf`配置文件中关于RDB save时机的配置，它映射在`redisServer`结构体的`saveparams`字段中：
```
struct redisServer{
    ....
    // 保存了redis.conf配置的属性
    struct saveparam *saveparams;
    
    // 记录上一次save的时间
    time_t lastsave;
    
    // 修改计数器
    long long dirty;
    ...
};
```
那来看看它怎么保存的：
```
struct saveparam {
    // 秒数
    time_t seconds;
    // 修改次数
    int changes;
};
```

redis自己有一个定时任务每100毫秒执行一次，其中有一个任务就是检查save条件是否满足，如何判断的呢？就是用`lastsave`与`saveparam.seconds`比较时间是否满足，`dirty`与`changes`比较修改次数是否满足。

那bgsave如何实现呢，new一个子线程，然后拷贝个数据副本，然后和save一样处理。

好了，到这里，用Java写一个这应该是没问题了，那RDB的文件结构如何设计呢？
我们来看看redis的设计。
#### 1.4 RDB文件结构
>REDIS+数据库版本号+数据类型+数据+EOF(表示数据结束)(377)+检验和

我们知道java中Class文件结构很复杂，因为它包含了常量、接口、类、父类、字段等面向对象的信息，而RDB的就比较简单了，因为它只需要存放数据即可。

和class结构一样，它的开头也是文件标识`REDIS`+版本号标识.

```
[root@izuf6i2jk9azj2te13kjx8z redis-4.0.9]# od -c dump.rdb 
0000000   R   E   D   I   S   0   0   0   8 372  \t   r   e   d   i   s
0000020   -   v   e   r 005   4   .   0   .   9 372  \n   r   e   d   i
0000040   s   -   b   i   t   s 300   @ 372 005   c   t   i   m   e 302
0000060 231   ; 017   ] 372  \b   u   s   e   d   -   m   e   m 302 310
0000100   p  \r  \0 372  \f   a   o   f   -   p   r   e   a   m   b   l
0000120   e 300  \0 376  \0 373   (  \0  \0 006   k   -   7   5   9   9
0000140 006   v   -   7   5   9   9  \0 022   c   p   t   : 254 355  \0
0000160 005   t  \0  \a   g   e   t   O   n   e   4 303   L 220   ^ 303
0000200 037 254 355  \0 005   s   r  \0   %   c   o   m   .   f   a   n
0000220   t   .   c   o   r   e   .   r   e   s   p   o   n   s   e   .
0000240   S 005   e   r   v   e   r   R 240 016 030 222 224   e 250   :
0000260 035 323   ? 002  \0 003   I  \0 006   s   t   a   t   u   s   L
0000300  \0 004   d   a      \t 031  \0 022   L   j   a   v   a   /   l
0000320   a   n   g   /   O   b   j   e   c   t   ;   L  \0 003   m   s
0000340   g 340 005 032  \f   S   t   r   i   n   g   ;   x   p  \0  \0
0000360  \0 310       y  \0 036 340 005   y 037   p   o   j   o   .   C
0000400   o   m   p   e   t   i   t   i   o   n   Z 276 231 334   b 025

...
0140540  \a 004   j   a   v   a 377  \v 006   n   u   m   b   e   r 024
0140560 002  \0  \0  \0 006  \0  \0  \0 001  \0 002  \0 003  \0 004  \0
0140600 005  \0 006  \0 016 004   l   i   s   t 001 027 027  \0  \0  \0
0140620 024  \0  \0  \0 006  \0  \0 362 002 363 002 364 002 365 002 366
0140640 002 367 377 377   - 022 036   ] 367 332 257   _

```
分析：
```
R   E   D   I   S:RDB文件标志
0   0   0   8：版本号
372:结束符
r   e   d   i   s
0000020   -   v   e   r 005   4   .   0   .   9:redis-version4.0.9
r   e   d   i
0000040   s   -   b   i   t   s 300   @:redis的位数64或32
c   t   i   m   e 302 0000060 231   ; 017   ]:时间戳
u   s   e   d   -   m   e   m 302 310 0000100   p  \r  \0:redis使用内存的大小
374：RDB_OPCODE_EXPIRETIME_MS(带有过期时间标识)
\0: 表示字符串
最后8字节为校验和
```
更详细的可以查看http://redisbook.com/preview/rdb/rdb_struct.html


手写过Jedis的朋友都熟悉RESP协议，RDB的数据段和它的排版方式很相似。
比如：`\0 \0 003 m s g 005 h e l l o 377`就表示键值对：`msg(3个长度):hello(5个长度)`


### AOF
>AOF以拼接和重写命令的方式来实现。

```
# 是否开启aof
appendonly yes

# 文件名称
appendfilename "appendonly.aof"

# 同步方式
##每次收到写命令就立即强制写入磁盘，最慢的，但是保证完全的持久化，不推荐使用
# appendfsync always      
##每秒钟强制写入磁盘一次，在性能和持久化方面做了很好的折中，系统默认
appendfsync everysec    
##完全依赖os，性能最好,持久化没保证
# appendfsync no    

# aof重写期间是否同步
no-appendfsync-on-rewrite no

# 重写触发配置
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# 加载aof时如果有错如何处理
aof-load-truncated yes

# 文件重写策略
aof-rewrite-incremental-fsync yes
```

这一段配置中，大家着重理解同步方式的配置。redis默认采用的每秒一次写入AOF文件的策略。
#### 实现原理
```
struct redisServer {

    // ...
    // 存放AOF缓冲
    sds aof_buf;

    // ...
};
```
当有新的命令进来，redis就会将其(协议化后)追加到`aof_buf`的末尾。

同理，redis的事件循环也会监听AOF的配置，如果满足配置文件中的同步方式`appendfsync everysec等`，就会将`aof_buf`中的内容保存到AOF文件里。


#### 为什么要进行AOF重写
>我们知道，redis对AOF有重写机制，用来控制AOF文件的大小。

1. AOF体积过大不利于存储。
2. AOF体积过大，使用AOF数据还原的时间更长。

#### AOF重写多个键值对的数据一定是使用一条数据完成吗
>发生在重写列表、哈希表、集合、有序集合可能会带有多个元素的键时。

不是，如果它的值超过64项，则会用多条命令来完成。(避免客户端输入缓冲区溢出)

#### AOF谁来执行

Redis不希望AOF重写造成服务器阻塞，所以用子进程(带有数据副本)去处理。

#### AOF期间有新的数据进来会导致AOF文件与当前数据不一致吗
不会。为了解决这个问题，Reids设置了AOF重写缓冲区(创建子进程后开启)，当Redis执行命令时，redis会同时将这个信息发送给`aof_buf`和AOF重写缓冲区。

### 扩展

#### 过期键的删除策略

1. 定时删除。过期键较多的情况下，大量的CPU用于删除键而影响了客户端的请求。
2. 惰性删除。只有过期键被访问才删除，可能会导致过期键过多，造成内存浪费和溢出。
3. 定期删除。限制时长和频率对过期键进行删除，难点在于时长和频率难以确定。

#### Redis的过期键删除策略
Redis采用的是惰性删除和定期删除，配合这两种策略来取得CPU和内存的平衡。


#### RDB和AOF文件中会包含过期键吗
不包含。

在生成RDB和AOF文件时，程序会对键进行检查，已过期的键不保存到文件中。