>如何手写一个切片呢。假设我现在需要一个计时切片，我想把每一次调用服务锁花费的时间打印到控制台，该怎么做呢？

####拦截机制有三种：
######1. [过滤器（Filter）](https://www.jianshu.com/p/3960fd97a294)能拿到http请求，但是拿不到处理请求方法的信息。
######2. [拦截器（Interceptor）](https://www.jianshu.com/p/43e937436386)既能拿到http请求信息，也能拿到处理请求方法的信息，但是拿不到方法的参数信息。
######3. [切片（Aspect）](https://www.jianshu.com/p/38930293748d)能拿到方法的参数信息，但是拿不到http请求信息。
他们三个各有优缺点，需要根据自己的业务需求来选择最适合的拦截机制。
![拦截机制图](https://upload-images.jianshu.io/upload_images/5786888-821480ad23e4ce5f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

好了下面开始正文。

写一个切片就比较简单了，可以直接用现成的几个注解

* @Before()   //相当于拦截器的 preHandle
* @After()   //相当于postHandle
* @AfterThrowing   //相当于afterCompletion如果出现异常
* @Around()    包括以上三点，所以一般使用它(简洁)


######TimeAspect .java
```
/**
 * 时间切片类  （比拦截器好，能拿到具体参数）
 * Created by Fant.J.
 */
@Aspect
@Component
public class TimeAspect {

    @Around("execution(* com.laojiao.securitydemo.controller.UserController.*(..))")   //第一个* 表示任何返回值   第二个* 表示任何方法
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {    //pjp是一个 包含拦截方法信息的对象

        System.out.println("time aspect start");

        //参数数组
        Object[] args = pjp.getArgs();

        for (Object arg:args){
            System.out.println("arg is :" +arg);
        }

        long start = System.currentTimeMillis();

        //调用被拦截的方法
        Object object = pjp.proceed();

        System.out.println("time aspect 耗时："+(System.currentTimeMillis() - start));
        System.out.println("time aspect end");
        return object;

    }
}

```
代码解释：
* ` @Around("execution(* com.laojiao.securitydemo.controller.UserController.*(..))") `第一个* 表示任何返回值   第二个* 表示任何方法
* ProceedingJoinPoint (pjp)是一个 包含拦截方法信息的对象
*  `pjp.proceed(); `   调用被拦截的方法


####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

######底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)
