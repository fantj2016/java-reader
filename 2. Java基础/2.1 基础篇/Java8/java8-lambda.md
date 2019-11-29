####1. lambda解决的问题：
* java不能函数式编程
* java匿名内部类只能单线程运行
* 匿名内部类缺陷：
  1. 语法复杂
  2. this容易混淆
  3. 不能引用外部变量
  4. 不能抽象化来控制流程
####2. Lambda实现匿名内部类
* 在lambda中，可以使用方法内部变量 ，但是不能改变它的值（i++报错）
```
package com.fantJ.JAVA_8;

/**
 * Created by Fant.J.
 * 2017/12/12 20:58
 */
public class Lambda_Runnable {
    public static void main(String[] args) {
        new Runnable(){
            @Override
            public void run(){
                System.out.println("匿名内部类实现Runnable接口");
            }
        }.run();


        int i = 1;
        Runnable r=()->{
            System.out.println("lambda实现Runnable接口");
            System.out.println("i="+i); //测试是否可以使用方法内部变量  （匿名内部类只可以使用常量）
            //i++;   //报错
        };
        r.run();
    }
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-034dab23f831bd69.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####3. Lambda实现自定义接口
```
package com.fantJ.JAVA_8;

/**
 * Created by Fant.J.
 * 2017/12/12 21:11
 */
public class Lambda_Interface {
    public static void main(String[] args) {
        new Message() {

            @Override
            public void message(String msg) {
                System.out.println(msg);   //打印传参 msg
            }
        }.message("用匿名内部类调用自定义的接口");

        Message m = (String msg) -> {
            System.out.println(msg);
        };
        m.message("lambda 调用自定义接口");
    }

    static interface Message {
        void message(String msg);
    }
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-2c596b4f8c7ff2fa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

