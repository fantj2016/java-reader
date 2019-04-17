>因为wait()、notify()是和synchronized配合使用的，因此如果使用了显示锁Lock，就不能用了。所以显示锁要提供自己的等待/通知机制，Condition应运而生。

>Condition中的`await()`方法相当于Object的`wait()`方法，Condition中的`signal()`方法相当于Object的`notify()`方法，Condition中的`signalAll()`相当于Object的`notifyAll()`方法。不同的是，Object中的`wait(),notify(),notifyAll()`方法是和`"同步锁"`(synchronized关键字)捆绑使用的；而Condition是需要与`"互斥锁"/"共享锁"`捆绑使用的。

### 1. 函数列表
1. `void await() throws InterruptedException`
当前线程进入等待状态，直到被通知（signal）或者被中断时，当前线程进入运行状态，从await()返回；

2. `void awaitUninterruptibly()`
当前线程进入等待状态，直到被通知，对中断不做响应；

3. `long awaitNanos(long nanosTimeout) throws InterruptedException`
在接口1的返回条件基础上增加了超时响应，返回值表示当前剩余的时间，如果在nanosTimeout之前被唤醒，返回值 = nanosTimeout - 实际消耗的时间，返回值 <= 0表示超时；

4. `boolean await(long time, TimeUnit unit) throws InterruptedException`
同样是在接口1的返回条件基础上增加了超时响应，与接口3不同的是：
可以自定义超时时间单位；
返回值返回true/false，在time之前被唤醒，返回true，超时返回false。

5. `boolean awaitUntil(Date deadline) throws InterruptedException`
当前线程进入等待状态直到将来的指定时间被通知，如果没有到指定时间被通知返回true，否则，到达指定时间，返回false；

6. `void signal()`
唤醒一个等待在Condition上的线程；

7. `void signalAll()`
唤醒等待在Condition上所有的线程。

### 2. 具体实现
看到电脑上当初有帮人做过一道题,就拿它做实例演示：
>编写一个Java应用程序，要求有三个进程：student1，student2，teacher，其中线程student1准备“睡”1分钟后再开始上课，线程student2准备“睡”5分钟后再开始上课。Teacher在输出4句“上课”后，“唤醒”了休眠的线程student1；线程student1被“唤醒”后，负责再“唤醒”休眠的线程student2. 

#### 2.1 实现一：
>基于Object和synchronized实现。

```
package com.fantJ.bigdata;

/**
 * Created by Fant.J.
 * 2018/7/2 16:36
 */
public class Ten {
    static class Student1{
        private boolean student1Flag = false;
        public synchronized boolean isStudent1Flag() {
            System.out.println("学生1开始睡觉1min");
            if (!this.student1Flag){
                try {
                    System.out.println("学生1睡着了");
                    wait(1*1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("学生1被唤醒");

            return student1Flag;
        }

        public synchronized void setStudent1Flag(boolean student1Flag) {
            this.student1Flag = student1Flag;
            notify();
        }
    }
    static class Student2{
        private boolean student2Flag = false;
        public synchronized boolean isStudent2Flag() {
            System.out.println("学生2开始睡觉5min");
            if (!this.student2Flag){
                try {
                    System.out.println("学生2睡着了");
                    wait(5*1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("学生2被唤醒");
            return student2Flag;
        }

        public synchronized void setStudent2Flag(boolean student2Flag) {
            notify();
            this.student2Flag = student2Flag;
        }
    }
    static class Teacher{
        private boolean teacherFlag = true;
        public synchronized boolean isTeacherFlag() {
            if (!this.teacherFlag){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("老师准备吼着要上课");

            return teacherFlag;
        }

        public synchronized void setTeacherFlag(boolean teacherFlag) {
            this.teacherFlag = teacherFlag;
            notify();
        }
    }
    public static void main(String[] args) {
        Student1 student1 = new Student1();
        Student2 student2 = new Student2();
        Teacher teacher = new Teacher();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0;i<4;i++){
                    System.out.println("上课");
                }
                teacher.isTeacherFlag();
                System.out.println("学生1被吵醒了,1s后反应过来");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                student1.setStudent1Flag(true);
            }
        });
        Thread s1 = new Thread(new Runnable() {
            @Override
            public void run() {
                student1.isStudent1Flag();
                System.out.println("准备唤醒学生2,唤醒需要1s");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                student2.setStudent2Flag(true);
            }
        });
        Thread s2 = new Thread(new Runnable() {
            @Override
            public void run() {
                student2.isStudent2Flag();
            }
        });

        s1.start();
        s2.start();
        t.start();
    }
}
```
当然，用`notifyAll`可能会用更少的代码，这种实现方式虽然复杂，单性能上会比使用`notifyAll()`要强很多，因为没有锁争夺导致的资源浪费。但是可以看到，代码很复杂，实例与实例之间也需要保证很好的隔离。

#### 2.2 实现二：
>基于Condition、ReentrantLock实现。
```
public class xxx{
        private int signal = 0;
        public Lock lock = new ReentrantLock();
        Condition teacher = lock.newCondition();
        Condition student1 = lock.newCondition();
        Condition student2 = lock.newCondition();

        public void teacher(){
            lock.lock();
            while (signal != 0){
                try {
                    teacher.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("老师叫上课");
            signal++;
            student1.signal();
            lock.unlock();
        }
        public void student1(){
            lock.lock();
            while (signal != 1){
                try {
                    student1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("学生1醒了,准备叫醒学生2");
            signal++;
            student2.signal();
            lock.unlock();
        }
        public void student2(){
            lock.lock();
            while (signal != 2){
                try {
                    student2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("学生2醒了");
            signal=0;
            teacher.signal();
            lock.unlock();
        }

        public static void main(String[] args) {
            ThreadCommunicate2 ten = new ThreadCommunicate2();
            new Thread(() -> ten.teacher()).start();
            new Thread(() -> ten.student1()).start();
            new Thread(() -> ten.student2()).start();
        }
}
```
`Condition`依赖于`Lock`接口，生成一个Condition的基本代码是`lock.newCondition() `
调用`Condition`的`await()`和`signal()`方法，都必须在`lock`保护之内，就是说必须在`lock.lock()`和`lock.unlock`之间才可以使用。

可以观察到，我取消了`synchronized`方法关键字，在每个加锁的方法前后分别加了`lock.lock(); lock.unlock();`来获取/施放锁，并且在释放锁之前施放想要施放的`Condition`对象。同样的，我们使用`signal`来完成线程间的通信。
### 3. Condition实现有界队列
>为什么要用它来实现有界队列呢，因为我们可以利用Condition来实现阻塞（当队列空或者满的时候）。这就为我们减少了很多的麻烦。

```
public class MyQueue<E> {

    private Object[] objects;
    private Lock lock = new ReentrantLock();
    private Condition addCDT = lock.newCondition();
    private Condition rmCDT = lock.newCondition();

    private int addIndex;
    private int rmIndex;
    private int queueSize;

    MyQueue(int size){
        objects = new Object[size];
    }
    public void add(E e){
        lock.lock();
        while (queueSize == objects.length){
            try {
                addCDT.await();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        objects[addIndex] = e;
        System.out.println("添加了数据"+"Objects["+addIndex+"] = "+e);
        if (++addIndex == objects.length){
            addIndex = 0;
        }
        queueSize++;
        rmCDT.signal();
        lock.unlock();

    }
    public Object remove(){
        lock.lock();
        while (queueSize == 0){
            try {
                System.out.println("队列为空");
                rmCDT.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Object temp = objects[rmIndex];
        objects[rmIndex] = null;
        System.out.println("移除了数据"+"Objects["+rmIndex+"] = null");
        if (++rmIndex == objects.length){
            rmIndex = 0;
        }
        queueSize--;
        addCDT.signal();
        lock.unlock();
        return temp;
    }
    public void foreach(E e){
        if (e instanceof String){
            Arrays.stream(objects).map(obj->{
                if (obj == null){
                    obj = " ";
                }
                return obj;
            }).map(Object::toString).forEach(System.out::println);
        }
        if (e instanceof Integer){
            Arrays.stream(objects).map(obj -> {
                if (obj == null ){
                    obj = 0;
                }
                return obj;
            }).map(object -> Integer.valueOf(object.toString())).forEach(System.out::println);
        }
    }
}
```
`add` 方法就是往队列中添加数据。
`remove`  是从队列中按FIFO移除数据。
`foreach` 方法是一个观察队列内容的工具方法，很容易看出，它是用来遍历的。

```
    public static void main(String[] args) {
        MyQueue<Integer> myQueue = new MyQueue<>(5);
        myQueue.add(5);
        myQueue.add(4);
        myQueue.add(3);
//        myQueue.add(2);
//        myQueue.add(1);
        myQueue.remove();
        myQueue.foreach(5);
    }
```
```
添加了数据Objects[0] = 5
添加了数据Objects[1] = 4
添加了数据Objects[2] = 3
移除了数据Objects[0] = null
0
4
3
0
0
```

### 4. 源码分析
`ReentrantLock.class`
```
public Condition newCondition() {
    return sync.newCondition();
}
```
sync溯源：
```
private final Sync sync;
```
Sync类中有一个newCondition()方法：
```
final ConditionObject newCondition() {
    return new ConditionObject();
}
```
```
public class ConditionObject implements Condition, java.io.Serializable {
        /** First node of condition queue. */
        private transient Node firstWaiter;
        /** Last node of condition queue. */
        private transient Node lastWaiter;
}
```
```
public interface Condition {
    void await() throws InterruptedException;
    void awaitUninterruptibly();
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    boolean await(long time, TimeUnit unit) throws InterruptedException;
    boolean awaitUntil(Date deadline) throws InterruptedException;
    void signal();
    void signalAll();
```
##### `await源码：`
```
public final void await() throws InterruptedException {
    // 1.如果当前线程被中断，则抛出中断异常
    if (Thread.interrupted())
        throw new InterruptedException();
    // 2.将节点加入到Condition队列中去，这里如果lastWaiter是cancel状态，那么会把它踢出Condition队列。
    Node node = addConditionWaiter();
    // 3.调用tryRelease，释放当前线程的锁
    long savedState = fullyRelease(node);
    int interruptMode = 0;
    // 4.为什么会有在AQS的等待队列的判断？
    // 解答：signal操作会将Node从Condition队列中拿出并且放入到等待队列中去，在不在AQS等待队列就看signal是否执行了
    // 如果不在AQS等待队列中，就park当前线程，如果在，就退出循环，这个时候如果被中断，那么就退出循环
    while (!isOnSyncQueue(node)) {
        LockSupport.park(this);
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    // 5.这个时候线程已经被signal()或者signalAll()操作给唤醒了，退出了4中的while循环
    // 自旋等待尝试再次获取锁，调用acquireQueued方法
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    if (node.nextWaiter != null)
        unlinkCancelledWaiters();
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
}
```
1. 将当前线程加入`Condition`锁队列。特别说明的是，这里不同于`AQS`的队列，这里进入的是`Condition`的`FIFO`队列。　

2. 释放锁。这里可以看到将锁释放了，否则别的线程就无法拿到锁而发生死锁。　

3. 自旋`(while`)挂起，直到被唤醒（`signal`把他重新放回到AQS的等待队列）或者超时或者CACELLED等。　

4. 获取锁`(acquireQueued`)。并将自己从`Condition`的`FIFO`队列中释放，表明自己不再需要锁（我已经拿到锁了）。


##### `signal()源码`
```
public final void signal() {
            if (!isHeldExclusively())
              //如果同步状态不是被当前线程独占，直接抛出异常。从这里也能看出来，Condition只能配合独占类同步组件使用。
                throw new IllegalMonitorStateException(); 
            Node first = firstWaiter;
            if (first != null)
                //通知等待队列队首的节点。
                doSignal(first); 
        }

private void doSignal(Node first) {
            do {
                if ( (firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) &&   //transferForSignal方法尝试唤醒当前节点，如果唤醒失败，则继续尝试唤醒当前节点的后继节点。
                     (first = firstWaiter) != null);
        }

    final boolean transferForSignal(Node node) {
        //如果当前节点状态为CONDITION，则将状态改为0准备加入同步队列；如果当前状态不为CONDITION，说明该节点等待已被中断，则该方法返回false，doSignal()方法会继续尝试唤醒当前节点的后继节点
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        /*
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting. If cancelled or
         * attempt to set waitStatus fails, wake up to resync (in which
         * case the waitStatus can be transiently and harmlessly wrong).
         */
        Node p = enq(node);  //将节点加入同步队列，返回的p是节点在同步队列中的先驱节点
        int ws = p.waitStatus;
        //如果先驱节点的状态为CANCELLED(>0) 或设置先驱节点的状态为SIGNAL失败，那么就立即唤醒当前节点对应的线程，线程被唤醒后会执行acquireQueued方法，该方法会重新尝试将节点的先驱状态设为SIGNAL并再次park线程；如果当前设置前驱节点状态为SIGNAL成功，那么就不需要马上唤醒线程了，当它的前驱节点成为同步队列的首节点且释放同步状态后，会自动唤醒它。
        //其实笔者认为这里不加这个判断条件应该也是可以的。只是对于CAS修改前驱节点状态为SIGNAL成功这种情况来说，如果不加这个判断条件，提前唤醒了线程，等进入acquireQueued方法了节点发现自己的前驱不是首节点，还要再阻塞，等到其前驱节点成为首节点并释放锁时再唤醒一次；而如果加了这个条件，线程被唤醒的时候它的前驱节点肯定是首节点了，线程就有机会直接获取同步状态从而避免二次阻塞，节省了硬件资源。
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }
```
`signal`就是唤醒`Condition`队列中的第一个非CANCELLED节点线程，而signalAll就是唤醒所有非`CANCELLED`节点线程，本质是将节点从`Condition`队列中取出来一个还是所有节点放到`AQS`的等待队列。尽管所有`Node`可能都被唤醒，但是要知道的是仍然只有一个线程能够拿到锁，其它没有拿到锁的线程仍然需要自旋等待，就上上面提到的第4步(`acquireQueued`)。

##### 实现过程概述

>我们知道`Lock的本质是AQS`，`AQS`自己维护的队列是当前等待资源的队列，`AQS`会在资源被释放后，依次唤醒队列中从前到后的所有节点，使他们对应的线程恢复执行，直到队列为空。而`Condition`自己也维护了一个队列，该队列的作用是维护一个等待`signal`信号的队列。但是，两个队列的作用不同的，事实上，每个线程也仅仅会同时存在以上两个队列中的一个，流程是这样的：

1. 线程1调用`reentrantLock.lock`时，尝试获取锁。如果成功，则返回，从`AQS`的队列中移除线程；否则阻塞，保持在`AQS`的等待队列中。
2. 线程1调用`await`方法被调用时，对应操作是被加入到`Condition`的等待队列中，等待signal信号；同时释放锁。
3. 锁被释放后，会唤醒AQS队列中的头结点，所以线程2会获取到锁。
4. 线程2调用`signal`方法，这个时候`Condition`的等待队列中只有线程1一个节点，于是它被取出来，并被加入到AQS的等待队列中。注意，这个时候，线程1 并没有被唤醒，只是被加入AQS等待队列。
5. `signal`方法执行完毕，线程2调用`unLock()`方法，释放锁。这个时候因为AQS中只有线程1，于是，线程1被唤醒，线程1恢复执行。
所以：
发送`signal`信号只是将`Condition`队列中的线程加到`AQS`的等待队列中。只有到发送`signal`信号的线程调用`reentrantLock.unlock()`释放锁后，这些线程才会被唤醒。

可以看到，整个协作过程是靠结点在`AQS`的等待队列和`Condition`的等待队列中来回移动实现的，`Condition`作为一个条件类，很好的自己维护了一个等待信号的队列，并在适时的时候将结点加入到`AQS`的等待队列中来实现的唤醒操作。

Condition等待通知的本质请参考：https://www.cnblogs.com/sheeva/p/6484224.html
