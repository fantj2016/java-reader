本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java反射，您可以检查类的方法并在运行时调用它们。 这可以用来检测给定的类有哪些getter和setter。 你不能明确地要求getter和setter，所以你将不得不扫描一个类的所有方法，并检查每个方法是否是getter或setter。
首先让我们建立一个 getters and setters的规则：
* get getter方法的名字以“get”开始，取0个参数，并返回一个值。
* set setter方法的名字以“set”开始，并且取1个参数。
安装者可能会也可能不会返回一个值。 一些setter返回void，一些set的值，另外一些setter被调用的对象用于方法链接。 因此你不应该假设一个setter的返回类型。

这是一个代码示例，可以找到一个类的getter和setter：
```
public static void printGettersSetters(Class aClass){
  Method[] methods = aClass.getMethods();

  for(Method method : methods){
    if(isGetter(method)) System.out.println("getter: " + method);
    if(isSetter(method)) System.out.println("setter: " + method);
  }
}

public static boolean isGetter(Method method){
  if(!method.getName().startsWith("get"))      return false;
  //get方法肯定没有参数
  if(method.getParameterTypes().length != 0)   return false;  
  if(void.class.equals(method.getReturnType()) return false;
  return true;
}

public static boolean isSetter(Method method){
  if(!method.getName().startsWith("set")) return false;
  //set可以在参数上做文章。参数肯定是1
  if(method.getParameterTypes().length != 1) return false;
  return true;
}
```

### 实战
```
package com.reflection.detail;

import java.lang.reflect.Method;

/**
 * Created by Fant.J.
 * 2018/2/7 15:20
 */
public class Reflection_GetterSetter {

    public static void printGettersSetters(Class aClass) {
        Method[] methods = aClass.getMethods();

        for (Method method : methods) {
            if (isGetter(method)) {
                System.out.println("getter: " + method);
            }
            if (isSetter(method)) {
                System.out.println("setter: " + method);
            }
        }
    }

    /**
     * 是否是getter
     *
     * @param method method对象
     * @return 布尔值
     */
    public static boolean isGetter(Method method) {
        //get开头
        if (!method.getName().startsWith("get")) {
            return false;
        }
        //参数长度不是0
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        //返回值不为空
        if (void.class.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }

    /**
     * 是否是setter
     *
     * @param method
     * @return
     */

    public static boolean isSetter(Method method) {
        //是否是set开头
        if (!method.getName().startsWith("set")) {
            return false;
        }
        //是否参数长度等于1
        if (method.getParameterTypes().length != 1) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Class aClass = People.class;
        printGettersSetters(aClass);
    }
}

```

```
getter: public java.lang.String com.reflection.detail.People.getName()
getter: public java.lang.Integer com.reflection.detail.People.getId()
setter: public void com.reflection.detail.People.setName(java.lang.String)
setter: public void com.reflection.detail.People.setId(java.lang.Integer)
getter: public final native java.lang.Class java.lang.Object.getClass()
```
















