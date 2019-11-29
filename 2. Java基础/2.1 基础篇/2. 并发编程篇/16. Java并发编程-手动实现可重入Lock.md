>Lock就像synchronized块一样是一个线程同步机制。 然而，Lock定比synchronized更灵活、更复杂。

### Lock和synchronized块 的区别
* 同步块不保证等待输入它的线程被授予访问权限的顺序。
* 不能将任何参数传递给同步块的条目。
* 同步块必须完全包含在单个方法中。 一个Lock可以在不同的方法中调用lock（）和unlock（）。

### 简单例子
```
Lock lock = new ReentrantLock();

lock.lock();

//要保证线程安全的代码

lock.unlock();
```
其中，你应该能够猜到，lock() 方法是加锁，unlock()方法是解锁。

### Lock接口含有的方法
* lock()
* lockInterruptibly()
* tryLock()
* tryLock(long timeout, TimeUnit timeUnit)
* unlock()

lock（）方法锁定Lock实例。 如果锁定实例已被锁定，则线程调用锁定（）将被锁定，直到解锁锁定。

lockInterruptibly（）方法锁定Lock，除非调用该方法的线程已被中断。如果一个线程被阻塞，等待通过此方法锁定Lock，该线程将被中断，并退出此方法调用。（获取锁的时候可以被中断）

tryLock（）方法立即尝试锁定Lock实例。 如果锁定成功则返回true;如果Lock已经被锁定，则返回false。 **这个方法永远不会阻塞**

tryLock（long timeout，TimeUnit timeUnit）的工作方式与tryLock（）方法相似，只是它对超时时间有所规定。

unlock（）方法解锁Lock实例。 通常，Lock实现将只允许已锁定Lock的线程调用此方法。 调用此方法的其他线程可能会导致未经检查的异常（RuntimeException）。

### ReentrantLock实例
ReentrantLock 可重入锁，是Lock的一个子类。我们这里来使用它实现线程安全编程。
```
package com.lock;

import com.thread.security.Task;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁
 * Created by Fant.J.
 * 2018/3/6 20:09
 */
public class ReentrantLockTest {

    public int value = 0;

    //实例化重入锁锁
    Lock lock = new ReentrantLock();

    public   int getValue() {
        //加锁
        lock.lock();
        int a = value++;
        //消除锁
        lock.unlock();
        return a;
    }

    public static void main(String[] args) {

        ReentrantLockTest task = new ReentrantLockTest();
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + "  " + task.getValue());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + "  " + task.getValue());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}

```

### 手写自己的Lock实现类
如果有特殊业务需求，我们也可以重写Lock接口，来打造一个自己的lock锁。
```
package com.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by Fant.J.
 * 2018/3/6 20:12
 */
public class MyLock implements Lock {

    //声明一个判断锁的布尔值
    private boolean isLocked = false;

    /**
     * 必须声明  synchronized 原自行操作，不然jvm不会识别是哪个线程的wait方法，notify也一样
     */
    @Override
    public synchronized void lock() {
        while (isLocked){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;

    }

    @Override
    public synchronized void unlock() {
        isLocked = false;
        notify();
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }


    @Override
    public boolean tryLock() {
        return false;
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }




    @Override
    public Condition newCondition() {
        return null;
    }
}

```
然后我们做测试
```
package com.lock;

/**
 * Created by Fant.J.
 * 2018/3/6 20:24
 */
public class MyLockTest {

    public int value = 0;

    MyLock myLock = new MyLock();

    public int  getValue(){
        myLock.lock();

        value++;

        myLock.unlock();

        return value;
    }

    public static void main(String[] args) {

        MyLockTest task = new MyLockTest();
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + "  " + task.getValue());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + "  " + task.getValue());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}

```
结果没有出现线程安全问题，这里不做截图了，自己可以试试。但是我们写的方法还有一定的问题，就是MyLock这个类不支持 可重入锁，意思就是如果有两个锁嵌套，如果相同的线程先调用a方法，再调用带锁的b方法，则就会进入自旋锁。
##### 测试方法源码
```
package com.lock;

/**
 * Created by Fant.J.
 * 2018/3/6 20:24
 */
public class MyLockTest2 {

    public int value = 0;

    MyLock myLock = new MyLock();

    public void a(){
        myLock.lock();
        System.out.println("a");
        b();
        myLock.unlock();
    }

    public void b(){
        myLock.lock();
        System.out.println("b");
        myLock.unlock();
    }

    public static void main(String[] args) {

        MyLockTest2 task = new MyLockTest2();
        new Thread(){
            @Override
            public void run() {
                task.a();
                }
        }.start();

        new Thread(){
            @Override
            public void run() {
                task.a();
            }
        }.start();
    }
}

```

执行该方法后，我们会发现，线程停止在打印出"a"后，一直在等待。这就是因为该锁不是可重入锁。

##### 可重入锁的设计
我在这里只贴和上面代码不同的部分。
```
public class MyLock implements Lock {

    //声明一个判断锁的布尔值
    private boolean isLocked = false;

    Thread lockBy = null;

    int lockCount = 0;

    @Override
    public synchronized void lock() {

        Thread currentThread = Thread.currentThread();  //获取到当前线程
        while (isLocked && currentThread != lockBy){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;
        lockBy = currentThread; //将currentThread线程指向 lockBy线程
        lockCount++;//计数器自增

    }

    @Override
    public synchronized void unlock() {
        if (lockBy == Thread.currentThread()){
            lockCount--;
            if (lockCount ==0 ){
                notify();
                isLocked = false;
            }
        }
    }

}
```
第一个线程执行a()方法，得到了锁，使lockedBy等于当前线程，也就是说，执行的这个方法的线程获得了这个锁，执行add()方法时，同样要先获得锁，因不满足while循环的条件，也就是不等待，继续进行，将此时的lockedCount变量，也就是当前获得锁的数量加一，当释放了所有的锁，才执行notify()。如果在执行这个方法时，有第二个线程想要执行这个方法，因为lockedBy不等于第二个线程，导致这个线程进入了循环，也就是等待，不断执行wait()方法。只有当第一个线程释放了所有的锁，执行了notify()方法，第二个线程才得以跳出循环，继续执行。
