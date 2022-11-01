在Java 7中，AsynchronousFileChannel被添加到Java NIO中。 AsynchronousFileChannel可以从文件中读取数据并将数据写入文件。 本教程将解释如何使用AsynchronousFileChannel。
###创建一个异步文件通道
您可以通过其静态方法open（）创建一个AsynchronousFileChannel。 这里是创建一个AsynchronousFileChannel的例子：
```
Path path = Paths.get("data/test.xml");

AsynchronousFileChannel fileChannel =
    AsynchronousFileChannel.open(path, StandardOpenOption.READ);
```
open（）方法的第一个参数是指向与AsynchronousFileChannel关联的文件的Path实例。

第二个参数是一个或多个打开的选项，告诉AsynchronousFileChannel在底层文件上执行什么操作。 在这个例子中，我们使用了StandardOpenOption.READ，这意味着文件将被打开以供阅读。
###读数据
您可以通过两种方式从AsynchronousFileChannel读取数据。 读取数据的每种方法都调用AsynchronousFileChannel的read（）方法之一。 这两种读取数据的方法将在下面的章节中介绍。
###通过Future来阅读数据
从AsynchronousFileChannel读取数据的第一种方法是调用返回Future的read（）方法。 这是如何调用read（）方法：
```
Future<Integer> operation = fileChannel.read(buffer, 0);
```
read（）方法的这个版本以ByteBuffer作为第一个参数。 从AsynchronousFileChannel读取的数据被读入这个ByteBuffer。 第二个参数是文件中开始读取的字节位置。

read（）方法立即返回，即使读操作没有完成。 您可以通过调用read（）方法返回的Future实例的isDone（）方法来检查读取操作何时完成。

这里是一个更长的例子，展示了如何使用这个版本的read（）方法：
```
AsynchronousFileChannel fileChannel = 
    AsynchronousFileChannel.open(path, StandardOpenOption.READ);

ByteBuffer buffer = ByteBuffer.allocate(1024);
long position = 0;

Future<Integer> operation = fileChannel.read(buffer, position);

while(!operation.isDone());

buffer.flip();
byte[] data = new byte[buffer.limit()];
buffer.get(data);
System.out.println(new String(data));
buffer.clear();
```
这个例子创建一个AsynchronousFileChannel，然后创建一个ByteBuffer作为参数传递给read（）方法，并且位置为0.调用read（）之后，循环直到返回的Future的isDone（）方法返回true。 当然，这不是一个非常有效的使用CPU - 但不知何故，你需要等到读操作完成。

一旦读取操作完成数据读入ByteBuffer，然后到一个字符串并打印到System.out。
###通过CompletionHandler读取数据
从AsynchronousFileChannel读取数据的第二种方法是调用以CompletionHandler作为参数的read（）方法版本。 这里是你如何调用这个read（）方法：
```
fileChannel.read(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("result = " + result);

        attachment.flip();
        byte[] data = new byte[attachment.limit()];
        attachment.get(data);
        System.out.println(new String(data));
        attachment.clear();
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
});
```
一旦读取操作完成，CompletionHandler的completed（）方法将被调用。 作为完成（）方法的参数传递一个Integer告诉有多少字节被读取，并传递给read（）方法的“附件”。 “附件”是read（）方法的第三个参数。 在这种情况下，数据也被读入的是ByteBuffer。 您可以自由选择要附加的对象。

如果读取操作失败，则将调用CompletionHandler的failed（）方法。
###写数据
就像阅读一样，你可以用两种方式将数据写入AsynchronousFileChannel。 每种写入数据的方法都调用AsynchronousFileChannel的write（）方法之一。 这两种写数据的方法将在下面的章节中介绍。
###Writing Data Via a Future
AsynchronousFileChannel还使您能够异步地写入数据。 这是一个完整的Java AsynchronousFileChannel写示例：
```
Path path = Paths.get("data/test-write.txt");
AsynchronousFileChannel fileChannel = 
    AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

ByteBuffer buffer = ByteBuffer.allocate(1024);
long position = 0;

buffer.put("test data".getBytes());
buffer.flip();

Future<Integer> operation = fileChannel.write(buffer, position);
buffer.clear();

while(!operation.isDone());

System.out.println("Write done");
```
首先在写模式下打开AsynchronousFileChannel。 然后创建一个ByteBuffer，并将一些数据写入它。 然后将ByteBuffer中的数据写入文件。 最后，该示例检查返回的Future以查看写入操作何时完成。

请注意，该代码将工作之前，该文件必须已经存在。 如果文件不存在，write（）方法将抛出一个java.nio.file.NoSuchFileException异常。

您可以使用以下代码确保Path指向的文件存在：
```
if(!Files.exists(path)){
    Files.createFile(path);
}
```
###Writing Data Via a CompletionHandler
您也可以使用CompletionHandler将数据写入AsynchronousFileChannel，告诉您写入完成而不是Future。 下面是一个使用CompletionHandler将数据写入AsynchronousFileChannel的示例：
```
Path path = Paths.get("data/test-write.txt");
if(!Files.exists(path)){
    Files.createFile(path);
}
AsynchronousFileChannel fileChannel = 
    AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

ByteBuffer buffer = ByteBuffer.allocate(1024);
long position = 0;

buffer.put("test data".getBytes());
buffer.flip();

fileChannel.write(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("bytes written: " + result);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("Write failed");
        exc.printStackTrace();
    }
});
```
当写入操作完成时，CompletionHandler的completed（）方法将被调用。 如果由于某种原因写入失败，则会调用failed（）方法。

注意ByteBuffer是如何作为附件的 - 传递给CompletionHandler方法的对象。




