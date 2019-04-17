###传统Socket中，NIO和IO的比较（Nio知识回顾）
#####IO
阻塞点： `server.accept(); 和 `
单线程情况下只能有一个客户端
用线程池可以有多个客户端连接，但是非常消耗性能
#####NIO
selector.select()会产生阻塞的效果，但是它其实是非阻塞的
为什么呢？
selector.select(1000); 1s检测一次
selector.wakeup(); 唤醒selector
可以任由我们控制休眠和唤醒操作，所以它其实是非阻塞型
要想更加了解NIO，请查看[我的NIO专题](https://www.jianshu.com/nb/21635138)

###什么是netty
netty可以运用在那些领域？

1分布式进程通信
例如: hadoop、dubbo、akka等具有分布式功能的框架，底层RPC通信都是基于netty实现的，这些框架使用的版本通常都还在用netty3.x

2、游戏服务器开发
最新的游戏服务器有部分公司可能已经开始采用netty4.x 或 netty5.x

###1、netty服务端hello world案例
###### 1. 创建Server类


```
package com.netty;


import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Fant.J.
 * 2018/2/2 21:28
 */
public class Server {
    public static void main(String[] args) {
        //服务类
        ServerBootstrap bootstrap = new ServerBootstrap();
        //两个线程池
        ExecutorService booss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();

        //设置niosocket工厂
        bootstrap.setFactory(new NioServerSocketChannelFactory(booss,worker));

        //设置管道工厂
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                //将网络传输中的字节码(ChannelBuffer) 转换成String  可看源码
                pipeline.addLast("decoder",new StringDecoder());
                //将管道交给HelloHandler处理
                pipeline.addLast("helloHandler",new HelloHandler());
                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(10101));
        System.out.println("server start");

    }
}

```
######2. 创建HelloHandler类
需要继承SimpleChannelHandler类，然后重写几个基本方法
* messageReceived()  接受消息
* exceptionCaught() 捕获异常
* channelConnected() 建立连接
* channelDisconnected() 断开连接：必须是连接已经建立，关闭通道的时候才会触发。
* channelClosed() 关闭通道
```
package com.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * 接受消息
 * Created by Fant.J.
 * 2018/2/2 22:54
 */
public class HelloHandler extends SimpleChannelHandler{
    /**
     * Invoked when a message object (e.g: {@link ChannelBuffer}) was received
     * from a remote peer.
     * 接受消息
     * @param ctx
     * @param e
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//        int i = 1/0;
//        System.out.println("messageReceived()");
//        ChannelBuffer message = (ChannelBuffer) e.getMessage();
//        String s = new String(message.array());
        System.out.println(e.getMessage());
//        System.out.println(s);

        //回复数据
        ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer("hi".getBytes());
        ctx.getChannel().write(channelBuffer);
        super.messageReceived(ctx, e);
    }

    /**
     * Invoked when an exception was raised by an I/O thread or a
     * {@link ChannelHandler}.
     * 捕获异常
     * @param ctx
     * @param e
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("exceptionCaught()");
        super.exceptionCaught(ctx, e);
    }

    /**
     * Invoked when a {@link Channel} is open, bound to a local address, and
     * connected to a remote address.
     * 建立连接
     * @param ctx
     * @param e
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelConnected() ");
        super.channelConnected(ctx, e);
    }

    /**
     * Invoked when a {@link Channel} was disconnected from its remote peer.
     * 断开连接：必须是连接已经建立，关闭通道的时候才会触发。
     * @param ctx
     * @param e
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelDisconnected()");
        super.channelDisconnected(ctx, e);
    }

    /**
     * Invoked when a {@link Channel} was closed and all its related resources
     * were released.
     * 关闭通道
     * @param ctx
     * @param e
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed()");
        super.channelClosed(ctx, e);
    }
}

```
因为网络之间传输是通过字节来传输，所以我们在messageReceived()方法里，获取到的message需要对其进行String转换。转换方法：
```
//先拿到message对应的channelbuffer对象
ChannelBuffer message = (ChannelBuffer) e.getMessage();
String s = new String(message.array());
```
同样，我们如果想要回复数据，也要将String转换成字节，这是实例中的一个例子：
```
//前面管道的实例化 这里省略，直接贴出给管道进行StringDecoder()处理代码
pipeline.addLast("decoder",new StringDecoder());
```
让我们来研究下原理，其实是这样转换的：

```
//创建一个ChannelBuffer对象，并赋值 字节
ChannelBuffer channelBuffer = ChannelBuffers.copiedBuffer("hi".getBytes());
//获取到Channel并执行write()方法
ctx.getChannel().write(channelBuffer);
```
github路径：https://github.com/jiaofanting/Java-nio-and-netty-spring-demo/tree/master/src/com/netty
###2、netty客户端hello world案例

#####Client类
```
package com.netty.client;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Fant.J.
 * 2018/2/3 10:59
 */
public class Client {
    public static void main(String []args){
        //服务类
        ClientBootstrap bootstrap = new ClientBootstrap();

        //线程池
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();

        //socket工厂
        bootstrap.setFactory(new NioClientSocketChannelFactory(boss,worker));

        //管道工厂
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder",new StringDecoder());
                pipeline.addLast("encoder",new StringEncoder());
                pipeline.addLast("hiHandler",new HiHandler());
                return pipeline;
            }
        });
        //连接服务器
        ChannelFuture connect  = bootstrap.connect(new InetSocketAddress("127.0.0.1",10101));
        Channel channel = connect.getChannel();

        System.out.println("client start");
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("please input string");
            channel.write(scanner.next());
        }
    }
}

```

#####HiHandler类
```
package com.netty.client;

import org.jboss.netty.channel.*;

/**
 * 消息接受处理器
 * Created by Fant.J.
 * 2018/2/3 10:59
 */
public class HiHandler extends SimpleChannelHandler{

    /**
     * 接收消息
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String s = (String) e.getMessage();
        System.out.println(s);
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("exceptionCaught()");
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelOpen()");
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelConnected()");
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelDisconnected()");
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed()");
        super.channelClosed(ctx, e);
    }
}

```
