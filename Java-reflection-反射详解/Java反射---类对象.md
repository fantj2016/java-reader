本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java反射，您可以在运行时检查Java类。在使用Reflection时，检查类往往是你做的第一件事。从课程中你可以获得有关的信息
* Class Name
* Class Modifies (public, private, synchronized etc.)
* Package Info
* Superclass
* Implemented Interfaces
* Constructors
* Methods
* Fields
* Annotations
加上更多与Java类相关的信息。有关完整列表，您应该查阅java.lang.Class的JavaDoc。本文将简要介绍如何访问上述信息。一些主题也将在单独的文本中进行更详细的审查。例如，这个文本将告诉你如何获得所有的方法或者一个特定的方法，但是一个单独的文本将告诉你如何调用这个方法，如果找到与给定的一组参数相匹配的方法，如果存在多个方法同名，通过反射从方法调用抛出什么异常，如何找到getter / setter等等。本文的目的主要是介绍Class对象和你可以从中获得的信息。

### Class对象
在对类进行任何检查之前，您需要获取其java.lang.Class对象。 Java中的所有类型（包括数组的基本类型（int，long，float等））都有一个关联的Class对象。 如果你在编译时知道这个类的名字，你可以像这样获得一个Class对象：
```
  Class myObjectClass = MyObject.class
```
如果您在编译时不知道名称，但是在运行时将类名称作为字符串，则可以这样做：

String className = ... //在运行时获取类名字符串Class class = Class.forName（className）;

使用Class.forName（）方法时，您必须提供完全限定的类名称。 这是包括所有包名称的类名称。 例如，如果MyObject位于包com.fantj.refletion中，那么完全限定的类名是com.jenkov.myapp.MyObject

如果在运行时无法在类路径中找到该类，则Class.forName（）方法可能会引发ClassNotFoundException。

### Class 名
从一个Class对象中你可以得到两个版本的名字。 完全限定的类名（包括包名）是使用getName（）方法获得的，如下所示：
```
    Class aClass = ... //obtain Class object. See prev. section
    String className = aClass.getName();
```
如果你想要没有包名称的类名，你可以使用getSimpleName（）方法获得它，如下所示：
```
  Class  aClass          = ... //obtain Class object. See prev. section
  String simpleClassName = aClass.getSimpleName();
```
### Modifiers  修改器堆栈（modifier的复数）
您可以通过Class对象访问类的修饰符。 类的修饰符是关键字 "public", "private", "static"等您可以获得这样的类修饰符：
```
  Class  aClass = ... //obtain Class object. See prev. section
  int modifiers = aClass.getModifiers();
```
修饰符被打包成一个int，其中每个修饰符都是一个标志位，可以被设置或清除。 您可以在类java.lang.reflect.Modifier中使用这些方法检查修饰符：
```
    Modifier.isAbstract(int modifiers)
    Modifier.isFinal(int modifiers)
    Modifier.isInterface(int modifiers)
    Modifier.isNative(int modifiers)
    Modifier.isPrivate(int modifiers)
    Modifier.isProtected(int modifiers)
    Modifier.isPublic(int modifiers)
    Modifier.isStatic(int modifiers)
    Modifier.isStrict(int modifiers)
    Modifier.isSynchronized(int modifiers)
    Modifier.isTransient(int modifiers)
    Modifier.isVolatile(int modifiers)
```
### 包信息
你可以像这样从一个Class对象中获得关于这个包的信息：
```
Class  aClass = ... //obtain Class object. See prev. section
Package package = aClass.getPackage();
```
从Package对象中可以获得有关包的信息，如名称。 您也可以在类路径中访问此程序包所在的JAR文件的Manifest文件中为此程序包指定的信息。 例如，您可以在Manifest文件中指定包版本号。 你可以在这里阅读关于Package类的更多信息：java.lang.Package
### Superclass
从Class对象中可以访问类的父类。
```
Class superclass = aClass.getSuperclass();
```
父类的对象和其他类一样是一个Class对象，所以你可以继续对它进行类反射。
### Implemented Interfaces
可以获得给定类实现的接口列表.
```
Class  aClass = ... //obtain Class object. See prev. section
Class[] interfaces = aClass.getInterfaces();
```
一个类可以实现多个接口。 因此返回一个Class数组。 接口也由Java Reflection中的Class对象表示。

注意：只有返回由特定类实现的接口。 如果这个类的父类实现了一个接口，但是这个类并没有明确说明它也实现了这个接口，那么这个接口将不会返回到这个数组中。 即使班级在实践中实现了这个接口，因为超类也是如此。

要获得由给定类实现的接口的完整列表，您将不得不递归地查阅类及其超类。
### Constructors
你可以像这样访问一个类的构造函数：
```
 Constructor[] constructors = aClass.getConstructors();
```
构造函数在构造函数的文章中有更详细的介绍。[Java反射--构造函数](https://www.jianshu.com/p/d63769039817)
### Methods
你可以像这样访问一个类的方法：
```
 Method[] method = aClass.getMethods();
```
方法在方法文本中有更详细的介绍。[Java反射 - 方法 Methods](https://www.jianshu.com/p/673cd98f7ed7)
### Fields
你可以像这样访问类的字段（成员变量）：
```
 Field[] method = aClass.getFields();
```
field在field文章中有更详细的介绍。[Java反射 - 字段](https://www.jianshu.com/p/4a227247b53e)
### Annotations 注解
您可以像这样访问类的类注解：
```
 Annotation[] annotations = aClass.getAnnotations();
```
注释文章中的注释更详细。[Java反射 - 注解](https://www.jianshu.com/p/d2919e00d483)








