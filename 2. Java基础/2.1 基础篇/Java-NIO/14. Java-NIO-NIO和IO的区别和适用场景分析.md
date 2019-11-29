在研究Java NIO和IO API时，很快就会想到一个问题：

什么时候应该使用IO，什么时候应该使用NIO？

在本文中，我将尝试阐明Java NIO和IO之间的区别，它们的用例以及它们如何影响代码的设计。
###Java NIO和IO之间的主要区别
下表总结了Java NIO和IO之间的主要区别。 我将在表格后面的部分详细介绍每个区别。

|IO           |   NIO|
|:----:|:----:|
|面向流   | 利用缓冲区|
|阻塞IO    |  非阻塞IO |
   |              |Selectors|
###流导向vs缓冲导向
Java NIO和IO之间的第一大区别是IO是面向流的，其中NIO是面向缓冲区的。 那么，这是什么意思？

面向流的Java IO意味着您一次从流中读取一个或多个字节。 你所做的读字节取决于你。 他们没有被缓存在任何地方。 而且，您不能前后移动数据流。 如果您需要前后移动从流中读取的数据，则需要先将其缓存在缓冲区中。

Java NIO的面向缓冲区的方法略有不同。 数据被读入一个缓冲区，稍后进行处理。 您可以根据需要前后移动缓冲区。 这给你在处理过程中更多的灵活性。 但是，您还需要检查缓冲区是否包含您需要的所有数据，以便对其进行全面处理。 而且，您需要确保在将更多数据读入缓冲区时，不会覆盖尚未处理的缓冲区中的数据。
###阻塞vs非阻塞IO
Java IO的各种流都被阻塞。 这意味着，当一个线程调用一个read（）或write（）时，该线程被阻塞，直到有一些数据要读取，或者数据被完全写入。 线程在此期间不能做其他事情。

Java NIO的非阻塞模式使得线程可以请求从一个通道读取数据，并且只获取当前可用的数据，或者根本没有任何数据可用。 线程可以继续使用别的东西，而不是在数据可用于读取之前保持阻塞状态。

无阻塞写入也是如此。 一个线程可以请求将一些数据写入一个通道，但不要等待它被完全写入。 线程然后可以继续，同时做其他事情。

线程在没有被IO阻塞的情况下花费空闲时间，通常是在其他通道上同时执行IO。 也就是说，一个线程现在可以管理多个输入输出通道。

###Selectors
Java NIO的选择器允许单线程监视多个输入通道。 您可以使用选择器注册多个通道，然后使用单个线程“选择”可用于处理的输入通道，或者选择准备写入的通道。 这个选择器机制使单个线程可以轻松管理多个通道。
###NIO和IO如何影响应用程序设计
无论您选择NIO还是IO作为您的IO工具包，都可能会影响您应用程序设计的以下方面：

1. API调用NIO或IO类。
2. 数据的处理。
3. 用于处理数据的线程数。
###API调用
当然，使用NIO时的API调用看起来不同于使用IO时的调用。 这并不奇怪。 而不只是从例如数据字节读取字节。 一个InputStream，数据必须首先被读入一个缓冲区，然后从那里被处理。
###数据处理
当使用纯粹的NIO设计时，数据的处理也会受到IO设计的影响。

在IO设计中，您从InputStream或Reader读取字节的数据字节。 想象一下，你正在处理一系列基于行的文本数据。 例如：
```
Name: Anna
Age: 25
Email: anna@mailserver.com
Phone: 1234567890
```
这条文本行可以像这样处理：
```
InputStream input = ... ; // get the InputStream from the client socket

BufferedReader reader = new BufferedReader(new InputStreamReader(input));

String nameLine   = reader.readLine();
String ageLine    = reader.readLine();
String emailLine  = reader.readLine();
String phoneLine  = reader.readLine();
```
注意处理状态是由程序执行的程度决定的。 换句话说，一旦第一个reader.readLine（）方法返回，您肯定知道已经读取了一整行文本。 readLine（）阻塞直到读完整行，这就是为什么。 你也知道这行包含名字。 同样，当第二个readLine（）调用返回时，您知道该行包含年龄等

正如你所看到的，只有当有新的数据要读取时，程序才会进行，并且每一步你都知道数据是什么。 一旦正在执行的线程通过读取代码中的某段数据，线程就不会在数据中倒退（大部分不是）。 这个原理在这个图中也有说明：![image.png](http://upload-images.jianshu.io/upload_images/5786888-cb030e7e28f3d662.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

NIO的实现看起来不同。 这是一个简单的例子：
```
ByteBuffer buffer = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buffer);
```
注意从通道读取字节到ByteBuffer的第二行。 当该方法调用返回时，您不知道所需的所有数据是否在缓冲区内。 所有你知道的是，缓冲区包含一些字节。 这使得处理有点困难。

想象一下，如果在第一次读取（缓冲区）调用之后，所有读入缓冲区的内容都是半行。 例如，"Name: An"。 你能处理这些数据吗？ 不是真的。 你需要等待，直到完整的一行数据已经进入缓冲区，才有意义处理任何数据。

那么如何知道缓冲区中是否包含足够的数据才能处理？ 那么，你没有。 唯一的办法就是查看缓冲区中的数据。 结果是，您可能需要多次检查缓冲区中的数据，才能知道所有数据是否在内部。 这既是低效的，而且在程序设计方面可能变得混乱。 例如：
```
ByteBuffer buffer = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buffer);

while(! bufferFull(bytesRead) ) {
    bytesRead = inChannel.read(buffer);
}
```
bufferFull（）方法必须跟踪有多少数据被读入缓冲区，并根据缓冲区是否已满而返回true或false。 换句话说，如果缓冲区准备好处理，则认为已满。

bufferFull（）方法扫描缓冲区，但必须保持缓冲区处于与调用bufferFull（）方法之前相同的状态。 否则，读入缓冲区的下一个数据可能无法在正确的位置读入。 这不是不可能的，但这是另一个需要注意的问题。

如果缓冲区已满，可以进行处理。 如果它没有满，那么你可能能够部分处理任何数据，如果这在你的特定情况下是有意义的。 在许多情况下，它不是。

图中显示了is-data-in-buffer-ready循环：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-452ed5f41ee0428d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###概要
NIO允许您仅使用一个（或几个）线程来管理多个通道（网络连接或文件），但代价是解析数据可能比从阻塞流读取数据时要复杂一些。

如果你需要管理数以千计的同时打开的连接，每一个只发送一点点的数据，例如聊天服务器，在NIO中实现服务器可能是一个优势。 同样，如果你需要保持与其他计算机的大量开放连接，例如 在P2P网络中，使用单个线程来管理所有出站连接可能是一个优势。 这一个线程，多个连接设计如下图所示：![image.png](http://upload-images.jianshu.io/upload_images/5786888-8b66bc83f7b918a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果您的连接带宽非常高，一次发送大量数据，那么传统的IO服务器实现可能是最合适的。 该图说明了一个经典的IO服务器设计：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-461e55be80d6e5b8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)




















