选择器是Java NIO组件，它可以检查一个或多个NIO通道，并确定哪些通道准备好 阅读或写作。 这样一个单一的线程可以管理多个通道，从而可以管理多个网络连接。
###为什么要用Selector
使用单个线程来处理多个通道的优点是您需要较少的线程来处理通道。 实际上，你可以只用一个线程来处理你所有的频道。 线程之间的切换对于操作系统而言是昂贵的，并且每个线程也占用操作系统中的一些资源（存储器）。 因此，你使用的线程越少越好。

但请记住，现代操作系统和CPU在多任务处理方面越来越好，所以多线程的开销随着时间的推移而变小。 事实上，如果一个CPU有多个核心，那么你可能会因为没有多任务而浪费CPU资源。 无论如何，这个设计讨论属于不同的文本。 在这里说一下就足够了，你可以使用一个Selector来处理单个线程的多个通道。

下面是一个使用Selector处理3个Channel的线程的例子：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-35adfaa138ae5bb8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###创建一个Selector
您可以通过调用Selector.open（）方法创建一个Selector，如下所示：
```
Selector selector = Selector.open();
```
###用Selector注册channel
为了将频道与选择器一起使用，您必须使用选择器注册频道。 这是使用SelectableChannel.register（）方法完成的，如下所示：
```
channel.configureBlocking(false);//非阻塞

SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
```
通道必须处于非阻塞模式才能与选择器一起使用。 这意味着你不能使用FileChannel和Selector，因为FileChannel不能切换到非阻塞模式。 套接字通道将正常工作。

注意第二个参数，这是一个按照类型设置的，意味着你设置的监听Channel的事件类型，经过选择器，有四个不通的事件：
1. Connect
2. Accept
3. Read
4. Write
“发生事件”的频道也被认为是“准备就绪”的事件。 因此，成功连接到另一台服务器的通道是“连接Connect就绪”。 接受传入连接的服务器套接字通道是“接受Accept”就绪。 准备好读取数据Read的通道已经准备就绪。 一个准备好写入数据的通道Write已经准备好了。
这四个事件由四个SelectionKey常量表示：
1. SelectionKey.OP_CONNECT
2. SelectionKey.OP_ACCEPT
3. SelectionKey.OP_READ
4. SelectionKey.OP_WRITE
如果你需要设置多个事件，或者将常量放在一起，就像这样：
```
int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;    
```
###SelectionKey
正如你在上一节看到的，当你用一个选择器注册一个Channel时，register（）方法返回一个SelectionKey对象。 这个SelectionKey对象包含一些有趣的属性：
* The interest set
* The ready set
* The Channel
* The Selector
* An attached object (optional)
下面我来介绍下这几个属性
#####Interest Set
The interest set是您在“选择”中感兴趣的事件集合，如“用选择器注册通道”部分所述。 您可以通过SelectionKey读取和写入如下所示的interest set：
```
int interestSet = selectionKey.interestOps();

boolean isInterestedInAccept  = interestSet & SelectionKey.OP_ACCEPT;
boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;    
```
正如你所看到的,您可以用给定的SelectionKey常数和兴趣组发现如果一个特定的事件是为了集合。
#####Ready Set
Ready Set是通道准备好的一组操作。 您将主要在选择后访问Ready Set。 选择在后面的章节中解释。 你可以这样访问readyset：
```
int readySet = selectionKey.readyOps();
```
您可以按照与兴趣集相同的方式测试频道准备好的事件/操作。 但是，您也可以使用这四种方法，而这些方法都是重新构造一个布尔值：
```
selectionKey.isAcceptable();
selectionKey.isConnectable();
selectionKey.isReadable();
selectionKey.isWritable();
```
#####Channel + Selector
从SelectionKey访问通道+选择器是很简洁的。 这是如何完成的：
```
Channel  channel  = selectionKey.channel();

Selector selector = selectionKey.selector();    
```
#####Attaching Objects
您可以将对象附加到SelectionKey，这是识别给定通道channel或将更多信息附加到通道channel的便捷方式。 例如，您可以将您正在使用的缓冲区与通道或包含更多聚合数据的对象连接起来。 这里是你如何附加对象：
```
selectionKey.attach(theObject);
Object attachedObj = selectionKey.attachment();
```
在register（）方法中，您也可以在向Selector注册Channel时附加一个对象。 这是如何看起来如此：
```
SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);
```
#####通过选择器来选择通道
一旦你用一个Selector注册了一个或多个通道，你可以调用其中一个select（）方法。 这些方法返回您感兴趣的事件（连接，接受，读取或写入）的“准备好”的通道。 换句话说，如果您对准备阅读的频道感兴趣，您将收到准备从select（）方法读取的通道。
这里是select()方法：
* int select()
* int select(long timeout)
* int selectNow()
int select()，直到至少有一个通道准备好注册的事件。
int select(long timeout)：和select()一样的，除了它的最大超时毫秒(参数)。
int selectNow()：它返回立即用什么渠道都准备好了。
select（）方法返回的int告诉准备好多少个通道。 也就是说，自上次调用select（）以来已经准备了多少个通道。 如果你调用select（），并返回1，因为一个通道已经准备好了，而且你再次调用select（），并且另外一个通道已经准备就绪，它将再次返回1。 如果您已经准备好的第一个频道没有做任何事情，那么您现在有2个就绪频道，但在每个select（）调用之间只有一个频道已准备就绪。
#####selectedKeys()
一旦你调用了一个select（）方法，并且它的返回值已经表明一个或者多个通道已经准备就绪，你可以通过调用选择器selectedKeys（）方法来通过“selected key set”访问ready channels。 这是如何看起来如此：
```
Set<SelectionKey> selectedKeys = selector.selectedKeys();    
```
当您使用Selector注册频道时，Channel.register（）方法将返回SelectionKey对象。 这个键表示通道注册到该选择器。 这些键可以通过selectedKeySet（）方法访问。 从SelectionKey。
您可以迭代此选定的selected key集以访问就绪通道。 这是如何看起来如此：
```
Set<SelectionKey> selectedKeys = selector.selectedKeys();

Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

while(keyIterator.hasNext()) {
    
    SelectionKey key = keyIterator.next();

    if(key.isAcceptable()) {
        // a connection was accepted by a ServerSocketChannel.

    } else if (key.isConnectable()) {
        // a connection was established with a remote server.

    } else if (key.isReadable()) {
        // a channel is ready for reading

    } else if (key.isWritable()) {
        // a channel is ready for writing
    }

    keyIterator.remove();
}
```
此循环迭代所选键集中的键。 对于每个SelectionKey ，它测试SelectionKey 以确定SelectionKey 引用的通道已准备好。

注意每次迭代结束时的keyIterator.remove（）调用。 选择器不会从选定的SelectionKey 集本身中删除SelectionKey实例。 当您完成频道处理后，您必须执行此操作。 通道下一次变为“就绪”时，选择器将再次将其添加到所选的selected key 。

SelectionKey.channel（）方法返回的频道应该被转换为你需要使用的频道，例如ServerSocketChannel或SocketChannel等。

###wakeUp()
调用被阻塞的select（）方法的线程可以离开select（）方法，即使没有通道尚未准备好。 这是通过让不同的线程调用Selector上的Selector.wakeup（）方法来完成的，第一个线程调用了select（）。 在select（）中等待的线程将立即返回。

如果一个不同的线程调用wakeup（）并且select（）内部当前没有线程被阻塞，那么调用select（）的下一个线程将立即“唤醒”。
###close()
当你完成Selector时，你调用close（）方法。 这将关闭选择器，并使在此选择器中注册的所有SelectionKey实例失效。 渠道本身并没有关闭。
###完整的Selector的例子
这是一个完整的例子，它打开一个选择器，注册一个通道（通道实例被省略），并持续监控选择器是否准备好四个事件（接受，连接，读取，写入）。
```
Selector selector = Selector.open();

channel.configureBlocking(false);

SelectionKey key = channel.register(selector, SelectionKey.OP_READ);


while(true) {

  int readyChannels = selector.select();

  if(readyChannels == 0) continue;


  Set<SelectionKey> selectedKeys = selector.selectedKeys();

  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

  while(keyIterator.hasNext()) {

    SelectionKey key = keyIterator.next();

    if(key.isAcceptable()) {
        // a connection was accepted by a ServerSocketChannel.

    } else if (key.isConnectable()) {
        // a connection was established with a remote server.

    } else if (key.isReadable()) {
        // a channel is ready for reading

    } else if (key.isWritable()) {
        // a channel is ready for writing
    }

    keyIterator.remove();
  }
}
```
