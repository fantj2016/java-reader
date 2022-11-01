本文翻译自： http://tutorials.jenkov.com/java-reflection/index.html

使用Java反射，您可以在运行时创建接口的动态实现。 你可以使用类java.lang.reflect.Proxy。 这个类的名字是我将这些动态接口实现称为动态代理的原因。 动态代理可以用于许多不同的目的，例如， 数据库连接和事务管理，用于单元测试的动态模拟对象，以及其他类似于AOP的方法拦截目的。

### 创建代理
您使用Proxy.newProxyInstance（）方法创建动态代理。 newProxyInstance（）方法需要3个参数：
1. 用于“加载”动态代理类的ClassLoader。
2. 要实现的接口数组。
3. 一个InvocationHandler将代理上的所有方法调用转发。

这里是一个例子：
```
InvocationHandler handler = new MyInvocationHandler();
MyInterface proxy = (MyInterface) Proxy.newProxyInstance(
                            MyInterface.class.getClassLoader(),
                            new Class[] { MyInterface.class },
                            handler);
```
运行此代码后，代理变量包含MyInterface接口的动态实现。 所有对代理的调用都将被转发给一般的InvocationHandler接口的处理程序实现。 InvocationHandler在下一节中介绍。
### InvocationHandler 

如前所述，您必须将InvocationHandler实现传递给Proxy.newProxyInstance（）方法。 所有对动态代理的方法调用都被转发给这个InvocationHandler实现。 以下是InvocationHandler接口的外观：
```
public interface InvocationHandler{
  Object invoke(Object proxy, Method method, Object[] args)
         throws Throwable;
}
```

下面是一个示例实现:
```
public class MyInvocationHandler implements InvocationHandler{

  public Object invoke(Object proxy, Method method, Object[] args)
  throws Throwable {
    //do something "dynamic"
  }
}
```
传递给invoke（）方法的proxy参数是实现接口的动态代理对象。 大多数情况下你不需要这个对象。

传递给invoke（）方法的Method对象表示在动态代理实现的接口上调用的方法。 从Method对象中，您可以获取方法名称，参数类型，返回类型等。有关更多信息，请参阅[Methods](https://www.jianshu.com/p/673cd98f7ed7)上的文本。

Object [] args数组包含调用接口中的方法时传递给代理的参数值。 注意：被实现的接口中的基本元素（int，long等）被封装在它们的对象（Integer，Long等）中。


##### 数据库连接和事务管理
Spring框架有一个事务代理，可以为你启动和提交/回滚事务。 在高级连接和事务划分和传播的文本中更详细地描述了这个工作原理，所以我只简要描述它。 通话顺序变成这
```
web controller --> proxy.execute(...);
  proxy --> connection.setAutoCommit(false);
  proxy --> realAction.execute();
    realAction does database work
  proxy --> connection.commit()
```

######DI容器
依赖注入容器有一个强大的功能，允许你将整个容器注入到它生成的bean中。 但是，由于不需要对容器接口进行依赖，因此容器可以自行适应设计的自定义工厂接口。 你只需要接口。 没有执行。 因此，工厂界面和你的类可以看起来像这样：
```
public interface IMyFactory {
  Bean   bean1();
  Person person();
  ...
}
```
```
public class MyAction{

  protected IMyFactory myFactory= null;

  public MyAction(IMyFactory factory){
    this.myFactory = factory;
  }

  public void execute(){
    Bean bean = this.myFactory.bean();
    Person person = this.myFactory.person();
  }

}
```
当MyAction类调用由容器注入其构造函数的IMyFactory实例上的方法时，方法调用将被转换为对IContainer.instance（）方法的调用，IContainer.instance（）方法是用于从容器中获取实例的方法。 这样一来，一个对象可以在运行时使用Butterfly Container作为工厂，而不仅仅是在创建时注入自己的依赖。 而这个没有任何容器特定接口的依赖。

#####类AOP方法拦截
Spring框架可以拦截方法调用给一个给定的bean，前提是bean实现了一些接口。 Spring框架将这个bean包装在一个动态代理中。 所有对bean的调用都会被代理拦截。 代理可以决定在其他对象上调用其他对象的方法，而不是或者在方法调用委托给包装的bean之后。  

###AOP动态代理底层实战
```
package com.reflection.detail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Created by Fant.J.
 * 2018/2/7 17:01
 */
public class Reflection_DynamicProx {

    public static void main(String[] args) {
        CommonDao commonDao = new CommonDaoImpl();
        InvocationHandler handler = new MyInvocationHandler(commonDao);
        CommonDao proxy = (CommonDao) Proxy.newProxyInstance(
                commonDao.getClass().getClassLoader(),
                commonDao.getClass().getInterfaces(),
                handler);
        proxy.insert(2,3);
        proxy.insert(3,3);
        proxy.delete(5,2);
    }

    /**
     * 重写InvocationHandler
     * 将代理上的所有方法调用转发
     * 相当于 Method一章节中，我们讲到的反射实例化
     * aClass.getMethod("setName", String.class);
     * 然后调用ethod.invoke(people,"Fant.J");
     */
    static final class MyInvocationHandler implements InvocationHandler{
        //这个就是我们要代理的真实对象
        private Object subject;

        //构造方法，需要把我们要代理的真实对象 传进来 供给invoke方法使用
        public MyInvocationHandler(Object subject)
        {
            this.subject = subject;
        }

        /**
         * 重写invoke方法
         * @param proxy 代理对象
         * @param method 代理方法
         * @param args 代理方法传入的参数
         * @return 返回反射结果
         * @throws Throwable 
         */

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //do something "dynamic"
            System.out.println("方法【" + method.getName() + "】开始执行, 参数为" + Arrays.asList(args) + "...");
            long start = System.currentTimeMillis();
            Object result = method.invoke(subject, args);
            long end = System.currentTimeMillis();
            System.out.println("方法【" + method.getName() + "】执行完成,运算结果为:" + result + ", 用时" + (end - start) + "毫秒!");
            return result;

        }
    }
    static interface CommonDao{
        public int insert(int a,int b);
        public int delete(int a,int b);
    }
    static class CommonDaoImpl implements CommonDao{
        @Override
        public int insert(int a, int b) {
            return a+b;
        }

        @Override
        public int delete(int a, int b) {
            return a-b;
        }
    }
}

```

```
方法【insert】开始执行, 参数为[2, 3]...
方法【insert】执行完成,运算结果为:5, 用时0毫秒!
方法【insert】开始执行, 参数为[3, 3]...
方法【insert】执行完成,运算结果为:6, 用时0毫秒!
方法【delete】开始执行, 参数为[5, 2]...
方法【delete】执行完成,运算结果为:3, 用时0毫秒!
```

有什么不懂的可以去先看下Java反射前面的东西。[Java反射文集](https://www.jianshu.com/nb/21989596)
.也可以在文章下方留言。DI容器相继推出。

项目代码：[github链接](https://github.com/jiaofanting/Java-nio-and-netty-spring-demo/tree/master/src/com/reflection/detail)








