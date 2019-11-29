本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

HttpSession 是一个用户的session。session包含了用户信息。

当用户第一次访问网站时，有一个唯一的ID明确他的身份，这个ID保存在cookie或者一个请求参数里。
 
我们可以这样来获得session信息：
```
protected void doPost(HttpServletRequest request,HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession();
}
```

我们也可以保存这个session信息，以方便后面的读取。
```
session.setAttribute("userName", "theUserName");
```

既然我们保存了session，那怎么获取保存的session呢？
```
String userName = (String) session.getAttribute("userName");
```
这些session是保存在servlet容器中。

#### session丢失问题处理
当我们的网站有多个不通的项目服务时，用户请求别的项目服务会存在session找不到的问题，有几个######解决办法：
1. 不使用session。
2. 将session保存到数据库，然后别的服务来获取。
3. 用粘性的session，就是让用户的请求统一发到同一个服务器来处理。
