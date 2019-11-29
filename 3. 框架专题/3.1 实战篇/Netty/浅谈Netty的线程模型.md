>说起netty的线程模型，首先我们应该能想到经典的Reactor线程模型，不同的NIO框架的实现方式尽管不同，但是本质上还是遵循了Reactor线程模型。


### 1. 前言
所以我们先大致看看Reactor线程模型：

1. Reactor单线程模型：所有的IO操作都由同一个NIO线程处理。
>使用的是异步非阻塞IO，所有的IO操作都不会导致阻塞，理论上一个线程可以独立处理所有IO相关的操作。但是即使线程利用率达到100%，也不会能撑住太高的并发。
2. Reactor多线程模型：Reactor多线程模型与单线程模型最大的区别就是有一组NIO线程来处理IO操作。
>有专门一个NIO线程—-Acceptor线程用于监听服务端，接收客户端的TCP连接请求,网络IO操作—-读、写等由一个NIO线程池负责。瓶颈在于客户端同时连接数，毕竟是一个线程再做监听工作。
3.   主从Reactor模型：服务端用于接收客户端连接的不再是一个单独的NIO线程，而是一个独立的NIO线程池。后续操作与2类似。
>官方推荐模型,可以解决一个服务端监听线程无法有效处理所有客户端连接的性能不足问题。

### 2. Netty线程模型
>Netty的线程模型并不是一成不变的，它实际取决于用户的启动参数配置。通过设置不同的启动参数，Netty可以同时支持Reactor但单线程模型、多线程模型和主从Reactor多线程模型。 

##### Demo
```
 // 配置服务端的NIO线程组
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();

                // 设置线程组及Socket参数
                b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childHandler(new ChildChannelHandler());
````
服务端启动的时候，创建了两个NioEventLoopGroup。它们实际是两个独立的Reactor线程池。一个用于接收客户端的TCP连接，另一个用于处理IO相关的读写操作，或者执行系统Task、定时任务等。

###### Netty用于接收客户端请求的线程池职责如下： 
1. 接收客户端TCP连接，初始化Channel参数； 
2. 将链路状态变更事件通知给ChannelPipeline。 

###### Netty处理IO操作的Reactor线程池职责如下： 
1. 异步读取通信对端的数据报，发送读事件到ChannalPipeline； 
2. 异步发送消息到通信对端，调用ChannelPipeline的消息发送接口； 
3. 执行系统调用task； 
4. 执行定时任务Task，例如链路空闲状态监测定时任务。

**通过调整线程池的线程个数、是否共享线程池等方式，Netty的Reactor模型可以在上述模型中灵活转换。**

### 3.  与Reactor类似却又不同
>为了尽可能的提升性能，Netty在很多地方进行了无锁化设计，例如在IO线程内部进行串行操作，避免多线程竞争导致的性能下降问题。表面上看，串行化设计似乎CPU利用率不高，并发程度不够，但是，通过调整NIO线程池的线程参数，可以同时启动多个串行化的线程并行运行，这种局部无锁化的串行线程设计相比一个队列---多个工作线程的模型性能更优。

Netty的`NioEventLoop`读取到消息之后，直接调用`ChannelPipeline的fireChannelRead(Object msg)`。只要用户不主动切换线程，一直都是由`NioEventLoopLoop`调用用户的`Handler`，期间不进行线程切换。这种串行处理方式避免了多线程操作导致的锁的竞争，从性能角度看是最优的。
