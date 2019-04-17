大概来讲：
* <? extends T>是"上界通配符"
* <? super T> 是"下界通配符"
#####1. 为什么要用通配符和边界？

使用泛型的过程中，经常出现一种很别扭的情况。比如按照题主的例子，我们有Fruit类，和它的派生类Apple类。
```java
public class Apple extends Fruit{
}

public class Fruit {

}
```
然后我在main方法里创建实例对象：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-629b9c7155be9162.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
逻辑上水果盘子当然可以装苹果，但实际上Java编译器不允许这个操作。会报错，“装苹果的盘子”无法转换成“装水果的盘子”。
```
Error:(9, 30) java: 不兼容的类型: com.generic.Plate<com.generic.Apple>无法转换为com.generic.Plate<com.generic.Fruit>
```

所以，就算容器里装的东西之间有继承关系，但容器之间是没有继承关系的。所以我们不可以把Plate<Apple>的引用传递给Plate<Fruit>.

为了让泛型用起来更舒服，Sun的大脑袋们就想出了<? extends T>和<? super T>的办法，来让”水果盘子“和”苹果盘子“之间发生关系。

![image.png](http://upload-images.jianshu.io/upload_images/5786888-3b1d8c4a2776ba95.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看出编译不再报错，程序正常运行。

#####? super T
这个也就不多解释了，上面的extend是说明实例对象必须是T的派生类。
则super则说明实例对象必须是T的基类。

###PECS原则
PECS（Producer Extends Consumer Super）原则
* 频繁往外读取内容的，适合用上界Extends。
* 经常往里插入的，适合用下界Super。

###总结
1)  参数写成：T<? super B>，对于这个泛型，?代表容器里的元素类型，由于只规定了元素必须是B的超类，导致元素没有明确统一的“根”（除了Object这个必然的根），所以这个泛型你其实无法使用它，对吧，除了把元素强制转成Object。所以，对把参数写成这样形态的函数，你函数体内，只能对这个泛型做插入操作，而无法读

2) 参数写成： T<? extends B>，由于指定了B为所有元素的“根”，你任何时候都可以安全的用B来使用容器里的元素，但是插入有问题，由于供奉B为祖先的子树有很多，不同子树并不兼容，由于实参可能来自于任何一颗子树，所以你的插入很可能破坏函数实参，所以，对这种写法的形参，禁止做插入操作，只做读取。
