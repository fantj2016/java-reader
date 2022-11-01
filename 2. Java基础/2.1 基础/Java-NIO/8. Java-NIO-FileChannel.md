Java NIO FileChannel是连接到文件的通道。 使用文件通道，您可以从文件读取数据，并将数据写入文件。 Java NIO FileChannel类是NIO用标准Java IO API读取文件的替代方法。

FileChannel**不能设置为非阻塞模式**。 它始终以阻塞模式运行。
###Opening a FileChannel
在您使用FileChannel之前，您必须打开它。 您无法直接打开FileChannel。 您需要通过InputStream，OutputStream或RandomAccessFile获取FileChannel。 以下是通过RandomAccessFile打开FileChannel的方法：
```
RandomAccessFile aFile     = new RandomAccessFile("data/nio-data.txt", "rw");
FileChannel      inChannel = aFile.getChannel();
```
###从FileChannel读取数据
  要从FileChannel读取数据，请调用read（）方法之一。 这里是一个例子：
```
ByteBuffer buf = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buf);
```
首先分配一个缓冲区。 从FileChannel读取的数据被读入缓冲区。

其次调用FileChannel.read（）方法。 这个方法从FileChannel读取数据到Buffer中。 read（）方法返回的int告诉在缓冲区中有多少个字节。 如果返回-1，则达到文件结束。
###FileChannel写入数据
完成数据写入FileChannel使用FileChannel.write()方法,以缓冲为参数。这是一个例子:
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
注意如何在while循环中调用FileChannel.write（）方法。 write（）方法写入FileChannel的字节数不能保证。 因此，我们重复write（）调用，直到Buffer没有其他字节要写入。
###关闭FileChannel
当你使用一个FileChannel必须关闭它。这是如何完成:
```
channel.close();    
```
###FileChannel Position
在读取或写入FileChannel时，您可以在特定的位置进行此操作。 您可以通过调用position（）方法来获取FileChannel对象的当前位置。

你也可以通过调用position（long pos）方法来设置FileChannel的位置。

这里有两个例子：
```
long pos channel.position();

channel.position(pos +123);
```
如果在文件结束后设置位置，并尝试从通道读取，则会得到-1 - 文件结束标记。

如果在文件结束后设置位置并写入通道，文件将被展开以适应位置和写入的数据。 这可能会导致“文件洞”，磁盘上的物理文件在写入的数据中存在空隙。
###FileChannel Size
FileChannel对象的size（）方法返回通道所连接文件的文件大小。 这是一个简单的例子：
```
long fileSize = channel.size();  
```
###FileChannel Truncate （裁断）
您可以通过调用FileChannel.truncate（）方法来截断文件。 当你截断一个文件时，你可以按照给定的长度截断它。 这里是一个例子：
```
channel.truncate(1024); //这个示例文件截断在1024字节长度。
```
###FileChannel Force (强迫)
FileChannel.force（）方法将所有未写入的数据从通道刷新到磁盘。 由于性能原因，操作系统可能会将数据缓存在内存中，因此，在调用force（）方法之前，不能保证写入通道的数据实际写入磁盘。

force（）方法以布尔值为参数，告诉文件元数据（权限等）是否也应该被刷新。

下面是一个刷新数据和元数据的例子：
```
channel.force(true);
```

