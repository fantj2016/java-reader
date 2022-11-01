本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

RequestDispatcher 让两个servlet相互通信成为可能，就像是浏览器发送request请求一样。所以我们可以从HttpRequest中获取到RequestDispatcher对象
```
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

  RequestDispatcher dispatcher=request.getRequestDispatcher("/servlet2");
}
```

通过上面的代码，我们能获取到一个映射到/servlet2（URI） 的Servlet的 RequestDispatcher。

然后可以使用include()或者forward()方法来调用RequestDispatcher
```
dispatcher.forward(request, response);

dispatcher.include(request, response);
```

### request.getRequestDispatcher和response.sendRedirct区别
* getRequestDispatcher是服务器内部跳转，地址栏信息不变，只能跳转到web应用内的网页。 
* sendRedirect是页面重定向，地址栏信息改变，可以跳转到任意网页。 
###### getRequestDispatcher实例：
```
   request.setAttribute("lover", "fantj");  
   request.getSession().setAttribute("lovered", "fantj");  
  
   request.getRequestDispatcher("/third?name=fantj").forward(request, response);  
   或  
   request.getRequestDispatcher("third?name=fantj").forward(request, response);  
   或  
   this.getServletContext().getRequestDispatcher("/third?name=fantj").forward(request, response);  
```
```
//ThirdServlet  
   PrintWriter out = response.getWriter();  
   String name = request.getParameter("name");  
   out.println(request.getContextPath()+"<hr/>");  
   out.println("name="+name+"<hr/>");  
   out.println("lover="+request.getAttribute("lover")+"<hr/>");  
   out.println("lovered="+request.getSession().getAttribute("lovered")+"<hr/>");  
```
结果显示，地址栏信息仍为http://localhost:8080/Test/second(不变)，这三种方式**都可以传值**到第二个Servlet。 

##### sendRedirct实例
```
//SecondServlet
   request.setAttribute("lover", "fantj");  
   request.getSession().setAttribute("lovered", "fantj");  
  
   response.sendRedirect("third?name=fantj");  
   或  
   response.sendRedirect(request.getContextPath()+"/third?name=fantj");  
```
```
//ThirdServlet  
   PrintWriter out = response.getWriter();  
   String name = request.getParameter("name");  
   out.println(request.getContextPath()+"<hr/>");  
   out.println("name="+name+"<hr/>");  
   out.println("lover="+request.getAttribute("lover")+"<hr/>");  
   out.println("lovered="+request.getSession().getAttribute("lovered")+"<hr/>");  
```
结果显示，地址栏信息变为http://localhost:8080/Test/third?name=fantj（发生改变），只有request.getAttribute("lover")获取不到值，session范围及url路径后的传值在第二个Servlet都可以获取到值。 

###### 总结
其实我们好好想想，他们需要建立的对象不同，一个是request一个是response。

**request**处理可以想象成帮用户再补充一些请求（根据自己项目的业务逻辑），所以它应该要获取到所有request发送过来的数据,并且让用户感觉不到我们有帮他处理一些事情（因为地址栏信息不变）。

**response**可以说是响应给用户一个新的东西，它不会在意request给它发送了什么参数，除非request把数据写在同一个容器下的session中或者url中。这也是response.sendRedirct可以给用户返回容器以外的URL（比如www.baidu.com）的原因。


