
本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

可以使用Java在运行时加载和重新加载类，虽然它不像人们希望的那样简单。 本文将解释何时以及如何在Java中加载和重新加载类。

### ClassLoader
Java应用程序中的所有类都使用java.lang.ClassLoader的一些子类加载。 因此，动态加载类也必须使用java.lang.ClassLoader子类来完成。

当一个类被加载时，它所引用的所有类也被加载。 这个类加载模式是递归的，直到所有需要的类都被加载。 这可能不是应用程序中的所有类。 未引用的类只有在引用时才加载。

### 装载等级
Java中的类加载器被组织成一个层次结构。 当你创建一个新的标准的Java ClassLoader时，你必须提供一个父类的ClassLoader。 如果一个ClassLoader被要求加载一个类，它会要求它的父类加载器加载它。 如果父类加载器找不到该类，那么子类加载器将尝试自己加载它。

### 类加载
给定类加载器在加载类时使用的步骤如下：

1. 检查类是否已经加载。
2. 如果未加载，则请求父类加载器加载该类。
3. 如果父类加载器无法加载类，请尝试将其加载到此类加载器中。
当你实现一个能够重新加载类的类加载器时，你将需要偏离这个序列。 父类加载器不应请求加载的类加载。详细情况请看[Java的类加载机制（ClassLoader）](https://www.jianshu.com/p/5dede0e41ec3)

### 动态类加载
动态加载类很容易。 所有你需要做的就是获得一个ClassLoader并且调用它的loadClass（）方法。
```
public class MainClass {

  public static void main(String[] args){

    ClassLoader classLoader = MainClass.class.getClassLoader();

    try {
        Class aClass = classLoader.loadClass("com.jenkov.MyClass");
        System.out.println("aClass.getName() = " + aClass.getName());
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

}
```
### 动态类重载
动态类重新加载有点更具挑战性。 Java的内置Class加载器在加载之前总是检查一个类是否已经被加载。 因此，使用Java的内置类加载器不能重新加载类。 重新加载一个类，你将不得不实现你自己的ClassLoader子类。

即使使用ClassLoader的自定义子类，也是一个挑战。 每个加载的类都需要链接。 这是使用ClassLoader.resolve（）方法完成的。 这个方法是final的，因此不能在你的ClassLoader子类中重写。 resolve（）方法将不允许任何给定的ClassLoader实例连接两次相同的类。 因此，每次你想重新加载一个类，你都必须使用ClassLoader子类的一个新实例。 这不是不可能的，但是在设计类重新加载时需要知道。

### 设计类重新加载
如前所述，您不能使用已经加载该类的ClassLoader重载一个类。 因此，您将不得不使用不同的ClassLoader实例重新加载类。 但是这带来了很多新的挑战。

加载到Java应用程序中的每个类都由其完全限定的名称（包名称+类名称）和加载它的ClassLoader实例标识。 这意味着，类加载器A加载的类MyObject与加载类加载器B的MyObject类不是同一个类。请看下面的代码：
```
MyObject object = (MyObject)myClassReloadingFactory.newInstance("com.jenkov.MyObject");
```
注意代码中MyObject类是如何作为对象变量的类型引用的。这会导致MyObject类被加载了该代码所在的类的同一个类加载器加载。

如果myClassReloadingFactory对象工厂使用与上面的代码所在的类不同的类加载器重新加载MyObject类，则不能将重新加载的MyObject类的实例转换为对象变量的MyObject类型。由于两个MyObject类用不同的类加载器加载，因此即使它们具有相同的完全限定类名，它们也被视为不同的类。尝试将一个类的对象转换为另一个类的引用将导致ClassCastException。

有可能解决此限制，但您将不得不以两种方式之一更改您的代码：

1. 使用一个接口作为变量类型，并重新加载实现类。
2. 使用父类作为变量类型，并重新加载一个子类。
这是代码示例：
```
MyObjectInterface object =(MyObjectInterface)myClassReloadingFactory.newInstance("com.jenkov.MyObject");
```
```
MyObjectSuperclass object = (MyObjectSuperclass)myClassReloadingFactory.newInstance("com.jenkov.MyObject");
```
如果在重载实现类或子类时变量的类型（接口或超类）没有重载，则这两种方法都可以工作。

为了使这个工作，你当然需要实现你的类加载器，让父类加载接口或父类。 当你的类加载器被要求加载MyObject类时，它也会被要求加载MyObjectInterface类或者MyObjectSuperclass类，因为这些类是从MyObject类中引用的。 您的类加载器必须将这些类的加载委托给加载包含接口或超类型变量的类的相同类加载器。
### 类加载器加载/重载的例子
上面的文字包含了很多的话题。 我们来看一个简单的例子。 下面是一个简单的ClassLoader子类的例子。 注意它是如何将类加载委派给它的父类，除了它可以重新加载的类之外。 如果将此类的加载委托给父类加载器，则以后无法重新加载。 请记住，一个类只能由相同的ClassLoader实例加载一次。

如前所述，这只是一个示例，用于向您展示ClassLoader行为的基础知识。你自己的类加载器可能不应该局限于一个类，而是你需要重新加载的类的集合。 另外，你不应该对类路径进行硬编码。
```
public class MyClassLoader extends ClassLoader{

    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        if(!"reflection.MyObject".equals(name))
                return super.loadClass(name);

        try {
            String url = "file:C:/data/projects/tutorials/web/WEB-INF/" +
                            "classes/reflection/MyObject.class";
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1){
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass("reflection.MyObject",
                    classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
```
###### 下面是一个示例使用MyClassLoader。
```
public static void main(String[] args) throws
    ClassNotFoundException,
    IllegalAccessException,
    InstantiationException {

    ClassLoader parentClassLoader = MyClassLoader.class.getClassLoader();
    MyClassLoader classLoader = new MyClassLoader(parentClassLoader);
    Class myObjectClass = classLoader.loadClass("reflection.MyObject");

    AnInterface2       object1 =
            (AnInterface2) myObjectClass.newInstance();

    MyObjectSuperClass object2 =
            (MyObjectSuperClass) myObjectClass.newInstance();

    //创建新的类加载以至于能被重载
    classLoader = new MyClassLoader(parentClassLoader);
    myObjectClass = classLoader.loadClass("reflection.MyObject");

    object1 = (AnInterface2)       myObjectClass.newInstance();
    object2 = (MyObjectSuperClass) myObjectClass.newInstance();

}
```
这是使用类加载器加载的reflection.MyObject类。 注意它是如何扩展一个超类并实现一个接口的。 这仅仅是为了这个例子。 在你自己的代码中，你只需要其中的一个 - 扩展或实现。
```
public class MyObject extends MyObjectSuperClass implements AnInterface2{
    //... 覆盖父类方法
    // 或者实现接口方法
}
```









