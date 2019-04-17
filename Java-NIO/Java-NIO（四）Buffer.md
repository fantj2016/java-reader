根据Channel的使用我们可以知道，与NIO channel交互时使用Java NIO buffer。 如您所知，数据从channel读入缓冲区，并从缓冲区写入通道。
buffer本质上是一块内存，您可以在其中写入数据，然后您可以再次读取数据。 该内存块被封装在一个NIO Buffer对象中，该对象提供了一组方法，可以更轻松地使用内存块。
###Basic Buffer Usage
使用buffer读取和写入数据通常遵循以下四个步骤：
1. Write data into the Buffer
2. Call buffer.flip()
3. Read data out of the Buffer
4. Call buffer.clear() or buffer.compact()
当您将数据写入缓冲区时，缓冲区会记录您写入的数据量。 一旦您需要读取数据，您需要使用flip（）方法调用将缓冲区从写入模式切换到读取模式。 在读取模式下，缓冲区可让您读取写入缓冲区的所有数据。

一旦你读完所有的数据，你需要清除缓冲区，使其准备好再次写入。 你可以通过两种方法来实现：通过调用clear（）或者调用compact（）。 clear（）方法清除整个缓冲区。 compact（）方法只会清除已经读取的数据。 任何未读数据将被移动到缓冲区的开始处，并且数据将在未读数据之后被写入缓冲区。

下面是一个简单的Buffer使用示例:
```
RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
FileChannel inChannel = aFile.getChannel();

//create buffer with capacity of 48 bytes
ByteBuffer buf = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buf); //从buffer里读
while (bytesRead != -1) {

  buf.flip();  //从buffer弹出数据

  while(buf.hasRemaining()){
      System.out.print((char) buf.get()); // 一次获取一个字节
  }

  buf.clear(); //make buffer ready for writing
  bytesRead = inChannel.read(buf);
}
aFile.close();
```
###Buffer的三个属性
* capacity
* position
* limit
limit和position的含义取决于缓冲区是处于读取还是写入模式。 capacity（容量）总是意味着相同的，和缓冲区模式无关。

以下是写入和读取模式下容量capacity，位置position和限制limit的说明。 解释如下图所示。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-58c7b94d35ba6c1a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####Capacity
作为一个内存块，一个Buffer有一个固定的大小Capacity，也称为“容量”。 您只能将容量字节，bytes, longs, chars等写入缓冲区。 一旦缓冲区已满，您需要将其清空（读取数据或清除数据），然后才能向其中写入更多数据。
#####Position
当您将数据写入缓冲区时，您可以在某个位置执行此操作。 最初的位置是0.当一个字节，长等被写入缓冲区时，位置被提前指向缓冲区中的下一个单元以插入数据。 位置可以最大限度地变成容量数Capacity - 1。
当你从缓冲区读取数据时，你也可以从给定的位置进行读取。 当您将缓冲区从写入模式flip到读取模式时，位置将重置为0.在从缓冲区中读取数据时，您可以从位置读取数据，位置将前进到下一个读取位置。
#####Limit
在写模式下，缓冲区的限制是可以写入缓冲区的数据量的限制。 在写入模式下，限制等于缓冲区的容量。
当将缓冲区flip为读取模式时，限制意味着您可以从数据中读取多少数据的限制。 因此，当将Buffer转换为读取模式时，将写入模式的写入位置设置为限制。 换句话说，您可以读取与写入数量相同的字节（限制设置为写入的字节数，由位置标记）。
###Buffer 类型
* ByteBuffer
* MappedByteBuffer
* CharBuffer
* DoubleBuffer
* FloatBuffer
* IntBuffer
* LongBuffer
* ShortBuffer
如您所见，这些Buffer类型表示不同的数据类型。 换句话说，它们让你用char，short，int，long，float或double来处理缓冲区中的字节。
###分配一个缓冲区
要获得一个Buffer对象，你必须先分配它。 每个Buffer类都有一个allocate（）方法。 以下是一个显示ByteBuffer分配的示例，容量为48个字节：
```
ByteBuffer buf = ByteBuffer.allocate(48);
```
这是一个分配CharBuffer的例子，空间为1024个字符
```
CharBuffer buf = CharBuffer.allocate(1024);
```
###将数据写入Buffer
您可以通过两种方式将数据写入缓冲区：
1. 将数据从通道写入缓冲区
2. 通过缓冲区的put（）方法自己将数据写入缓冲区。
下面是一个显示通道如何将数据写入缓冲区的例子：
```
int bytesRead = inChannel.read(buf); 
```
这是一个通过put（）方法将数据写入Buffer的例子：
```
buf.put(127);    
```
put（）方法还有许多其他版本，允许您以多种不同方式将数据写入缓冲区。 例如，写在特定的位置，或写入缓冲区的字节数组。 有关更多详细信息，请参阅具体缓冲区实现的JavaDoc。
###flip()方法
flip（）方法将Buffer从写入模式切换到读取模式。 调用flip（）会将位置设置回0，并将限制设置在刚刚位置的位置。
换句话说，位置现在标记为读取位置，并且限制标记有多少个字节，字符等被写入到缓冲区中.
###从缓冲区读取数据
有两种方法可以从缓冲区读取数据：
1. 从缓冲区读取数据到一个通道。
2. 使用其中一个get（）方法自己从缓冲区中读取数据。
下面是一个如何将数据从缓冲区读入通道的例子：
```
int bytesWritten = inChannel.write(buf);
```
这是一个使用get（）方法从Buffer读取数据的例子：
```
byte aByte = buf.get();    
```
get（）方法还有许多其他版本，允许您以多种方式从Buffer中读取数据。 例如，读取特定的位置，或从缓冲区读取一个字节数组。 有关更多详细信息，请参阅具体缓冲区实现的JavaDoc。
###rewind()方法
Buffer.rewind（）将位置设置回0，所以你可以重新读取缓冲区中的所有数据。 该限制保持不变，因此仍然标记可以从缓冲区读取多少个元素（字节，字符等）。
###clear() and compact()方法
注意这么几项：
1. 一旦完成从缓冲区读取数据，您必须使缓冲区准备好再次写入。 你可以通过调用clear（）或者通过调用compact（）来完成。
2. 如果你调用clear（），那么位置被设置回0，并且限制到容量。 换句话说，缓冲区被清除。 缓冲区中的数据不会被清除。 只有标记告诉你可以在Buffer的哪里写数据。
3. 但是如果在调用clear（）时Buffer中有任何未读取的数据，那么数据将被“忘记”，这意味着您不再有任何标记来指示已读取的数据以及尚未读取的数据。
4. 如果缓冲区中还有未读数据，并且您想稍后再读取，但是您需要先写一些数据，则请调用compact（）而不是clear（）。
5. compact（）将所有未读数据复制到Buffer的开始部分。 然后它将位置设置在最后一个未读元素之后。 限制属性仍然设置为容量，就像clear（）一样。 现在缓冲区已准备好写入，但是不会覆盖未读取的数据。
###mark() and reset()方法
您可以通过调用Buffer.mark（）方法在Buffer中标记给定的位置。 然后，您可以通过调用Buffer.reset（）方法将位置重置回标记的位置。 这里是一个例子：
```
buffer.mark();

//call buffer.get() a couple of times, e.g. during parsing.

buffer.reset();  //set position back to mark.  
```
###equals() and compareTo()
可以使用equals（）和compareTo（）比较两个缓冲区。
#####equals()
1. 它们是相同的类型（byte，char，int等）
2. 他们有相同数量的剩余字节，字符等在缓冲区中。
3. 所有剩余的字节，字符等是相等的。
正如你所看到的，等于只比较缓冲区的一部分，而不是每个元素。 事实上，它只是比较缓冲区中的其余元素。
#####compareTo()
compareTo（）方法比较两个缓冲区的剩余元素（字节，字符等），用于例如 排序。 如果出现以下情况，则认为缓冲区比另一个缓冲区“小":
1. 第一个元素等于另一个缓冲区中的对应元素，小于另一个缓冲区中的元素。
2. 所有的元素都是相等的，但第一个缓冲区在第二个缓冲区之前耗尽了元素（元素的数量较少）。
