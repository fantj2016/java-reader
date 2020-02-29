首先OOM原因有很多：

- java.lang.OutOfMemoryError: Java heap space 。如果没有代码无限制new对象，一般可通过JVM调参解决。
- java.lang.OutOfMemoryError: PermGen space 。 可采用-XX：MaxPermSize调节大小
- java.lang.OutOfMemoryError: Requested array size exceeds VM limit 尝试分配比堆大的数组
- java.lang.OutOfMemoryError: request bytes for . Out of swap space? 本机swap空间不足
- java.lang.OutOfMemoryError: (Native method)

1. 先根据报错确定原因。初步定位是不是调参可以解决的。
2. 调参解决不了， 分析thread dump、heap dump。 `jmap -dump:live打印堆日志`  `jstack -l 打印线程日志` 。`jstat -gc <pid> <period> <times>`查看gc信息
3. 使用MAT、jhat、等分析堆日志。 也可以借助jconsole 分析线程死锁、内存使用等。

元空间：1.8后取消了永久代(方法区)，改为元空间，类的信息存放在元空间，元空间不使用堆内存，使用的是本地内存，理论上讲本地内存有多大，元空间就有多大。