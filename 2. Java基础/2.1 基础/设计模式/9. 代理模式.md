#### 什么是代理模式?
>如果用专业术语来解：为其他对象提供一种代理以控制对这个对象的访问。如果投影在生活中，它可以理解成`中介` `黄牛`  `经纪人`等... 


 解决的问题:

>在直接访问对象时带来的问题，比如说：要访问的对象在远程的机器上。在面向对象系统中，有些对象由于某些原因（比如对象创建开销很大，或者某些操作需要安全控制，或者需要进程外的访问），直接访问会给使用者或者系统结构带来很多麻烦，我们可以在访问此对象时加上一个对此对象的访问层。`说白了就是在你代码前面插一段后面插一段`。

##### Java动态代理实现方式:

1. JDK 自带的动态代理
2. Cglib动态代理


### 1. JDK 自带的动态代理
>我以黄牛为例，黄牛刚开始了解该人需求，该人将信息(JAY演唱会门票)给予黄牛，黄牛给票。黄牛就是该买票人的代理。
##### 1.1 People.java
>注意这必须是一个接口，原因往下看。
```
public interface People {
    /**
     * 交谈
     */
    void speak();
}
```
这个接口很简单，就是一个讲话的功能，但是它为什么必须是一个接口呢。因为在`HuangNiu`这个类中，`Proxy.newProxyInstance` 这个方法的实现需要接口,这一点我在`HuangNiu`类下解释的很清楚，往下看。



##### 1.2 HuangNiu.java
>黄牛代理类，获取到People信息后调用Proxy来生成一个新的代理类,它必须实现`InvocationHandler`接口,这个接口使得它可以通过`invoke`方法实现对真实角色(`People`)的代理访问。
```
public class HuangNiu  implements InvocationHandler {

    private People people;
    /**
     * 获取被代理对象信息
     */
    public Object getInstance(People people){
        this.people = people;
        Class clazz = people.getClass();
        System.out.println("没生成代理之前的class对象:"+clazz );
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理中...");
        method.invoke(people);
        System.out.println("代理处理完毕,OK,请查收");
        return null;
    }
}
```

在实例化`HuangNiu`这个对象的时候，我们调用了`Proxy`的`newProxyInstance`方法:

``` 
return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
```
其中clazz是`People`的class对象。再来看看`newProxyInstance`的源码实现:
```java
public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h){
                    
        final Class<?>[] intfs = interfaces.clone();

        //Look up or generate the designated proxy class.
         
        Class<?> cl = getProxyClass0(loader, intfs);
 
        //获取代理类的构造函数对象
        //参数constructorParames为常量值：private static final Class<?>[] constructorParams = { InvocationHandler.class };
        final Constructor<?> cons = cl.getConstructor(constructorParames);
        final InvocationHandler ih = h;
        //根据代理类的构造函数对象来创建代理类对象
        return newInstance(cons, ih);
}
```
`getProxyClass0`方法源码:
```
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        return proxyClassCache.get(loader, interfaces);
    }
```
如果缓存中有该代理类，则取缓存，如果没有，则通过`ProxyClassFactory`来创建代理类。如果对如何生成代理类感兴趣则追踪下去即可。

我只取了核心代码和注释，可以看到JDK的动态代理实现是依据接口来重新生成一个新的代理类，

**什么是新的代理类**?

通俗点说就是综合和前后代理逻辑并重新生成一份`.class`文件来实现动态代理的类，下面也会具体说。
##### 1.3 Me.java
>被代理对象，实现了`People`接口,给代理提供需要的信息来实现被代理。
```
public class Me implements People {

    private String name;
    private String type;

    Me(String name, String type){
        this.name = name;
        this.type = type;
    }
    @Override
    public void speak() {
        System.out.println("我叫"+name+", 我要一张"+type);
    }
}
```

##### 1.4 Main.java
```
public class Main {
    public static void main(String[] args) {
        People instance = (People)new HuangNiu().getInstance(new Me("Fantj", "JAY演唱会门票"));
        instance.speak();
        System.out.println("生成代理对象后对象变成:"+instance.getClass());
    }
}
```

执行结果:
```
没生成代理之前的class对象:class com.fantj.proxy.jdk.Me
代理中...
我叫Fantj, 我要一张JAY演唱会门票
代理处理完毕,OK,请查收
生成代理对象后对象变成:class com.sun.proxy.$Proxy0
```
为了证明事实上真的有代理类的产生，我在代理完成前和代理完成后分别打印出它的类信息，可以看出是不同的，可以猜想到代理中是有代理类产生的，这个代理类就是`$Proxy0`。

那既然知道了这个类的信息，我们就可以逆向生成这个`.class`文件来看看(在main方法后追加):
```
/**
 * 生成代码
 */
try {
    byte[] $Proxy0s = ProxyGenerator.generateProxyClass("$Proxy0", new Class[]{instance.getClass()});
    String path = Main.class.getResource("").toString();
//            System.out.println("get the path"+path);
    FileOutputStream fileOutputStream = new FileOutputStream("$Proxy0.class");
    fileOutputStream.write($Proxy0s);
    fileOutputStream.close();
} catch (IOException e) {
    e.printStackTrace();
}
```
>它默认生成在项目根目录下:
![](https://upload-images.jianshu.io/upload_images/5786888-f19a81f851c07945.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我使用的IDEA工具会自动反编译.class文件为java代码，直接打开即刻看到源码，如果用别的工具的可以下载反编译工具来进行反编译。
##### $Proxy0.class
```
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.sun.proxy..Proxy0;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements Proxy0 {
    private static Method m1;
    private static Method m5;
    private static Method m2;
    private static Method m4;
    private static Method m11;
    private static Method m13;
    private static Method m0;
    private static Method m10;
    private static Method m12;
    private static Method m6;
    private static Method m9;
    private static Method m3;
    private static Method m7;
    private static Method m8;

    public $Proxy0(InvocationHandler var1) throws  {
        super(var1);
    }

    public final boolean equals(Object var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final InvocationHandler getInvocationHandler(Object var1) throws IllegalArgumentException {
        try {
            return (InvocationHandler)super.h.invoke(this, m5, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String toString() throws  {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Class getProxyClass(ClassLoader var1, Class[] var2) throws IllegalArgumentException {
        try {
            return (Class)super.h.invoke(this, m4, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final Class getClass() throws  {
        try {
            return (Class)super.h.invoke(this, m11, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void notifyAll() throws  {
        try {
            super.h.invoke(this, m13, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int hashCode() throws  {
        try {
            return (Integer)super.h.invoke(this, m0, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void wait() throws InterruptedException {
        try {
            super.h.invoke(this, m10, (Object[])null);
        } catch (RuntimeException | InterruptedException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void notify() throws  {
        try {
            super.h.invoke(this, m12, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Object newProxyInstance(ClassLoader var1, Class[] var2, InvocationHandler var3) throws IllegalArgumentException {
        try {
            return (Object)super.h.invoke(this, m6, new Object[]{var1, var2, var3});
        } catch (RuntimeException | Error var5) {
            throw var5;
        } catch (Throwable var6) {
            throw new UndeclaredThrowableException(var6);
        }
    }

    public final void wait(long var1) throws InterruptedException {
        try {
            super.h.invoke(this, m9, new Object[]{var1});
        } catch (RuntimeException | InterruptedException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final void speak() throws  {
        try {
            super.h.invoke(this, m3, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final boolean isProxyClass(Class var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m7, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void wait(long var1, int var3) throws InterruptedException {
        try {
            super.h.invoke(this, m8, new Object[]{var1, var3});
        } catch (RuntimeException | InterruptedException | Error var5) {
            throw var5;
        } catch (Throwable var6) {
            throw new UndeclaredThrowableException(var6);
        }
    }

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m5 = Class.forName("com.sun.proxy.$Proxy0").getMethod("getInvocationHandler", Class.forName("java.lang.Object"));
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m4 = Class.forName("com.sun.proxy.$Proxy0").getMethod("getProxyClass", Class.forName("java.lang.ClassLoader"), Class.forName("[Ljava.lang.Class;"));
            m11 = Class.forName("com.sun.proxy.$Proxy0").getMethod("getClass");
            m13 = Class.forName("com.sun.proxy.$Proxy0").getMethod("notifyAll");
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
            m10 = Class.forName("com.sun.proxy.$Proxy0").getMethod("wait");
            m12 = Class.forName("com.sun.proxy.$Proxy0").getMethod("notify");
            m6 = Class.forName("com.sun.proxy.$Proxy0").getMethod("newProxyInstance", Class.forName("java.lang.ClassLoader"), Class.forName("[Ljava.lang.Class;"), Class.forName("java.lang.reflect.InvocationHandler"));
            m9 = Class.forName("com.sun.proxy.$Proxy0").getMethod("wait", Long.TYPE);
            m3 = Class.forName("com.sun.proxy.$Proxy0").getMethod("speak");
            m7 = Class.forName("com.sun.proxy.$Proxy0").getMethod("isProxyClass", Class.forName("java.lang.Class"));
            m8 = Class.forName("com.sun.proxy.$Proxy0").getMethod("wait", Long.TYPE, Integer.TYPE);
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
```
里面大多是`Object`中的方法，还有我们定义的`speak()`方法，然后就是`静态代码块`(对变量进行初始化)。

当我们在Main中:
```
People instance = (People)new HuangNiu().getInstance(new Me("Fantj", "JAY演唱会门票"));
instance.speak();
```
调用`instance.speak();`时，事实上就调用了`$Proxy0`中的`speak()`方法，然后在该方法中再调用父类`Proxy`的`invoke`方法:
```
public final void speak() throws  {
    try {
        super.h.invoke(this, m3, (Object[])null);
    } catch (RuntimeException | Error var2) {
        throw var2;
    } catch (Throwable var3) {
        throw new UndeclaredThrowableException(var3);
    }
}
```
其中，`super.h.invoke`的调用的是父类`Proxy`中的`InvocationHandler.invoke()`方法.

#### 最后
你一定也很好奇它的名字为什么是`$Proxy0`,也一定很好奇第二个代理类的命名。

`ProxyClassFactory`类中的代码中有体现:
```
private static final class ProxyClassFactory
    implements BiFunction<ClassLoader, Class<?>[], Class<?>>
{
    // prefix for all proxy class names
    // 所有代理类的前缀
    private static final String proxyClassNamePrefix = "$Proxy";
    
    // next number to use for generation of unique proxy class names
    // 用java8新增的原子性AtomicLong类来做线程安全计数
    private static final AtomicLong nextUniqueNumber = new AtomicLong();

        /*
         * Choose a name for the proxy class to generate.
         */
      // 拼接代理类 类名
        long num = nextUniqueNumber.getAndIncrement();
        String proxyName = proxyPkg + proxyClassNamePrefix + num;
        ...
        ...
```

### 2. Cglib实现动态代理
>Cglib动态代理的实现原理和jdk基本一样，但是也有不同点。


**不同点:**
1. jdk动态代理生成的代理类是继承自Proxy，实现你的被代理类所实现的接口，要求必须有接口。
2. cglib动态代理生成的代理类是被代理者的子类，并且会重写父类的所有方法，要求该父类必须有空的构造方法,否则会报错:` Superclass has no null constructors but no arguments were given`,还有，private和final修饰的方法不会被子类重写。

**相同点:**
都是生成了新的代理类(字节码重组)。


##### Me.java
```
public class Me {

    private String name = "FantJ";
    private String type = "JAY演唱会门票";

    Me() {

    }

    Me(String name, String type){
        this.name = name;
        this.type = type;
    }
    public void speak() {
        System.out.println("我叫"+name+", 我要一张"+type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
```

##### HuangNiu.java
>该类要实现cglib包下的MethodInterceptor接口，从而实现对intercept的调用。
```
public class HuangNiu  implements MethodInterceptor {

    /**
     * 获取被代理对象信息
     *
     */
    public Object getInstance(Object object){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object.getClass());
        System.out.println("生成代理对象前对象是:"+object.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("代理中...");
        methodProxy.invokeSuper(o, objects);
//        methodProxy.invoke(o, objects);
        System.out.println("代理处理完毕,OK,请查收");
        return null;
    }
}
```

##### Main.java
```
public class Main {
    public static void main(String[] args) {
        Me instance = (Me)new HuangNiu().getInstance(new Me("Fantj", "JAY演唱会门票"));
//        Me instance = (Me)new HuangNiu().getInstance(new Me());
        instance.speak();
        System.out.println("生成代理对象后对象变成:"+instance.getClass());

        /**
         * 生成代码
         */
        try {
            byte[] $Proxy0s = ProxyGenerator.generateProxyClass("Me$$EnhancerByCGLIB$$5d2d06a1", new Class[]{instance.getClass()});
            String path = com.fantj.proxy.jdk.Main.class.getResource("").toString();
//            System.out.println("get the path"+path);
            FileOutputStream fileOutputStream = new FileOutputStream("Me$$EnhancerByCGLIB$$5d2d06a1.class");
            fileOutputStream.write($Proxy0s);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

控制台结果:
```
生成代理对象前对象是:class com.fantj.proxy.cglib.Me
代理中...
我叫FantJ, 我要一张JAY演唱会门票
代理处理完毕,OK,请查收
生成代理对象后对象变成:class com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1
```

同上，我们也把它生成的代理类反编译得到`Me$$EnhancerByCGLIB$$5d2d06a1.java`,原理和上面JDK代理类似，举一反三，这里不做介绍。
##### Me$$EnhancerByCGLIB$$5d2d06a1.java
```
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.fantj.proxy.cglib.Me..EnhancerByCGLIB..5d2d06a1;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodProxy;

public final class Me$$EnhancerByCGLIB$$5d2d06a1 extends Proxy implements 5d2d06a1 {
    private static Method m1;
    private static Method m6;
    private static Method m7;
    private static Method m2;
    private static Method m15;
    private static Method m21;
    private static Method m23;
    private static Method m0;
    private static Method m20;
    private static Method m13;
    private static Method m12;
    private static Method m3;
    private static Method m10;
    private static Method m22;
    private static Method m8;
    private static Method m11;
    private static Method m14;
    private static Method m4;
    private static Method m19;
    private static Method m9;
    private static Method m18;
    private static Method m16;
    private static Method m17;
    private static Method m5;

    public Me$$EnhancerByCGLIB$$5d2d06a1(InvocationHandler var1) throws  {
        super(var1);
    }

    public final boolean equals(Object var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Object newInstance(Callback[] var1) throws  {
        try {
            return (Object)super.h.invoke(this, m6, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setName(String var1) throws  {
        try {
            super.h.invoke(this, m7, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String toString() throws  {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Callback getCallback(int var1) throws  {
        try {
            return (Callback)super.h.invoke(this, m15, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Class getClass() throws  {
        try {
            return (Class)super.h.invoke(this, m21, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void notifyAll() throws  {
        try {
            super.h.invoke(this, m23, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int hashCode() throws  {
        try {
            return (Integer)super.h.invoke(this, m0, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void wait() throws InterruptedException {
        try {
            super.h.invoke(this, m20, (Object[])null);
        } catch (RuntimeException | InterruptedException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void CGLIB$SET_STATIC_CALLBACKS(Callback[] var1) throws  {
        try {
            super.h.invoke(this, m13, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void setCallbacks(Callback[] var1) throws  {
        try {
            super.h.invoke(this, m12, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final String getName() throws  {
        try {
            return (String)super.h.invoke(this, m3, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setCallback(int var1, Callback var2) throws  {
        try {
            super.h.invoke(this, m10, new Object[]{var1, var2});
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final void notify() throws  {
        try {
            super.h.invoke(this, m22, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String getType() throws  {
        try {
            return (String)super.h.invoke(this, m8, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void setType(String var1) throws  {
        try {
            super.h.invoke(this, m11, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void CGLIB$SET_THREAD_CALLBACKS(Callback[] var1) throws  {
        try {
            super.h.invoke(this, m14, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Object newInstance(Class[] var1, Object[] var2, Callback[] var3) throws  {
        try {
            return (Object)super.h.invoke(this, m4, new Object[]{var1, var2, var3});
        } catch (RuntimeException | Error var5) {
            throw var5;
        } catch (Throwable var6) {
            throw new UndeclaredThrowableException(var6);
        }
    }

    public final void wait(long var1) throws InterruptedException {
        try {
            super.h.invoke(this, m19, new Object[]{var1});
        } catch (RuntimeException | InterruptedException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final void speak() throws  {
        try {
            super.h.invoke(this, m9, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void wait(long var1, int var3) throws InterruptedException {
        try {
            super.h.invoke(this, m18, new Object[]{var1, var3});
        } catch (RuntimeException | InterruptedException | Error var5) {
            throw var5;
        } catch (Throwable var6) {
            throw new UndeclaredThrowableException(var6);
        }
    }

    public final Callback[] getCallbacks() throws  {
        try {
            return (Callback[])super.h.invoke(this, m16, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final MethodProxy CGLIB$findMethodProxy(Signature var1) throws  {
        try {
            return (MethodProxy)super.h.invoke(this, m17, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Object newInstance(Callback var1) throws  {
        try {
            return (Object)super.h.invoke(this, m5, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m6 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("newInstance", Class.forName("[Lnet.sf.cglib.proxy.Callback;"));
            m7 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("setName", Class.forName("java.lang.String"));
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m15 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("getCallback", Integer.TYPE);
            m21 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("getClass");
            m23 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("notifyAll");
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
            m20 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("wait");
            m13 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("CGLIB$SET_STATIC_CALLBACKS", Class.forName("[Lnet.sf.cglib.proxy.Callback;"));
            m12 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("setCallbacks", Class.forName("[Lnet.sf.cglib.proxy.Callback;"));
            m3 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("getName");
            m10 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("setCallback", Integer.TYPE, Class.forName("net.sf.cglib.proxy.Callback"));
            m22 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("notify");
            m8 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("getType");
            m11 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("setType", Class.forName("java.lang.String"));
            m14 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("CGLIB$SET_THREAD_CALLBACKS", Class.forName("[Lnet.sf.cglib.proxy.Callback;"));
            m4 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("newInstance", Class.forName("[Ljava.lang.Class;"), Class.forName("[Ljava.lang.Object;"), Class.forName("[Lnet.sf.cglib.proxy.Callback;"));
            m19 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("wait", Long.TYPE);
            m9 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("speak");
            m18 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("wait", Long.TYPE, Integer.TYPE);
            m16 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("getCallbacks");
            m17 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("CGLIB$findMethodProxy", Class.forName("net.sf.cglib.core.Signature"));
            m5 = Class.forName("com.fantj.proxy.cglib.Me$$EnhancerByCGLIB$$5d2d06a1").getMethod("newInstance", Class.forName("net.sf.cglib.proxy.Callback"));
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
```
