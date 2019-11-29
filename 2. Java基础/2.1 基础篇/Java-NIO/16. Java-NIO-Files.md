Java NIO Files类（java.nio.file.Files）提供了几种方法来处理文件系统中的文件。 这个Java NIO文件教程将涵盖这些方法中最常用的。 Files类包含许多方法，所以如果你需要一个在这里没有描述的方法，那么也检查JavaDoc。 Files类可能还有一个方法。

java.nio.file.Files类与java.nio.file.Path实例一起使用，所以在使用Files类之前，您需要了解Path类。
###Files.exists()
Files.exists（）方法检查给定的Path是否存在于文件系统中。

可以创建文件系统中不存在的Path实例。 例如，如果您打算创建一个新的目录，您将首先创建相应的Path实例，然后创建该目录。

由于Path实例可能指向或不指向文件系统中存在的路径，因此可以使用Files.exists（）方法来确定它们是否执行（以防需要检查）。

这里是一个Java Files.exists（）的例子：
```
Path path = Paths.get("data/logging.properties");

boolean pathExists =
        Files.exists(path,
            new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
```
这个例子首先创建一个Path实例，指向我们要检查的路径是否存在。 其次，该示例使用Path实例调用Files.exists（）方法作为第一个参数。

注意Files.exists（）方法的第二个参数。 此参数是影响Files.exists（）如何确定路径是否存在的选项数组。 在上面的例子中，数组包含LinkOption.NOFOLLOW_LINKS，这意味着Files.exists（）方法不应该跟随文件系统中的符号链接来确定路径是否存在。
###Files.createDirectory()
Files.createDirectory（）方法从Path实例创建一个新目录。 这是一个Java Files.createDirectory（）示例：
```
Path path = Paths.get("data/subdir");

try {
    Path newDir = Files.createDirectory(path);
} catch(FileAlreadyExistsException e){
    // the directory already exists.
} catch (IOException e) {
    //something else went wrong
    e.printStackTrace();
}
```
第一行创建表示要创建的目录的Path实例。 在try-catch块内，调用Files.createDirectory（）方法，路径为参数。 如果创建目录成功，则会返回指向新创建路径的Path实例。

如果该目录已经存在，则会抛出java.nio.file.FileAlreadyExistsException异常。 如果出现其他问题，可能会抛出IOException异常。 例如，如果所需新目录的父目录不存在，则可能会抛出IOException。 父目录是要在其中创建新目录的目录。 因此，它意味着新目录的父目录。
###Files.copy()
Files.copy()方法复制一个文件到另一个从一个路径。这是一个Java NIO Files.copy()例子:
```
Path sourcePath      = Paths.get("data/logging.properties");
Path destinationPath = Paths.get("data/logging-copy.properties");

try {
    Files.copy(sourcePath, destinationPath);
} catch(FileAlreadyExistsException e) {
    //destination file already exists
} catch (IOException e) {
    //something else went wrong
    e.printStackTrace();
}
```
首先，该示例创建一个源和目标路径实例。 然后该示例调用Files.copy（），传递两个Path实例作为参数。 这将导致源路径引用的文件被复制到目标路径引用的文件。

如果目标文件已经存在，则抛出java.nio.file.FileAlreadyExistsException异常。 如果出现其他问题，则抛出IOException异常。 例如，如果复制文件的目录不存在，则抛出IOException。
#####覆盖现有的文件
可以强制Files.copy（）覆盖现有的文件。 这里有一个例子展示如何使用Files.copy（）覆盖现有的文件：
```
Path sourcePath      = Paths.get("data/logging.properties");
Path destinationPath = Paths.get("data/logging-copy.properties");

try {
    Files.copy(sourcePath, destinationPath,
            StandardCopyOption.REPLACE_EXISTING);
} catch(FileAlreadyExistsException e) {
    //destination file already exists
} catch (IOException e) {
    //something else went wrong
    e.printStackTrace();
}
```
注意第三个参数Files.copy()方法。这个参数指示复制()方法将覆盖现有的文件如果目标文件已经存在。
###Files.move()
Java NIO Files类还包含将文件从一个路径移动到另一个路径的功能。 移动文件与重命名文件相同，只是移动文件可以将文件移动到不同的目录并在同一操作中更改其名称。 是的，java.io.File类也可以通过renameTo（）方法来实现，但是现在在java.nio.file.Files类中也有文件移动功能。

这是一个Java Files.move（）示例：
```
Path sourcePath      = Paths.get("data/logging-copy.properties");
Path destinationPath = Paths.get("data/subdir/logging-moved.properties");

try {
    Files.move(sourcePath, destinationPath,
            StandardCopyOption.REPLACE_EXISTING);
} catch (IOException e) {
    //moving file failed.
    e.printStackTrace();
}
```
首先创建源路径和目标路径。 源路径指向要移动的文件，目标路径指向文件应移至的位置。 然后调用Files.move（）方法。 这导致文件被移动。

注意传递给Files.move（）的第三个参数。 该参数告诉Files.move（）方法覆盖目标路径上的任何现有文件。 这个参数实际上是可选的。

如果移动文件失败，Files.move（）方法可能会引发IOException。 例如，如果文件已经存在于目标路径中，并且已经省去了StandardCopyOption.REPLACE_EXISTING选项，或者要移动的文件不存在等。
###Files.delete()
Files.delete（）方法可以删除文件或目录。 这里是一个Java Files.delete（）的例子：
```
Path path = Paths.get("data/subdir/logging-moved.properties");

try {
    Files.delete(path);
} catch (IOException e) {
    //deleting file failed
    e.printStackTrace();
}
```
首先创建指向要删除的文件的路径。 其次调用Files.delete（）方法。 如果Files.delete（）由于某种原因（例如文件或目录不存在）而无法删除文件，则抛出IOException。

###Files.walkFileTree()
Files.walkFileTree（）方法包含递归遍历目录树的功能。 walkFileTree（）方法将Path实例和FileVisitor作为参数。 Path实例指向您想要遍历的目录。 FileVisitor在转换过程中被调用。

在我解释遍历如何工作之前，首先是FileVisitor接口：
```
public interface FileVisitor {

    public FileVisitResult preVisitDirectory(
        Path dir, BasicFileAttributes attrs) throws IOException;

    public FileVisitResult visitFile(
        Path file, BasicFileAttributes attrs) throws IOException;

    public FileVisitResult visitFileFailed(
        Path file, IOException exc) throws IOException;

    public FileVisitResult postVisitDirectory(
        Path dir, IOException exc) throws IOException {

}
```
您必须自己实现FileVisitor接口，并将实现的实例传递给walkFileTree（）方法。 您的FileVisitor实现的每个方法将在目录遍历期间的不同时间被调用。 如果您不需要挂接所有这些方法，则可以扩展SimpleFileVisitor类，该类包含FileVisitor接口中所有方法的默认实现。

这里是一个walkFileTree（）的例子：
```
Files.walkFileTree(path, new FileVisitor<Path>() {
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    System.out.println("pre visit dir:" + dir);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    System.out.println("visit file: " + file);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    System.out.println("visit file failed: " + file);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
    System.out.println("post visit directory: " + dir);
    return FileVisitResult.CONTINUE;
  }
});
```
FileVisitor实现中的每个方法在遍历期间的不同时间被调用：

在访问任何目录之前调用preVisitDirectory（）方法。 postVisitDirectory（）方法在访问目录之后被调用。

visitFile（）方法是在文件遍历期间访问的每个文件被调用的。 它不被称为目录 - 只有文件。 visitFileFailed（）方法在访问文件失败的情况下被调用。 例如，如果您没有正确的权限，或其他问题出错。

四个方法中的每一个都返回一个FileVisitResult枚举实例。 FileVisitResult枚举包含以下四个选项：
* CONTINUE
* TERMINATE
* SKIP_SIBLINGS
* SKIP_SUBTREE
通过返回这些值之一，被调用的方法可以决定文件的走向应该如何继续。

继续意味着文件散步应该照常继续。

TERMINATE表示文件散步现在应该终止。

SKIP_SIBLINGS表示应该继续执行文件，但不访问此文件或目录的任何兄弟。

SKIP_SUBTREE表示文件行走应继续，但不访问此目录中的条目。 如果从preVisitDirectory（）返回，此值只有一个函数。 如果从其他方法返回，它将被解释为CONTINUE。

#####搜索文件
下面是一个walkFileTree（），它扩展了SimpleFileVisitor以查找一个名为README.txt的文件：
```
Path rootPath = Paths.get("data");
String fileToFind = File.separator + "README.txt";

try {
  Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      String fileString = file.toAbsolutePath().toString();
      //System.out.println("pathString = " + fileString);

      if(fileString.endsWith(fileToFind)){
        System.out.println("file found at path: " + file.toAbsolutePath());
        return FileVisitResult.TERMINATE;
      }
      return FileVisitResult.CONTINUE;
    }
  });
} catch(IOException e){
    e.printStackTrace();
}
```
#####递归删除目录
Files.walkFileTree（）也可以用来删除一个包含所有文件和子目录的目录。 Files.delete（）方法只会删除一个目录，如果它是空的。 通过遍历所有目录并删除每个目录中的所有文件（在visitFile（）内），然后删除目录本身（在postVisitDirectory（）内），可以删除包含所有子目录和文件的目录。 这是一个递归目录删除的例子：
```
Path rootPath = Paths.get("data/to-delete");

try {
  Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      System.out.println("delete file: " + file.toString());
      Files.delete(file);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      Files.delete(dir);
      System.out.println("delete dir: " + dir.toString());
      return FileVisitResult.CONTINUE;
    }
  });
} catch(IOException e){
  e.printStackTrace();
}
```
###文件类中的其他方法
java.nio.file.Files类包含许多其他有用的功能，比如创建符号链接，确定文件大小，设置文件权限等功能。查看java.nio.file.Files类的JavaDoc以获取更多信息 这些方法。








































