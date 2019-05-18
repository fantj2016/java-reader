本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

javax.servlet.http.HttpServlet类是比Simple Servlet示例中显示的GenericServlet稍高级的基类。

HttpServlet类读取HTTP请求，并确定请求是否为HTTP GET，POST，PUT，DELETE，HEAD等，并调用一个相应的方法。

为了响应例如 仅HTTP GET请求，您将扩展HttpServlet类，并仅覆盖doGet（）方法。 
比如：
```
public class SimpleHttpServlet extends HttpServlet {

  protected void doGet( HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException {

      response.getWriter().write("<html><body>GET response</body></html>");
  }
}

```

HttpServlet类也可以覆盖每个HTTP方法（GET，POST等）的方法。所有方法如下：
* doGet()
* doPost()
* doHead()
* doPut()
* doDelete()
* doOptions()
* doTrace()

大多数情况下，我们只想响应HTTP GET或POST请求，因此只需重写这两种方法。并且二者可以相互调用：
```
public class SimpleHttpServlet extends HttpServlet {

  protected void doGet( HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException {

      doPost(request, response);
  }

  protected void doPost( HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {

      response.getWriter().write("GET/POST response");
    }
}
```
我们尽可能使用HttpServlet而不是GenericServlet。 HttpServlet比GenericServlet更容易使用，并且有更多的便捷方法。








