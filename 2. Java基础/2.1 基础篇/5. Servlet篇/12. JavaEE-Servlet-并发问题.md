本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

Java servlet容器/ Web服务器通常是多线程的。

为了确保servlet是线程安全的，必须遵循以下基本规则：

* servlet service（）方法不应该访问任何成员变量，除非这些成员变量本身是线程安全的。
* servlet service（）不应该重新分配成员变量，因为这可能会影响在service（）方法内执行的其他线程。如果您确实需要重新分配一个成员变量，请确保这是在一个同步块内完成的。

对线程不了解的可以看我的文集： [java并发学习笔录](https://www.jianshu.com/nb/22549959)  做深一步的了解。这里就不多介绍变量和线程安全的问题。

下面是一个图表，说明了上面提到的servlet并发规则/问题。 红色框表示您的servlet的service（）方法应该小心访问的状态（变量）。![image.png](http://upload-images.jianshu.io/upload_images/5786888-8ae96294e69a1c49.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
