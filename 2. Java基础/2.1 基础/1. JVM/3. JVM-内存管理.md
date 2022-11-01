>JVM将内存主要划分为：方法区、虚拟机栈、本地方法栈、堆、程序计数器。

JVM运行时数据区.


#### 关系图：
![](https://upload-images.jianshu.io/upload_images/5786888-19ec249144c09df4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 程序计数器
记录当前线程锁执行的字节码的行号。
1. 程序计数器是一块较小的内存空间。
2. 处于线程独占区。
3. 执行java方法时，它记录正在执行的虚拟机字节码指令地址。执行native方法，它的值为undefined
4. 该区域是唯一一个没有规定任何OutOfMemoryError的区域

### 虚拟机栈
存放方法运行时所需的数据，成为栈帧。其实它很简单！它里面存放的是一个函数的上下文，具体存放的是执行的函数的一些数据。执行的函数需要的数据无非就是局部变量表（保存函数内部的变量）、操作数栈（执行引擎计算时需要），方法出口等等。

* 栈帧：执行引擎每调用一个方法时，就为这个函数创建一个栈帧，并加入虚拟机栈。换个角度理解，每个函数从调用到执行结束，其实是对应一个栈帧的入栈和出栈。

* 局部变量表： 存放编译期间可知的各种基本数据类型、引用类型、return Address类型
    * 局部变量表的内存空间在编译期间就完成分配，运行期间不会改变。

相关报错：
StackOverflowError：当栈帧大于我们设置的栈大小，就会出现栈溢出（递归没有出口等因素）
OutOfMemoryError：




### 本地方法栈
本地方法栈与虚拟机栈所发挥的作用很相似，他们的区别在于虚拟机栈为执行Java代码方法服务，而本地方法栈是为Native方法服务。与虚拟机栈一样，本地方法栈也会抛出StackOverflowError和OutOfMemoryError异常。


### 堆内存
存储对象实例。

Java堆可以说是虚拟机中最大一块内存了。它是所有线程所共享的内存区域，几乎所有的实例对象都是在这块区域中存放。当然，睡着JIT编译器的发展，所有对象在堆上分配渐渐变得不那么“绝对”了。

Java堆是垃圾收集器管理的主要区域。由于现在的收集器基本上采用的都是分代收集算法，所有Java堆可以细分为：新生代和老年代。在细致分就是把新生代分为：Eden空间、From Survivor空间、To Survivor空间。当堆无法再扩展时，会抛出OutOfMemoryError异常。

分配堆内存指令参数：-Xms -Xmx


### 方法区
存储运行时常量池，已被虚拟机加载的类信息，常量，静态变量，即时编译器编译后的代码等数据。（类版本、字段、方法、接口）。

运行时常量池：占用方法区中的一块。

方法区是各个线程共享区域，很容易理解，我们在写Java代码时，每个线程度可以访问同一个类的静态变量对象。

由于使用反射机制的原因，虚拟机很难推测那个类信息不再使用，因此这块区域的回收很难。另外，对这块区域主要是针对常量池回收，值得注意的是JDK1.7已经把常量池转移到堆里面了。同样，当方法区无法满足内存分配需求时，会抛出OutOfMemoryError。 
制造方法区内存溢出，注意，必须在JDK1.6及之前版本才会导致方法区溢出，原因后面解释,执行之前，可以把虚拟机的参数-XXpermSize和-XX：MaxPermSize限制方法区大小。
```
List<String> list =new ArrayList<String>();
int i =0;
while(true){
    list.add(String.valueOf(i).intern());
} 
```

运行后会抛出`java.lang.OutOfMemoryError:PermGen space`异常。 
解释一下，String的intern()函数作用是如果当前的字符串在常量池中不存在，则放入到常量池中。上面的代码不断将字符串添加到常量池，最终肯定会导致内存不足，抛出方法区的OOM。

下面解释一下，为什么必须将上面的代码在JDK1.6之前运行。我们前面提到，JDK1.7后，把常量池放入到堆空间中，这导致`intern()`函数的功能不同，具体怎么个不同法，且看看下面代码：
```
String str1 =new StringBuilder("fant").append("j").toString();
System.out.println(str1.intern()==str1);

String str2=new StringBuilder("ja").append("va").toString();
System.out.println(str2.intern()==str2);
```
这段代码在JDK1.6和JDK1.7运行的结果不同。JDK1.6结果是：`false,false` ，JDK1.7结果是`true, false`。原因是：JDK1.6中，`intern()`方法会吧首次遇到的字符串实例复制到常量池中，返回的也是常量池中的字符串的引用，而StringBuilder创建的字符串实例是在堆上面，所以必然不是同一个引用，返回false。

在JDK1.7中，**intern不再复制实例，常量池中只保存首次出现的实例的引用**，因此intern()返回的引用和由StringBuilder创建的字符串实例是同一个。为什么对str2比较返回的是false呢？这是因为，JVM中内部在加载类的时候，就已经有"java"这个字符串，不符合“首次出现”的原则，因此返回false。

###### 更深入的了解常量池和intern：
```java
/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {
        String a = "fantj";
        String b = "fantj";
        //a和b 会存到常量池里，常量池类似一个set集合，不允许有重复的值，所以加入第二个重复的值会返回已存在值的索引
        System.out.println(a == b);
        //new操作会实例化一个对象，会把他放到堆中。
        String c = new String("fantj");
        //所以a和c比较，a在常量池，c在堆，索引肯定不同，结果自然不同，返回false
        System.out.println(a == c);
        //a和c.intern比较，intern会把c搬到常量池，所以加入第二个重复的值会返回已存在值的索引，返回true
        System.out.println(a == c.intern());
    }
}
```
有注释，仔细看注释。
