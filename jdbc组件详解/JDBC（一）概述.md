本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

JDBC api包含以下几个核心部分：
* JDBC Drivers
* Connections
* Statements
* Result Sets

### JDBC Drivers -- 驱动

jdbc driver(驱动)是一个可以让你连接数据库的一个java类，它继承了很多个jdbc接口，当你用jdbc驱动的时候，它用标准的jdbc接口，所使用的具体JDBC驱动程序隐藏在JDBC接口后面。

### Connections -- 连接
一旦加载并初始化JDBC驱动程序，就需要连接到数据库。 您可以通过JDBC API和加载的驱动程序获取与数据库的连接。 与数据库的所有通信都通过连接进行。 应用程序可以同时向数据库打开多个连接。

### Statements -- 报告
Statement是用来对数据库执行语句。 可以使用几种不同类型的语句。 每条语句都对应一个增删改查。

### ResultSets -- 结果集

对数据库执行查询(statement操作)后，你会得到一个ResultSet。 然后可以遍历此ResultSet以读取查询的结果。

### 工作原理图
![jdbc工作原理](http://upload-images.jianshu.io/upload_images/5786888-15414920653dd3e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)










