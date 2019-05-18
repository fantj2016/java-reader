本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

HttpServlet 类需要两个参数HttpRequest和HttpResponse。比如doGet方法
```
protected void doGet(
    HttpServletRequest request,
    HttpServletResponse response)
      throws ServletException, IOException {
}
```
我在这先介绍HttpResponse。
HttpResponse对象的目的是为了响应浏览器发送给Web应用程序的HTTP请求，表示Web应用程序发送回浏览器的HTTP响应。

HttpResponse对象有很多方法，我会介绍这里最常用的方法。如果你有兴趣其他方法你可以在JavaDoc中阅读。

#### Writing HTML

你能获取PrintWriter从HttpResponse对象中然后发送html到浏览器。这是个例子：
```
PrintWriter writer = response.getWriter();

writer.write("<html><body>GET/POST response</body></html>");
```
#### Headers

HttpRequest对象可以获取请求头，那么HttpResponse必须设置这些参数。例如：
```
response.setHeader("Header-Name", "Header Value");
```
#### Content-Type

这个请求头是响应给浏览器说明发送的内容类型。例如，HTML的内容类型是text/html。例如：
```
response.setHeader("Content-Type", "text/html");
```
#### 写文本
您可以编写文本回浏览器而不是HTML,
```
response.setHeader("Content-Type", "text/plain");

PrintWriter writer = response.getWriter();
writer.write("This is just plain text");
```
#### Content-Length
Content-Length告诉浏览器你的响应信息是多少个字节
```
response.setHeader("Content-Length", "31642");
```
#### 写二进制数据

你也能写二进制数据给浏览器，例如，你能发送一个图片、pdf文件等。
这样的话，contentType 应该是 image/png。
为了发送二进制数据，你就不能从response.gtWriter()方法中获取Writer了，因为它只是发送text类型的。
你能用OutputStream 从response.getOutputStream()方法中获取 输出流。例如：
```
OutputStream outputStream = response.getOutputStream();

outputStream.write(...);
```
#### URL重定向

你可以从servlet从其中重定向一个不同的URL发送给浏览器，当重定向的时候不能发送任何数据。例如：
```
response.sendRedirect("http://www.baidu.com");
```



