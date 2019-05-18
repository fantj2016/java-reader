本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java泛型通常分为两种不同的情况:
1. 声明一个类/接口是可参数化的。
2. 使用可参数化的类。
当你写一个类或接口时，你可以指定它应该是可参数化的。 java.util.List接口就是这种情况。 而不是创建一个Object列表，你可以参数化java.util.List来创建一个String String列表，如下所示：
```
List<String> myList = new ArrayList<String>();
```
当通过反射在运行时检查可参数化类型本身时，如java.util.List，无法知道类型已经被参数化。对象本身不知道它被参数化了什么。

但是，该对象的引用知道包含它所引用的泛型的类型。也就是说，如果它不是一个局部变量。如果对象被对象中的字段引用，则可以通过反射来查看Field声明，以获取有关由该字段声明的泛型类型的信息。

如果对象被方法中的参数引用，也是一样的。通过该方法的Parameter对象（一个Java反射对象），您可以看到声明了哪个泛型类型的参数。

最后，您还可以查看方法的返回类型，以查看它声明的泛型类型。同样，你不能从返回的实际对象中看到它。您需要通过反射来查看方法声明，以查看它声明的返回类型（包括泛型）。

综上所述：只能从引用的声明（字段，参数，返回类型）中看到这些引用引用的对象所具有的通用类型。你不能从对象本身看到它。

以下各节将仔细研究这些情况。
### 泛型方法返回类型
如果已经获得java.lang.reflect.Method对象，则可以获取有关其通用返回类型的信息。 您可以阅读如何获取文本“Java泛型：方法”中的Method对象。 下面是一个带有参数化返回类型的方法的示例类：
```
public class MyClass {

  protected List<String> stringList = ...;

  public List<String> getStringList(){
    return this.stringList;
  }
}
```
在这个类中，可以获得getStringList（）方法的通用返回类型。 换句话说，可以检测到getStringList（）返回一个List <String>而不仅仅是一个List。 
```
Method method = MyClass.class.getMethod("getStringList", null);

Type returnType = method.getGenericReturnType();

if(returnType instanceof ParameterizedType){
    ParameterizedType type = (ParameterizedType) returnType;
    Type[] typeArguments = type.getActualTypeArguments();
    for(Type typeArgument : typeArguments){
        Class typeArgClass = (Class) typeArgument;
        System.out.println("typeArgClass = " + typeArgClass);
    }
}
```
这段代码将打印出“typeArgClass = java.lang.String”文本。 Type []数组typeArguments数组将包含一个项目 - 一个表示java.lang.String类的Class实例。 类实现了Type接口。

### 泛型方法参数类型
您也可以通过Java Reflection在运行时访问通用类型的参数类型。 下面是一个带有参数化List作为参数的方法的示例类：
```
public class MyClass {
  protected List<String> stringList = ...;

  public void setStringList(List<String> list){
    this.stringList = list;
  }
}
```
```
method = Myclass.class.getMethod("setStringList", List.class);

Type[] genericParameterTypes = method.getGenericParameterTypes();

for(Type genericParameterType : genericParameterTypes){
    if(genericParameterType instanceof ParameterizedType){
        ParameterizedType aType = (ParameterizedType) genericParameterType;
        Type[] parameterArgTypes = aType.getActualTypeArguments();
        for(Type parameterArgType : parameterArgTypes){
            Class parameterArgClass = (Class) parameterArgType;
            System.out.println("parameterArgClass = " + parameterArgClass);
        }
    }
}
```
这段代码将打印出“parameterArgType = java.lang.String”文本。 Type []数组的parameterArgTypes数组将包含一个项 - 一个表示类java.lang.String的Class实例。 类实现了Type接口。
### 通用字段类型
也可以访问通用类型的公共字段。 字段是类成员变量 - 静态或实例变量。 您可以阅读关于在文本“Java Generics：Fields”中获取Field对象的信息。 这是前面的例子，带有一个名为stringList的实例字段。
```
public class MyClass {
  public List<String> stringList = ...;
}
```
```
Field field = MyClass.class.getField("stringList");

Type genericFieldType = field.getGenericType();

if(genericFieldType instanceof ParameterizedType){
    ParameterizedType aType = (ParameterizedType) genericFieldType;
    Type[] fieldArgTypes = aType.getActualTypeArguments();
    for(Type fieldArgType : fieldArgTypes){
        Class fieldArgClass = (Class) fieldArgType;
        System.out.println("fieldArgClass = " + fieldArgClass);
    }
}
```
这段代码将打印出“fieldArgClass = java.lang.String”文本。 Type []数组fieldArgTypes数组将包含一个项目 - 一个表示类java.lang.String的Class实例。 类实现了Type接口.

### 实战
```
package com.reflection.detail;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射泛型
 * Created by Fant.J.
 * 2018/2/7 16:31
 */
public class Reflection_Generics {
    public static void main(String[] args) throws NoSuchMethodException {

        Class aClass = People.class;
        Method getStringList = aClass.getMethod("getStringList", null);
        //获取方法的返回值类型
        Type returnType = getStringList.getGenericReturnType();
        System.out.println(returnType);

        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
            }
        }

    }
}

```

```
java.util.List<java.lang.String>
typeArgClass = class java.lang.String
```


















