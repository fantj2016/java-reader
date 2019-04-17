Java NIO SocketChannel是连接到TCP网络套接字的通道。 Java NIO相当于Java Networking的套接字。 有两种方法可以创建一个SocketChannel：
1. 你打开一个SocketChannel并连接到互联网上的某个服务器。
2. 当传入的连接到达ServerSocketChannel时，可以创建一个SocketChannel。
###Opening a SocketChannel
这是你如何打开SocketChannel:
```
SocketChannel socketChannel = SocketChannel.open();
socketChannel.connect(new InetSocketAddress("http://xxx.com", 80));
```
###Closing a SocketChannel
通过调用SocketChannel.close（）方法来关闭一个SocketChannel。 这是如何做到的:
```
socketChannel.close();  
```
###从SocketChannel读取
要从SocketChannel读取数据，请调用read（）方法。 这里是一个例子：
```
ByteBuffer buf = ByteBuffer.allocate(48);

int bytesRead = socketChannel.read(buf);
```
首先分配一个缓冲区。 从SocketChannel读取的数据被读入缓冲区。

其次调用SocketChannel.read（）方法。 这个方法从SocketChannel读取数据到Buffer中。 read（）方法返回的int告诉在缓冲区中有多少个字节。 如果返回-1，则到达流结束（连接关闭）。
###写东西给SocketChannel
写数据使用SocketChannel.write SocketChannel完成()方法,以缓冲为参数。这是一个例子:
```
String newData = "New String to write to file..." + System.currentTimeMillis();

ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());

buf.flip();

while(buf.hasRemaining()) {
    channel.write(buf);
}
```
注意SocketChannel.write（）方法在while循环中被调用。 write（）方法写入SocketChannel的字节数不能保证。 因此，我们重复write（）调用，直到Buffer没有其他字节要写入。
###非阻塞模式
你可以设置SocketChannel进入非阻塞模式。当你这样做时,你可以调用connect(),读()和写()在异步模式。
#####connect()
如果SocketChannel处于非阻塞模式，并调用connect（）方法，则可能会在连接建立之前返回该方法。 要确定连接是否建立，可以调用finishConnect（）方法，如下所示：
```
socketChannel.configureBlocking(false);
socketChannel.connect(new InetSocketAddress("http://xxx.com", 80));

while(! socketChannel.finishConnect() ){
    //wait, or do something else...    
}
```
#####write()
在非阻塞模式下，write（）方法可能会返回而不写入任何内容。 因此，您需要在循环中调用write（）方法。 但是，由于这已经在前面的写例子中完成了，所以在这里不需要做任何不同的事情。
#####read()
在非阻塞模式下，read（）方法可能返回而根本没有读取任何数据。 所以你需要注意返回的int，它告诉读了多少字节。
###非阻塞模式选择器
使用Selector的SocketChannel的非阻塞模式效果更好。 通过在Selector中注册一个或多个SocketChannel，你可以向Selector询问是否准备好读取，写入的通道。如何在SocketChannel中使用Selector将在本教程后面的文章中详细介绍。
