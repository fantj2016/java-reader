面试中，redis也是很受面试官亲睐的一部分。我向在这里讲的是redis的底层数据结构，而不是你理解的五大数据结构。你有没有想过redis底层是怎样的数据结构呢，他们和我们java中的HashMap、List、等使用的数据结构有什么区别呢。


### 1. 字符串处理(string)
我们都知道redis是用C语言写，但是C语言处理字符串和数组的成本是很高的，下面我分别说几个例子。

#### 没有数据结构支撑的几个问题
1. 及其容易造成缓冲区溢出问题，比如用`strcat()`，在用这个函数之前必须要先给目标变量分配足够的空间，否则就会溢出。
2. 如果要获取字符串的长度，没有数据结构的支撑，可能就需要遍历，它的复杂度是O(N)
3. 内存重分配。C字符串的每次变更(曾长或缩短)都会对数组作内存重分配。同样，如果是缩短，没有处理好多余的空间，也会造成内存泄漏。

好了，Redis自己构建了一种名叫`Simple dynamic string(SDS)`的数据结构，他分别对这几个问题作了处理。我们先来看看它的结构源码：
```
struct sdshdr{
     //记录buf数组中已使用字节的数量
     //等于 SDS 保存字符串的长度
     int len;
     //记录 buf 数组中未使用字节的数量
     int free;
     //字节数组，用于保存字符串
     char buf[];
}
```
再来说说它的优点：
1. 开发者不用担心字符串变更造成的内存溢出问题。
2. 常数时间复杂度获取字符串长度`len字段`。
3. 空间预分配`free字段`，会默认留够一定的空间防止多次重分配内存。

更多了解：https://redis.io/topics/internals-sds

这就是string的底层实现，更是redis对所有字符串数据的处理方式(SDS会被嵌套到别的数据结构里使用)。

### 2. 链表
>Redis的链表在双向链表上扩展了头、尾节点、元素数等属性。


![](https://upload-images.jianshu.io/upload_images/5786888-f6280610218dac01.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 2.1 源码

ListNode节点数据结构：
```
typedef  struct listNode{
       //前置节点
       struct listNode *prev;
       //后置节点
       struct listNode *next;
       //节点的值
       void *value;  
}listNode
```
链表数据结构：
```
typedef struct list{
     //表头节点
     listNode *head;
     //表尾节点
     listNode *tail;
     //链表所包含的节点数量
     unsigned long len;
     //节点值复制函数
     void (*free) (void *ptr);
     //节点值释放函数
     void (*free) (void *ptr);
     //节点值对比函数
     int (*match) (void *ptr,void *key);
}list;
```
从上面可以看到，Redis的链表有这几个特点：
1. 可以直接获得头、尾节点。
2. 常数时间复杂度得到链表长度。
3. 是双向链表。


### 3. 字典(Hash)
>Redis的Hash，就是在`数组+链表`的基础上，进行了一些rehash优化等。

![](https://upload-images.jianshu.io/upload_images/5786888-e40d385f15f6c461.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 3.1 数据结构源码

哈希表：
```
typedef struct dictht {
    // 哈希表数组
    dictEntry **table;
    // 哈希表大小
    unsigned long size;
    // 哈希表大小掩码，用于计算索引值
    // 总是等于 size - 1
    unsigned long sizemask;
    // 该哈希表已有节点的数量
    unsigned long used;
} dictht;
```
Hash表节点：
```
typedef struct dictEntry {
    // 键
    void *key;
    // 值
    union {
        void *val;
        uint64_t u64;
        int64_t s64;
    } v;
    // 指向下个哈希表节点，形成链表
    struct dictEntry *next;  // 单链表结构
} dictEntry;
```

字典：
```
typedef struct dict {
    // 类型特定函数
    dictType *type;
    // 私有数据
    void *privdata;
    // 哈希表
    dictht ht[2];
    // rehash 索引
    // 当 rehash 不在进行时，值为 -1
    int rehashidx; /* rehashing not in progress if rehashidx == -1 */
} dict;
```

可以看出：
1. Reids的Hash采用链地址法来处理冲突，然后它没有使用红黑树优化。
2. 哈希表节点采用单链表结构。
3. rehash优化。

下面我们讲一下它的rehash优化。

#### 3.2 rehash
>当哈希表的键对泰国或者太少，就需要对哈希表的大小进行调整，redis是如何调整的呢？

1. 我们仔细可以看到`dict`结构里有个字段`dictht ht[2]`代表有两个dictht数组。第一步就是为ht[1]哈希表分配空间，大小取决于ht[0]当前使用的情况。
2. 将保存在ht[0]中的数据rehash(重新计算哈希值)到ht[1]上。
3. 当ht[0]中所有键值对都迁移到ht[1]后，释放ht[0]，将ht[1]设置为ht[0]，并ht[1]初始化，为下一次rehash做准备。

#### 3.3 渐进式rehash
>我们在3.2中看到，redis处理rehash的流程，但是更细一点的讲，它如何进行数据迁的呢？

这就涉及到了渐进式rehash，redis考虑到大量数据迁移带来的cpu繁忙(可能导致一段时间内停止服务)，所以采用了渐进式rehash的方案。步骤如下：
1. 为ht[1]分配空间，同时持有两个哈希表(一个空表、一个有数据)。
2. 维持一个技术器rehashidx，初始值0。
3. 每次对字典增删改查，会顺带将ht[0]中的数据迁移到ht[1],`rehashidx++`(注意：ht[0]中的数据是只减不增的)。
4. 直到rehash操作完成，rehashidx值设为-1。

它的好处：采用分而治之的思想，将庞大的迁移工作量划分到每一次CURD中，避免了服务繁忙。


### 4. 跳跃表
>这个数据结构是我面试中见过最多的，它其实特别简单。学过的人可能都知道，它和平衡树性能很相似，但为什么不用平衡树而用skipList呢?

![](https://upload-images.jianshu.io/upload_images/5786888-173930379a3690fc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 4.1 skipList & AVL 之间的选择
1. 从算法实现难度上来比较，skiplist比平衡树要简单得多。
2. 平衡树的插入和删除操作可能引发子树的调整，逻辑复杂，而skiplist的插入和删除只需要修改相邻节点的指针，操作简单又快速。
3. 查找单个key，skiplist和平衡树的时间复杂度都为O(log n)，大体相当。
4. 在做范围查找的时候，平衡树比skiplist操作要复杂。
5. skiplist和各种平衡树（如AVL、红黑树等）的元素是有序排列的。

可以看到，skipList中的元素是有序的，所以跳跃表在redis中用在**有序集合键、集群节点内部数据结构**

#### 4.2 源码

跳跃表节点：
```
typedef struct zskiplistNode {

    // 后退指针
    struct zskiplistNode *backward;

    // 分值
    double score;

    // 成员对象
    robj *obj;

    // 层
    struct zskiplistLevel {

        // 前进指针
        struct zskiplistNode *forward;

        // 跨度
        unsigned int span;

    } level[];

} zskiplistNode;
```
跳跃表：
```
typedef struct zskiplist {

    // 表头节点和表尾节点
    struct zskiplistNode *header, *tail;

    // 表中节点的数量
    unsigned long length;

    // 表中层数最大的节点的层数
    int level;

} zskiplist;
```

它有几个概念：
##### 4.2.1 层(level[])
层，也就是`level[]`字段，层的数量越多，访问节点速度越快。(因为它相当于是索引，层数越多，它索引就越细，就能很快找到索引值)

##### 4.2.2 前进指针(forward)
层中有一个`forward`字段，用于从表头向表尾方向访问。

##### 4.2.3 跨度(span)
用于记录两个节点之间的距离

##### 4.2.4 后退指针(backward)
用于从表尾向表头方向访问。


### 案例
```
level0    1---------->5
level1    1---->3---->5
level2    1->2->3->4->5->6->7->8
```
比如我要找键为6的元素，在level0中直接定位到5，然后再往后走一个元素就找到了。


### 5. 整数集合(intset)
>Reids对整数存储专门作了优化，intset就是redis用于保存整数值的集合数据结构。当一个结合中只包含整数元素，redis就会用这个来存储。

```
127.0.0.1:6379[2]> sadd number 1 2 3 4 5 6
(integer) 6
127.0.0.1:6379[2]> object encoding number
"intset"
```


#### 源码

intset数据结构：
```
typedef struct intset {

    // 编码方式
    uint32_t encoding;

    // 集合包含的元素数量
    uint32_t length;

    // 保存元素的数组
    int8_t contents[];

} intset;
```

你肯定很好奇编码方式(encoding)字段是干嘛用的呢？

* 如果 encoding 属性的值为 INTSET_ENC_INT16 ， 那么 contents 就是一个 int16_t 类型的数组， 数组里的每个项都是一个 int16_t 类型的整数值 （最小值为 -32,768 ，最大值为 32,767 ）。
* 如果 encoding 属性的值为 INTSET_ENC_INT32 ， 那么 contents 就是一个 int32_t 类型的数组， 数组里的每个项都是一个 int32_t 类型的整数值 （最小值为 -2,147,483,648 ，最大值为 2,147,483,647 ）。
* 如果 encoding 属性的值为 INTSET_ENC_INT64 ， 那么 contents 就是一个 int64_t 类型的数组， 数组里的每个项都是一个 int64_t 类型的整数值 （最小值为 -9,223,372,036,854,775,808 ，最大值为 9,223,372,036,854,775,807 ）。

说白了就是根据contents字段来判断用哪个int类型更好，也就是对int存储作了优化。

说到优化，那redis如何作的呢？就涉及到了升级。

##### 5.1 encoding升级
如果我们有个Int16类型的整数集合，现在要将65535(int32)加进这个集合，int16是存储不下的，所以就要对整数集合进行升级。

###### 它是怎么升级的呢(过程)？

假如现在有2个int16的元素:1和2，新加入1个int32位的元素65535。

1. 内存重分配，新加入后应该是3个元素，所以分配3*32-1=95位。
2. 选择最大的数65535, 放到(95-32+1, 95)位这个内存段中，然后2放到(95-32-32+1+1, 95-32)位...依次类推。

###### 升级的好处是什么呢？
1. 提高了整数集合的灵活性。
2. 尽可能节约内存(能用小的就不用大的)。

##### 5.2 不支持降级
按照上面的例子，如果我把65535又删掉，encoding会不会又回到Int16呢，答案是不会的。官方没有给出理由，我觉得应该是降低性能消耗吧，毕竟调整一次是O(N)的时间复杂度。


### 6. 压缩列表(ziplist)
>ziplist是redis为了节约内存而开发的顺序型数据结构。它被用在列表键和哈希键中。一般用于小数据存储。

引用https://segmentfault.com/a/1190000016901154中的两个图：

![](https://upload-images.jianshu.io/upload_images/5786888-336a0927247c66c9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-560c7b73056a190d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 6.1 源码
>ziplist没有明确定义结构体，这里只作大概的演示。

```
typedef struct entry {
     /*前一个元素长度需要空间和前一个元素长度*/
    unsigned int prevlengh;
     /*元素内容编码*/
    unsigned char encoding;
     /*元素实际内容*/
    unsigned char *data;
}zlentry;
```

```
typedef struct ziplist{
     /*ziplist分配的内存大小*/
     uint32_t zlbytes;
     /*达到尾部的偏移量*/
     uint32_t zltail;
     /*存储元素实体个数*/
     uint16_t zllen;
     /*存储内容实体元素*/
     unsigned char* entry[];
     /*尾部标识*/
     unsigned char zlend;
}ziplist;
```


第一次看可能会特别蒙蔽，你细细的把我这段话看完就一定能懂。

##### Entry的分析
entry结构体里面有三个重要的字段：
1. previous_entry_length: 这个字段记录了ziplist中前一个节点的长度，什么意思？就是说通过该属性可以进行指针运算达到表尾向表头遍历，这个字段还有一个大问题下面会讲。
2. encoding:记录了数据类型(int16? string?)和长度。
3. data/content: 记录数据。

##### 连锁更新

###### previous_entry_length字段的分析
上面有说到，previous_entry_length这个字段存放上个节点的长度，那默认长度给分配多少呢?redis是这样分的，如果前节点长度小于254,就分配1字节，大于的话分配5字节，那问题就来了。

**如果前一个节点的长度刚开始小于254字节，后来大于254,那不就存放不下了吗？**
这就涉及到previous_entry_length的更新，但是改一个肯定不行阿，后面的节点内存信息都需要改。所以就需要重新分配内存，然后连锁更新包括该受影响节点后面的所有节点。

除了增加新节点会引发连锁更新、删除节点也会触发。


### 7. 快速列表(quicklist)
>一个由ziplist组成的双向链表。但是一个quicklist可以有多个quicklist节点，它很像B树的存储方式。是在redis3.2版本中新加的数据结构，用在列表的底层实现。

![](https://upload-images.jianshu.io/upload_images/5786888-5c5d06777182c89a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 结构体源码
表头结构：
```
typedef struct quicklist {
    //指向头部(最左边)quicklist节点的指针
    quicklistNode *head;

    //指向尾部(最右边)quicklist节点的指针
    quicklistNode *tail;

    //ziplist中的entry节点计数器
    unsigned long count;        /* total count of all entries in all ziplists */

    //quicklist的quicklistNode节点计数器
    unsigned int len;           /* number of quicklistNodes */

    //保存ziplist的大小，配置文件设定，占16bits
    int fill : 16;              /* fill factor for individual nodes */

    //保存压缩程度值，配置文件设定，占16bits，0表示不压缩
    unsigned int compress : 16; /* depth of end nodes not to compress;0=off */
} quicklist;
```
quicklist节点结构:
```
typedef struct quicklistNode {
    struct quicklistNode *prev;     //前驱节点指针
    struct quicklistNode *next;     //后继节点指针

    //不设置压缩数据参数recompress时指向一个ziplist结构
    //设置压缩数据参数recompress指向quicklistLZF结构
    unsigned char *zl;

    //压缩列表ziplist的总长度
    unsigned int sz;                  /* ziplist size in bytes */

    //ziplist中包的节点数，占16 bits长度
    unsigned int count : 16;          /* count of items in ziplist */

    //表示是否采用了LZF压缩算法压缩quicklist节点，1表示压缩过，2表示没压缩，占2 bits长度
    unsigned int encoding : 2;        /* RAW==1 or LZF==2 */

    //表示一个quicklistNode节点是否采用ziplist结构保存数据，2表示压缩了，1表示没压缩，默认是2，占2bits长度
    unsigned int container : 2;       /* NONE==1 or ZIPLIST==2 */

    //标记quicklist节点的ziplist之前是否被解压缩过，占1bit长度
    //如果recompress为1，则等待被再次压缩
    unsigned int recompress : 1; /* was this node previous compressed? */

    //测试时使用
    unsigned int attempted_compress : 1; /* node can't compress; too small */

    //额外扩展位，占10bits长度
    unsigned int extra : 10; /* more bits to steal for future usage */
} quicklistNode;
```

##### 相关配置
在redis.conf中的ADVANCED CONFIG部分：
```
list-max-ziplist-size -2
list-compress-depth 0
```
###### list-max-ziplist-size参数
我们来详细解释一下`list-max-ziplist-size`这个参数的含义。它可以取正值，也可以取负值。

当取正值的时候，表示按照数据项个数来限定每个quicklist节点上的ziplist长度。比如，当这个参数配置成5的时候，表示每个quicklist节点的ziplist最多包含5个数据项。

当取负值的时候，表示按照占用字节数来限定每个quicklist节点上的ziplist长度。这时，它只能取-1到-5这五个值，每个值含义如下：

-5: 每个quicklist节点上的ziplist大小不能超过64 Kb。（注：1kb => 1024 bytes）

-4: 每个quicklist节点上的ziplist大小不能超过32 Kb。

-3: 每个quicklist节点上的ziplist大小不能超过16 Kb。

-2: 每个quicklist节点上的ziplist大小不能超过8 Kb。（-2是Redis给出的默认值）

###### list-compress-depth参数
这个参数表示一个quicklist两端不被压缩的节点个数。注：这里的节点个数是指quicklist双向链表的节点个数，而不是指ziplist里面的数据项个数。实际上，一个quicklist节点上的ziplist，如果被压缩，就是整体被压缩的。

参数list-compress-depth的取值含义如下：

0: 是个特殊值，表示都不压缩。这是Redis的默认值。
1: 表示quicklist两端各有1个节点不压缩，中间的节点压缩。
2: 表示quicklist两端各有2个节点不压缩，中间的节点压缩。
3: 表示quicklist两端各有3个节点不压缩，中间的节点压缩。
依此类推…


Redis对于quicklist内部节点的压缩算法，采用的LZF——一种无损压缩算法。