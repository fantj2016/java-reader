>“Lambda 表达式”(lambda expression)是一个[匿名函数](https://baike.baidu.com/item/%E5%8C%BF%E5%90%8D%E5%87%BD%E6%95%B0/4337265)，Lambda表达式基于数学中的[λ演算](https://baike.baidu.com/item/%CE%BB%E6%BC%94%E7%AE%97)得名，直接对应于其中的lambda抽象(lambda abstraction)，是一个匿名函数，即没有函数名的函数。Lambda表达式可以表示[闭包](https://baike.baidu.com/item/%E9%97%AD%E5%8C%85/10908873)（注意和数学传统意义上的不同）。



### 认识lambda
```
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("匿名内部类的执行");
            }
        }).start();
    }
```
设计匿名内部类的目的，就是为了方便程序猿将代码作为数据来传递。但是你会发现，这个对象看起来是很多余的，所以我们不想传入对象，只想传入行为。
```
        new Thread(()->{
            System.out.println("lambda代替内部类");
        }).start();
```
和实现某接口的对象不同，我们传入了一段代码块--一个没有名字的函数。`->`将参数和表达式主体分开，左边是参数，右边是方法体。

### Lambda的不同形式
```
Runnable runnable =  ()-> System.out.println("hello world");
```
该Lambda表达式不包含参数（因为是空括号）。

```
    interface Test{
        void oneParam(String name);
    }

        Test test = s -> System.out.println("oneParam方法传递参数："+s);
        test.oneParam("我是传递的值");

控制台输出：
oneParam方法传递参数：我是传递的值
```
（lambda只能用于函数式接口），如果参数只包含一个参数，可以省略参数的括号。

```
    interface Test2{
        int add(int a,int b);
    }
    Test2 test2 = (x,y) -> x+y;
    int add = test2.add(10, 10);
    System.out.println(add);

控制台输出：
20
```
可以看到，我们在使用lambda的时候创建了一个函数`x+y`，`Test2`对象不是表示两个数字的和，而是表示两个数字相加的代码。以上的代码中，参数类型都是由编译器自己推断的，同样，我们可以明确的声明参数类型：

```
    interface Test3{
        long add(long a,long b);
    }

    Test3 test3 = (long x,long y) -> x+y;
    long add = test3.add(10, 10);
```

### 引用值的要求

>Error:(25, 64) java: 从lambda 表达式引用的本地变量必须是最终变量或实际上的最终变量

```
        String  name = "FantJ";
        Runnable runnable =  ()-> System.out.println("hello " +name);
        runnable.run();
```
```
hello FantJ
```
上面这段代码，Lambda可以引用非final变量这个属性你可以早已了解，但是你更需要知道，java8只是放松了这一语法的限制，但实际上要求该变量还是final。

![](https://upload-images.jianshu.io/upload_images/5786888-b966fc356202c529.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-288f691904034970.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到，不管`name`在使用`lambda`的前还是后做改动，`lambda`都会报错：*表达式引用的本地变量必须是最终变量或实际上的最终变量*，简单的，我们可以称他为既成事实上的final变量。所以，lambda也被称为闭包。



### Lambda表达式类型
>java中，所有方法都有返回类型，那lambda返回类型是什么呢。

是接口方法的返回类型。这一点前文有体现过。这里再详细解释下。

```
    interface Test{
        void oneParam(String name);
    }
```
拿这个例子来讲，`oneParam`方法表示一种行为：接受一个`String`，返回`void`。只要方法名和行为和Lambda表达式匹配即可完成调用。


**注意**：如果编译器猜不出参数和返回值的类型，则都将视为`Object`处理。





