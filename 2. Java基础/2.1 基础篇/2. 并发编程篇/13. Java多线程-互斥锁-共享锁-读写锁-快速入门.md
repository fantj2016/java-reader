##### 什么是互斥锁?

在访问共享资源之前对进行加锁操作，在访问完成之后进行解锁操作。 加锁后，任何其他试图再次加锁的线程会被阻塞，直到当前进程解锁。 

如果解锁时有一个以上的线程阻塞，那么所有该锁上的线程都被编程就绪状态， 第一个变为就绪状态的线程又执行加锁操作，那么其他的线程又会进入等待。 在这种方式下，只有一个线程能够访问被互斥锁保护的资源。

##### 什么是共享锁？

互斥锁要求只能有一个线程访问被保护的资源，共享锁从字面来看也即是允许多个线程共同访问资源。


##### 什么是读写锁？

读写锁既是互斥锁，又是共享锁，read模式是共享，write是互斥(排它锁)的。

读写锁有三种状态：读加锁状态、写加锁状态和不加锁状态 

一次只有一个线程可以占有写模式的读写锁，但是多个线程可以同时占有读模式的读写锁。

用ReentrantLock手动实现一个简单的读写锁。

MyReadWriteLock.java
```
/**
 * Created by Fant.J.
 */
public class MyReadWriteLock {
    private Map<String,Object> map = new HashMap<>();

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    private Lock r = rwl.readLock();
    private Lock w = rwl.writeLock();

    public Object get(String key){
        try {
            r.lock();
            System.out.println(Thread.currentThread().getName()+"read 操作执行");
            Thread.sleep(500);
            return map.get(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            r.unlock();
            System.out.println(Thread.currentThread().getName()+"read 操作结束");
        }

    }

    public void put(String key,Object value){
        try {
            w.lock();
            System.out.println(Thread.currentThread().getName()+"write 操作执行");
            Thread.sleep(500);
            map.put(key,value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            w.unlock();
            System.out.println(Thread.currentThread().getName()+"write 操作结束");
        }
    }
}

```

##### 测试读读共享(不互斥)
```
/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {

        MyReadWriteLock myReadWriteLock = new MyReadWriteLock();
        myReadWriteLock.put("a","fantj_a");
        //读读不互斥（共享）
        //读写互斥
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();

    }
}



mainwrite 操作执行
mainwrite 操作结束
Thread-1read 操作执行
Thread-2read 操作执行
Thread-0read 操作执行
Thread-3read 操作执行
Thread-4read 操作执行
Thread-5read 操作执行
Thread-1read 操作结束
Thread-0read 操作结束
Thread-2read 操作结束
Thread-3read 操作结束
fantj_a
fantj_a
fantj_a
fantj_a
Thread-4read 操作结束
fantj_a
Thread-5read 操作结束
fantj_a

```
可以看出，中间有很多read操作是并发进行的。

##### 那么我们再看下写写是否有互斥性：
```
/**
 * 测试 写-写 模式  是互斥的
 * Created by Fant.J.
 */
public class TestWriteWrite {
    public static void main(String[] args) {

        MyReadWriteLock myReadWriteLock = new MyReadWriteLock();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myReadWriteLock.put("b","fantj_b");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myReadWriteLock.put("b","fantj_b");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myReadWriteLock.put("b","fantj_b");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myReadWriteLock.put("b","fantj_b");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myReadWriteLock.put("b","fantj_b");
            }
        }).start();
    }
}




Thread-0write 操作执行
Thread-1write 操作执行
Thread-0write 操作结束
Thread-1write 操作结束
Thread-2write 操作执行
Thread-2write 操作结束
Thread-3write 操作执行
Thread-3write 操作结束
Thread-4write 操作执行
Thread-4write 操作结束
```
控制台能明显感觉到线程休息的时间。所以它的写-写操作肯定是互斥的。

最后再看看，写-读 操作是否互斥。

##### 写-读互斥 测试
```
/**
 * 测试  写-读模式 互斥
 * Created by Fant.J.
 */
public class TestWriteRead {
    public static void main(String[] args) {

        MyReadWriteLock myReadWriteLock = new MyReadWriteLock();
        //读读不互斥（共享）
        //读写互斥
        new Thread(new Runnable() {
            @Override
            public void run() {
                myReadWriteLock.put("a","fantj_a");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(myReadWriteLock.get("a"));
            }
        }).start();


    }
}



Thread-0write 操作执行
Thread-1read 操作执行
Thread-0write 操作结束
Thread-1read 操作结束
fantj_a
```
控制台可以看到write操作执行后线程被阻塞。直到写释放了锁。

#### 问题分析

###### 问题1：仔细想了想，如果有一种场景，就是用户一直再读，写获取不到锁，那么不就造成脏读吗？

上一章我介绍了公平锁/非公平锁，资源的抢占不就是非公平锁造成的吗，那我们用公平锁把它包装下不就能避免了吗，我做了个简单的实现：（不知道公平锁的可以翻我上章教程）

```
/**
 * 测试  读写锁 的公平锁 实现
 * Created by Fant.J.
 */
public class TestReadWriteRead {
    public static void main(String[] args) {
         ReentrantLock fairLock = new ReentrantLock(true);
        MyReadWriteLock myReadWriteLock = new MyReadWriteLock();
        myReadWriteLock.put("a","fantj_a");
        new Thread(new Runnable() {
            @Override
            public void run() {
                fairLock.lock();
                System.out.println(myReadWriteLock.get("a"));
                System.out.println("fair队列长度"+fairLock.getQueueLength());
                fairLock.unlock();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fairLock.lock();
                System.out.println(myReadWriteLock.get("a"));
                System.out.println("fair队列长度"+fairLock.getQueueLength());

                fairLock.unlock();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fairLock.lock();
                System.out.println(myReadWriteLock.get("a"));
                System.out.println("fair队列长度"+fairLock.getQueueLength());
                fairLock.unlock();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                fairLock.lock();
                myReadWriteLock.put("a","fantj_a_update");
                System.out.println("fair队列长度"+fairLock.getQueueLength());
                fairLock.unlock();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fairLock.lock();
                System.out.println(myReadWriteLock.get("a"));
                System.out.println("fair队列长度"+fairLock.getQueueLength());
                fairLock.unlock();
            }
        }).start();
    }
}


mainwrite 操作执行
mainwrite 操作结束
Thread-0read 操作执行
Thread-0read 操作结束
fantj_a
fair队列长度4
Thread-1read 操作执行
Thread-1read 操作结束
fantj_a
fair队列长度3
Thread-2read 操作执行
Thread-2read 操作结束
fantj_a
fair队列长度2
Thread-3write 操作执行
Thread-3write 操作结束
fair队列长度1
Thread-4read 操作执行
Thread-4read 操作结束
fantj_a_update
fair队列长度0
```

如果谁有更好的实现方式(或者java有现成的实现工具类/包)，可在评论区留言，我在百度上没有找到读写锁的公平锁实现~~谢谢！



















