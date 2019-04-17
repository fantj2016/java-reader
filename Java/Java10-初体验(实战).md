>最近 IDEA 发布支持 java10的新版本。

#####Java10 简介：
详细版本更新特性请查看国外的一篇文章：https://www.azul.com/109-new-features-in-jdk-10/

我在这里只简单的介绍 最热的一个特性：局部变量的类型推断

简单demo：
```
var list = new ArrayList<String>();  // infers ArrayList<String>
var stream = list.stream();          // infers Stream<String>
```

是不是很像js？但是我们要知道，java依旧是强类型语言，只是jvm帮助我们做了变量类型推断。


好了开始正文，java10需要最新版本的IDEA支持。否则JDK你都加不进去。

#####所以我们先下载最新版的idea：


最新IDEA下载地址：https://www.jetbrains.com/idea/download/#section=windows

安装好后，启动IDEA。

随便进一个项目，然后打开项目架构 快捷键 ctrl + shift + alt + s

##### 添加SDK
![](https://upload-images.jianshu.io/upload_images/5786888-ecc9c06ea8de4f52.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##### 给项目适配JDK10

![](https://upload-images.jianshu.io/upload_images/5786888-5007b60ac0af192a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##### 测试
我们都听说过java10的新特性吧。最热的一个特性是 用var 来声明变量，是的，就像js一样。

那接下来直接进入让java粉迫不及待的场面。
```
/**
 * Created by Fant.J.
 */
public class NewJavaTest {
    public static void main(String[] args) {

        var list = new ArrayList<>();
        list.add(1);
        list.add("fantj");
        list.add(1.00);


        list.forEach(System.out::println);
    }
}

```
控制台输出：
```
1
fantj
1.0
```

我在这里故意不给ArrayList 赋泛型，因为它默认就是Object，这样我可以给list赋任意类型的变量，给人感觉很像弱类型语言，但是我们应该清楚是因为jvm帮我们猜测了类型。


最后附上java10的官方更新文档：http://openjdk.java.net/jeps/286
