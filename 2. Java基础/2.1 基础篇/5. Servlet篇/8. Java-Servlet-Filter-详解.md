本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

Servlet Filter 可以拦截所有指向服务端的请求。

![Servlet Filter.png](http://upload-images.jianshu.io/upload_images/5786888-5cf65328e0331541.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果你想创建一个ServletFilter ，你需要实现一个接口`javax.servlet.Filter`

```
import javax.servlet.*;
import java.io.IOException;

public class SimpleServletFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                                    FilterChain filterChain)
    throws IOException, ServletException {

    }

    public void destroy() {
    }
}
```
servlet filter 一旦被装载，首先会调用它的init()方法。

当HTTP请求指向过滤器截获的服务端，过滤器可以检查URI，请求参数和请求头，并根据它决定是否要将请求阻止或转发到目标Servlet，JSP 等

具有拦截功能的方法是doFilter()

```
public void doFilter(ServletRequest request, ServletResponse response,
                     FilterChain filterChain)
throws IOException, ServletException {

    String myParam = request.getParameter("myParam");

    if(!"blockTheRequest".equals(myParam)){
        filterChain.doFilter(request, response);
    }
}
```
Notice how the doFilter() method checks a request parameter, myParam, to see if it equals the string "blockTheRequest". If not, the request is forwarded to the target of the request, by calling the filterChain.doFilter() method. If this method is not called, the request is not forwarded, but just blocked.

The servlet filter above just ignores the request if the request parameter myParam equals "blockTheRequest". You can also write a different response back to the browser. Just use the ServletResponse object to do so, just like you would inside a servlet.

You may have to cast the ServletResponse to a HttpResponse to obtain a PrintWriter from it. Otherwise you only have the OutputStream available via getOutputStream().

Here is an example:
这个doFilter()方法检查request参数：myParam，看它是不是和"blockTheRequest"相爱南瓜灯，如果不是，这个请求会被filterChain.doFilter()方法调用，如果没有被调用，线程挂起。如果相等，你能在ServletResponse对象中写一写返回给浏览器的数据。

必须将ServletResponse强制转换为HttpResponse才可以从中获取PrintWriter。 否则，只能通过getOutputStream（）获得OutputStream。

```
public void doFilter(ServletRequest request, ServletResponse response,
                     FilterChain filterChain)
throws IOException, ServletException {

    String myParam = request.getParameter("myParam");

    if(!"blockTheRequest".equals(myParam)){
        filterChain.doFilter(request, response);
        return;
    }

    HttpResponse httpResponse = (HttpResponse) httpResponse;
    httpResponse.getWriter().write("a different response... e.g in HTML");
}
```

### 在web.xml里配置过滤器/拦截器
```
<filter>
    <filter-name>myFilter</filter-name>
    <filter-class>servlets.SimpleServletFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>myFilter</filter-name>
    <url-pattern>*.simple</url-pattern>
</filter-mapping>
```
通过这种配置，所有URL以.simple结尾的请求都将被servlet过滤器拦截。
















