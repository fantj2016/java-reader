>AbstractQueuedSynchronizer是为实现依赖于先进先出 (FIFO) 等待队列的阻塞锁和相关同步器（信号量、事件，等等）提供的一个框架。

### 1. 构造方法
```
    /**
     * Creates a new {@code AbstractQueuedSynchronizer} instance
     * with initial synchronization state of zero.
     */
    protected AbstractQueuedSynchronizer() { }
```
创建具有初始同步状态 0 的新 AbstractQueuedSynchronizer 实例。

### 2. 方法详细信息
方法有很多，因为aqs怎么是个框架，需要大概了解每个方法，不然谈不到使用。

主要有三大类：状态(state)、获得锁(acquire)、释放锁(release)、附加一个独占线程(ExclusiveOwnerThread)

这四个概念一定要非常清楚。不然很难学会。当然，队列和链表基础也得扎实。

好了，我基于官方文档和源码整理了以下方法（过目理解）：

##### getState
```
    protected final int getState() {
        return state;
    }
```
返回同步状态的当前值。此操作具有 volatile 读的内存语义。
返回：
当前状态值

##### setState
```
    protected final int getState() {
        return state;
    }
```
设置同步状态的值。此操作具有 volatile 写的内存语义。
###### 参数：
newState - 新的状态值


##### compareAndSetState
```
    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }
```
compareAndSwap即CAS，详细可查找[Java并发编程 -- Atomic包](https://www.jianshu.com/p/288bdd29ec06)文章。
如果当前状态值等于预期值，则以原子方式将同步状态设置为给定的更新值。此操作具有 volatile 读和写的内存语义。
###### 参数：
expect - 预期值
update - 新值
###### 返回：
如果成功，则返回 true。返回 false 指示实际值与预期值不相等。


##### tryAcquire
```
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }
```
试图在独占模式下获取对象状态。此方法应该查询是否允许它在独占模式下获取对象状态，如果允许，则获取它。

此方法总是由执行 acquire 的线程来调用。如果此方法报告失败，则 acquire 方法可以将线程加入队列（如果还没有将它加入队列），直到获得其他某个线程释放了该线程的信号。可以用此方法来实现 lock.tryLock()方法。默认实现将抛出[UnsupportedOperationException](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/UnsupportedOperationException.html "java.lang 中的类")。
###### 参数：
arg - acquire 参数。该值总是传递给 acquire 方法的那个值，或者是因某个条件等待而保存在条目上的值。该值是不间断的，并且可以表示任何内容。
###### 返回：
如果成功，则返回 true。在成功的时候，此对象已经被获取。
###### 抛出：
[IllegalMonitorStateException](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/IllegalMonitorStateException.html "java.lang 中的类")- 如果正在进行的获取操作将在非法状态下放置此同步器。必须以一致的方式抛出此异常，以便同步正确运行。
[UnsupportedOperationException](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/UnsupportedOperationException.html "java.lang 中的类")- 如果不支持独占模式

##### tryRelease
```
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }
```
试图设置状态来反映独占模式下的一个释放。
此方法总是由正在执行释放的线程调用。

默认实现将抛出 UnsupportedOperationException。

###### 参数：
arg - release 参数。该值总是传递给 release 方法的那个值，或者是因某个条件等待而保存在条目上的当前状态值。该值是不间断的，并且可以表示任何内容。
###### 返回：
如果此对象现在处于完全释放状态，从而使等待的线程都可以试图获得此对象，则返回 true；否则返回 false。
###### 抛出：
IllegalMonitorStateException - 如果正在进行的释放操作将在非法状态下放置此同步器。必须以一致的方式抛出此异常，以便同步正确运行。
UnsupportedOperationException - 如果不支持独占模式

##### tryAcquireShared
```
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }
```
试图在共享模式下获取对象状态。此方法应该查询是否允许它在共享模式下获取对象状态，如果允许，则获取它。
此方法总是由执行 acquire 线程来调用。如果此方法报告失败，则 acquire 方法可以将线程加入队列（如果还没有将它加入队列），直到获得其他某个线程释放了该线程的信号。

默认实现将抛出 UnsupportedOperationException。

参数：
arg - acquire 参数。该值总是传递给 acquire 方法的那个值，或者是因某个条件等待而保存在条目上的值。该值是不间断的，并且可以表示任何内容。
返回：
在失败时返回负值；如果共享模式下的获取成功但其后续共享模式下的获取不能成功，则返回 0；如果共享模式下的获取成功并且其后续共享模式下的获取可能够成功，则返回正值，在这种情况下，后续等待线程必须检查可用性。（对三种返回值的支持使得此方法可以在只是有时候以独占方式获取对象的上下文中使用。）在成功的时候，此对象已被获取。
抛出：
IllegalMonitorStateException - 如果正在进行的获取操作将在非法状态下放置此同步器。必须以一致的方式抛出此异常，以便同步正确运行。
UnsupportedOperationException - 如果不支持共享模式

##### tryReleaseShared
```
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }
```
试图设置状态来反映共享模式下的一个释放。
此方法总是由正在执行释放的线程调用。

默认实现将抛出 UnsupportedOperationException。

参数：
arg - release 参数。该值总是传递给 release 方法的那个值，或者是因某个条件等待而保存在条目上的当前状态值。该值是不间断的，并且可以表示任何内容。
返回：
如果此对象现在处于完全释放状态，从而使正在等待的线程都可以试图获得此对象，则返回 true；否则返回 false
抛出：
IllegalMonitorStateException - 如果正在进行的释放操作将在非法状态下放置此同步器。必须以一致的方式抛出此异常，以便同步正确运行
UnsupportedOperationException - 如果不支持共享模式


##### isHeldExclusively
```
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }
```
如果对于当前（正调用的）线程，同步是以独占方式进行的，则返回 true。此方法是在每次调用非等待 AbstractQueuedSynchronizer.ConditionObject 方法时调用的。（等待方法则调用 release(int)。）
默认实现将抛出 UnsupportedOperationException。此方法只是 AbstractQueuedSynchronizer.ConditionObject 方法内进行内部调用，因此，如果不使用条件，则不需要定义它。

返回：
如果同步是以独占方式进行的，则返回 true；其他情况则返回 false
抛出：
UnsupportedOperationException - 如果不支持这些条件


##### acquire
```
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
以独占模式获取对象，忽略中断。通过至少调用一次 tryAcquire(int) 来实现此方法，并在成功时返回。否则在成功之前，一直调用 tryAcquire(int) 将线程加入队列，线程可能重复被阻塞或不被阻塞。可以使用此方法来实现 Lock.lock() 方法。
参数：
arg - acquire 参数。此值被传送给 tryAcquire(int)，但它是不间断的，并且可以表示任何内容。


##### acquireInterruptibly
```
    public final void acquireInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (!tryAcquire(arg))
            doAcquireInterruptibly(arg);
    }
```
以独占模式获取对象，如果被中断则中止。通过先检查中断状态，然后至少调用一次 tryAcquire(int) 来实现此方法，并在成功时返回。否则在成功之前，或者线程被中断之前，一直调用 tryAcquire(int) 将线程加入队列，线程可能重复被阻塞或不被阻塞。可以使用此方法来实现 Lock.lockInterruptibly() 方法。
参数：
arg - acquire 参数。此值被传送给 tryAcquire(int)，但它是不间断的，并且可以表示任何内容。
抛出：
InterruptedException - 如果当前线程被中断


##### tryAcquireNanos
```
    public final boolean tryAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquire(arg) ||
            doAcquireNanos(arg, nanosTimeout);
    }
```
试图以独占模式获取对象，如果被中断则中止，如果到了给定超时时间，则会失败。通过先检查中断状态，然后至少调用一次 tryAcquire(int) 来实现此方法，并在成功时返回。否则，在成功之前、线程被中断之前或者到达超时时间之前，一直调用 tryAcquire(int) 将线程加入队列，线程可能重复被阻塞或不被阻塞。可以用此方法来实现 Lock.tryLock(long, TimeUnit) 方法。
##### 参数：
arg - acquire 参数。此值被传送给 tryAcquire(int)，但它是不间断的，并且可以表示任何内容。
nanosTimeout - 等待的最长时间，以毫微秒为单位
##### 返回：
如果获取对象，则返回 true，如果超时，则返回 false
##### 抛出：
InterruptedException - 如果当前线程被中断


##### release
```
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```
以独占模式释放对象。如果 tryRelease(int) 返回 true，则通过消除一个或多个线程的阻塞来实现此方法。可以使用此方法来实现 Lock.unlock() 方法
##### 参数：
arg - release 参数。此值被传送给 tryRelease(int)，但它是不间断的，并且可以表示任何内容。
##### 返回：
从 tryRelease(int) 返回的值
##### acquireShared
```
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0)
            doAcquireShared(arg);
    }
```
以共享模式获取对象，忽略中断。通过至少先调用一次 tryAcquireShared(int) 来实现此方法，并在成功时返回。否则在成功之前，一直调用 tryAcquireShared(int) 将线程加入队列，线程可能重复被阻塞或不被阻塞。
##### 参数：
arg - acquire 参数。此值被传送给 tryAcquireShared(int)，但它是不间断的，并且可以表示任何内容。


##### acquireSharedInterruptibly
```
    public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }
```
以共享模式获取对象，如果被中断则中止。通过先检查中断状态，然后至少调用一次 tryAcquireShared(int) 来实现此方法，并在成功时返回。否则在成功或线程被中断之前，一直调用 tryAcquireShared(int) 将线程加入队列，线程可能重复被阻塞或不被阻塞。
##### 参数：
arg - acquire 参数。此值被传送给 tryAcquireShared(int)，但它是不间断的，并且可以表示任何内容。
##### 抛出：
InterruptedException - 如果当前线程被中断



##### tryAcquireSharedNanos
```
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquireShared(arg) >= 0 ||
            doAcquireSharedNanos(arg, nanosTimeout);
    }
```
试图以共享模式获取对象，如果被中断则中止，如果到了给定超时时间，则会失败。通过先检查中断状态，然后至少调用一次 tryAcquireShared(int) 来实现此方法，并在成功时返回。否则在成功之前、线程被中断之前或者到达超时时间之前，一直调用 tryAcquireShared(int) 将线程加入队列，线程可能重复被阻塞或不被阻塞。
##### 参数：
arg - acquire 参数。此值被传送给 tryAcquireShared(int)，但它是不间断的，并且可以表示任何内容。
nanosTimeout - 等待的最长时间，以毫微秒为单位
##### 返回：
如果获取对象，则返回 true，如果超时，则返回 false
##### 抛出：
InterruptedException - 如果当前线程被中断


##### releaseShared
```
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
```
以共享模式释放对象。如果 tryReleaseShared(int) 返回 true，则通过消除一个或多个线程的阻塞来实现该方法。
##### 参数：
arg - release 参数。此值被传送给 tryReleaseShared(int)，但它是不间断的，并且可以表示任何内容。
##### 返回：
从 tryReleaseShared(int) 中返回的值

##### hasQueuedThreads
```
    public final boolean hasQueuedThreads() {
        return head != tail;
    }
```
查询是否有正在等待获取的任何线程。注意，随时可能因为中断和超时而导致取消操作，返回 true 并不能保证其他任何线程都将获取对象。
在此实现中，该操作是以固定时间返回的。

##### 返回：
如果可能有其他线程正在等待获取锁，则返回 true。
##### hasContended
```
    public final boolean hasContended() {
        return head != null;
    }
```
查询是否其他线程也曾争着获取此同步器；也就是说，是否某个 acquire 方法已经阻塞。
在此实现中，该操作是以固定时间返回的。

##### 返回：
如果曾经出现争用，则返回 true
##### getFirstQueuedThread
```
    private Thread fullGetFirstQueuedThread() {
        /*
         * The first node is normally head.next. Try to get its
         * thread field, ensuring consistent reads: If thread
         * field is nulled out or s.prev is no longer head, then
         * some other thread(s) concurrently performed setHead in
         * between some of our reads. We try this twice before
         * resorting to traversal.
         */
        Node h, s;
        Thread st;
        if (((h = head) != null && (s = h.next) != null &&
             s.prev == head && (st = s.thread) != null) ||
            ((h = head) != null && (s = h.next) != null &&
             s.prev == head && (st = s.thread) != null))
            return st;

        /*
         * Head's next field might not have been set yet, or may have
         * been unset after setHead. So we must check to see if tail
         * is actually first node. If not, we continue on, safely
         * traversing from tail back to head to find first,
         * guaranteeing termination.
         */

        Node t = tail;
        Thread firstThread = null;
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }

```
返回队列中第一个（等待时间最长的）线程，如果目前没有将任何线程加入队列，则返回 null.
在此实现中，该操作是以固定时间返回的，但是，如果其他线程目前正在并发修改该队列，则可能出现循环争用。

##### 返回：
队列中第一个（等待时间最长的）线程，如果目前没有将任何线程加入队列，则返回 null
##### isQueued
```
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.thread == thread)
                return true;
        return false;
    }
```
如果给定线程的当前已加入队列，则返回 true。
该实现将遍历队列，以确定给定线程是否存在。

##### 参数：
thread - 线程
##### 返回：
如果给定线程在队列中，则返回 true
##### 抛出：
NullPointerException - 如果 thread 为 null

##### getQueueLength
`public final int getQueueLength()`
返回等待获取的线程数估计值。该值只能是一个估计值，因为在此方法遍历内部数据结构时，线程的数量可能发生大的变化。该方法是为用来监视系统状态而设计的，不是为同步控制设计的。
##### 返回：
正等待获取的线程估计数
##### getQueuedThreads
```
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null)
                list.add(t);
        }
        return list;
    }
```
返回包含可能正在等待获取的线程 collection。因为在构造该结果时，实际线程 set 可能发生大的变化，所以返回的 collection 只是尽最大努力获得的一个估计值。返回 collection 的元素并不是以特定顺序排列的。此方法是为促进子类的构造而设计的，这些子类提供了大量的监视设施。
##### 返回：
线程的 collection
getExclusiveQueuedThreads
`public final Collection<Thread> getExclusiveQueuedThreads()`
返回包含可能正以独占模式等待获取的线程 collection。此方法具有与 getQueuedThreads() 相同的属性，除了它只返回那些因独占获取而等待的线程。
##### 返回：
线程的 collection
##### getSharedQueuedThreads
```
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }
```
返回包含可能正以共享模式等待获取的线程 collection。此方法具有与 getQueuedThreads() 相同的属性，除了它只返回那些因共享获取而等待的线程。
##### 返回：
线程的 collection
##### toString
```
    public String toString() {
        int s = getState();
        String q  = hasQueuedThreads() ? "non" : "";
        return super.toString() +
            "[State = " + s + ", " + q + "empty queue]";
    }
```
返回标识此同步器及其状态的字符串。此状态被括号括起来，它包括字符串 "State ="，后面是 getState() 的当前值，再后面是 "nonempty" 或 "empty"，这取决于队列是否为空。
##### 覆盖：
类 Object 中的 toString
##### 返回：
标识此同步器及其状态的字符串

##### owns
```
    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }
```
查询给定的 ConditionObject 是否使用了此同步器作为其锁。
##### 参数：
condition - 条件
##### 返回：
如果具备此条件，则返回 true
##### 抛出：
NullPointerException - 如果 condition 为 null
##### hasWaiters
```
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }
```
查询是否有线程正在等待给定的、与此同步器相关的条件。注意，因为随时可能发生超时和中断，所以返回 true 并不能保证将来某个 signal 将唤醒任何线程。此方法主要是为了监视系统状态而设计的。
##### 参数：
condition - 条件
##### 返回：
如果有正在等待的线程，则返回 true
##### 抛出：
IllegalMonitorStateException - 如果不进行独占同步
IllegalArgumentException - 如果给定的 condition 与此同步器无关
NullPointerException - 如果 condition 为 null
##### getWaitQueueLength
```
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }

```
返回正在等待与此同步器有关的给定条件的线程数估计值。注意，因为随时可能发生超时和中断，所以估计值只是实际等待线程的数量上限。此方法是为监视系统状态而设计的，不是为同步控制设计的。
##### 参数：
condition - 条件
##### 返回：
等待线程的估计数
##### 抛出：
IllegalMonitorStateException - 如果不进行独占同步
IllegalArgumentException - 如果给定的 condition 与此同步器无关
NullPointerException - 如果 condition 为 null

##### getWaitingThreads
```
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }
```
返回一个 collection，其中包含可能正在等待与此同步器有关的给定条件的那些线程。因为在构造该结果时，实际线程 set 可能发生大的变化，所以返回的 collection 只是尽最大努力获得的一个估计值。返回 collection 的元素并不是以特定顺序排列的。
##### 参数：
condition - 条件
##### 返回：
线程的 collection
##### 抛出：
IllegalMonitorStateException - 如果不进行独占同步
IllegalArgumentException - 如果给定的 condition 与此同步器无关
NullPointerException - 如果 condition 为 null

### 3. 源码详解
为什么把这些单独拿出来呢,个人认为它是上面方法实现的底层，它提现了对队列和链表的详细处理，也是上面方法的核心处理代码。我只拿了几个最核心的部分，看懂都好。想详细查看请自己查找源码。

为什么说它需要队列和链表的基础呢？

我们来看下AQS的数据结构Node结点的相关代码
##### addWaiter方法
```
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // 尝试快速方式直接放到队尾。
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }
```
为当前线程创建并排队节点,其实就是增长链表。详细请参考[Java AbstractQueuedSynchronizer源码阅读2-addWaiter()](https://www.jianshu.com/p/c806dd7f60bc)


##### enq方法
```
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```
将节点插入队列，如有必要进行初始化。

因为上面两个方法都调用了compareAndSetTail方法，所以都有在链表后加结点的操作。
##### unparkSuccessor方法
```
    private void unparkSuccessor(Node node) {
        //这里，node一般为当前线程所在的结点。
        int ws = node.waitStatus;
        if (ws < 0)//置零当前线程所在的结点状态，允许失败。
            compareAndSetWaitStatus(node, ws, 0);
        //找到下一个需要唤醒的结点s
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)//从这里可以看出，<=0的结点，都是还有效的结点。
                    s = t;
        }
        if (s != null)
            //唤醒
            LockSupport.unpark(s.thread);
    }
```
如果结点后还有结点的话,唤醒节点的后继者。

##### cancelAcquire方法
```
    private void cancelAcquire(Node node) {
        // 如果节点不存在
        if (node == null)
            return;

        node.thread = null;

        // 获取到前一个结点
        Node pred = node.prev;
        //一直获取到队列中的最后一个结点
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;
      
        // predNext is the apparent node to unsplice. CASes below will
        // fail if not, in which case, we lost race vs another cancel
        // or signal, so no further action is necessary.
        Node predNext = pred.next;

        // Can use unconditional write instead of CAS here.
        // After this atomic step, other Nodes can skip past us.
        // Before, we are free of interference from other threads.
        node.waitStatus = Node.CANCELLED;

        // If we are the tail, remove ourselves.
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, predNext, null);
        } else {
            // If successor needs signal, try to set pred's next-link
            // so it will get one. Otherwise wake it up to propagate.
            int ws;
            if (pred != head &&
                ((ws = pred.waitStatus) == Node.SIGNAL ||
                 (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    compareAndSetNext(pred, predNext, next);
            } else {
                unparkSuccessor(node);
            }

            node.next = node; // help GC
        }
    }
```
取消获取锁。emmm，自己静下心去分析。它判断了所有的结点可能出现的情况，来对链表的最后一个结点进行操作。详细请参考文章：[Java AbstractQueuedSynchronizer源码阅读3-cancelAcquire()](https://www.jianshu.com/p/01f2046aab64)

##### 条件唤醒 以后有时间研究了再说，谢谢大家！                                                       
