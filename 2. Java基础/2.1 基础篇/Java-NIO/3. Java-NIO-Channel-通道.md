java nio channel 和 流有一些小小的区别：
* 你能在channel进行读写二者，但是流只能进行读者其一。
* channel能异步进行读写。
* channel一般从（借助）buffer进行读写。
如下图所示：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-9d649f3294a34435.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###Channel Implementations
以下是Java NIO中最重要的Channel实现：
* FileChannel
* DatagramChannel
* SocketChannel
* ServerSocketChannel
The FileChannel 从文件读取数据。
The DatagramChannel 可以通过UDP在网络上读写数据。
The SocketChannel 可以通过TCP在网络上读写数据。
The ServerSocketChannel允许您监听传入的TCP连接，就像Web服务器一样。 为每个传入的连接创建一个SocketChannel。
###实战
Here is a basic example that uses a FileChannel to read some data into a Buffer:
一个简单的例子：用FileChannel去读数据从Buffer里
```
    RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
    FileChannel inChannel = aFile.getChannel();

    ByteBuffer buf = ByteBuffer.allocate(48);

    int bytesRead = inChannel.read(buf);
    while (bytesRead != -1) {

      System.out.println("Read " + bytesRead);
      buf.flip();

      while(buf.hasRemaining()){
          System.out.print((char) buf.get());
      }

      buf.clear();
      bytesRead = inChannel.read(buf);
    }
    aFile.close();
```
注意`buf.flip()`的调用，首先读入一个缓冲区，然后将内容弹出，然后读出数据。
