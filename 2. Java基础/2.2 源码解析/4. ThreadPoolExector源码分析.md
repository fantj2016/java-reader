>线程池源码也是面试经常被提问到的点，我会将全局源码做一分析，然后告诉你面试考啥，怎么答。

### 为什么要用线程池?
>简洁的答两点就行。

1. 降低系统资源消耗。
2. 提高线程可控性。

### 如何创建使用线程池?

JDK8提供了五种创建线程池的方法：

1. 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
```
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}
```
2. (JDK8新增)会根据所需的并发数来动态创建和关闭线程。能够合理的使用CPU进行对任务进行并发操作，所以适合使用在很耗时的任务。
>注意返回的是ForkJoinPool对象。
```
public static ExecutorService newWorkStealingPool(int parallelism) {
    return new ForkJoinPool
        (parallelism,
         ForkJoinPool.defaultForkJoinWorkerThreadFactory,
         null, true);
}

什么是ForkJoinPool:

public ForkJoinPool(int parallelism,
                        ForkJoinWorkerThreadFactory factory,
                        UncaughtExceptionHandler handler,
                        boolean asyncMode) {
        this(checkParallelism(parallelism),
             checkFactory(factory),
             handler,
             asyncMode ? FIFO_QUEUE : LIFO_QUEUE,
             "ForkJoinPool-" + nextPoolId() + "-worker-");
        checkPermission();
    }
使用一个无限队列来保存需要执行的任务，可以传入线程的数量，
不传入，则默认使用当前计算机中可用的cpu数量，
使用分治法来解决问题，使用fork()和join()来进行调用
```
3. 创建一个可缓存的线程池,可灵活回收空闲线程，若无可回收，则新建线程。
```
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
```
4. 创建一个单线程的线程池。
```
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1,
                                0L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<Runnable>()));
}
```
5. 创建一个定长线程池，支持定时及周期性任务执行。
```
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize);
}
```

### 上层源码结构分析

>Executor结构:
![](https://upload-images.jianshu.io/upload_images/5786888-3c7c75e9b406f155.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### Executor
>一个运行新任务的简单接口
```
public interface Executor {

    void execute(Runnable command);
}
```

#### ExecutorService
>扩展了Executor接口。添加了一些用来管理执行器生命周期和任务生命周期的方法
![](https://upload-images.jianshu.io/upload_images/5786888-3d8ddd3eeb25b148.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### AbstractExecutorService
>对ExecutorService接口的抽象类实现。不是我们分析的重点。


#### ThreadPoolExecutor
>Java线程池的核心实现。


### ThreadPoolExecutor源码分析

#### 属性解释

```
// AtomicInteger是原子类  ctlOf()返回值为RUNNING；
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
// 高3位表示线程状态
private static final int COUNT_BITS = Integer.SIZE - 3;
// 低29位表示workerCount容量
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

// runState is stored in the high-order bits
// 能接收任务且能处理阻塞队列中的任务
private static final int RUNNING    = -1 << COUNT_BITS;
// 不能接收新任务，但可以处理队列中的任务。
private static final int SHUTDOWN   =  0 << COUNT_BITS;
// 不接收新任务，不处理队列任务。
private static final int STOP       =  1 << COUNT_BITS;
// 所有任务都终止
private static final int TIDYING    =  2 << COUNT_BITS;
// 什么都不做
private static final int TERMINATED =  3 << COUNT_BITS;

// 存放任务的阻塞队列
private final BlockingQueue<Runnable> workQueue;

```
值的注意的是状态值越大线程越不活跃。
##### 线程池状态的转换模型：

![](https://upload-images.jianshu.io/upload_images/5786888-97fe7aee4be7bb4c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 构造器
```
public ThreadPoolExecutor(int corePoolSize,//线程池初始启动时线程的数量
                          int maximumPoolSize,//最大线程数量
                          long keepAliveTime,//空闲线程多久关闭?
                          TimeUnit unit,// 计时单位
                          BlockingQueue<Runnable> workQueue,//放任务的阻塞队列
                          ThreadFactory threadFactory,//线程工厂
                          RejectedExecutionHandler handler// 拒绝策略) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
            null :
            AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```

在向线程池提交任务时，会通过两个方法：execute和submit。

**本文着重讲解execute方法**。submit方法放在下次和Future、Callable一起分析。
#### execute方法:
```
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
    // clt记录着runState和workerCount
    int c = ctl.get();
    //workerCountOf方法取出低29位的值，表示当前活动的线程数
    //然后拿线程数和 核心线程数做比较
    if (workerCountOf(c) < corePoolSize) {
        // 如果活动线程数<核心线程数
        // 添加到
        //addWorker中的第二个参数表示限制添加线程的数量是根据corePoolSize来判断还是maximumPoolSize来判断
        if (addWorker(command, true))
            // 如果成功则返回
            return;
        // 如果失败则重新获取 runState和 workerCount
        c = ctl.get();
    }
    // 如果当前线程池是运行状态并且任务添加到队列成功
    if (isRunning(c) && workQueue.offer(command)) {
        // 重新获取 runState和 workerCount
        int recheck = ctl.get();
        // 如果不是运行状态并且 
        if (! isRunning(recheck) && remove(command))
            reject(command);
        else if (workerCountOf(recheck) == 0)
            //第一个参数为null，表示在线程池中创建一个线程，但不去启动
            // 第二个参数为false，将线程池的有限线程数量的上限设置为maximumPoolSize
            addWorker(null, false);
    }
    //再次调用addWorker方法，但第二个参数传入为false，将线程池的有限线程数量的上限设置为maximumPoolSize
    else if (!addWorker(command, false))
        //如果失败则拒绝该任务
        reject(command);
}
```
总结一下它的工作流程：

1. 当`workerCount < corePoolSize`，创建线程执行任务。

2. 当`workerCount >= corePoolSize`&&阻塞队列`workQueue`未满，把新的任务放入阻塞队列。

3. 当`workQueue`已满，并且`workerCount >= corePoolSize`，并且`workerCount < maximumPoolSize`，创建线程执行任务。

4. 当workQueue已满，`workerCount >= maximumPoolSize`，采取拒绝策略,默认拒绝策略是直接抛异常。


![](https://upload-images.jianshu.io/upload_images/5786888-654f22886b8240b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

通过上面的execute方法可以看到，最主要的逻辑还是在addWorker方法中实现的，那我们就看下这个方法：

#### addWorker方法
>主要工作是在线程池中创建一个新的线程并执行

参数定义：
* `firstTask` the task the new thread should run first (or null if none). (指定新增线程执行的第一个任务或者不执行任务)
* `core` if true use corePoolSize as bound, else maximumPoolSize.(core如果为true则使用corePoolSize绑定，否则为maximumPoolSize。 （此处使用布尔指示符而不是值，以确保在检查其他状态后读取新值）。)
```
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    for (;;) {
        
        int c = ctl.get();
        //  获取运行状态
        int rs = runStateOf(c);

        // Check if queue empty only if necessary.
        // 如果状态值 >= SHUTDOWN (不接新任务&不处理队列任务)
        // 并且 如果 ！(rs为SHUTDOWN 且 firsTask为空 且 阻塞队列不为空)
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            // 返回false
            return false;

        for (;;) {
            //获取线程数wc
            int wc = workerCountOf(c);
            // 如果wc大与容量 || core如果为true表示根据corePoolSize来比较,否则为maximumPoolSize
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            // 增加workerCount（原子操作）
            if (compareAndIncrementWorkerCount(c))
                // 如果增加成功，则跳出
                break retry;
            // wc增加失败，则再次获取runState
            c = ctl.get();  // Re-read ctl
            // 如果当前的运行状态不等于rs，说明状态已被改变，返回重新执行
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
    }

    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        // 根据firstTask来创建Worker对象
        w = new Worker(firstTask);
        // 根据worker创建一个线程
        final Thread t = w.thread;
        if (t != null) {
            // new一个锁
            final ReentrantLock mainLock = this.mainLock;
            // 加锁
            mainLock.lock();
            try {
                // Recheck while holding lock.
                // Back out on ThreadFactory failure or if
                // shut down before lock acquired.
                // 获取runState
                int rs = runStateOf(ctl.get());
                // 如果rs小于SHUTDOWN(处于运行)或者(rs=SHUTDOWN && firstTask == null)
                // firstTask == null证明只新建线程而不执行任务
                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    // 如果t活着就抛异常
                    if (t.isAlive()) // precheck that t is startable
                        throw new IllegalThreadStateException();
                    // 否则加入worker(HashSet)
                    //workers包含池中的所有工作线程。仅在持有mainLock时访问。
                    workers.add(w);
                    // 获取工作线程数量
                    int s = workers.size();
                    //largestPoolSize记录着线程池中出现过的最大线程数量
                    if (s > largestPoolSize)
                        // 如果 s比它还要大，则将s赋值给它
                        largestPoolSize = s;
                    // worker的添加工作状态改为true    
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            // 如果worker的添加工作完成
            if (workerAdded) {
                // 启动线程
                t.start();
                // 修改线程启动状态
                workerStarted = true;
            }
        }
    } finally {
        if (! workerStarted)
            addWorkerFailed(w);
    }
    // 返回线启动状态
    return workerStarted;

```
###### 为什么需要持有mainLock？
因为workers是HashSet类型的，不能保证线程安全。



那`w = new Worker(firstTask);`如何理解呢

#### Worker.java
```
private final class Worker
    extends AbstractQueuedSynchronizer
    implements Runnable
```
可以看到它继承了AQS并发框架还实现了Runnable。证明它还是一个线程任务类。那我们调用t.start()事实上就是调用了该类重写的run方法.

##### Worker为什么不使用ReentrantLock来实现呢？
tryAcquire方法它是不允许重入的，而ReentrantLock是允许重入的。对于线程来说，如果线程正在执行是不允许其它锁重入进来的。

线程只需要两个状态，一个是独占锁，表明正在执行任务；一个是不加锁，表明是空闲状态。
```
public void run() {
    runWorker(this);
}
```
run方法又调用了runWorker方法：
```
final void runWorker(Worker w) {
    // 拿到当前线程
    Thread wt = Thread.currentThread();
    // 拿到当前任务
    Runnable task = w.firstTask;
    // 将Worker.firstTask置空 并且释放锁
    w.firstTask = null;
    w.unlock(); // allow interrupts
    boolean completedAbruptly = true;
    try {
        // 如果task或者getTask不为空，则一直循环
        while (task != null || (task = getTask()) != null) {
            // 加锁
            w.lock();
            // If pool is stopping, ensure thread is interrupted;
            // if not, ensure thread is not interrupted.  This
            // requires a recheck in second case to deal with
            // shutdownNow race while clearing interrupt
            //  return ctl.get() >= stop 
            // 如果线程池状态>=STOP 或者 (线程中断且线程池状态>=STOP)且当前线程没有中断
            // 其实就是保证两点：
            // 1. 线程池没有停止
            // 2. 保证线程没有中断
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                // 中断当前线程
                wt.interrupt();
            try {
                // 空方法
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    // 执行run方法(Runable对象)
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    afterExecute(task, thrown);
                }
            } finally {
                // 执行完后， 将task置空， 完成任务++， 释放锁
                task = null;
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        // 退出工作
        processWorkerExit(w, completedAbruptly);
    }

```
总结一下runWorker方法的执行过程：

1. while循环中，不断地通过getTask()方法从workerQueue中获取任务
2. 如果线程池正在停止，则中断线程。否则调用3.
3. 调用task.run()执行任务；
4. 如果task为null则跳出循环，执行processWorkerExit()方法，销毁线程`workers.remove(w);`

这个流程图非常经典：
![](https://upload-images.jianshu.io/upload_images/5786888-4c08a0bb6a78abe1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


除此之外，`ThreadPoolExector`还提供了`tryAcquire`、`tryRelease`、`shutdown`、`shutdownNow`、`tryTerminate`、等涉及的一系列线程状态更改的方法有兴趣可以自己研究。大体思路是一样的，这里不做介绍。

##### Worker为什么不使用ReentrantLock来实现呢？
tryAcquire方法它是不允许重入的，而ReentrantLock是允许重入的。对于线程来说，如果线程正在执行是不允许其它锁重入进来的。

线程只需要两个状态，一个是独占锁，表明正在执行任务；一个是不加锁，表明是空闲状态。

##### 在runWorker方法中，为什么要在执行任务的时候对每个工作线程都加锁呢？
shutdown方法与getTask方法存在竞态条件.(这里不做深入，建议自己深入研究，对它比较熟悉的面试官一般会问)


### 高频考点
1. 创建线程池的五个方法。
2. 线程池的五个状态
3. execute执行过程。
4. runWorker执行过程。(把两个流程图记下，理解后说个大该就行。)
5. 比较深入的问题就是我在文中插入的问题。
6. ...期望大家能在评论区补充。


声明：图片来源于网络，若有侵犯请联系博主。