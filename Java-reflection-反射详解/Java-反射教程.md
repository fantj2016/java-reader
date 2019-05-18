本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

>Java反射使得可以在运行时检查类，接口，字段和方法，而无需在编译时知道类名，方法等。也可以实例化新对象，调用方法并使用反射来获取/设置字段值。

>Java反射是相当强大的，可以是非常有用的。例如，Java Reflection可用于将JSON文件中的属性映射到Java对象中的getter / setter方法，如Jackson，GSON，Boon等。或者，Reflection可用于将JDBC ResultSet的列名映射到Java对象中的getter / setter方法。

>本教程将深入Java反射。它将解释Java反射的基础知识，包括如何处理数组，注释，泛型和动态代理，以及动态类加载和重载。

>它还将向您展示如何执行更具体的Java反射任务，例如读取类的所有getter方法，或者访问类的私有字段和方法。

>这个Java反射教程也将清除一些关于什么泛型信息在运行时可用的混淆。有些人声称所有的仿制药信息在运行时都会丢失。这不是真的。
本教程介绍Java 8中的Java反射版本。

### Java反射的例子
这是一个Java反射示例向您展示使用反射是什么样子:
```
Method[] methods = MyObject.class.getMethods();

for(Method method : methods){
    System.out.println("method = " + method.getName());
}
```
本示例从名为MyObject的类中获取Class对象。 使用类对象的例子获取该类中的方法的列表，迭代方法并打印出他们的名字。

详细对类的使用，请看后续本系列教程。
### Java类对象
当使用Java反射时，起始点通常是一些Class对象，它们表示您想要通过反射来检查的某个Java类。 例如，要获得一个名为MyObject的类的Class对象，你可以写：
```
Class myObjectClass = MyObject.class;
```
现在您有一个MyObject类的Class对象的引用。

详细对方法的使用，请看后续本系列教程。[Java反射 - 类对象](https://www.jianshu.com/p/647f2debbf2c)
### Methods and Fields 方法和字段
一旦你引用了表示某个类的Class对象，就可以访问该类的方法和字段。 下面是一个访问Java类的方法和字段的例子：
```
Class myObjectClass = MyObject.class;

Method[] methods = myObjectClass.getMethods();

Field[] fields   = myObjectClass.getFields();
```
一旦你有一个类的方法和领域的引用，你可以开始检查他们。 你可以获得方法和字段名称，他们采取什么参数等。你甚至可以通过这些方法和字段反射对象调用方法和获取/设置字段值。

这里只是给大家概括反射的大概功能。详情请看后文。[Java反射 - 方法 Methods](https://www.jianshu.com/p/673cd98f7ed7)
[Java反射 - 字段](https://www.jianshu.com/p/4a227247b53e)


项目代码：[github链接](https://github.com/jiaofanting/Java-nio-and-netty-spring-demo/tree/master/src/com/reflection/detail)









