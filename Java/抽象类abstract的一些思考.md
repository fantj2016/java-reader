>最近有个小朋友问我什么是抽象类，他一直搞不懂抽象类，让我通俗的给他讲下。Emmmmmm，我问他：你用抽象类干过哪些事情。他的回答也在意料之中：压根没用过。这就加大了我的难度，我不知道从哪下手给他科普。

>然后我接着问：你对抽象类有什么了解呢？
他：我只知道它不能new。
我灵机一动：那你知道它为啥不能被new(实例化)呢？
他：好像是...(这乱略了，怕把你也搞懵)

### 问题
他说了一大堆，我听出来的几个问题就是：
1. 和接口概念混淆
2. 对抽象类的使用缺乏理解

### 答案
好了，那今天就简单的解决下这个问题。
1. 首先和interface的区别我之前有个文章：[Java中抽象类和接口有什么异同?](https://www.jianshu.com/p/b0cf5d770a86)
2. 对抽象类的使用缺乏理解，我认为就是用的太少，如果你用过抽象类工厂的开发模式，你一定会对抽象类有很深的理解。我简单了写了一个demo：

###### Phone.class
```
/**
 * Created by Fant.J.
 */
public abstract class Phone {
    void print(){
        System.out.println("this is a Phone abstract class method");
    }

    abstract void show();
}
```

###### PhoneMi.class
```
/**
 * Created by Fant.J.
 */
public class PhoneMi extends Phone {
    @Override
    void show() {
        System.out.println("my name is mi");
    }
}

```
###### 测试类Test.class
```
/**
 * Created by Fant.J.
 */
public class Test {

    public static void main(String[] args) {
        Phone phone = new Phone() {
            @Override
            void show() {
                System.out.println("my name is mi");
            }
        };
        phone.show();

        Phone phone1 = new PhoneMi();
        phone1.show();

        phone1.print();

    }

}

```
###### 执行结果
```
my name is mi
my name is mi
this is a Phone abstract class method
```

### 疑点解答：
这几个问题清楚了，为什么不能实例化abstract类的思路就很清晰了。

###### 1. Phone类里的print方法如何能被调用？
子类继承Phone后，会把print方法继承，通过子类对象就可以调用到print方法


###### 2. new Phone会有什么结果？
我在测试类里main方法的第一行就是在重写Phone类，实例化Phone是走不通的。重写这个类实质上也是对该类的一个继承实现。即，我重写了show方法，和PhoneMi里的show方法一模一样，那么我们就可以认为，我们通过实现内部类的形式实例化了一个新的子类对象 phone ，通过phone，我们也可以调用print方法，因为它的本质是Phone的子类实现。而不是抽象类本身。


###### 3. 为什么能new Phone的子类PhoneMi
我们实例化Phone的子类PhoneMi是行的通的，因为PhoneMi是前者的子类，作为抽象类的子类，必须要继承抽象类方法，只有重写了抽象类方法，才符合java规范(不会报错)，此时，我们就可以把PhoneMi当作一个普通的类使用，和普通类不一样的是：它可以调用抽象类(父类)的方法。

>小朋友继续问：那如果我写一个普通类，里面写上show方法和print方法，不是更加简洁吗？
我：emmmmm（心里一万个草泥马，我前面一堆是白说了吗?然后想了想是不是自己说的还不够清澈，思考了一会说）我认为抽象类本身就是在做设计模式，它的存在就是为了更好的设计而存在，你之前不是说过你从来没有用过它就可以完成任务工作麽？对，就是这个道理。如果你真正想解决冗余、效率、代码架构清晰等问题，你才会考虑设计模式。



好了，还看？最后一些客气的话还需要我贴出来吗？如果你现在对它还不够清楚，下方留言，看到后会回复。谢谢！


















