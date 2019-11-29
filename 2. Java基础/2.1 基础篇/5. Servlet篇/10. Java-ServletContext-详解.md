本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

ServletContext是一个获取web应用信息的对象，我们可以通过HttpRequest对象来得到它：
```
ServletContext context = request.getSession().getServletContext();
```
##### Context Attributes

就像session对象一样，你可以存储一些属性在servlet容器里。
```
context.setAttribute("someValue", "aValue");
```
获取到属性
```
Object attribute = context.getAttribute("someValue");
```
存储在ServletContext中的属性可用于应用程序中的所有Servlet以及request和session之间。 这意味着，这些属性可用于Web应用程序的所有访问者。 session属性仅供单个用户使用。

ServletContext属性仍然存储在servlet容器的内存中。 这就存在与服务器群集中的session属性相同的问题。
