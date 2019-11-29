Java NIO Pipe是两个线程之间的单向数据连接。 一个管道有一个源通道和一个接收通道。 您将数据写入接收器通道。 然后可以从源通道读取这些数据。

这是一个管道原理的例子：![image.png](http://upload-images.jianshu.io/upload_images/5786888-657d14a7924c8cee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###Creating a Pipe管道
通过调用Pipe.open（）方法打开Pipe。 这是如何看起来如此：
```
Pipe pipe = Pipe.open();
```
###Writing to a Pipe
要写入管道，您需要访问接收器通道。 这是如何做到的：
```
Pipe.SinkChannel sinkChannel = pipe.sink();
```
你可以通过调用write（）方法来写入SinkChannel，如下所示：
```
String newData = "New String to write to file..." + System.currentTimeMillis();

ByteBuffer buf = ByteBuffer.allocate(48);
buf.clear();
buf.put(newData.getBytes());

buf.flip();

while(buf.hasRemaining()) {
    sinkChannel.write(buf);
}
```
###Reading from a Pipe
要从管道中读取，您需要访问源通道。 这是如何做到的：
```
Pipe.SourceChannel sourceChannel = pipe.source();
```
要从源通道读取，请调用其read（）方法，如下所示：
```
ByteBuffer buf = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buf);
```
read()方法返回的int告诉有多少字节读取到缓冲区。
