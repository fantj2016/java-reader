Java NIO DatagramChannel是可以发送和接收UDP数据包的通道。 由于UDP是无连接的网络协议，所以不能像从其他通道那样默认读取和写入DatagramChannel。 而是发送和接收数据包。
###Opening a DatagramChannel
这里是你如何打开一个DatagramChannel：
```
DatagramChannel channel = DatagramChannel.open();
channel.socket().bind(new InetSocketAddress(9999));
```
这个例子打开一个DatagramChannel，它可以在UDP端口9999上接收数据包。
###Receiving Data
您通过调用其receive（）方法接收DatagramChannel的数据，如下所示：
```
ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();

channel.receive(buf);
```
receive（）方法会将接收到的数据包的内容复制到给定的Buffer中。 如果收到的数据包中包含的数据多于缓冲区可以包含的数据，则剩下的数据将被静默丢弃。
###Sending Data
您可以通过调用其send（）方法通过DatagramChannel发送数据，如下所示：
```
String newData = "New String to write to file..."
                    + System.currentTimeMillis();
    
ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());
buf.flip();

int bytesSent = channel.send(buf, new InetSocketAddress("jenkov.com", 80));
```
这个例子将字符串发送到UDP端口80上的“jenkov.com”服务器。虽然没有任何监听端口，所以什么都不会发生。 您不会收到发送数据包是否被接收的通知，因为UDP不保证数据的传送。
###连接到一个特定的地址
可以将DatagramChannel“连接”到网络上的特定地址。 由于UDP是无连接的，所以这种连接到地址的方式不会像TCP通道那样创建真正的连接。 而是锁定你的DatagramChannel，所以你只能发送和接收来自一个特定地址的数据包。
Here is an example:
```
channel.connect(new InetSocketAddress("jenkov.com", 80));    
```
连接时，也可以使用read（）和write（）方法，就像使用传统通道一样。 您对发送的数据没有任何保证。 这里有一些例子：
```
int bytesRead = channel.read(buf);    
int bytesWritten = channel.write(buf);
```














