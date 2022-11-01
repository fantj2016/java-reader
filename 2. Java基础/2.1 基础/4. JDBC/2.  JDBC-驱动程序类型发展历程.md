本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

有4种不同类型的JDBC驱动程序：
* 类型1：JDBC-ODBC桥驱动程序
* 类型2：Java +程序代码驱动程序
* 类型3：Java + Middleware转化驱动程序
* 类型4：Java驱动程序。
大多数类型都是4类型。

### JDBC-ODBC桥驱动程序
该类型的jdbc驱动运行原理是 在jdbc接口来调用odbc进行操作，这是最开始的做法。
科普：
jdbc：java database connectivity
odbc：open database connectivity 微软公司开放服务结构中有关数据库的一个组成部分

![JDBC-ODBC桥驱动](http://upload-images.jianshu.io/upload_images/5786888-477c43f0be81a7be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### Java +程序代码驱动程序
该类型与odbc桥驱动很类似，就是把odbc的方式换成了程序代码。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-8dbc4fba2d44ebbd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### Java + Middleware转化驱动程序
该驱动是将JDBC接口调用发送到中间服务器的全部Java驱动程序。 中间服务器然后代表JDBC驱动程序连接到数据库。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-4d9bd1861ec98760.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### Java驱动程序。即jdbc
JDBC驱动程序是直接连接到数据库的Java驱动程序。 它针对特定的数据库产品实施。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-f75e4fb26c497061.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
