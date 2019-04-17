在Java NIO中，如果其中一个通道是FileChannel，则可以将数据直接从一个通道传输到另一个通道。 FileChannel类有一个transferTo（）和一个transferFrom（）方法，可以为你做到这一点。
###transferFrom()
FileChannel.transferFrom（）方法将来自源通道的数据传输到FileChannel中。 这是一个简单的例子：
```
RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
FileChannel      fromChannel = fromFile.getChannel();

RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
FileChannel      toChannel = toFile.getChannel();

long position = 0;
long count    = fromChannel.size();

toChannel.transferFrom(fromChannel, position, count);
```
参数的位置和计数，告诉目的文件开始写入的位置（位置），以及最多传输多少字节（计数）。 如果源信道少于计数字节，则传输的信道少。

此外，一些SocketChannel实现可能只传递SocketChannel在其内部缓冲区中准备好的数据 - 即使SocketChannel稍后可能有更多的数据可用。 因此，它可能不会将从SocketChannel请求的所有数据（计数）传送到FileChannel。
###transferTo()
transferTo（）方法从FileChannel转移到其他通道。 这是一个简单的例子：
```
RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
FileChannel      fromChannel = fromFile.getChannel();

RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
FileChannel      toChannel = toFile.getChannel();

long position = 0;
long count    = fromChannel.size();

fromChannel.transferTo(position, count, toChannel);
```
注意这个例子与前一个例子类似。 唯一真正的区别是调用该方法的FileChannel对象。 其余的是一样的。

SocketChannel的问题也出现在transferTo（）方法中。 SocketChannel实现只能从FileChannel传输字节，直到发送缓冲区已满，然后停止。
