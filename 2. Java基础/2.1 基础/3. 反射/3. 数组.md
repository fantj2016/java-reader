本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

在Java中使用数组反射有时可能有点棘手。 特别是如果您需要获取某个类型的数组的类对象，如int []等。本文将讨论如何通过Java Reflection创建数组并获取其类对象。
### java.lang.reflect.Array中
通过Java处理数组反射是使用java.lang.reflect.Array类完成的。 请不要将此类与Java Collections套件中的java.util.Arrays类混淆，该类包含用于对数组进行排序的实用程序方法，将它们转换为集合等。

### 创建数组
通过Java创建数组反射是使用java.lang.reflect.Array类完成的。 下面是一个显示如何创建数组的示例：
```
int[] intArray = (int[]) Array.newInstance(int.class, 3);
```
此代码示例创建一个int数组。 给Array.newInstance（）方法的第一个参数int.class告诉数组中每个元素应该是什么类型。 第二个参数说明数组应该有多少空间。
### 访问数组
也可以使用Java Reflection来访问数组的元素。 这是通过Array.get（...）和Array.set（...）方法完成的。 这里是一个例子：
```
int[] intArray = (int[]) Array.newInstance(int.class, 3);

Array.set(intArray, 0, 123);
Array.set(intArray, 1, 456);
Array.set(intArray, 2, 789);

System.out.println("intArray[0] = " + Array.get(intArray, 0));
System.out.println("intArray[1] = " + Array.get(intArray, 1));
System.out.println("intArray[2] = " + Array.get(intArray, 2));
```
### 获取类对象的一个数组
我在Butterfly DI Container中实现脚本语言时碰到的一个问题是如何通过Java Reflection获取数组的Class对象。 使用非反射代码，你可以这样做：
```
Class stringArrayClass = String[].class;
```
这样做使用Class.forName（）不是很简单。 例如，你可以像这样访问基本的int数组类对象：
```
Class intArray = Class.forName("[I");
```
JVM通过字母I表示一个int。[左边的意思是它是我感兴趣的int数组的类。这也适用于所有其他基元。

对于需要使用稍微不同的记号的对象：
```
Class stringArrayClass = Class.forName("[Ljava.lang.String;");
```
注意[L在类名的左边， 在右边。 这意味着具有给定类型的对象数组。

作为一个方面说明，你不能使用Class.forName（）来获取原语的类对象。 下面的两个例子都会导致一个ClassNotFoundException异常：
```
Class intClass1 = Class.forName("I");
Class intClass2 = Class.forName("int");
```
我通常做这样的事情来获取原语和对象的类名：
```
public Class getClass(String className){
  if("int" .equals(className)) return int .class;
  if("long".equals(className)) return long.class;
  ...
  return Class.forName(className);
}
```
一旦获得了一个类型的Class对象，就有了一个简单的方法来获得该类型的数组的Class。 解决方法或者你可能称之为的解决方法是创建一个所需类型的空数组，并从该空数组中获取类对象。 这是一个骗子，但它可以工作。 
```
Class theClass = getClass(theClassName);
Class stringArrayClass = Array.newInstance(theClass, 0).getClass();
```
这提供了一个单一的，统一的方法来访问任何类型的数组类。 没有摆弄类名等

为了确保Class对象确实是一个数组，你可以调用Class.isArray（）方法来检查：
```
Class stringArrayClass = Array.newInstance(String.class, 0).getClass();
System.out.println("is array: " + stringArrayClass.isArray());
```
### 获取数组的组件类型
一旦获得数组的Class对象，就可以通过Class.getComponentType（）方法访问其组件类型。 组件类型是数组中项目的类型。 例如，一个int []数组的类型是int.class类对象。 String []数组的类型是java.lang.String类的对象。

这是访问组件类型数组的一个例子：
```
String[] strings = new String[3];
Class stringArrayClass = strings.getClass();
Class stringArrayComponentType = stringArrayClass.getComponentType();
System.out.println(stringArrayComponentType);
```
这个例子将打印出字符串数组类型的文本“java.lang.String”。     


### 实战
```
package com.reflection.detail;

import java.lang.reflect.Array;

/**
 * Created by Fant.J.
 * 2018/2/7 16:50
 */
public class Reflection_Arrays {
    public static void main(String[] args) throws ClassNotFoundException {

        //用反射来定义一个int类型，3长度的数组
        int[] intArray = (int[]) Array.newInstance(int.class, 3);

        Array.set(intArray, 0, 123);
        Array.set(intArray, 1, 456);
        Array.set(intArray, 2, 789);

        System.out.println("intArray[0] = " + Array.get(intArray, 0));
        System.out.println("intArray[1] = " + Array.get(intArray, 1));
        System.out.println("intArray[2] = " + Array.get(intArray, 2));

        //获取类对象的类型
        Class aClass = getClass("int");
        //获取类对象的一个数组
        Class stringArrayClass = Array.newInstance(aClass, 0).getClass();
        System.out.println("is array: " + stringArrayClass.isArray());


        //获取数组的组件类型
        String[] strings = new String[3];
        Class stringArrayClass2 = strings.getClass();
        Class stringArrayComponentType = stringArrayClass2.getComponentType();
        System.out.println(stringArrayComponentType);






    }
    static Class getClass(String className) throws ClassNotFoundException {
        if("int" .equals(className)) {return int .class;}
        if("long".equals(className)) {return long.class;}

        return Class.forName(className);
    }
}

```

```
intArray[0] = 123
intArray[1] = 456
intArray[2] = 789
is array: true
class java.lang.String
```













