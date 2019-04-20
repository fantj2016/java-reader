>带你快速了解这几个同步信号工具。

### 1. CountDownLatch
>用给定的计数 初始化 CountDownLatch。由于调用了 countDown() 方法，所以在当前计数到达零之前，await 方法会一直受阻塞。之后，会释放所有等待的线程，await 的所有后续调用都将立即返回。

案例：项目经理改个需求，当小王、小李、小赵都相继改完代码后，项目经理才进行审查。

##### 1.1 Boss.java
```
public class Boss implements Runnable{
    private CountDownLatch countDownLatch;

    public Boss(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("Boss: 需求改动!速度更新!");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Boss: 都改完了是吗，我看看阿!");
    }
}
```
##### 1.2 Worker.java
```
public class Worker implements Runnable{
    private CountDownLatch countDownLatch;
    private String name;

    public Worker(CountDownLatch countDownLatch, String name) {
        this.countDownLatch = countDownLatch;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + ": 开始改代码");
        try {
            Thread.sleep((long) (Math.random()*10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + ": 代码改完了!");
        countDownLatch.countDown();
    }
}
```

##### 1.3 Main
```
public class Main {
    public static void main(String[] args) {
        // 当调用三次countDown时释放所有等待线程
        CountDownLatch countDownLatch = new CountDownLatch(3);
        ExecutorService executorService = Executors.newCachedThreadPool();
        Boss boss = new Boss(countDownLatch);
        Worker worker1 = new Worker(countDownLatch, "小王");
        Worker worker2 = new Worker(countDownLatch, "小李");
        Worker worker3 = new Worker(countDownLatch, "小赵");
        executorService.execute(boss);
        executorService.execute(worker1);
        executorService.execute(worker2);
        executorService.execute(worker3);
        executorService.shutdown();
    }
}
```


结果:
```
Boss: 需求改动!速度更新!
小王: 开始改代码
小李: 开始改代码
小赵: 开始改代码
小王: 代码改完了!
小李: 代码改完了!
小赵: 代码改完了!
Boss: 都改完了是吗，我看看阿!
```

### 2. CyclicBarrier
>一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)。

案例：老板叫开会，等所有人都来了后就开始开会，先来的人先wait。

#### 2.1 Boss
```
public class Boss implements Runnable{
    private CyclicBarrier cyclicBarrier;

    Boss(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        System.out.println("Boss: 开会!");
    }
}
```

#### 2.2 Worker
```
public class Worker implements Runnable{
    private CyclicBarrier cyclicBarrier;
    private String name;

    public Worker(CyclicBarrier cyclicBarrier, String name) {
        this.cyclicBarrier = cyclicBarrier;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + ": 我正在路上");
            Thread.sleep((long) (Math.random()*10000));
            System.out.println(name + ": 我到了!");
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
```

#### 2.3 Main
```
public class Main {
    public static void main(String[] args) {
        // 当三个线程处于barrier(wait)状态的时候，同时开始执行后续任务。
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () ->System.out.println("Boss: 都到了吗?那我来说两句...balabala..."));
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Boss boss = new Boss(cyclicBarrier);
        Worker worker1 = new Worker(cyclicBarrier, "小王");
        Worker worker2 = new Worker(cyclicBarrier, "小李");
        Worker worker3 = new Worker(cyclicBarrier, "小赵");

        executorService.execute(boss);
        executorService.execute(worker1);
        executorService.execute(worker2);
        executorService.execute(worker3);
        executorService.shutdown();
    }
}
```
我设置当三个线程处于barrier(wait)状态的时候，同时开始执行后续任务。
```
Boss: 开会!
小王: 我正在路上
小李: 我正在路上
小赵: 我正在路上
小王: 我到了!
小李: 我到了!
小赵: 我到了!
Boss: 都到了吗?那我来说两句...balabala...
```


### 3. Semaphore
>一个计数信号量。从概念上讲，信号量维护了一个许可集。如有必要，在许可可用前会阻塞每一个 acquire()，然后再获取该许可。每个 release() 添加一个许可，从而可能释放一个正在阻塞的获取者。但是，不使用实际的许可对象，Semaphore 只对可用许可的号码进行计数，并采取相应的行动。

通常用于限制可以访问某些资源（物理或逻辑的）的线程数目。(类似网络带宽)

案例：老板发红包，办公室只能站下三个人，员工排队去领。


#### 3.1 Boss
```
public class Boss implements Runnable{
    @Override
    public void run() {
        System.out.println("排队领红包!我身边最多围三个人!");
    }
}
```
#### 3.2 Worker
```
public class Worker implements Runnable{
    private Semaphore semaphore;
    private String name;

    Worker(Semaphore semaphore, String name) {
        this.semaphore = semaphore;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println(name + ": 我正在老板面前等红包");
            Thread.sleep((long) (Math.random()*10000));
            System.out.println(name +": 我拿到红包了");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
#### 3.3 Main
```
public class Main {
    public static void main(String[] args) {
        // 设置三个信号量，只有拿到信号才能执行后续任务
        Semaphore semaphore = new Semaphore(3);
        ExecutorService executorService = Executors.newCachedThreadPool();
        Worker worker1 = new Worker(semaphore, "小王");
        Worker worker2 = new Worker(semaphore, "小Li");
        Worker worker3 = new Worker(semaphore, "小赵");
        Worker worker4 = new Worker(semaphore, "小孙");
        Worker worker5 = new Worker(semaphore, "小周");
        Boss boss = new Boss();

        executorService.execute(boss);
        executorService.execute(worker1);
        executorService.execute(worker2);
        executorService.execute(worker3);
        executorService.execute(worker4);
        executorService.execute(worker5);
        executorService.shutdown();
    }
}
```
```
排队领红包!我身边最多围三个人!
小王: 我正在老板面前等红包
小Li: 我正在老板面前等红包
小孙: 我正在老板面前等红包
小Li: 我拿到红包了
小赵: 我正在老板面前等红包
小孙: 我拿到红包了
小周: 我正在老板面前等红包
小周: 我拿到红包了
小王: 我拿到红包了
小赵: 我拿到红包了
```