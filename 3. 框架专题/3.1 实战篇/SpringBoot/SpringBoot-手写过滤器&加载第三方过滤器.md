>如何手写一个过滤器呢。假设我现在需要一个计时过滤器，我想把每一次调用服务锁花费的时间打印到控制台，我该怎么做呢？


####拦截机制有三种：
######1. [过滤器（Filter）](https://www.jianshu.com/p/3960fd97a294)能拿到http请求，但是拿不到处理请求方法的信息。
######2. [拦截器（Interceptor）](https://www.jianshu.com/p/43e937436386)既能拿到http请求信息，也能拿到处理请求方法的信息，但是拿不到方法的参数信息。
######3. [切片（Aspect）](https://www.jianshu.com/p/38930293748d)能拿到方法的参数信息，但是拿不到http请求信息。
他们三个各有优缺点，需要根据自己的业务需求来选择最适合的拦截机制。
![拦截机制图](https://upload-images.jianshu.io/upload_images/5786888-821480ad23e4ce5f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

好了下面开始正文。

###本教程大概目录：
#####1. 手写过滤器
#####2. 加载第三方过滤器

好了，开始正文。
###1. 手写过滤器
特别容易，继承Filter ，然后根据需求重写它的三个方法。
```
/**
 * TimeFilter  计时过滤器
 * Created by Fant.J.
 */
@Component
public class TimeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        System.out.println("time filter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("time filter start");
        long start  = new Date().getTime();
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("耗时："+(new Date().getTime() - start));
        System.out.println("time filter finish");
    }

    @Override
    public void destroy() {
        System.out.println("time filter destroy");
    }
}

```
其中 init方法是初始化方法，最先执行。
然后执行doFilter方法，熟悉servlet的应该都知道，它其实就是调用业务。
最后destroy方法，是最后执行的。


然后我们启动服务，随便调用一个controller，控制台打印如下：
![](https://upload-images.jianshu.io/upload_images/5786888-c2da4bacbf6a2a9f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###2. 加载第三方过滤器

一般在开发的时候，我们可能用到第三方的过滤器，我们不可能在其源码上添加`@Component`注解。所以我们必须写一个配置类来引入它。


下面我们把上面的例子当作第三方过滤器，把TimeFilter类上面的@Component注解去掉。做一个模拟练习。

```

/**
 * 引入第三方过滤器 将其放入spring容器
 * Created by Fant.J.
 */
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean timeFilter(){
        //创建 过滤器注册bean
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      
        TimeFilter timeFilter = new TimeFilter();
        
        registrationBean.setFilter(timeFilter);

        List urls = new ArrayList();
        urls.add("/*");   //给所有请求加过滤器
        //设置 有效url
        registrationBean.setUrlPatterns(urls);

        return registrationBean;
    }
}

```

注意一定要去掉TimeFilter类上面的@Component注解，否则过滤器会失效。



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
