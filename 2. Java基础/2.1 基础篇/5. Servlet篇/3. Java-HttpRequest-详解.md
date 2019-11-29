本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

HttpServlet 类需要两个参数HttpRequest和HttpResponse。比如doGet方法
```
protected void doGet(
    HttpServletRequest request,
    HttpServletResponse response)
      throws ServletException, IOException {
}
```
那我在这先介绍HttpRequest。

HttpRequest对象的目的是代替浏览器把Http请求发送给web应用，因此，任何浏览器能发送的，HttpRequest都可以接受到。

HttpRequest对象有很多方法，这里只介绍重要的，剩余的有兴趣的话你可以自己看JavaDoc。

##### 参数
请求参数是从浏览器连同请求一起发送的参数。 请求参数通常作为URL的一部分（在“查询字符串”中）或作为HTTP请求主体的一部分发送。 例如：
```
www.baidu.com?param1=hello&param2=world
```
在这个URL中，有两个参数
```
param1=hello
param2=world
```
你可以用HttpRequest访问这些参数，例如:
```
protected void doGet(HttpServletRequest request,HttpServletResponse response)
      throws ServletException, IOException {

    String param1 = request.getParameter("param1");
        String param2 = request.getParameter("param2");

}
```
通常，如果浏览器发送HTTP GET请求，则参数将包含在URL中的查询字符串中。 如果浏览器发送HTTP POST请求，则参数将包含在HTTP请求的正文部分(form)中。

##### 请求头

请求头是浏览器伴随HttpRequest发送的的一个'键值对',请求标题包含关于例如 使用什么浏览器软件，浏览器能够接收哪些文件类型等等。
我们可以使用HttpRequest对象来接受请求头：
```
String contentLength = request.getHeader("Content-Length");    
```
Content-Length头包含在HTTP请求正文中发送的字节数，以防浏览器发送HTTP POST请求。 如果浏览器发送HTTP GET请求，则不使用Content-Length标头，并且上述代码将返回null。

##### 输入流
如果浏览器发送HTTP POST请求，请求参数和其他潜在数据将发送到HTTP请求正文中的服务器。 它不一定是在HTTP请求主体中发送的请求参数。 它可能几乎是任何数据，如文件或SOAP请求（Web服务请求）。

为了可以访问HTTP POST请求的主题内容，我们可以用inputStream。这是一个例子：
```
InputStream requestBodyInput = request.getInputStream();   
```
注意：在调用任何getParameter（）方法之前，您必须调用此方法，因为在HTTP POST请求上调用getParameter（）方法将导致servlet引擎 解析 HTTP请求主体 以 获取参数。 一旦分析完毕，就无法再以原始字节流的形式访问主体。

##### Session
也可以从HttpRequest对象获取Session对象。

Session对象可以在请求之间保存关于给定用户的信息。 因此，如果您在一个请求期间将对象设置为会话对象，则它可供您在同一会话时间范围内的任何后续请求和期间读取。

这里是如何获取Session对象：
```
HttpSession session = request.getSession();
```
##### ServletContext

也可以从HttpRequest对象中获取ServletContext对象。ServletContext包含一些web应用信息。例如，能获取到web.xml文件里的一些参数，也能将request请求转发给别的Servlet，也能在ServletContext里存储一些应用参数。
这是获取ServletContext的例子：
```
ServletContext context = request.getSession().getServletContext();    
```
可以看到，我们要先获取Session对象，再获取ServletContext。





