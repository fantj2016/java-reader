本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

Java Servlets是Java的一项Web技术。 这是Java的第一个Web技术，许多新的Web技术已经到来。 尽管如此，Java Servlets非常有用。

Java Servlet是Java企业版（Java EE）的一部分。 您将需要在兼容Servlet的“Servlet容器”（例如Web服务器）中运行您的Java Servlet以使其工作。

### 什么是Servlet
Java Servlet是一个响应HTTP请求的Java对象。 它运行在一个Servlet容器内。 
![image.png](http://upload-images.jianshu.io/upload_images/5786888-14f76a199ddf69f7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
Servlet是Java Web应用程序的一部分。 一个Servlet容器可以同时运行多个Web应用程序，每个应用程序内部都有多个Servlet。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-af6d36921bae36c3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
Java Web应用程序可以包含除servlet之外的其他组件。 它也可以包含Java Server Pages（JSP），Java Server Faces（JSF）和Web Services。
### HTTP Request and Response
浏览器向Java Web服务器发送HTTP请求。 Web服务器检查请求是否用于servlet。 如果是，servlet容器就会通过请求。 然后，servlet容器将找出请求的servlet，并激活该servlet。 Servlet通过调用Servlet.service（）方法来激活。

一旦通过service（）方法激活了servlet，servlet将处理请求并生成一个响应。 该响应然后被发送回浏览器。

### Servlet容器
Java servlet容器通常运行在Java web服务器。一些常见的众所周知的,免费Java web服务器:
*   [Jetty](http://jetty.codehaus.org/jetty/)
*   [Tomcat](http://tomcat.apache.org/)

### Servlet的生命周期
一个servlet遵循一定的生命周期。 servlet生命周期由servlet容器管理。 生命周期包含以下步骤：
1. 加载Servlet类。
2. 创建Servlet的实例。
3. 调用Servlet的init（）方法。
4. 调用servlets service（）方法。
5. 调用servlet的destroy（）方法。
当servlet初始加载时，步骤1,2和3只执行一次。 默认情况下，只有在接收到第一个请求之前，servlet才会被加载。 当容器启动时，您可以强制容器加载servlet。 有关更多详细信息，请参阅web.xml Servlet配置。

第4步被执行多次 - 每个HTTP请求一次到servlet。
当servlet容器卸载servlet时执行第5步。
下面将更详细地描述每个步骤：
![servlet生命周期.png](http://upload-images.jianshu.io/upload_images/5786888-072a973b5cacc227.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

详细介绍各个生命周期工作:
##### 加载Servlet类
在调用servlet之前，servlet容器必须首先加载它的类定义。这与其他类加载完成一样。
##### 创建Servlet的实例
当加载servlet类时，servlet容器会创建一个servlet实例。

通常，只创建一个servlet实例，并且在同一个servlet实例上执行对该servlet的并发请求。不过，这真的取决于servlet容器的决定。但通常情况下，只创建一个实例。
##### 调用Servlets的init（）方法
当一个servlet实例被创建时，它的init（）方法被调用。 init（）方法允许servlet在处理第一个请求之前初始化自己。

您可以在web.xml文件中指定Servlet的init参数。有关更多详细信息，请参阅web.xml Servlet配置。
##### 调用Servlets service（）方法
对于接收到servlet的每个请求，调用servlets service（）方法。对于HttpServlet子类，通常会调用doGet（），doPost（）等方法之一。

只要servlet在servlet容器中处于活动状态，就可以调用service（）方法。因此，生命周期中的这一步可以多次执行。

##### 调用Servlets的destroy（）方法
当一个servlet被servlet容器销毁时，它的destroy（）方法被调用。这一步只执行一次，因为一个servlet只能销毁一次。

如果容器关闭，或者容器在运行时重新加载整个Web应用程序，则Servlet将由容器销毁。

### Servlet实战
一个Java Servlet只是一个普通的Java类实现的接口`javax.servlet.Servlet;`

最简单的方法是来继承这个接口的扩展类GenericServlet或HttpServlet。

```
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class SimpleServlet extends GenericServlet {

  public void service(ServletRequest request, ServletResponse response)
        throws ServletException, IOException {

       // do something in here
  }
}
```
当HTTP请求到达Web服务器时，Web服务器会调用您的Servlet的service（）方法。

然后service（）方法读取请求，并生成一个发送回客户端（例如浏览器）的响应。

示例:
```
public void service(ServletRequest request, ServletResponse response)
        throws ServletException, IOException {

  String yesOrNoParam = request.getParameter("param");

  if("yes".equals(yesOrNoParam) ){

      response.getWriter().write(
        "<html><body>You said yes!</body></html>");
  }

  if("no".equals(yesOrNoParam) ){
    
      response.getWriter().write(
        "<html><body>You said no!</body></html>");
  }
}
```
