###一、HttpServletRequest介绍
　　HttpServletRequest对象代表客户端的请求，当客户端通过HTTP协议访问服务器时，HTTP请求头中的所有信息都封装在这个对象中，通过这个对象提供的方法，可以获得客户端请求的所有信息。
###二、Request常用方法
#####2.1、获得客户机信息

　　getRequestURL方法返回客户端发出请求时的完整URL。
　　getRequestURI方法返回请求行中的资源名部分。
　　getQueryString 方法返回请求行中的参数部分。
　　getPathInfo方法返回请求URL中的额外路径信息。额外路径信息是请求URL中的位于Servlet的路径之后和查询参数之前的内容，它以“/”开头。
　　getRemoteAddr方法返回发出请求的客户机的IP地址。
　　getRemoteHost方法返回发出请求的客户机的完整主机名。
　　getRemotePort方法返回客户机所使用的网络端口号。
　　getLocalAddr方法返回WEB服务器的IP地址。
　　getLocalName方法返回WEB服务器的主机名。

```
/**
          * 1.获得客户机信息
         */
         String requestUrl = request.getRequestURL().toString();//得到请求的URL地址
         String requestUri = request.getRequestURI();//得到请求的资源
         String queryString = request.getQueryString();//得到请求的URL地址中附带的参数
         String remoteAddr = request.getRemoteAddr();//得到来访者的IP地址
         String remoteHost = request.getRemoteHost();
         int remotePort = request.getRemotePort();
         String remoteUser = request.getRemoteUser();
         String method = request.getMethod();//得到请求URL地址时使用的String           
         pathInfo = request.getPathInfo();
         String localAddr = request.getLocalAddr();//获取WEB服务器的IP地址
         String localName = request.getLocalName();//获取WEB服务器的主机名
         response.setCharacterEncoding("UTF-8");//设置将字符以"UTF-8"编码输出到客户端浏览器
         //通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
         response.setHeader("content-type", "text/html;charset=UTF-8");
```
#####2.2、获得客户机请求头

getHeader(string name)方法:String 
getHeaders(String name)方法:Enumeration 
getHeaderNames()方法
```
             Enumeration<String> reqHeadInfos = request.getHeaderNames();//获取所有的请求头
 while (reqHeadInfos.hasMoreElements()) {
             String headName = (String) reqHeadInfos.nextElement();
             String headValue = request.getHeader(headName);//根据请求头的名字获取对应的请求头的值
             out.write(headName+":"+headValue);
             out.write("<br/>");
         }
```
#####2.3、获得客户机请求参数(客户端提交的数据)

getParameter(String)方法(常用)
getParameterValues(String name)方法(常用)
getParameterNames()方法(不常用)
getParameterMap()方法(编写框架时常用)
```
===================getParameter()=====================
String userid = request.getParameter("userid");//获取填写的编号，userid是文本框的名字、
===================getParameterValues()=====================
 //获取选中的兴趣，因为可以选中多个值，所以获取到的值是一个字符串数组，因此需要使用getParameterValues方法来获取
String[] insts = request.getParameterValues("inst");
===================getParameterNames()=====================
Enumeration<String> paramNames = request.getParameterNames();//获取所有的参数名
         while (paramNames.hasMoreElements()) {
             String name = paramNames.nextElement();//得到参数名
             String value = request.getParameter(name);//通过参数名获取对应的值
             System.out.println(MessageFormat.format("{0}={1}", name,value));
         }
```
###三、request接收表单提交中文参数乱码问题
####3.1、以POST方式提交表单中文参数的乱码问题
```
  /**
          * 客户端是以UTF-8编码传输数据到服务器端的，所以需要设置服务器端以UTF-8的编码进行接收，否则对于中文数据就会产生乱码
          */
         request.setCharacterEncoding("UTF-8");
         String userName = request.getParameter("userName");
```
#####3.2、以GET方式提交表单中文参数的乱码问题
```
/**
  *
  * 对于以get方式传输的数据，request即使设置了以指定的编码接收数据也是无效的，默认的还是使用ISO8859-1这个字符编码来接收数据
   */
String name = request.getParameter("name");//接收数据
name =new String(name.getBytes("ISO8859-1"), "UTF-8") ;//获取request对象以ISO8859-1字符编码接收到的原始数据的字节数组，然后通过字节数组以指定的编码构建字符串，解决乱码问题
```
###四、Request对象实现请求转发
#####4.1、请求转发的基本概念

　　请求转发：指一个web资源收到客户端请求后，通知服务器去调用另外一个web资源进行处理。
　　请求转发的应用场景：MVC设计模式

　　在Servlet中实现请求转发的两种方式：

　　1、通过ServletContext的getRequestDispatcher(String path)方法，该方法返回一个RequestDispatcher对象，调用这个对象的forward方法可以实现请求转发。
```
 RequestDispatcher reqDispatcher =this.getServletContext().getRequestDispatcher("/test.jsp");
 reqDispatcher.forward(request, response);
//或者
request.getRequestDispatcher("/test.jsp").forward(request, response);
```
request对象作为一个域对象(Map容器)使用时，主要是通过以下的四个方法来操作

* setAttribute(String name,Object o)方法，将数据作为request对象的一个属性存放到request对象中，例如：request.setAttribute("data", data);
* getAttribute(String name)方法，获取request对象的name属性的属性值，例如：request.getAttribute("data")
* removeAttribute(String name)方法，移除request对象的name属性，例如：request.removeAttribute("data")
* getAttributeNames方法，获取request对象的所有属性名，返回的是一个，例如：Enumeration<String> attrNames = request.getAttributeNames();
###4.2、请求重定向和请求转发的区别

1. 一个web资源收到客户端请求后，通知**服务器**去调用另外一个web资源进行处理，称之为请求转发/307。
2. 一个web资源收到客户端请求后，通知**浏览器**去访问另外一个web资源进行处理，称之为请求重定向/302。
