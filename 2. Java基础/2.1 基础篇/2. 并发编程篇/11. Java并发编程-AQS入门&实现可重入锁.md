>[Java并发编程 -- AQS](https://www.jianshu.com/p/b842f5f8b7ad)可能会看的一脸懵逼，今天实战一个项目练手AQS

#### MyAQSLock.java
```
/**
 * Created by Fant.J.
 */
public class MyAQSLock implements Lock {

    private Helper helper = new Helper();

    private class Helper extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {

            // 如果第一个线程进来，可以拿到锁，因此我们可以返回true

            // 如果第二个线程进来，则拿不到锁，返回false。有种特例，如果当前进来的线程和当前保存的线程是同一个线程，则可以拿到锁，但是有代价，要更新状态值

            // 如何判断是第一个线程进来还是其他线程进来？
            //获取状态值
            int state = getState();
            Thread t = Thread.currentThread();
            //如果状态=0，那就是第一个线程
            if (state == 0) {
                if (compareAndSetState(0, arg)) {
                    //设置当前线程为独占锁线程
                    setExclusiveOwnerThread(t);
                    return true;
                }
            } else if (getExclusiveOwnerThread() == t) {
                setState(state + 1);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {

            // 锁的获取和释放肯定是一一对应的，那么调用此方法的线程一定是当前线程
            //获取当前线程,如果不等于独占锁的线程
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new RuntimeException();
            }

            int state = getState() - arg;

            boolean flag = false;

            if (state == 0) {
                setExclusiveOwnerThread(null);
                flag = true;
            }

            setState(state);

            return flag;
        }

        Condition newCondition() {
            return new ConditionObject();
        }

    }

    @Override
    public void lock() {
        helper.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        helper.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return helper.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return helper.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        helper.release(1);
    }

    @Override
    public Condition newCondition() {
        return helper.newCondition();
    }
}

```
从上往下分析，首先继承Lock接口，然后定义一个子类为非公共内部帮助器类Helper类，Helper类继承AQS，重写它的tryAcquire和tryRelease方法。作为锁的获取和释放。然后填充Lock的子类实现。（为什么Lock子类方法里传值都是1呢，因为AQS源码就是这样，1一路传到底），注释还算详细，就不在这多说了。
```
/**
 * Created by Fant.J.
 */
class TestAQS {

    private int value;

    private MyAQSLock myAQSLock = new MyAQSLock();

    public int next(){
        myAQSLock.lock();
        try {
            Thread.sleep(300);
            return value++;
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }finally {
            myAQSLock.unlock();
        }
    }
    public void a() {
        myAQSLock.lock();
        System.out.println("a");
        b();
        myAQSLock.unlock();
    }

    public void b() {
        myAQSLock.lock();
        System.out.println("b");
        myAQSLock.unlock();
    }
    public static void main(String[] args) {
        TestAQS test = new TestAQS();


        new Thread(new Runnable() {
            @Override
            public void run() {
                test.a();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println(Thread.currentThread().getName()+"  "+test.next());
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println(Thread.currentThread().getName()+"  "+test.next());
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println(Thread.currentThread().getName()+"  "+test.next());
                }
            }
        }).start();
    }
}

```
开了四个线程，一个线程去跑a方法，a方法中调用b方法，a、b方法调用之前都有加锁，之后有解锁，这个线程用来做可重入锁的测试，其他三个线程是测试线程安全。

### 结果图
![](https://upload-images.jianshu.io/upload_images/5786888-1d6c5476245c7232.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
