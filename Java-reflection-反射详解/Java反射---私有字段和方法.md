本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

尽管普遍认为通过Java Reflection可以访问其他类的私有字段和方法。 这并不困难。 这在单元测试中可以非常方便。 本文将告诉你如何。
  
### 访问私有字段
要访问私有字段，您需要调用Class.getDeclaredField（String name）或Class.getDeclaredFields（）方法。 方法Class.getField（String name）和Class.getFields（）方法只返回公共字段，所以它们将不起作用。 下面是一个带有私有字段的类的简单例子，下面是通过Java反射访问该字段的代码：
```
public class PrivateObject {

  private String privateString = null;

  public PrivateObject(String privateString) {
    this.privateString = privateString;
  }
}

PrivateObject privateObject = new PrivateObject("The Private Value");

Field privateStringField = PrivateObject.class.
            getDeclaredField("privateString");

//关闭对此特定字段实例的访问检查
privateStringField.setAccessible(true);

String fieldValue = (String) privateStringField.get(privateObject);
System.out.println("fieldValue = " + fieldValue);
```
此代码示例将打印出“fieldValue = The Private Value”文本，该文本是在代码示例开始时创建的PrivateObject实例的private字段privateString的值。

注意使用PrivateObject.class.getDeclaredField（“privateString”）方法。 这是这个方法调用返回私人字段。 此方法只返回在该特定类中声明的字段，而不是在任何超类中声明的字段。

注意粗线也是。 通过调用Field.setAccessible（true），可以关闭对此特定字段实例的访问检查，仅用于反射。 现在，即使调用者不是这些范围的一部分，即使它是私有的，受保护的或包的范围，也可以访问它。 您仍然无法使用正常代码访问该字段。 编译器不会允许它。
### 访问私有方法
要访问私有方法，您将需要调用Class.getDeclaredMethod（String name，Class [] parameterTypes）或Class.getDeclaredMethods（）方法。 方法Class.getMethod（String name，Class [] parameterTypes）和Class.getMethods（）方法只返回公共方法，所以它们将不起作用。 下面是一个带私有方法的类的简单示例，下面是通过Java反射访问该方法的代码：
```
public class PrivateObject {

  private String privateString = null;

  public PrivateObject(String privateString) {
    this.privateString = privateString;
  }

  private String getPrivateString(){
    return this.privateString;
  }
}
```
```
PrivateObject privateObject = new PrivateObject("The Private Value");

Method privateStringMethod = PrivateObject.class.
        getDeclaredMethod("getPrivateString", null);

privateStringMethod.setAccessible(true);

String returnValue = (String)
        privateStringMethod.invoke(privateObject, null);

System.out.println("returnValue = " + returnValue);
```
此代码示例将在代码示例的开头处创建的PrivateObject实例上调用时，打印出文本“returnValue = The Private Value”，这是getPrivateString（）方法返回的值。
注意使用PrivateObject.class.getDeclaredMethod（“privateString”）方法。 正是这个方法调用返回私有方法。
 通过调用Method.setAccessible（true），可以关闭此特定Method实例的访问检查，仅用于反射。 现在，即使调用者不是这些范围的一部分，即使它是私有的，受保护的或包的范围，也可以访问它。 您仍然无法使用普通代码访问该方法。 编译器不会允许它。

### 实战


```
package com.reflection.detail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Fant.J.
 * 2018/2/7 15:28
 */
public class Reflection_Private {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        //获取对象
        Class aClass = People.class;
        Field  privateField = aClass.getDeclaredField("privateString");

        //设置允许jvm编译通过。(jvm 默认不允许访问 私有类型的东西)
        privateField.setAccessible(true);

        //获取私有字段的值
        People people = new People();
        Object privateStringResult = privateField.get(people);
        System.out.println(privateStringResult);

        //获取私有方法
            //获取setPrivateString方法
        Method privateMethod = aClass.getDeclaredMethod("setPrivateString", String.class);
            //获取getPrivateString方法
        Method privateMethod1 = aClass.getDeclaredMethod("getPrivateString", null);
            //jvm编译通过允许
        privateMethod.setAccessible(true);
            //反射对象和参数 给setPrivateString方法
        privateMethod.invoke(people,"Fant.J is so cool");
            //反射对象和参数 给getPrivateString方法
        Object obj = privateMethod1.invoke(people,null);
        System.out.println(obj);
    }
}

```

```
package com.reflection.detail;

/**
 * Created by Fant.J.
 * 2018/2/7 14:37
 */
public class People {
    private Integer id;
    private String name;
    //field 字段
    public String someField = "FantJ";

    private String privateString = "shuai";

    public People(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getSomeField() {
        return someField;
    }

    public void setSomeField(String someField) {
        this.someField = someField;
    }

    public String getPrivateString() {
        return privateString;
    }

    public void setPrivateString(String privateString) {
        this.privateString = privateString;
    }

    public People(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

```
shuai
Fant.J is so cool
```
