单例模式是指对一个对象进行一次实例化，然后全局都可以调用该实例化对象来完成项目的开发。

在计算机系统中，线程池、缓存、日志对象、对话框、打印机、显卡的驱动程序对象常被设计成单例。这些应用都或多或少具有资源管理器的功能。每台计算机可以有若干个打印机，但只能有一个Printer Spooler，以避免两个打印作业同时输出到打印机中。每台计算机可以有若干通信端口，系统应当集中管理这些通信端口，以避免一个通信端口同时被两个请求同时调用。总之，选择单例模式就是为了避免不一致状态，避免政出多头。

### 实现单例的不同方式

##### 饿汉式单例
饿汉式单例是指在方法调用前，实例就已经创建好了。下面是实现代码：
```
package com.thread.singleton;

/**
 * 单例模式-- 饿汉式
 * Created by Fant.J.
 * 2018/2/25 19:24
 */
public class Singleton1 {
    /** 私有化构造方法,在外部不能实例化对象 */
    private Singleton1(){}

    /** 在这里实例化 静态对象  （优点：不存在线程安全问题。  缺点：每次调用都实例化，占用空间） */
    private static Singleton1 singleton1 = new Singleton1();

    public static Singleton1 getInstance(){
        return singleton1;
    }

}

```
优点：不存在线程安全问题。  缺点：每次调用都实例化，占用空间

##### 懒汉式单例
懒汉式单例是指在方法调用获取实例时才创建实例，因为相对饿汉式显得“不急迫”，所以被叫做“懒汉模式”。下面是实现代码：
```
package com.thread.singleton;

/**
 * 单例模式 -- 懒汉式
 * Created by Fant.J.
 * 2018/2/25 19:30
 */
public class Singleton2 {


    private Singleton2(){}

    private static Singleton2 instance;

    public synchronized static Singleton2 getInstance()  {
        /* 下面这段代码  不是原子性操作  会出现线程安全问题 。**/
        if (instance == null) {
                    instance = new Singleton2();                            
        }
        return instance;
    }
}

```
在这段代码中，在if语句里面，就可能跑有多个线程同步判断和同步new。会产生线程安全问题。
###### 解决方法:
1. 给方法加上synchronized（变成单线程，影响性能）
2. 给代码块加synchronized（双重检查加锁）
                      虽然2方法解决了性能问题，  但是还会有问题 。
                      问题来自  jvm 的优化：指令重排序（有兴趣了解）
                      我们可以在对象中添加volatile 关键字来 不让jvm对该 对象做优化
完善后的代码如下:
```
package com.thread.singleton;

/**
 * 单例模式 -- 懒汉式
 * Created by Fant.J.
 * 2018/2/25 19:30
 */
public class Singleton2 {


    private Singleton2(){}

    private static Singleton2 instance;

    public synchronized static Singleton2 getInstance()  {
        /* 下面这段代码  不是原子性操作  会出现线程安全问题 。
            解决方法：1.给方法加上synchronized（变成单线程，影响性能）
                    2.给代码块加synchronized（双重检查加锁）
                      虽然2方法解决了性能问题，  但是还会有问题 。
                      问题来自  jvm 的优化：指令重排序（有兴趣了解）
                      我们可以在对象中添加volatile 关键字来 不让jvm对该 对象做优化
        **/
        if (instance == null) {
            synchronized (Singleton2.class){
                if (instance == null){
                    instance = new Singleton2();
                }
            }
        }
        return instance;
    }
}

```
