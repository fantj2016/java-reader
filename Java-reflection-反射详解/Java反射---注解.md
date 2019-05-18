本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java反射，您可以在运行时访问附加到Java类的注解。

### 什么是Java注释？
注释是Java 5中的一项新功能。注释是一种可以在Java代码中插入的注释或元数据。 这些注释可以在编译时通过预编译工具进行处理，也可以在运行时通过Java Reflection进行处理。 这是一个类注释的例子：
```
@MyAnnotation(name="someName",  value = "Hello World")
public class TheClass {
}
```
类TheClass的注释@MyAnnotation写在自己类上。 注释被定义为接口。 这是MyAnnotation定义：
```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface MyAnnotation {
    public String name();
    public String value();
}
```
前面的@标记为注释。 一旦定义了注释，就可以在代码中使用它，如前面的示例所示。

注解定义中的两条指令@Retention（RetentionPolicy.RUNTIME）和@Target（ElementType.TYPE）指定了注释的使用方式。

@Retention（RetentionPolicy.RUNTIME）意味着注释可以在运行时通过反射来访问。 如果你没有设置这个指令，注释将不会在运行时被保留下来，因此不能通过反射来获得。

@Target（ElementType.TYPE）意味着注释只能在类型（通常是类和接口）的类上使用。 您也可以指定METHOD或FIELD，或者可以将目标放在一起，以便注释可以用于类，方法和字段。
### 类注解
您可以在运行时访问类，方法或字段的注释。 以下是访问类注释的示例：
```
Class aClass = TheClass.class;
Annotation[] annotations = aClass.getAnnotations();

for(Annotation annotation : annotations){
    if(annotation instanceof MyAnnotation){
        MyAnnotation myAnnotation = (MyAnnotation) annotation;
        System.out.println("name: " + myAnnotation.name());
        System.out.println("value: " + myAnnotation.value());
    }
}
```
您也可以像这样访问特定的类注解：
```
Class aClass = TheClass.class;
Annotation annotation = aClass.getAnnotation(MyAnnotation.class);

if(annotation instanceof MyAnnotation){
    MyAnnotation myAnnotation = (MyAnnotation) annotation;
    System.out.println("name: " + myAnnotation.name());
    System.out.println("value: " + myAnnotation.value());
}
```
### 方法注解
以下是带注解的方法的示例：
```
public class TheClass {
  @MyAnnotation(name="someName",  value = "Hello World")
  public void doSomething(){}
}
```
您可以像这样访问方法注释：
```
Method method = ... //obtain method object
Annotation[] annotations = method.getDeclaredAnnotations();

for(Annotation annotation : annotations){
    if(annotation instanceof MyAnnotation){
        MyAnnotation myAnnotation = (MyAnnotation) annotation;
        System.out.println("name: " + myAnnotation.name());
        System.out.println("value: " + myAnnotation.value());
    }
}
```
您也可以像这样访问特定的方法注释：
```
Method method = ... // obtain method object
Annotation annotation = method.getAnnotation(MyAnnotation.class);

if(annotation instanceof MyAnnotation){
    MyAnnotation myAnnotation = (MyAnnotation) annotation;
    System.out.println("name: " + myAnnotation.name());
    System.out.println("value: " + myAnnotation.value());
}

```
### 参数注释
也可以为方法参数声明添加注释。
```
public class TheClass {
  public static void doSomethingElse(
        @MyAnnotation(name="aName", value="aValue") String parameter){
  }
}
```
您可以像这样访问Method对象的参数注释：
```
Method method = ... //obtain method object
Annotation[][] parameterAnnotations = method.getParameterAnnotations();
Class[] parameterTypes = method.getParameterTypes();

int i=0;
for(Annotation[] annotations : parameterAnnotations){
  Class parameterType = parameterTypes[i++];

  for(Annotation annotation : annotations){
    if(annotation instanceof MyAnnotation){
        MyAnnotation myAnnotation = (MyAnnotation) annotation;
        System.out.println("param: " + parameterType.getName());
        System.out.println("name : " + myAnnotation.name());
        System.out.println("value: " + myAnnotation.value());
    }
  }
}
```
请注意Method.getParameterAnnotations（）方法如何返回一个二维Annotation数组，其中包含每个方法参数的注释数组。
### 字段注解
以下是带注释的字段示例：
```
public class TheClass {

  @MyAnnotation(name="someName",  value = "Hello World")
  public String myField = null;
}
```
您可以像这样访问字段注释：  
```
Field field = ... //obtain field object
Annotation[] annotations = field.getDeclaredAnnotations();

for(Annotation annotation : annotations){
    if(annotation instanceof MyAnnotation){
        MyAnnotation myAnnotation = (MyAnnotation) annotation;
        System.out.println("name: " + myAnnotation.name());
        System.out.println("value: " + myAnnotation.value());
    }
}
```
您也可以像这样访问特定的字段注释：
```
Field field = ... // obtain method object
Annotation annotation = field.getAnnotation(MyAnnotation.class);

if(annotation instanceof MyAnnotation){
    MyAnnotation myAnnotation = (MyAnnotation) annotation;
    System.out.println("name: " + myAnnotation.name());
    System.out.println("value: " + myAnnotation.value());
}
```

### 实战

```
package com.reflection.detail;

import java.lang.annotation.Annotation;

/**
 * Created by Fant.J.
 * 2018/2/7 16:13
 */
public class Reflection_Annotations {
    public static void main(String[] args) {

        //获取对象
        Class aClass = People.class;
        Annotation[] annotations = aClass.getAnnotations();

        //获取类注解
        for(Annotation annotation : annotations){
            if(annotation instanceof MyAnnotation){
                MyAnnotation myAnnotation = (MyAnnotation) annotation;
                System.out.println("name: " + myAnnotation.name());
                System.out.println("value: " + myAnnotation.value());
            }
        }


    }
}

```

```
name: someName
value: Hello World

```



















