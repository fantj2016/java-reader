Java NIO带有内置的分散/聚集支持。 分散/聚集是用于阅读和写入频道的概念。

从通道读取散射是一种读取操作，将数据读取到多个缓冲区中。 因此，通道将来自通道的数据“分散”到多个缓冲区中。

写入通道的写入操作是将来自多个缓冲区的数据写入单个通道的写入操作。 因此，通道将来自多个缓冲区的数据“收集”到一个通道中。

如果您需要单独处理传输数据的各个部分，Scatter / Gather可能非常有用。 例如，如果一个消息由一个头部和一个主体组成，你可以将头部和主体保存在不同的缓冲区中。 这样做可能会使您更容易分开处理标题和正文。
###Scattering Reads 散射读取
“散读”将数据从单个通道读入多个缓冲区。 这是这个原则的例证：
这是一个散点图的例子：![image.png](http://upload-images.jianshu.io/upload_images/5786888-20df49a325aae2e3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这里是一个代码示例，显示如何执行散射读取：
```
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);

ByteBuffer[] bufferArray = { header, body };

channel.read(bufferArray);
```
注意如何将缓冲区首先插入到数组中，然后将数组作为参数传递给channel.read（）方法。 read（）方法会按照缓冲区出现在数组中的顺序写入通道中的数据。 一旦缓冲区满了，通道继续填充下一个缓冲区。在转移到下一个之前，散列读取填充一个缓冲区的事实意味着它不适合动态调整大小的消息部分。 换句话说，如果你有一个头部和一个主体，并且头部是固定的大小（例如128字节），那么散射阅读工作正常。
###Gathering Writes 
“收集写入”将来自多个缓冲区的数据写入单个通道。 这是这个原则的例证：![](http://upload-images.jianshu.io/upload_images/5786888-016aa99a29ed58f4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这里是一个代码示例，显示如何执行一次聚集写入：
```
ByteBuffer header = ByteBuffer.allocate(128);
ByteBuffer body   = ByteBuffer.allocate(1024);

//write data into buffers

ByteBuffer[] bufferArray = { header, body };

channel.write(bufferArray);
```
将缓冲区数组传递到write（）方法，该方法按照在数组中遇到的顺序写入缓冲区的内容。 只有缓冲区的位置和限制之间的数据被写入。 因此，如果缓冲区的容量为128字节，但只包含58个字节，则只有58个字节从该缓冲区写入该信道。 因此，聚集写入与动态大小的消息部分一起工作良好，与散列读取相反。
