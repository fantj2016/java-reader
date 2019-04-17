###第一步：添加maven依赖
```
<!-- https://mvnrepository.com/artifact/taglibs/standard -->
<dependency>
    <groupId>taglibs</groupId>
    <artifactId>standard</artifactId>
    <version>1.1.2</version><
/dependency>
<!-- https://mvnrepository.com/artifact/jstl/jstl -->
<dependency><
    groupId>jstl</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>

```
如果不是maven项目，请百度下jstl 的相关jar包，加载到项目里
###第二步：在jsp中引入支持
在jsp文件的顶部加入以下内容：
```
	1. <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
	2. <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
	3. <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
```
###第三步：实战常见用法
#####1. <c:forEach>实例
  ```
<table class="list_tab">
    <tr>
        <th class="tc">ID</th>
        <th>标题</th>
        <th>文章类别</th>
        <th>更新时间</th>
        <th>操作</th>
    </tr>
    <tr>
        <c:forEach items="${page.list}" var="arts">   /*items用来获取后来传来的集合，var是对集合的重命名（方便下面调用）*/
            <td class="tc">${arts.id}</td>      
            <td>
            <a href="${APP_PATH}/art/sel/${arts.id}.html">${arts.title}</a>
        </td>
        <td>${arts.type.name}</td>
        <td><fmt:formatDate value="${arts.updatetime}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>   /*用fmt标签修改时间格式*/
        <td>
            <a href="${APP_PATH}/art/edit/${arts.id}.html">修改</a>
            <a href="${APP_PATH}/art/del/${arts.id}.html">删除</a>
        </td>
    </tr>
    </c:forEach>
</table>
  ```
效果图：![效果图.png](http://upload-images.jianshu.io/upload_images/5786888-50ea5513ff6d59a9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####2. 在jsp里面简单获取js当前时间
  1.在最顶部添加引用标签
     ` <jsp:useBean id="now" class="java.util.Date" scope="page"/>`
  2.jsp网页中获取
          `现在是 :<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/>`

#####3. jstl修改时间格式
1.先在jsp上引用taglib标签
`<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>`
2.在需要修改格式的地方，用<fmt>对格式进行修改
`现在是 :<fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/>`
   value值为时间值，pattern为时间显示格式

* 以上紧紧是本人开发遇到所需的，不是全部的jstl。有什么重大遗漏请邮箱通知844072586@qq.com。谢谢
