### 1. 两个对象的hashCode相同，则equals也一定为true，对吗？
不对，答案见下面的代码：
```
@Override
public int hashCode() {
    return 1;
}
```
##### 两个对象equals为true，则hashCode也一定相同，对吗？

这块肯定是有争议的。面试的时候这样答：如果按照官方设计要求来打代码的话，hashcode一定相等。但是如果不按官方照设计要求、不重写hashcode方法，就会出现不相等的情况。
### 2. java线程池用过没有？
>Executors提供了四种方法来创建线程池。
1. newFixedThreadPool() :创建固定大小的线程池。
2. newCachedThreadPool(): 创建无限大小的线程池，线程池中线程数量不固定，可根据需求自动更改。
3. newSingleThreadPool() : 创建单个线程池，线程池中只有一个线程。
4.  newScheduledThreadPool() 创建固定大小的线程池，可以延迟或定时的执行任务。

手写一个：
```
public static void main(String[] args) {

    ExecutorService threadPool = Executors.newCachedThreadPool();
    threadPool.execute(() -> {
        for (int i = 0; i< 20;i++) {
            System.out.println(Thread.currentThread().getName()+":"+i);
        }
    });
    threadPool.shutdown();
}
```


##### 线程池作用
1. 限制线程个数，避免线程过多导致系统运行缓慢或崩溃。
2. 不需要频繁的创建和销毁，节约资源、响应更快。



### 3. Math.round(-2.5)等于多少？
>不要认为它是四舍五入!不要认为它是四舍五入!不要认为它是四舍五入!

口诀：+0.5后向下取整。所以结果是-2。

留个题，Math.round(-2.6)结果和Math.round(2.6)结果


### 4. 面向对象六大原则
1. 单一职责原则——SRP
>让每个类只专心处理自己的方法。
2. 开闭原则——OCP
>软件中的对象(类，模块，函数等)应该对于扩展是开放的，但是对于修改是关闭的。
3. 里式替换原则——LSP
>子类可以去扩展父类，但是不能改变父类原有的功能。
4. 依赖倒置原则——DIP
>应该通过调用接口或抽象类(比较高层)，而不是调用实现类(细节)。
5. 接口隔离原则——ISP
>把接口分成满足依赖关系的最小接口，实现类中不能有不需要的方法。
6. 迪米特原则——LOD
>高内聚,低耦合。

### 5. static和final区别

|关键词|修饰物|影响|
----|----|----|
|final|变量|分配到常量池中，程序不可改变其值
|final|方法|子类中将不能被重写
|final|类|不能被继承
|static|变量|分配在内存堆上，引用都会指向这一个地址而不会重新分配内存
|static|方法块|虚拟机优先加载
|static|类|可以直接通过类来调用而不需要new


### 6. String s = "hello"和String s = new String("hello");区别

`String s = new String("hello");`可能创建两个对象也可能创建一个对象。如果常量池中有`hello`字符串常量的话，则仅仅在堆中创建一个对象。如果常量池中没有`hello`对象，则堆上和常量池都需要创建。


` String s = "hello"`这样创建的对象，JVM会直接检查字符串常量池是否已有"hello"字符串对象，如没有，就分配一个内存存放"hello"，如有了，则直接将字符串常量池中的地址返回给栈。(没有new，没有堆的操作)


### 7. 引用类型是占用几个字节？

hotspot在64位平台上，占8个字节，在32位平台上占4个字节。

### 8. `(1<3)?"a":"b")+3+4`和`(1<3)?"a":"b")+(3+4)`区别

```
System.out.println(((1<3)?"a":"b")+3+4);
System.out.println(((1<3)?"a":"b")+(3+4));
```
控制台:
```
a34
a7
```

##### 8.1 什么情况下,加号会变成字符串连接符

依据上面的例子来思考。


### 9. java中的switch选择结构可以使用数据类型的数据(JDK1.8)
>![](https://upload-images.jianshu.io/upload_images/5786888-2ac44edc213e77c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

1. char
2. byte
3. short
4. int
5. Character
6. Byte
7. Short
8. Integer
9. String
10. enum

更好的记忆方法:

基本类型中，没有`boolean`和`浮点类型`+`长类型long`.相应的包装类型也没有。

外加`String`和`enum`。

### 10. `4&5``4^5``4&10>>1`各等于多少

```
// 0100 & 0101 = 0100 = 4
System.out.println(4&5);
// 0100 ^ 0101 = 0001 = 1
System.out.println(4^5);
System.out.println(10>>1);
 // 有疑问参考下面的运算符优先级
System.out.println(4&10>>1);
```
```
4
1
5
4
```

##### `4|5`等于多少呢

答案：5

##### 运算符优先级

|运算符|结合性|
----|----
`[ ] . ( )` (方法调用)	|从左向右
`! ~ ++ -- +`(一元运算) -(一元运算)	|从右向左
`* / %`	|从左向右
`+ -`	|从左向右
`<< >> >>>`	|从左向右
`< <= > >=` instanceof	|从左向右
`== !=`	|从左向右
`&`	|从左向右
`^`	|从左向右
`|`	|从左向右
`&&	`|从左向右
`||`	|从左向右
`?:	`| 从右向左
`=`| 从右向左


### 11. 某些java类为什么要实现Serializable接口
为了网络进行传输或者持久化

##### 什么是序列化
将对象的状态信息转换为可以存储或传输的形式的过程

##### 除了实现Serializable接口还有什么序列化方式

* Json序列化
* FastJson序列化
* ProtoBuff序列化
...

### 12. JVM垃圾处理方法
###### 标记-清除算法（老年代）
该算法分为“标记”和“清除”两个阶段: 首先标记出所有需要回收的对象(可达性分析), 在标记完成后统一清理掉所有被标记的对象.
![](https://upload-images.jianshu.io/upload_images/5786888-fe117c02e0afe526.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

该算法会有两个问题：
1. 效率问题，标记和清除效率不高。
2. 空间问题: 标记清除后会产生大量不连续的内存碎片, 空间碎片太多可能会导致在运行过程中需要分配较大对象时无法找到足够的连续内存而不得不提前触发另一次垃圾收集。

所以它一般用于"垃圾不太多的区域，比如老年代"。
###### 复制算法（新生代）
该算法的核心是将可用内存按容量划分为大小相等的两块, 每次只用其中一块, 当这一块的内存用完, 就将还存活的对象（非垃圾）复制到另外一块上面, 然后把已使用过的内存空间一次清理掉.

优点：不用考虑碎片问题，方法简单高效。
缺点：内存浪费严重。

现代商用VM的新生代均采用复制算法, 但由于新生代中的98%的对象都是生存周期极短的, 因此并不需完全按照1∶1的比例划分新生代空间, 而是将新生代划分为一块较大的Eden区和两块较小的Survivor区(HotSpot默认Eden和Survivor的大小比例为8∶1), 每次只用Eden和其中一块Survivor. 当发生MinorGC时, 将Eden和Survivor中还存活着的对象一次性地拷贝到另外一块Survivor上, 最后清理掉Eden和刚才用过的Survivor的空间. 当Survivor空间不够用(不足以保存尚存活的对象)时, 需要依赖老年代进行空间分配担保机制, 这部分内存直接进入老年代。

![](https://upload-images.jianshu.io/upload_images/5786888-c4f578b8b9d56356.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**复制算法的空间分配担保：**
在执行Minor GC前, VM会首先检查老年代是否有足够的空间存放新生代尚存活对象, 由于新生代使用复制收集算法, 为了提升内存利用率, 只使用了其中一个Survivor作为轮换备份, 因此当出现大量对象在Minor GC后仍然存活的情况时, 就需要老年代进行分配担保, 让Survivor无法容纳的对象直接进入老年代, 但前提是老年代需要有足够的空间容纳这些存活对象. 但存活对象的大小在实际完成GC前是无法明确知道的, 因此Minor GC前, VM会先首先检查老年代连续空间是否大于新生代对象总大小或历次晋升的平均大小, 如果条件成立, 则进行Minor GC, 否则进行Full GC(让老年代腾出更多空间).
然而取历次晋升的对象的平均大小也是有一定风险的, 如果某次Minor GC存活后的对象突增,远远高于平均值的话,依然可能导致担保失败(Handle Promotion Failure, 老年代也无法存放这些对象了), 此时就只好在失败后重新发起一次Full GC(让老年代腾出更多空间).


---

###### 标记-整理算法（老年代）
标记清除算法会产生内存碎片问题, 而复制算法需要有额外的内存担保空间, 于是针对老年代的特点, 又有了标记整理算法. 标记整理算法的标记过程与标记清除算法相同, 但后续步骤不再对可回收对象直接清理, 而是让所有存活的对象都向一端移动,然后清理掉端边界以外的内存.
![](https://upload-images.jianshu.io/upload_images/5786888-da40e93e00c99d86.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 13. 新生代、老年代、持久代都存储哪些东西
新生代：
1. 方法中new一个对象，就会先进入新生代。

老年代：

1. 新生代中经历了N次垃圾回收仍然存活的对象就会被放到老年代中。
2. 大对象一般直接放入老年代。
3. 当Survivor空间不足。需要老年代担保一些空间，也会将对象放入老年代。


永久代：
指的就是方法区。


### 14. 可达性算法中，哪些对象可作为GC Roots对象。
1. 虚拟机栈中引用的对象
2. 方法区静态成员引用的对象
3. 方法区常量引用对象
4. 本地方法栈JNI引用的对象


### 15. 什么时候进行MinGC和FullGC

MinGC:

1. 当Eden区满时,触发Minor GC.


FullGC:
1. 调用System.gc时，系统建议执行Full GC，但是不必然执行
2. 老年代空间不足
3. 方法区空间不足
4. 通过Minor GC后进入老年代的平均大小大于老年代的剩余空间
5. 堆中分配很大的对象，而老年代没有足够的空间

### 16. 如何判定对象为垃圾对象
在堆里面存放着Java世界中几乎所有的对象实例, 垃圾收集器在对堆进行回收前, 第一件事就是判断哪些对象已死(可回收).
###### 引用计数法
在JDK1.2之前，使用的是引用计数器算法。
在对象中添加一个引用计数器，当有地方引用这个对象的时候，引用计数器的值就+1，当引用失效的时候，计数器的值就-1，当引用计数器被减为零的时候，标志着这个对象已经没有引用了，可以回收了！


![](https://upload-images.jianshu.io/upload_images/5786888-600690e652e1f10a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
**问题：**如果在A类中调用B类的方法，B类中调用A类的方法，这样当其他所有的引用都消失了之后，A和B还有一个相互的引用，也就是说两个对象的引用计数器各为1，而实际上这两个对象都已经没有额外的引用，已经是垃圾了。但是该算法并不会计算出该类型的垃圾。


###### 可达性分析法
在主流商用语言(如Java、C#)的主流实现中, 都是通过可达性分析算法来判定对象是否存活的: 通过一系列的称为 GC Roots 的对象作为起点, 然后向下搜索; 搜索所走过的路径称为引用链/Reference Chain, 当一个对象到 GC Roots 没有任何引用链相连时, 即该对象不可达, 也就说明此对象是不可用的, 如下图:虽然E和F相互关联， 但它们到GC Roots是不可达的, 因此也会被判定为可回收的对象。
![](https://upload-images.jianshu.io/upload_images/5786888-37cee8b2398fc4d7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**注:** 即使在可达性分析算法中不可达的对象, VM也并不是马上对其回收, 因为要真正宣告一个对象死亡, 至少要经历两次标记过程: 第一次是在可达性分析后发现没有与GC Roots相连接的引用链, 第二次是GC对在F-Queue执行队列中的对象进行的小规模标记(对象需要覆盖finalize()方法且没被调用过).


### 17. 你能说出来几个垃圾收集器
##### Serial

Serial收集器是Hotspot运行在Client模式下的**默认新生代收集器**, 它在进行垃圾收集时，会暂停所有的工作进程，用一个线程去完成GC工作
![](https://upload-images.jianshu.io/upload_images/5786888-252c53a3c2bb9915.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


特点：简单高效，适合jvm管理内存不大的情况（十兆到百兆）。
##### Parnew
ParNew收集器其实是Serial的多线程版本，回收策略完全一样，但是他们又有着不同。
![](https://upload-images.jianshu.io/upload_images/5786888-1595c74d6b0c74ac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们说了Parnew是多线程gc收集，所以它配合多核心的cpu效果更好，如果是一个cpu，他俩效果就差不多。（可用-XX:ParallelGCThreads参数控制GC线程数）
##### Cms
CMS(Concurrent Mark Sweep)收集器是一款具有划时代意义的收集器, 一款**真正意义上的并发收集器**, 虽然现在已经有了理论意义上表现更好的G1收集器, 但现在主流互联网企业线上选用的仍是CMS(如Taobao),又称多并发低暂停的收集器。
![](https://upload-images.jianshu.io/upload_images/5786888-a259cf6c47afdc51.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

由他的英文组成可以看出，它是基于标记-清除算法实现的。整个过程分4个步骤：
1. 初始标记(CMS initial mark):仅只标记一下GC Roots能直接关联到的对象, 速度很快
2. 并发标记(CMS concurrent mark: GC Roots Tracing过程)
3. 重新标记(CMS remark):修正并发标记期间因用户程序继续运行而导致标记产生变动的那一部分对象的标记记录
4. 并发清除(CMS concurrent sweep: 已死对象将会就地释放)

可以看到，初始标记、重新标记需要STW(stop the world 即：挂起用户线程)操作。因为最耗时的操作是并发标记和并发清除。所以总体上我们认为CMS的GC与用户线程是并发运行的。

**优点**:并发收集、低停顿

**缺点**：
1.  CMS默认启动的回收线程数=(CPU数目+3)*4
当CPU数>4时, GC线程最多占用不超过25%的CPU资源, 但是当CPU数<=4时, GC线程可能就会过多的占用用户CPU资源, 从而导致应用程序变慢, 总吞吐量降低.
2. 无法清除浮动垃圾（GC运行到并发清除阶段时用户线程产生的垃圾），因为用户线程是需要内存的，如果浮动垃圾施放不及时，很可能就造成内存溢出，所以CMS不能像别的垃圾收集器那样等老年代几乎满了才触发，CMS提供了参数`-XX:CMSInitiatingOccupancyFraction`来设置GC触发百分比(1.6后默认92%),当然我们还得设置启用该策略`-XX:+UseCMSInitiatingOccupancyOnly`
3. 因为CMS采用标记-清除算法，所以可能会带来很多的碎片，如果碎片太多没有清理，jvm会因为无法分配大对象内存而触发GC，因此CMS提供了`-XX:+UseCMSCompactAtFullCollection`参数，它会在GC执行完后接着进行碎片整理，但是又会有个问题，碎片整理不能并发，所以必须单线程去处理，所以如果每次GC完都整理用户线程stop的时间累积会很长，所以`XX:CMSFullGCsBeforeCompaction`参数设置隔几次GC进行一次碎片整理（默认为0）。



##### G1

同优秀的CMS垃圾回收器一样，G1也是关注最小时延的垃圾回收器，也同样适合大尺寸堆内存的垃圾收集，官方也推荐使用G1来代替选择CMS。G1最大的特点是**引入分区**的思路，**弱化分代**的概念，合理利用垃圾收集各个周期的资源，解决了其他收集器甚至CMS的众多缺陷。


![](https://upload-images.jianshu.io/upload_images/5786888-128c8d9f95e69320.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

因为每个区都有E、S、O代，所以在G1中，不需要对整个Eden等代进行回收，而是寻找可回收对象比较多的区，然后进行回收（虽然也需要STW操作，但是花费的时间是很少的），保证高效率。

###### 新生代收集
G1的新生代收集跟ParNew类似，如果存活时间超过某个阈值，就会被转移到S/O区。

年轻代内存由一组不连续的heap区组成, 这种方法使得可以动态调整各代区域的大小

###### 老年代收集
分为以下几个阶段：
1. 初始标记 (Initial Mark: Stop the World Event)
在G1中, 该操作附着一次年轻代GC, 以标记Survivor中有可能引用到老年代对象的Regions.
2. 扫描根区域 (Root Region Scanning: 与应用程序并发执行)
扫描Survivor中能够引用到老年代的references. 但必须在Minor GC触发前执行完
3. 并发标记 (Concurrent Marking : 与应用程序并发执行)
在整个堆中查找存活对象, 但该阶段可能会被Minor GC中断
4. 重新标记 (Remark : Stop the World Event)
完成堆内存中存活对象的标记. 使用snapshot-at-the-beginning(SATB, 起始快照)算法, 比CMS所用算法要快得多(空Region直接被移除并回收, 并计算所有区域的活跃度).
5. 清理 (Cleanup : Stop the World Event and Concurrent)
在含有存活对象和完全空闲的区域上进行统计(STW)、擦除Remembered Sets(使用Remembered Set来避免扫描全堆，每个区都有对应一个Set用来记录引用信息、读写操作记录)(STW)、重置空regions并将他们返还给空闲列表(free list)(Concurrent)

[详情请看参考文档](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/G1GettingStarted/index.html#t5)


### 18. JVM中对象的创建过程

#### 1. 拿到内存创建指令
当虚拟机遇到内存创建的指令的时候（new 类名），来到了方法区，找 根据new的参数在常量池中定位一个类的符号引用。
#### 2. 检查符号引用
检查该符号引用有没有被加载、解析和初始化过，如果没有则执行类加载过程，否则直接准备为新的对象分配内存
#### 3. 分配内存
虚拟机为对象分配内存（堆）分配内存分为指针碰撞和空闲列表两种方式；分配内存还要要保证并发安全，有两种方式。 
###### 3.1. 指针碰撞
所有的存储空间分为两部分，一部分是空闲，一部分是占用，需要分配空间的时候，只需要计算指针移动的长度即可。

###### 3.2. 空闲列表
虚拟机维护了一个空闲列表，需要分配空间的时候去查该空闲列表进行分配并对空闲列表做更新。

可以看出，内存分配方式是由java堆是否规整决定的，java堆的规整是由垃圾回收机制来决定的

###### 3.2.5 安全性问题的思考
假如分配内存策略是指针碰撞，如果在高并发情况下，多个对象需要分配内存，如果不做处理，肯定会出现线程安全问题，导致一些对象分配不到空间等。

下面是解决方案：

###### 3.3 线程同步策略
也就是每个线程都进行同步，防止出现线程安全。

###### 3.4. 本地线程分配缓冲
也称TLAB（Thread Local Allocation Buffer），在堆中为每一个线程分配一小块独立的内存，这样以来就不存并发问题了，Java 层面与之对应的是 ThreadLocal 类的实现

#### 4. 初始化
1. 分配完内存后要对对象的头（Object Header）进行初始化，这新信息包括：该对象对应类的元数据、该对象的GC代、对象的哈希码。
2. 抽象数据类型默认初始化为null，基本数据类型为0，布尔为false。。。
#### 5. 调用对象的初始化方法
也就是执行构造方法。