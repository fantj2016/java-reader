###1.volatile关键字的两层语义
一旦一个共享变量（类的成员变量、类的静态成员变量）被volatile修饰之后，那么就具备了两层语义：

1. 保证了不同线程对这个变量进行操作时的可见性，即一个线程修改了某个变量的值，这新值对其他线程来说是立即可见的。
2. 禁止进行指令重排序。

我们来看一段代码：
```
假如线程1先执行，线程2后执行：

//线程1
boolean stop = false;
while(!stop){
    //doSomething();
}

//线程2
stop = true;
```
事实上，这段代码会真的先执行线程1，然后再执行2吗，答案是肯定的：不是。两个线程各干各的事情，没有绝对的先后问题，所以会出现两种答案（一个线程用stop=false跑线程1，一个线程用stop=true跑线程二，互不相关）。

######但是用volatile修饰之后就变得不一样了：

第一：使用volatile关键字会强制将修改的值立即写入主存；

第二：使用volatile关键字的话，当线程2进行修改时，会导致线程1的工作内存中缓存变量stop的缓存行无效（反映到硬件层的话，就是CPU的L1或者L2缓存中对应的缓存行无效）；

第三：由于线程1的工作内存中缓存变量stop的缓存行无效，所以线程1再次读取变量stop的值时会去主存读取。

所以肯定能保证不管 多少个线程跑的时候，stop的值是相同的。这是利用了volatile的线程可见性原理。

###2.volatile保证原子性吗？
从上面知道volatile关键字保证了操作的可见性，但是volatile能保证对变量的操作是原子性吗？看一段代码：
```
public class Test {
    public volatile int inc = 0;

    public void increase() {
        inc++;
    }

    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }

        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```
经过上面的介绍volatile的可见性，我想大家能很快的得出这段代码的答案10000。然而事实上打印出来的数字总比10000小。这就涉及到了volatile与原子性操作的联系。
原因：自增操作不是原子性操作，而且volatile也无法保证对变量的任何操作都是原子性的。
* 自增操作是不具备原子性的，它包括读取变量的原始值、进行加1操作、写入工作内存。那么就是说自增操作的三个子操作可能会分割开执行
* 假如线程1从住内存中获取到变量值，在执行自增的时候是阻塞性质的，这时候线程2也拿到一个相同的值，然后也进行自增，那么这两个线程最终写入的值是一样的。


#####那如何修改呢？有三种方式
1. 给自增方法加上同步锁synchronized 
```
public class Test {
    public  int inc = 0;

    public synchronized void increase() {
        inc++;
    }

    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }

        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```
2. 采用Lock
```
public class Test {
    public  int inc = 0;
    Lock lock = new ReentrantLock();

    public  void increase() {
        lock.lock();
        try {
            inc++;
        } finally{
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }

        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```
3. 采用AtomicInteger
```
public class Test {
    public  AtomicInteger inc = new AtomicInteger();

    public  void increase() {
        inc.getAndIncrement();
    }

    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }

        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```
在java 1.5的java.util.concurrent.atomic包下提供了一些原子操作类，即对基本数据类型的 自增（加1操作），自减（减1操作）、以及加法操作（加一个数），减法操作（减一个数）进行了封装，保证这些操作是原子性操作。atomic是利用CAS来实现原子性操作的（Compare And Swap），CAS实际上是利用处理器提供的CMPXCHG指令实现的，而处理器执行CMPXCHG指令是一个原子性操作。

###3.volatile能保证有序性吗？
在前面提到volatile关键字能禁止指令重排序，所以volatile能在一定程度上保证有序性。


###4.volatile的原理和实现机制
前面讲述了源于volatile关键字的一些使用，下面我们来探讨一下volatile到底如何保证可见性和禁止指令重排序的。

下面这段话摘自《深入理解Java虚拟机》：

“观察加入volatile关键字和没有加入volatile关键字时所生成的汇编代码发现，加入volatile关键字时，会多出一个lock前缀指令”

lock前缀指令实际上相当于一个内存屏障（也成内存栅栏），内存屏障会提供3个功能：

1. 它确保指令重排序时不会把其后面的指令排到内存屏障之前的位置，也不会把前面的指令排到内存屏障的后面；即在执行到内存屏障这句指令时，在它前面的操作已经全部完成；

2. 它会强制将对缓存的修改操作立即写入主存；

3. 如果是写操作，它会导致其他CPU中对应的缓存行无效。












