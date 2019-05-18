本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

要从浏览器访问Java servlet，必须告诉servlet容器要部署哪些servlet以及要将servlet映射到哪个URL。 这是在Java Web应用程序的web.xml文件中完成的。

### 配置和映射Servlet
我们来看一个例子：

```
<?xml version="1.0" encoding="ISO-8859-1"?>

<!DOCTYPE web-app
    PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
    "http://java.sun.com/dtd/web-app_2_3.dtd">

<web-app>

  <servlet>
    <servlet-name>controlServlet</servlet-name>
    <servlet-class>com.xxx.ControlServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>controlServlet</servlet-name>
    <url-pattern>*.html</url-pattern>
  </servlet-mapping>
</web-app>    
```
首先配置servlet。 这是使用<servlet>元素完成的。 在这里给servlet一个名字，并写下servlet的类名。

其次，将servlet映射到URL或URL模式。 这在<servlet-mapping>元素中完成。 在上面的例子中，所有以.html结尾的URL都被发送到servlet。

我们一般还可能使用的servlet URL映射是：
```
/myServlet

/myServlet.do

/myServlet*
```
*是通配符，意思是任何文本。 如您所见，您可以使用通配符（*）将servlet映射到单个特定的URL或URL的模式。 你将使用什么取决于servlet的功能。

##### Servlet初始参数
您可以从web.xml文件将参数传递给servlet。 servlet的init参数只能由该servlet访问。 
如何在web.xml文件中配置它们的方法：
```
<servlet>
    <servlet-name>controlServlet</servlet-name>
    <servlet-class>com.xxxControlServlet</servlet-class>
    
        <init-param>
        <param-name>myParam</param-name>
        <param-value>paramValue</param-value>
        </init-param>
</servlet>
```
如何从Servlet内部读取init参数的方法 - 在Servlet init（）方法中：
```
public class SimpleServlet extends GenericServlet {

  protected String myParam = null;

  public void init(ServletConfig servletConfig) throws ServletException{
    this.myParam = servletConfig.getInitParameter("myParam");
  }

  public void service(ServletRequest request, ServletResponse response)
        throws ServletException, IOException {

    response.getWriter().write("<html><body>myParam = " +
            this.myParam + "</body></html>");
  }
}
```
servlet容器首次加载servlet时会调用servlets init（）方法。 在加载servlet之前，是不会允许访问该servlet。
#### Servlet加载启动
<servlet>元素有一个名为<load-on-startup>的子元素，您可以使用它来控制何时servlet容器应该加载servlet。 如果不指定<load-on-startup>元素，那么servlet容器通常会在第一个请求到达时加载servlet。

通过设置<load-on-startup>元素，可以告诉servlet容器在servlet容器启动后立即加载servlet。 请记住，在加载servlet时调用Servlet init（）方法。

这里是一个<load-on-startup>配置的例子：

```
<servlet>
    <servlet-name>controlServlet</servlet-name>
    <servlet-class>com.xxx.xxx.ControlServlet</servlet-class>
    <init-param><param-name>container.script.static</param-name>
                <param-value>/WEB-INF/container.script</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```
<load-on-startup>元素中的数字告诉servlet容器应该按什么顺序加载servlet。 较低的数字首先被加载。 如果该值为负数或未指定，则servlet容器可以随时加载servlet。

#### Context 参数
可以设置一些上下文参数，这些参数可以从应用程序中的所有servlet中读取。
那该如何配置呢？
```
<context-param>
    <param-name>myParam</param-name>
    <param-value>the value</param-value>
</context-param>
```
如果获得这些参数呢？
```
String myContextParam =
        request.getSession()
               .getServletContext()
               .getInitParameter("myParam");
```








