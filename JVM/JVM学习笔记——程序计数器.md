

######在网上挑选了这么几段话，精简易懂

>程序计数器（program counter register）只占用了一块比较小的内存空间，至于小到什么程度呢，这样说吧，有时可以忽略不计的。


>可以看作是当前线程所执行的字节码文件（class）的行号指示器。在虚拟机的世界中，字节码解释器就是通过改变计数器的值来选取下一条执行的字节码指令，分支、循环、跳转、异常处理、线程恢复都需要这玩意来实现的。

上面提到线程恢复，那下面介绍下其详细流程：
>java虚拟机多线程是通过线程间轮流切换来分配给处理器执行时间；在确定时间节点，一个处理器（一核）只会执行一个线程的指令；为保证 线程切换 回来后能恢复到原执行位置，各个线程间计数器互相不影响，独立存储（称之为 线程私有 的内存）；

>如果执行的是java方法，那么记录的是正在执行的虚拟机字节码指令的地址的地址，如果是native方法，计数器的值为空（undefined）。

最后，动动自己的大脑，想下面的问题：
>这个内存区域是唯一一个在java虚拟界规范中没有规定任何OutOfMemoryError的情况的区域。至于为什么没有这个异常呢，要是一个计数的功能在出这个异常，那么我也是醉了。


####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

######底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)
