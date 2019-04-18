>即将到来金三银四人才招聘的高峰期，渴望跳槽的朋友肯定跟我一样四处找以往的面试题，但又感觉找的又不完整，在这里我将把我所见到的题目做一总结，并尽力将答案术语化、标准化。预祝大家面试顺利。

术语会让你的面试更有说服力，让你感觉更踏实，建议大家多记背点术语。

### 1. 简单说下什么是跨平台
>术语：操作系统指令集、屏蔽系统之间的差异


由于各种操作系统所支持的指令集不是完全一致，所以在操作系统之上加个虚拟机可以来提供统一接口，屏蔽系统之间的差异。


### 2. Java有几种基本数据类型
>有八种基本数据类型。


|数据类型	|字节	|默认值|
---|---|---
|byte|	1|	0|
|short	|2　|	0|
|int|	4|	0|
|long|	8|	0|
|float|	4|	0.0f|
|double	|8|	0.0d|
|char|	2	|'\u0000'|
|boolean|	4|	false|

各自占用几字节也记一下。

### 3. 面向对象特征
面向对象的编程语言有封装、继承 、抽象、多态等4个主要的特征。

1. 封装： 把描述一个对象的属性和行为的代码封装在一个模块中，也就是一个类中，属性用变量定义，行为用方法进行定义，方法可以直接访问同一个对象中的属性。

2. 抽象： 把现实生活中的对象抽象为类。分为过程抽象和数据抽象
*  数据抽象 -->鸟有翅膀,羽毛等(类的属性)
* 过程抽象 -->鸟会飞,会叫(类的方法)

3. 继承：子类继承父类的特征和行为。子类可以有父类的方法，属性（非private）。子类也可以对父类进行扩展，也可以重写父类的方法。缺点就是提高代码之间的耦合性。

4. 多态： 多态是指程序中定义的引用变量所指向的具体类型和通过该引用变量发出的方法调用在编程时并不确定，而是在程序运行期间才确定(比如：向上转型，只有运行才能确定其对象属性)。方法覆盖和重载体现了多态性。


### 4. 为什么要有包装类型
>术语：让基本类型也具有对象的特征


基本类型 | 包装器类型
---|---
boolean| Boolean
char|	Character
int|	Integer
byte|	Byte
short|	Short
long|	Long
float|	Float
double|	Double


>为了让基本类型也具有对象的特征，就出现了包装类型（如我们在使用集合类型Collection时就一定要使用包装类型而非基本类型）因为容器都是装object的，这是就需要这些基本类型的包装器类了。

自动装箱：`new Integer(6);`，底层调用:`Integer.valueOf(6)`

自动拆箱: `int i = new Integer(6);`，底层调用`i.intValue();`方法实现。

```
Integer i  = 6;
Integer j = 6;
System.out.println(i==j);
```
答案在下面这段代码中找：
```
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}
```
##### 二者的区别：

1. 声明方式不同：基本类型不使用new关键字，而包装类型需要使用new关键字来在**堆中分配存储空间**；

2. 存储方式及位置不同：基本类型是直接将变量值存储在栈中，而包装类型是将对象放在堆中，然后通过引用来使用；

3. 初始值不同：基本类型的初始值如int为0，boolean为false，而包装类型的初始值为null；

4. 使用方式不同：基本类型直接赋值直接使用就好，而包装类型在集合如Collection、Map时会使用到。

### 5. ==和equals区别
	    
 * `==`较的是两个引用在内存中指向的是不是同一对象（即同一内存空间），也就是说在内存空间中的存储位置是否一致。如果两个对象的引用相同时（指向同一对象时），“==”操作符返回true，否则返回flase。   	    
	
* `equals`用来比较**某些特征**是否一样。我们平时用的String类等的equals方法都是重写后的，实现比较两个对象的内容是否相等。

我们来看看String重写的equals方法：
>它不止判断了内存地址，还增加了字符串是否相同的比较。
```
public boolean equals(Object anObject) {
    //判断内存地址是否相同
    if (this == anObject) {
        return true;
    }
    // 判断参数类型是否是String类型
    if (anObject instanceof String) {
        // 强转
        String anotherString = (String)anObject;
        int n = value.length;
        // 判断两个字符串长度是否相等
        if (n == anotherString.value.length) {
            char v1[] = value;
            char v2[] = anotherString.value;
            int i = 0;
            // 一一比较 字符是否相同
            while (n-- != 0) {
                if (v1[i] != v2[i])
                    return false;
                i++;
            }
            return true;
        }
    }
    return false;
}
```

### 6. String、StringBuffer和StringBuilder区别
>java中String、StringBuffer、StringBuilder是编程中经常使用的字符串类，他们之间的区别也是经常在面试中会问到的问题。现在总结一下，看看他们的不同与相同。

##### 1. 数据可变和不可变
1. `String`底层使用一个不可变的字符数组`private final char value[];`所以它内容不可变。
2. `StringBuffer`和`StringBuilder`都继承了`AbstractStringBuilder`底层使用的是可变字符数组：`char[] value;`

##### 2. 线程安全
* `StringBuilder`是线程不安全的，效率较高；而`StringBuffer`是线程安全的，效率较低。

通过他们的`append()`方法来看，`StringBuffer`是有同步锁，而`StringBuilder`没有：
```
@Override
public synchronized StringBuffer append(Object obj) {
    toStringCache = null;
    super.append(String.valueOf(obj));
    return this;
}
```
```
@Override
public StringBuilder append(String str) {
    super.append(str);
    return this;
}
```
##### 3. 相同点
`StringBuilder`与`StringBuffer`有公共父类`AbstractStringBuilder`。


最后，操作可变字符串速度：`StringBuilder > StringBuffer > String`，这个答案就显得不足为奇了。
### 7. 讲一下Java中的集合

1. Collection下：List系(有序、元素允许重复)和Set系(无序、元素不重复)

>set根据equals和hashcode判断，一个对象要存储在Set中，必须重写equals和hashCode方法

2. Map下：HashMap线程不同步；ConcurrentMap线程同步

3. Collection系列和Map系列：Map是对Collection的补充，两个没什么关系

### 8. ArrayList和LinkedList区别？
>之前专门有写过ArrayList和LinkedList源码的文章。

1. ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。 
2. 对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。 
3. 对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。 


### 9. ConcurrentModificationException异常出现的原因

```
public class Test {
    public static void main(String[] args)  {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        Iterator<Integer> iterator = list.iterator();
        while(iterator.hasNext()){
            Integer integer = iterator.next();
            if(integer==2)
                list.remove(integer);
        }
    }
}
```
执行上段代码是有问题的，会抛出`ConcurrentModificationException`异常。

**原因**：调用`list.remove()`方法导致`modCount`和`expectedModCount`的值不一致。

```
final void checkForComodification() {
    if (modCount != expectedModCount)
    throw new ConcurrentModificationException();
}
```


**解决办法**：在迭代器中如果要删除元素的话，需要调用`Iterator`类的`remove`方法。

```
public class Test {
    public static void main(String[] args)  {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        Iterator<Integer> iterator = list.iterator();
        while(iterator.hasNext()){
            Integer integer = iterator.next();
            if(integer==2)
                iterator.remove();   //注意这个地方
        }
    }
}
```

### 10. HashMap和HashTable、ConcurrentHashMap区别？
相同点:
1. HashMap和Hashtable都实现了Map接口
2. 都可以存储key-value数据

不同点：
1. HashMap可以把null作为key或value，HashTable不可以
2. HashMap线程不安全，效率高。HashTable线程安全，效率低。
3. HashMap的迭代器(Iterator)是fail-fast迭代器，而Hashtable的enumerator迭代器不是fail-fast的。

>什么是fail-fast?
>就是最快的时间能把错误抛出而不是让程序执行。

##### 10.2 如何保证线程安全又效率高？
Java 5提供了ConcurrentHashMap，它是HashTable的替代，比HashTable的扩展性更好。

ConcurrentHashMap将整个Map分为N个segment(类似HashTable)，可以提供相同的线程安全，但是效率提升N倍，默认N为16。
##### 10.3 我们能否让HashMap同步？
HashMap可以通过下面的语句进行同步：
`Map m = Collections.synchronizeMap(hashMap);`


### 11. 拷贝文件的工具类使用字节流还是字符流
>答案：字节流
##### 11.1 什么是字节流，什么是字符流？

字节流：传递的是字节（二进制），

字符流：传递的是字符

##### 11.2 答案

我们并不支持下载的文件有没有包含字节流(图片、影像、音源)，所以考虑到通用性，我们会用字节流。


### 12. 线程创建方式
>这个之前自己做过总结，也算比较全面。

#### 方法一：继承Thread类，作为线程对象存在（继承Thread对象）
```
public class CreatThreadDemo1 extends Thread{
    /**
     * 构造方法： 继承父类方法的Thread(String name)；方法
     * @param name
     */
    public CreatThreadDemo1(String name){
        super(name);
    }

    @Override
    public void run() {
        while (!interrupted()){
            System.out.println(getName()+"线程执行了...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        CreatThreadDemo1 d1 = new CreatThreadDemo1("first");
        CreatThreadDemo1 d2 = new CreatThreadDemo1("second");

        d1.start();
        d2.start();

        d1.interrupt();  //中断第一个线程
    }
}
```
常规方法，不多做介绍了，interrupted方法，是来判断该线程是否被中断。（终止线程不允许用stop方法，该方法不会施放占用的资源。所以我们在设计程序的时候，要按照中断线程的思维去设计，就像上面的代码一样）。

###### 让线程等待的方法
* Thread.sleep(200);  //线程休息2ms
* Object.wait()；  //让线程进入等待，直到调用Object的notify或者notifyAll时，线程停止休眠

#### 方法二：实现runnable接口，作为线程任务存在
```
public class CreatThreadDemo2 implements Runnable {
    @Override
    public void run() {
        while (true){
            System.out.println("线程执行了...");
        }
    }

    public static void main(String[] args) {
        //将线程任务传给线程对象
        Thread thread = new Thread(new CreatThreadDemo2());
        //启动线程
        thread.start();
    }
}
```
Runnable 只是来修饰线程所执行的任务，它不是一个线程对象。想要启动Runnable对象，必须将它放到一个线程对象里。

#### 方法三：匿名内部类创建线程对象
```
public class CreatThreadDemo3 extends Thread{
    public static void main(String[] args) {
        //创建无参线程对象
        new Thread(){
            @Override
            public void run() {
                System.out.println("线程执行了...");
            }
        }.start();
       //创建带线程任务的线程对象
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程执行了...");
            }
        }).start();
        //创建带线程任务并且重写run方法的线程对象
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable run 线程执行了...");
            }
        }){
            @Override
            public void run() {
                System.out.println("override run 线程执行了...");
            }
        }.start();
    }

}

```
创建带线程任务并且重写run方法的线程对象中，为什么只运行了Thread的run方法。我们看看Thread类的源码，![image.png](http://upload-images.jianshu.io/upload_images/5786888-8fc80d17feb58198.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
，我们可以看到Thread实现了Runnable接口，而Runnable接口里有一个run方法。
所以，我们最终调用的重写的方法应该是Thread类的run方法。而不是Runnable接口的run方法。

#### 方法四：创建带返回值的线程
```
public class CreatThreadDemo4 implements Callable {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CreatThreadDemo4 demo4 = new CreatThreadDemo4();

        FutureTask<Integer> task = new FutureTask<Integer>(demo4); //FutureTask最终实现的是runnable接口

        Thread thread = new Thread(task);

        thread.start();

        System.out.println("我可以在这里做点别的业务逻辑...因为FutureTask是提前完成任务");
        //拿出线程执行的返回值
        Integer result = task.get();
        System.out.println("线程中运算的结果为:"+result);
    }

    //重写Callable接口的call方法
    @Override
    public Object call() throws Exception {
        int result = 1;
        System.out.println("业务逻辑计算中...");
        Thread.sleep(3000);
        return result;
    }
}

```
Callable接口介绍：
```
public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    V call() throws Exception;
}
```
返回指定泛型的call方法。然后调用FutureTask对象的get方法得道call方法的返回值。

#### 方法五：定时器Timer
```
public class CreatThreadDemo5 {

    public static void main(String[] args) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时器线程执行了...");
            }
        },0,1000);   //延迟0，周期1s

    }
}
```
#### 方法六：线程池创建线程
```
public class CreatThreadDemo6 {
    public static void main(String[] args) {
        //创建一个具有10个线程的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        long threadpoolUseTime = System.currentTimeMillis();
        for (int i = 0;i<10;i++){
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"线程执行了...");
                }
            });
        }
        long threadpoolUseTime1 = System.currentTimeMillis();
        System.out.println("多线程用时"+(threadpoolUseTime1-threadpoolUseTime));
        //销毁线程池
        threadPool.shutdown();
        threadpoolUseTime = System.currentTimeMillis();
    }

}

```
#### 方法七：利用java8新特性  stream 实现并发
lambda表达式不懂的，可以看看我的java8新特性文章：

java8-lambda：https://www.jianshu.com/p/3a08dc78a05f

java8-stream：https://www.jianshu.com/p/ea16d6712a00
```
public class CreatThreadDemo7 {
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(10,20,30,40);
        //parallel 平行的，并行的
        int result = values.parallelStream().mapToInt(p -> p*2).sum();
        System.out.println(result);
        //怎么证明它是并发处理呢
        values.parallelStream().forEach(p-> System.out.println(p));
    }
}

```
```
200
40
10
20
30
```
怎么证明它是并发处理呢,他们并不是按照顺序输出的 。







