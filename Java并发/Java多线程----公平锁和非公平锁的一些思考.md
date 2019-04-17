>在java的锁机制中，公平和非公平的参考物是什么，个人而言觉得是相对产生的结果而立，简单的来说，如果一个线程组里，能保证每个线程都能拿到锁，那么这个锁就是公平锁。相反，如果保证不了每个线程都能拿到锁，也就是存在有线程饿死，那么这个锁就是非公平锁。本文围绕ReenTrantLock来讲。

### 实现原理

那如何能保证每个线程都能拿到锁呢，队列FIFO是一个完美的解决方案，也就是先进先出，java的ReenTrantLock也就是用队列实现的公平锁和非公平锁。

在公平的锁中，如果有另一个线程持有锁或者有其他线程在等待队列中等待这个所，那么新发出的请求的线程将被放入到队列中。而非公平锁上，只有当锁被某个线程持有时，新发出请求的线程才会被放入队列中（此时和公平锁是一样的）。所以，它们的差别在于非公平锁会有更多的机会去抢占锁。

公平锁：
```
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }


    #hasQueuedPredecessors的实现
    public final boolean hasQueuedPredecessors() {
   
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
            ((s = h.next) == null || s.thread != Thread.currentThread());
    }
```
非公平锁：
```
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
```

### 示例
##### 公平锁：
```
package com.thread.fair;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Fant.J.
 */
public class MyFairLock {
    /**
     *     true 表示 ReentrantLock 的公平锁
     */
    private  ReentrantLock lock = new ReentrantLock(true);

    public   void testFail(){
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() +"获得了锁");
        }finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        MyFairLock fairLock = new MyFairLock();
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName()+"启动");
            fairLock.testFail();
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


```
```

Thread-0启动
Thread-0获得了锁
Thread-1启动
Thread-1获得了锁
Thread-2启动
Thread-2获得了锁
Thread-3启动
Thread-3获得了锁
Thread-4启动
Thread-4获得了锁
Thread-5启动
Thread-5获得了锁
Thread-6启动
Thread-6获得了锁
Thread-8启动
Thread-8获得了锁
Thread-7启动
Thread-7获得了锁
Thread-9启动
Thread-9获得了锁
```
可以看到，获取锁的线程顺序正是线程启动的顺序。


非公平锁：
```
/**
 * Created by Fant.J.
 */
public class MyNonfairLock {
    /**
     *     false 表示 ReentrantLock 的非公平锁
     */
    private  ReentrantLock lock = new ReentrantLock(false);

    public  void testFail(){
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() +"获得了锁");
        }finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        MyNonfairLock nonfairLock = new MyNonfairLock();
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName()+"启动");
            nonfairLock.testFail();
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

```
```
Thread-1启动
Thread-0启动
Thread-0获得了锁
Thread-1获得了锁
Thread-8启动
Thread-8获得了锁
Thread-3启动
Thread-3获得了锁
Thread-4启动
Thread-4获得了锁
Thread-5启动
Thread-2启动
Thread-9启动
Thread-5获得了锁
Thread-2获得了锁
Thread-9获得了锁
Thread-6启动
Thread-7启动
Thread-6获得了锁
Thread-7获得了锁

```
可以看出非公平锁对锁的获取是乱序的，即有一个抢占锁的过程。

### 最后

那非公平锁和公平锁适合什么场合使用呢，他们的优缺点又是什么呢？
##### 优缺点：
非公平锁性能高于公平锁性能。首先，在恢复一个被挂起的线程与该线程真正运行之间存在着严重的延迟。而且，非公平锁能更充分的利用cpu的时间片，尽量的减少cpu空闲的状态时间。

##### 使用场景
使用场景的话呢，其实还是和他们的属性一一相关，举个栗子：如果业务中线程占用(处理)时间要远长于线程等待，那用非公平锁其实效率并不明显，但是用公平锁会给业务增强很多的可控制性。
