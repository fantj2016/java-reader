本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

## Java反射——构造函数

使用Java反射，您可以检查类的构造函数，并在运行时实例化对象。 这是通过Java类java.lang.reflect.Constructor完成的。 本文将更详细地介绍Java构造器对象。
### 获取对象的构造函数
构造函数的类是获得的类对象。这是一个例子:
```
Class aClass = ...//obtain class object
Constructor[] constructors = aClass.getConstructors();
```
Constructor []数组对于在类中声明的每个公共构造函数都将有一个Constructor实例。

如果您知道要访问的构造函数的确切参数类型，则可以这样做，而不是获取数组所有构造函数。 此示例返回以String作为参数的给定类的公共构造函数：
```
Class aClass = ...//获取类对象
Constructor constructor =
        aClass.getConstructor(new Class[]{String.class});
```
如果没有构造函数匹配给定的构造函数参数（在这种情况下是String.class），则抛出NoSuchMethodException。
### 构造函数参数
你可以阅读哪些参数给定构造函数如下:
```
Constructor constructor = ... // 获取构造器，看上文
Class[] parameterTypes = constructor.getParameterTypes();
```
### 使用构造器对象实例化对象
你可以像这样实例化一个对象：
```
//获得一个参数为String类型的构造器
Constructor constructor = MyObject.class.getConstructor(String.class);

MyObject myObject = (MyObject)
        constructor.newInstance("constructor-arg1");
```
Constructor.newInstance（）方法接受可选数量的参数，但是您必须在您正在调用的构造函数中为每个参数提供一个参数。 在这种情况下，它是一个构造函数接受一个字符串，所以必须提供一个字符串。







