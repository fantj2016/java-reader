本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java反射，您可以检查类的方法并在运行时调用它们。 这是通过Java类java.lang.reflect.Method完成的。 本文将更详细地介绍Java方法对象。
### 获取方法对象
Method类是从Class对象中获得的。 这里是一个例子：
```
Class aClass = ...//obtain class object
Method[] methods = aClass.getMethods();
```
Method []数组对于在该类中声明的每个公用方法都将有一个Method实例。

如果您知道要访问的方法的确切参数类型，则可以这样做，而不是获取数组的所有方法。 这个例子返回一个名为“doSomething”的公共方法，该方法以String为参数：
```
Class  aClass = ...//obtain class object
Method method =
    aClass.getMethod("doSomething", new Class[]{String.class});
```
如果没有方法匹配给定的方法名称和参数，在这种情况下String.class，抛出一个NoSuchMethodException。

如果您尝试访问的方法不带参数，请将null作为参数类型数组，如下所示：
```
Class  aClass = ...//obtain class object
Method method =
    aClass.getMethod("doSomething", null);
```
### 方法参数和返回类型
你可以阅读一个给定的方法是这样的参数：
```
Method method = ... // obtain method - see above
Class[] parameterTypes = method.getParameterTypes();
```
你可以像这样访问一个方法的返回类型：
```
Method method = ... // obtain method - see above
Class returnType = method.getReturnType();
```
### 使用方法对象调用方法   invoke
你可以调用像这样的方法：
```
//get method that takes a String as argument
Method method = MyObject.class.getMethod("doSomething", String.class);

Object returnValue = method.invoke(null, "parameter-value1");
```
null参数是要调用该方法的对象。 如果方法是静态的，则提供null而不是对象实例。 在这个例子中，如果doSomething（String.class）不是静态的，则需要提供一个有效的MyObject实例而不是null;

Method.invoke（Object target，Object ... parameters）方法接受可选数量的参数，但是您必须为要调用的方法中的每个参数提供一个参数。 在这种情况下，这是一个采取字符串的方法，所以必须提供一个字符串。

### 实战

```
package com.reflection.detail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Fant.J.
 * 2018/2/7 15:04
 */
public class Reflection_Methods {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //获取所有的共有方法
        Class aClass = People.class;
        Method [] methods = aClass.getMethods();

        //获取知道方法名称和参数 的方法,   如果没有参数，则传入null
        Method method = aClass.getMethod("setName", String.class);
        Method method1 = aClass.getMethod("getName",null);

        //根据method获取参数类型
        method.getParameterTypes();
        //根据method获取返回值类型
        method.getReturnType();

        /**
         *  使用反射来调用方法。如果方法是静态方法，则不需要实例该对象。
         *  因为我这里这个方法不是静态的。所以我实例化People对象
         *  仔细看看method和method1 是啥。代表了啥
         */
        People people = new People();
            method.invoke(people,"Fant.J");
            Object obj = method1.invoke(people,null);
            System.out.println(obj);
    }
}

```
结果
```
Fant.J
```




















