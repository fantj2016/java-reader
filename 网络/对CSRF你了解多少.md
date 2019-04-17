>CSRF（Cross-site request forgery）跨站请求伪造，也被称为“One Click Attack”或者Session Riding，通常缩写为CSRF或者XSRF，是一种对网站的恶意利用。尽管听起来像跨站脚本（[XSS](https://baike.baidu.com/item/XSS)），但它与XSS非常不同，XSS利用站点内的信任用户，而CSRF则通过伪装来自受信任用户的请求来利用受信任的网站。与[XSS](https://baike.baidu.com/item/XSS)攻击相比，CSRF攻击往往不大流行（因此对其进行防范的资源也相当稀少）和难以防范，所以被认为比[XSS](https://baike.baidu.com/item/XSS)
更具危险性。   --摘自百度百科

我在这里给大家举个形象的例子来帮助大家理解。

攻击通过在授权用户访问的页面中包含链接或者脚本的方式工作。例如：一个网站用户Bob可能正在浏览聊天论坛，而同时另一个用户Alice也在此论坛中，并且后者刚刚发布了一个具有Bob银行链接的图片消息。设想一下，Alice编写了一个在Bob的银行站点上进行取款的**form提交的链接**，并将此链接作为**图片src（获取图片就是GET请求，对方就能抓取到你的浏览器cookie和更多的请求头）**。如果Bob的银行在cookie中保存他的授权信息，并且此**cookie没有过期**，那么当Bob的浏览器尝试装载图片时将提交这个取款form和他的**cookie**，这样在没经Bob同意的情况下便授权了这次事务。

CSRF是一种依赖web浏览器的、被混淆过的代理人攻击（deputy attack）。在上面银行示例中的代理人是Bob的web浏览器，它被混淆后误将Bob的授权直接交给了Alice使用。

下面是CSRF的常见特性：
依靠用户标识危害网站
利用网站对用户标识的信任
欺骗用户的浏览器发送HTTP请求给目标站点
另外可以通过IMG标签会触发一个GET请求，可以利用它来实现CSRF攻击。

防范：
* 使用图片的CSRF攻击常常出现在网络论坛中，因为那里允许用户发布图片而不能使用JavaScript。
* 当我们用鼠标在Blog/BBS/WebMail点击别人留下的链接的时候，说不定一场精心准备的CSRF攻击正等着我们。

Springsecurity 和shiro都有对csrf的验证模块，帮你更好的解决项目开发的这种风险。


