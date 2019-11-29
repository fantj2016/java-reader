Java路径接口是Java NIO 2更新的一部分，Java NIO在Java 6和Java 7中接收Java路径接口。Java路径接口已添加到Java 7中的Java NIO。路径接口位于java.nio.file包中，所以Java Path接口的完全限定名是java.nio.file.Path。

Java Path实例表示文件系统中的路径。路径可以指向文件或目录。路径可以是绝对的或相对的。绝对路径包含从文件系统根目录到指向的文件或目录的完整路径。相对路径包含相对于其他路径的文件或目录的路径。相对路径可能听起来有点混乱。别担心我将在后面的Java NIO Path教程中更详细地解释相关路径。

在某些操作系统中，不要将文件系统路径与路径环境变量混淆。 java.nio.file.Path接口与路径环境变量无关。

在许多方面java.nio.file.Path接口类似于java.io.File类，但有一些细微的差别。不过，在许多情况下，您可以使用Path接口替换File类的使用。

###Creating a Path 实例
为了使用java.nio.file.Path实例，您必须创建一个Path实例。 您可以在名为Paths.get（）的Paths类（java.nio.file.Paths）中使用静态方法创建Path实例。 这里是一个Java Paths.get（）的例子：
```
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathExample {

    public static void main(String[] args) {

        Path path = Paths.get("c:\\data\\myfile.txt");

    }
}
```
请注意示例顶部的两个导入语句。 要使用Path接口和Paths类，我们必须先导入它们。

其次，注意Paths.get（“c：\\ data \\ myfile.txt”）方法调用。 这是对创建Path实例的Paths.get（）方法的调用。 换句话说，Paths.get（）方法是Path实例的工厂方法。
###创建一个绝对路径
通过以绝对文件为参数调用Paths.get（）工厂方法来创建绝对路径。 下面是创建一个代表绝对路径的Path实例的例子：
```
Path path = Paths.get("c:\\data\\myfile.txt");
```
绝对路径是c：\ data \ myfile.txt。 双字符在Java字符串中是必须的，因为\是一个转义字符，这意味着下面的字符告诉在字符串中这个位置究竟是什么字符。 通过编写\\告诉Java编译器在字符串中写入一个\字符。

上面的路径是一个Windows文件系统路径。 在Unix系统（Linux，MacOS，FreeBSD等）上面的绝对路径可能是这样的：
```
Path path = Paths.get("/home/jakobjenkov/myfile.txt");
```
绝对路径现在是/home/jakobjenkov/myfile.txt。

如果在Windows计算机上使用这种路径（以/开头的路径），路径将被解释为相对于当前驱动器。 例如，路径
```
/home/jakobjenkov/myfile.txt
```
可以被解释为位于C驱动器上。 那么路径将对应于这个完整的路径：
```
C:/home/jakobjenkov/myfile.txt
```
###创建一个相对路径
相对路径是指从一个路径（基本路径）指向一个目录或文件的路径。 相对路径的完整路径（绝对路径）是通过将基本路径与相对路径组合而得到的。

Java NIO Path类也可以用来处理相对路径。 您可以使用Paths.get（basePath，relativePath）方法创建一个相对路径。 以下是Java中的两个相对路径示例：
```
Path projects = Paths.get("d:\\data", "projects");

Path file     = Paths.get("d:\\data", "projects\\a-project\\myfile.txt");
```
第一个示例创建一个指向路径（目录）d：\ data \ projects的Java Path实例。 第二个示例创建一个指向路径（文件）d：\ data \ projects \ a-project \ myfile.txt的Path实例。

使用相对路径时，可以在路径字符串中使用两个特殊代码。 这些代码是：.  和 ..
###Path.normalize（） 路径规范化
Path接口的normalize（）方法可以规范路径。 规范化意味着它将删除所有的。 和..代码在路径字符串的中间，并且解析路径字符串引用的路径。 这里是一个Java Path.normalize（）的例子：
```
String originalPath =
        "d:\\data\\projects\\a-project\\..\\another-project";

Path path1 = Paths.get(originalPath);
System.out.println("path1 = " + path1);

Path path2 = path1.normalize();
System.out.println("path2 = " + path2);
```
```
path1 = d:\data\projects\a-project\..\another-project
path2 = d:\data\projects\another-project
```
正如你所看到的，规范化的路径不包含a-project \ ..部分，因为这是多余的。 被删除的部分没有增加任何最后的绝对路径。




























