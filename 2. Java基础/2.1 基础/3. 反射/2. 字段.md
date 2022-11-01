本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java反射，您可以检查类的字段（成员变量）并在运行时获取/设置它们。 这是通过Java类java.lang.reflect.Field完成的。 本文将更详细地介绍Java Field对象。 请记住也要检查Sun的JavaDoc。
### 获取字段对象
Field类是从Class对象中获取的。 这里是一个例子：
```
Class aClass = ...//获取一个class对象
Field[] fields = aClass.getFields();
```
Field []数组将为每个在类中声明的公共字段设置一个Field实例。

如果您知道要访问的字段的名称，则可以像这样访问它：
```
Class  aClass = MyObject.class
Field field = aClass.getField("someField");
```
上面的例子将返回Field实例对应的字段someField，如下面的MyObject所声明的：
```
public class MyObject{
  public String someField = null;

}
```
如果没有字段以getField（）方法的参数形式存在，则抛出NoSuchFieldException。
### 字段名称
获得Field实例后，可以使用Field.getName（）方法获取其字段名称，如下所示：
```
Field field = ... //obtain field object
String fieldName = field.getName();
```
### 字段类型

您可以使用Field.getType（）方法确定字段的字段类型（String，int等）：
```
Field field = aClass.getField("someField");
Object fieldType = field.getType();
```
### 获取和设置字段值

获得Field引用后，可以使用Field.get（）和Field.set（）方法来获取和设置其值，如下所示：
```
Class  aClass = MyObject.class
Field field = aClass.getField("someField");

MyObject objectInstance = new MyObject();

Object value = field.get(objectInstance);

field.set(objetInstance, value);
```
传递给get和set方法的objectInstance参数应该是拥有该字段的类的一个实例。 在上面的例子中，使用了MyObject的实例，因为someField是MyObject类的实例成员。

它是一个静态字段（public static ...），它传递null作为get和set方法的参数，而不是上面传递的objectInstance参数。


### 实战
```
package com.reflection.detail;

import java.lang.reflect.Field;

/**
 * Created by Fant.J.
 * 2018/2/7 14:51
 */
public class Reflection_Fields {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        //获取field对象
        Class aClass = People.class;
        Field[] fields = aClass.getFields();

        //如果你知道一些字段的名字，你可以这样获取到它
        Field field = aClass.getField("someField");
        System.out.println(field.getName()+"   "+field.getType());

        //那么，我们能获取到它的字段类型，那如何取出该字段的值呢
        People people = new People();
        Object obj = field.get(people);
        System.out.println(obj.toString());

        //如何给字段设置值呢
        field.set(people,"shuai");
        Object obj2 = field.get(people);
        System.out.println(obj2.toString());
    }
}

```
结果
```
someField   class java.lang.String
FantJ
shuai
```




