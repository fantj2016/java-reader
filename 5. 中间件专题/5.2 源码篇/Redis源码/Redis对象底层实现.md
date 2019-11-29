>上一章我们讲了Redis的底层数据结构，不了解的人可能会有疑问：这个和平时用的五大对象有啥关系呢？这一章我们就主要解释他们所建立的联系。

看这个文件之前，如果对ziplist、skiplist、intset等数据结构不熟悉的话，建议先回顾一下上一章节：xxxxx

### 0. 五类对象分别是什么
>五类对象就是我们常用的string、list、set、zset、hash


### 1. 为什么要有对象
>我们平时主要是通过操作对象的api来操作redis，而不是通过它的调用它底层数据结构来完成（外观模式）。但我们还需要了解其底层，只有这样才能写最优化高效的代码。


1. 跟java一样，对象使开发更方便简洁，降低开发门槛。开发者不需要了解其复杂的底层API，直接调用高层接口即可实现开发。
2. Redis根据对象类型来判断命令是否违法，如果你set key value1 value2就报错。
3. 对象下可以包含多种数据结构，使数据存储更加多态化。(下面主讲)
4. Reids基于对象做了垃圾回收(引用计数法)。
5. 对象带有更丰富的属性，来帮助redis实现更高级的功能。(比如对象的闲置时间)。

### 2. Redis对象(RedisObject)源码分析

```
typedef struct redisObject {

    // 类型
    unsigned type:4;

    // 编码
    unsigned encoding:4;

    // 指向底层实现数据结构的指针
    void *ptr;

    // ...

} robj;
```

#### type字段
>记录对象类型。

我们平时用的命令`type <key>`，其实就是返回这个字段的属性。
```
127.0.0.1:6379> set hello world
OK
127.0.0.1:6379> type hello
string
127.0.0.1:6379> rpush list 1 2 3
(integer) 3
127.0.0.1:6379> type list
list
...
```
那type有多少中类型呢？看下面这个表:

对象|type 字段|	TYPE命令的输出
---|---|---
字符串对象|	REDIS_STRING |"string"|
|列表对象|	REDIS_LIST	|"list"|
|哈希对象|	REDIS_HASH	|"hash"|
|集合对象|	REDIS_SET	|"set"|
|有序集合对象|	REDIS_ZSET	|"zset"|


#### encoding字段
>记录对象使用的编码(数据结构)，Reids中称数据结构为encoding。


我们可以这样查看我们redis对象中的encoding:
```
127.0.0.1:6379> object encoding hello
"embstr"
127.0.0.1:6379> object encoding list
"quicklist"
...
```

既然它是标明该`redisObject`是使用的什么数据结构，那肯定也有个对应的表：

类型|	编码|	对象
----|---|---
REDIS_STRING|	REDIS_ENCODING_INT|	使用整数值实现的字符串对象。
REDIS_STRING|	REDIS_ENCODING_EMBSTR|	使用 embstr 编码的简单动态字符串实现的字符串对象。
REDIS_STRING|	REDIS_ENCODING_RAW|	使用简单动态字符串实现的字符串对象。
REDIS_LIST|	REDIS_ENCODING_ZIPLIST|	使用压缩列表实现的列表对象。
REDIS_LIST|	REDIS_ENCODING_LINKEDLIST|	使用双端链表实现的列表对象。
REDIS_HASH|	REDIS_ENCODING_ZIPLIST|	使用压缩列表实现的哈希对象。
REDIS_HASH|	REDIS_ENCODING_HT|	使用字典实现的哈希对象。
REDIS_SET|	REDIS_ENCODING_INTSET|	使用整数集合实现的集合对象。
REDIS_SET|	REDIS_ENCODING_HT|	使用字典实现的集合对象。
REDIS_ZSET|	REDIS_ENCODING_ZIPLIST|	使用压缩列表实现的有序集合对象。
REDIS_ZSET|	REDIS_ENCODING_SKIPLIST|	使用跳跃表和字典实现的有序集合对象。

我们可以看到，Redis对对象的底层encoding分的很细，String类型就有三个，其它四个对象都分别有两种不同的底层数据结构的实现。他们有一规律，就是用`ziplist`、`intset`、`embstr`来实现少量的数据，数据量一旦庞大，就会升级到`skiplist`、`raw`、`linkedlist`、`ht`来实现，后面我会仔细讲解。


### 3. 分别分析各个对象的底层编码实现(数据结构)

#### 3.1 字符串(string)
>字符串编码有三个：int、raw、embstr。

##### 3.1.1 int
>当string对象的值全部是数字，就会使用int编码。

```
127.0.0.1:6379> set number 123455
OK
127.0.0.1:6379> object encoding number
"int"
```
##### 3.1.2 embstr
>字符串或浮点数长度小于等于39字节，就会使用embstr编码方式来存储，embstr存储内存一般很小，所以redis一次性分配且内存连续(效率高)。

```
127.0.0.1:6379> set shortStr "suwe suwe suwe"
OK
127.0.0.1:6379> object encoding shortStr
"embstr"
```
##### 3.1.2 raw
>当一个字符串或浮点数长度大于39字节，就使用SDS来保存，编码为raw，由于不确定值的字节大小，所以键和值各分配各的，所以就分配两次内存(回收也是两次)，同理它一定不是内存连续的。

```
127.0.0.1:6379> set longStr "hello everyone, we dont need to sleep around to go aheard! do you think?"
OK
127.0.0.1:6379> object encoding longStr
"raw"
```

##### 3.1.3 编码转换
>前面说过，Redis会自动对编码进行转换来适应和优化数据的存储。

int->raw
>条件：数字对象进行append字母，就会发生转换。
```
127.0.0.1:6379> object encoding number
"int"
127.0.0.1:6379> append number " is a lucky number"
(integer) 24
127.0.0.1:6379> object encoding number
"raw"
```
embstr->raw
>条件：对embstr进行修改，redis会先将其转换成raw，然后才进行修改。所以embstr实际上是只读性质的。
```
127.0.0.1:6379> object encoding shortStr
"embstr"
127.0.0.1:6379> append shortStr "(hhh"
(integer) 18
127.0.0.1:6379> object encoding shortStr
"raw"
```


#### 3.2 列表(list)
>列表对象编码可以是：ziplist或linkedlist。

1. `ziplist`压缩列表不知道大家还记得不，就是`zlbytes  zltail  zllen  entry1 entry2 ..end`结构,`entry节点`里有`pre-length、encoding、content`属性，忘记的可以返回去看下。

2. `linkedlist`,类似双向链表，也是上一章的知识。

##### 3.2.1 编码转换

ziplist->linkedlist
>条件：列表对象的所有字符串元素的长度大于等于64字节 & 列表元素数大于等于512. 反之，小于64和小于512会使用ziplist而不是用linkedlist。

>这个阈值是可以修改的，修改选项：`list-max-ziplist-value`和`list-max-ziplist-entriess`


#### 3.3 哈希(hash)
>哈希对象的编码有:ziplist和hashtable


##### 3.3.1 编码转换
ziplist->hashtable
>条件：哈希对象所有键和值字符串长度大于等于64字节 & 键值对数量大于等于512

>这个阈值也是可以修改的，修改选项：`hash-max-ziplist-value`和`hash-max-ziplist-entriess`



#### 3.4. 集合(set)
>集合对象的编码有：intset和hashtable

##### 3.4.1 intset

1. 集合对象所有元素都是整数
2. 集合对象元素数不超过512个

##### 3.4.2 编码转换
intset->hashtable
>条件：元素不都是整数 & 元素数大于等于512

#### 3.5. 有序集合(zset)
>有序集合用到的编码：ziplist和skiplist

大家可能很好奇阿，ziplist的entry中只有属性content可以存放数据，集合也是`key-value`形式，那怎么存储呢?
>第一个节点保存key、第二个节点保存value 以此类推...

##### 3.5.1 为什么要用这两个编码
1. 如果只用ziplist来实现，无法做到元素的排序，不支持范围查找，能做到元素的快速查找。
2. 如果只用skiplist来实现，无法做到快速查找，但能做到元素排序、范围操作。


##### 3.5.2 编码转换

ziplist->skiplist
>条件：有序集合元素数 >= 128 & 含有元素的长度 >= 64

>这个阈值也是可以修改的，修改选项：`zset-max-ziplist-value`和`zset-max-ziplist-entriess`

### 4. 垃圾回收
>为什么要说内存回收呢，因为redisObject有一个字段：
```
typedef struct redisObject {

    // ...

    // 引用计数
    int refcount;

    // ...

} robj;
```
redis的垃圾回收采用引用计数法(和jvm一样)，底层采用一个变量对对象的使用行为进行计数。
* 初始化为1
* 对象被引用，+1
* 对象引用消除，-1
* 计数器==0, 回收对象



### 5. 对象共享


#### 5.1 对象共享的体现
1. redis中，值是整数值且相等的两个对象，redis会将该对象进行共享，且引用计数+1
2. redis启动会自动生成0-9999的整数值放到内存中来共享。

#### 5.2 为什么要对象共享
节约内存

#### 5.3 为什么不对字符串进行共享
>成本太高。

验证整数相等只需要O(1)的时间复杂度，而验证字符串要O(n).


### 6. 对象的空闲时长
>最后，redisObject还有一个字段,记录了对象最后一次被访问的时间：
```
typedef struct redisObject {

    // ...

    unsigned lru:22;

    // ...

} robj;
```

因为这个字段记录对象最后一次被访问的时间，所以它可以用来查看该对象多久未使用，即：用当前时间-lru

```
127.0.0.1:6379> object idletime hello
(integer) 5110
```

它还关系到redis的热点数据实现，如果我们选择lr算法，当内存超出阈值后会对空闲时长较高的对象进行释放，回收内存。


参考文献:
1. 《Redis设计与实现》黄健宏著
2. http://redisbook.com/index.html
