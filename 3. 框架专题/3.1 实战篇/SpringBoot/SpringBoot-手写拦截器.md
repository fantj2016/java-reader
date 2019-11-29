>如何手写一个拦截器呢。假设我现在需要一个计时拦截器，我想把每一次调用服务锁花费的时间打印到控制台，我该怎么做呢？

####拦截机制有三种：
######1. [过滤器（Filter）](https://www.jianshu.com/p/3960fd97a294)能拿到http请求，但是拿不到处理请求方法的信息。
######2. [拦截器（Interceptor）](https://www.jianshu.com/p/43e937436386)既能拿到http请求信息，也能拿到处理请求方法的信息，但是拿不到方法的参数信息。
######3. [切片（Aspect）](https://www.jianshu.com/p/38930293748d)能拿到方法的参数信息，但是拿不到http请求信息。
他们三个各有优缺点，需要根据自己的业务需求来选择最适合的拦截机制。
![拦截机制图](https://upload-images.jianshu.io/upload_images/5786888-821480ad23e4ce5f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

好了下面开始正文。

###手写拦截器实战

```

/**
 * Time 时间拦截器（比时间过滤器准））
 * Created by Fant.J.
 */
@Component
public class TimeInterceptor  implements HandlerInterceptor {
    //controller 调用之前被调用
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        System.out.println("preHandle");

        System.out.println(((HandlerMethod)handler).getBean().getClass().getName());
        System.out.println(((HandlerMethod)handler).getMethod().getName());
        httpServletRequest.setAttribute("startTime",System.currentTimeMillis());
        return true;
    }

    //controller 调用之后被调用，如果有异常则不调用
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        System.out.println("postHandle");

        long startTime = (long) httpServletRequest.getAttribute("startTime");
        System.out.println("时间拦截器耗时:"+(System.currentTimeMillis() -startTime));
    }

    //controller 调用之后被调用，有没有异常都会被调用,Exception 参数里放着异常信息
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("afterCompletion");
        long startTime = (long) httpServletRequest.getAttribute("startTime");
        System.out.println("时间拦截器耗时:"+(System.currentTimeMillis() -startTime));
    }
}

```
代码解释：
1. 其中，preHandle()方法再controller 调用之前被调用
2. postHandle()在controller 调用之后被调用，如果有异常则不调用
3. afterCompletion()在controller 调用之后被调用，有没有异常都会被调用,Exception 参数里放着异常信息。

但是值写这个处理拦截器还不行，还需要进一步的配置，请看下面一段代码：

```
/**
 * 引入第三方过滤器 将其放入spring容器
 * Created by Fant.J.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

//    注入Time 拦截器
    @Autowired
    private TimeInterceptor timeInterceptor;
    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //往拦截器注册器里添加拦截器
        registry.addInterceptor(timeInterceptor);
    }

```

首先我们继承WebMvcConfigurerAdapter类，重写它的addInterceptors()方法，该方法是添加拦截器至Spring容器中。
然后调用拦截器注册器InterceptorRegistry 进行注册。


![](https://upload-images.jianshu.io/upload_images/5786888-85c4d0dd1b805232.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)




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
