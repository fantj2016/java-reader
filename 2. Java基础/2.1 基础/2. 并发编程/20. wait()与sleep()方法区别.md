本质上的区别： sleep是对线程状态的控制， wait是线程间通讯。一个是Thread类， 一个是Object类。Thread不会影响锁的行为，锁相关的方法都定义在Object类中

1. sleep是Thread的方法， wait是Object的方法。
2. sleep不会释放锁， wait会施放锁并将锁添加到一个等待队列。
3. sleep不依赖monitor， wait依赖monitor。
4. sleep不需要唤醒、wait需要。

![img](https://img-blog.csdn.net/20150309140927553)

**注意**:锁池是对象头中的一个标记，用于存放等待锁的线程。调用wait()方法, 意味着当前线程需要释放自己的所有锁放入锁池, 然后进入该对象的等待队列。当调用notify()时, 就通知等待队列中的一个线程出列, 然后进入锁池去拿到锁。