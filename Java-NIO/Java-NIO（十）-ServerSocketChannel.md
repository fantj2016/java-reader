Java NIO ServerSocketChannel是一个可以侦听传入TCP连接的通道，就像标准Java Networking中的ServerSocket一样。 ServerSocketChannel类位于java.nio.channels包中。
这是一个例子:
```
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

serverSocketChannel.socket().bind(new InetSocketAddress(9999));

while(true){
    SocketChannel socketChannel =
            serverSocketChannel.accept();

    //do something with socketChannel...
}
```
###Opening a ServerSocketChannel
通过调用ServerSocketChannel.open（）方法打开一个ServerSocketChannel。 这是如何看起来如此：
```
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
```
###Closing a ServerSocketChannel
  关闭ServerSocketChannel是通过调用ServerSocketChannel.close（）方法完成的。 这是如何看起来如此：

serverSocketChannel.close（）;
###监听传入的连接
监听传入的连接是通过调用ServerSocketChannel.accept（）方法完成的。 accept（）方法返回时，它将返回一个带有传入连接的SocketChannel。 因此，accept（）方法阻塞，直到传入的连接到达。

由于您通常不希望只听一个连接，所以您可以在while循环中调用accept（）。 这是如何看起来如此：
```
while(true){
    SocketChannel socketChannel =
            serverSocketChannel.accept();

    //do something with socketChannel...
}
```
当然，你会在while循环中使用其他的停止标准。
###非阻塞模式
ServerSocketChannel可以设置为非阻塞模式。 在非阻塞模式下，accept（）方法立即返回，如果没有到达连接，可能会返回null。 因此，您将不得不检查返回的SocketChannel是否为空。 这里是一个例子：
```
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

serverSocketChannel.socket().bind(new InetSocketAddress(9999));
serverSocketChannel.configureBlocking(false);

while(true){
    SocketChannel socketChannel =
            serverSocketChannel.accept();

    if(socketChannel != null){
        //do something with socketChannel...
        }
}
```

