本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

HTTP Cookies是Web应用程序可以存储在访问Web应用程序的用户的客户机上的一小部分数据。 通常最多4千字节的数据。我将解释如何设置，读取和删除Java Servlet（或JSP）内部的Cookie。

为什么有个或JSP呢，因为Jsp其实是Servlet演变来的，它具备Servlet的所有性质。但是它偏向于做模版引擎。

### Java Cookie 示例

你能用HttpServletResponse对象来设置cookie。
```
Cookie cookie = new Cookie("myCookie", "myCookieValue");

response.addCookie(cookie);
```
这个cookie通过name：myCookie；value：myCookieValue。被识别和鉴定。它的name属性必须是唯一，就像哈希地址一样。只要浏览器访问Web应用程序，它就会将存储在客户机上的Cookie提交给Web应用程序。 仅提交访问的Web应用程序存储的Cookie。 不需要提交来自其他Web应用程序的Cookie。

### 从浏览器发送的数据中获取cookie
```
Cookie[] cookies = request.getCookies();
```
可以看到它获取到的是一个cookie数组，所以我们要遍历这个数组来找到自己想要的一部分。它也有可能是null。
```
Cookie[] cookies = request.getCookies();

String userId = null;
for(Cookie cookie : cookies){
    if("uid".equals(cookie.getName())){
        userId = cookie.getValue();
    }
}
```
#### Cookie 的生命周期
cookie的生命周期是干啥用的呢，简单的说：是为了客户安全，你可以设置一个合适的生命周期来保护客户的隐私。
```
Cookie cookie = new Cookie("uid", "123");

cookie.setMaxAge(24 * 60 * 60);  // 24 hours. 

response.addCookie(cookie);
```
可以看到，设置了cookie后，需要在response对象上进行添加，它会告诉浏览器。我设置的生命周期是多少，浏览器会按照这个时间对cookie做销毁处理。

### 删除cookie
删除cookie是浏览器的任务，上面说到cookie的生命周期，但是总会有一些特殊的情况。比如你想立刻删除该用户的cookie，防止他进行违法操作。那可以给它的生命周期设置为0，如果你想让用户关闭浏览器的一瞬间删掉cookie，那你就给它的生命周期赋值为-1.
```
Cookie cookie = new Cookie("uid", "");

cookie.setMaxAge(0);   //或者-1

response.addCookie(cookie);
```
有的人想，如果以前有个生命周期还没结束的cookie，我再给它新赋值为0，那浏览器会执行哪个。
这个问题servlet官网有声明:If the browser already has a cookie stored with the name "uid", it will be deleted after receiving the cookie with the same name ("uid") with an expiration time of 0. 意思是优先执行新收到的cookie的生命周期。
























