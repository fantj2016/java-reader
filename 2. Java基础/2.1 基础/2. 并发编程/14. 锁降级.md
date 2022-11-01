>锁降级指当前线程把持住写锁再获取到读锁，随后释放先前拥有的写锁的过程。

概念网上有很多，随便一查一大堆，我就不多浪费大家时间。

##### 为什么要锁降级?
主要原因：读锁是共享锁。写锁是排它锁，如果在写锁施放之前施放了写锁，会造成别的线程很快又拿到了写锁，然后阻塞了读锁，造成数据的不可控性（感知不到数据的变化），也造成了不必要的cpu资源浪费，写只需要一个线程来进行，然后共享锁，不需要多线程都去获取这个写锁，如果先施放写锁再获取读锁，后果就是如此。


###### 案例

```
package com.thread.demotionLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁降级
 * Created by Fant.J.
 */
public class MyDemotionLock {

    private Map<String,Object> map = new HashMap<>();

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    private Lock r = rwl.readLock();
    private Lock w = rwl.writeLock();

    int value = 100;


    public void readAndWrite(){
        //获取读锁
        r.lock();
        Object object = map.get("a");
        if (object == null){
            System.out.println("获取到了空值");
            //缓存是空，模拟从新从数据库中获取
            //关闭读锁，获取写锁
            r.unlock();
            w.lock();
            map.put("a","Fant.J"+value);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //完成写操作后，应该在写锁释放之前获取到读锁
            w.unlock();
            System.out.println("我要施放锁啦");
            r.lock();
        }
        System.out.println("线程"+Thread.currentThread().getName()+map.get("a"));
        r.unlock();
    }

    public static void main(String[] args) {

        MyDemotionLock lock = new MyDemotionLock();
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName()+"启动");
            lock.readAndWrite();
        };
        Thread[] threadArray = new Thread[10];
        for (int i=0; i<10; i++) {
            threadArray[i] = new Thread(runnable);
        }
        for (int i=0; i<10; i++) {
            threadArray[i].start();
        }
    }

}



Thread-0启动
Thread-1启动
Thread-2启动
获取到了空值
获取到了空值
获取到了空值
Thread-3启动
Thread-4启动
Thread-5启动
Thread-6启动
Thread-7启动
Thread-8启动
Thread-9启动
我要施放锁啦
我要施放锁啦
我要施放锁啦
线程Thread-3Fant.J100
线程Thread-6Fant.J100
线程Thread-8Fant.J100
线程Thread-7Fant.J100
线程Thread-5Fant.J100
线程Thread-4Fant.J100
线程Thread-2Fant.J100
线程Thread-0Fant.J100
线程Thread-1Fant.J100
线程Thread-9Fant.J100
```

###### 下面是把持住写锁并获取读锁后再施放写锁：

```
            r.lock();
            System.out.println("我要施放锁啦");
            w.unlock();


Thread-0启动
Thread-1启动
Thread-2启动
获取到了空值
获取到了空值
Thread-3启动
Thread-4启动
Thread-5启动
Thread-6启动
Thread-7启动
Thread-8启动
Thread-9启动
我要施放锁啦
线程Thread-2Fant.J100
我要施放锁啦
线程Thread-1Fant.J100
线程Thread-0Fant.J100
线程Thread-3Fant.J100
线程Thread-6Fant.J100
线程Thread-8Fant.J100
线程Thread-5Fant.J100
线程Thread-4Fant.J100
线程Thread-9Fant.J100
线程Thread-7Fant.J100
```
比较这两种结果，其实他们大概看起来差异不大，都没有影响到读值，但是仔细观察你会发现他们执行的步骤不一样。

代码块一：**很容易看出来写锁释放后，读锁全部都在争取资源（从控制台打印顺序可以看到）**。

代码块二：**我要施放锁啦 -> 线程Thread-2Fant.J100 -> 我要施放锁啦**，可以看出打印第一个`线程Thread-2Fant.J100`是没有读锁争抢的


### 质疑？

就算我不使用锁降级，程序的运行结果也是正确的（这是因为锁的机制和volatile关键字相似）。

###### 肯定有人问，看着没啥大的区别啊！
因为我当时就是这样质疑的，后来看了很多个大牛的分析，基本上分为两派。
###### 一派(主流)的描述：没有感知到数据变化的读锁冲进来会继续占用写锁，阻塞已完成写操作的线程去继续获取读锁。
###### 二派的描述：为了性能，因为读锁的抢占必将引起资源分配和判断等操作，降级锁减少了线程的阻塞唤醒，实时连续性更强。


其实我觉得技术没有谁对谁错，只有出发点和自己擅长的领域的不同。

有精力的可以自己追踪源码，分析字节码去深入，以后我会把这部分的研究贴出来。

小小性能的提升，可能对吞吐量就是小几百甚至上千的提升。

养成良好的习惯看似微不足道~

谢谢！

