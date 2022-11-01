Java NIO consist of the following core components:
* Channels
* Buffers
* Selectors
Java NIO有比这些更多的类和组件，但在我看来，Channel，Buffer和Selector是API的核心。 其余的组件，如Pipe和FileLock只是实用程序类，要与三个核心组件结合使用。 因此，我将在这个NIO概述中关注这三个组件。
###Channels and Buffers
通常，NIO中的所有IO都以一个通道开始。 channel有点像流。 通道(channel)数据可以被读入一个缓冲区。 数据也可以从缓冲区写入通道。 这是一个例子：
![Channels and Buffers.png](http://upload-images.jianshu.io/upload_images/5786888-9d8eea93793fd45a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
有几个通道和缓冲区类型。 以下是Java NIO中通道(channel)主要实现的列表：
* FileChannel
* DatagramChannel
* SocketChannel
* ServerSocketChannel
正如您所看到的，这些通道涵盖UDP + TCP网络IO和文件IO。

以下是Java NIO中的核心缓冲区实现列表：
* ByteBuffer
* CharBuffer
* DoubleBuffer
* FloatBuffer
* IntBuffer
* LongBuffer
* ShortBuffer
这些缓冲区涵盖了您可以通过IO发送的基本数据类型：byte, short, int, long, float, double and characters.
###Selectors
选择器允许单个线程处理多个通道。 如果您的应用程序有多个连接（通道）打开，但每个连接只有较低的通信量，则这很方便。 例如，在一个聊天服务器。

下面是一个使用Selector处理3个Channel的线程的例子：
![Selectors.png](http://upload-images.jianshu.io/upload_images/5786888-6fb871409fae0c2a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
要使用selector，您需要注册channel。 然后你调用它的select（）方法。 此方法将阻塞，直到有一个事件准备好注册的频道之一。 一旦方法返回，线程就可以处理这些事件。 事件的例子是传入的连接，收到的数据等
