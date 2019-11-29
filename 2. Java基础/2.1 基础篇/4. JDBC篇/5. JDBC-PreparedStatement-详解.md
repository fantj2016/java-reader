本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

PreparedStatement 是一个特殊的Statement对象，如果我们只是来查询或者更新数据的话，最好用PreparedStatement代替Statement，因为它有以下有点：
* 简化Statement中的操作
* 提高执行语句的性能
* 可读性和可维护性更好
* 安全性更好。
       使用PreparedStatement能够预防SQL注入攻击，所谓SQL注入，指的是通过把SQL命令插入到Web表单提交或者输入域名或者页面请求的查询字符串，最终达到欺骗服务器，达到执行恶意SQL命令的目的。注入只对SQL语句的编译过程有破坏作用，而执行阶段只是把输入串作为数据处理，不再需要对SQL语句进行解析，因此也就避免了类似select * from user where name='aa' and password='bb' or 1=1的sql注入问题的发生。

Statement 和 PreparedStatement之间的关系和区别.
- 关系：PreparedStatement继承自Statement,都是接口
- 区别：PreparedStatement可以使用占位符，是预编译的，批处理比Statement效率高    

### 创建一个PreparedStatement
PreparedStatement对象的创建也同样离不开 DriverManger.getConnect()对象，因为它也是建立在连接到数据库之上的操作。
```
connection = DriverManager.getConnection(url,user,password);
String sql = "update user set username=? where id = ?";
PreparedStatement preparedStatement = connection.prepareStatement(sql);
```

### 往PreparedStatement里写入参数

看上面那个sql 字符串，中间有几个?，它在这里有点占位符的意思，然后我们可以通过PreparedStatement的setString(),等方法来给占位符进行赋值，使得sql语句变得灵活。
```
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url,user,password);
            String sql = "update user set username=? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,"Fant.J");
            preparedStatement.setInt(2,27);
```
参数中的第一个参数分别是1和2，它代表的是第几个问号的位置。如果sql语句中只有一个问号，那就不用声明这个参数。

### 执行PreparedStatement
###### 执行查询
如果是执行查询数据库的话，也像Statement对象执行excuteQuery()一样返回一个ResultSet结果集。这里就不多详述：
```
            String sql = "select * from user";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String birthday = resultSet.getString("birthday");
                String sex = resultSet.getString("sex");
                String address = resultSet.getString("address");

                System.out.println("  " + username + " " + birthday + " " + sex
                        + " " + address);
            }
```
```
  Fant.J 2017-04-20 男 xxxx
```

### 复用PreparedStatement
什么叫复用，就是一次实例化，多次使用。
```
            preparedStatement.setString(1,"Fant.J");
            preparedStatement.setInt(2,27);

            int result = preparedStatement.executeUpdate();
            preparedStatement.setString(1,"Fant.J reUseTest");
            preparedStatement.setInt(2,27);
            preparedStatement.executeUpdate();
```
那如何在同一个PreparedStatement对象中进行查询操作呢
```
            String sql2 = "select * from user";

            ResultSet resultSet = preparedStatement.executeQuery(sql2);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String birthday = resultSet.getString("birthday");
                String sex = resultSet.getString("sex");
                String address = resultSet.getString("address");

                System.out.println("  " + username + " " + birthday + " " + sex
                        + " " + address);
            }
```
```
  Fant.J reUseTest 2017-04-20 男 xxxx
```
### 完整代码
```
package com.jdbc;

import java.sql.*;

/**
 * Created by Fant.J.
 * 2018/3/3 13:35
 */
public class PreparedStatementTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/user";
        String user = "root";
        String password = "root";

        Connection connection =null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url,user,password);
            String sql = "update user set username=? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,"Fant.J");
            preparedStatement.setInt(2,27);

            int result = preparedStatement.executeUpdate();
            preparedStatement.setString(1,"Fant.J reUseTest");
            preparedStatement.setInt(2,27);
            preparedStatement.executeUpdate();


            String sql2 = "select * from user";

            ResultSet resultSet = preparedStatement.executeQuery(sql2);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String birthday = resultSet.getString("birthday");
                String sex = resultSet.getString("sex");
                String address = resultSet.getString("address");

                System.out.println("  " + username + " " + birthday + " " + sex
                        + " " + address);
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}

```

### PreparedStatement性能分析
数据库解析SQL字符串需要时间，并为其创建查询计划。查询计划是分析数据库如何以最有效的方式执行查询。

如果为每个查询或数据库更新提交新的完整SQL语句，则数据库必须解析SQL，并为查询创建查询计划。通过重用现有的PreparedStatement，可以为后续查询重用SQL解析和查询计划。这通过减少每个执行的解析和查询计划开销来加速查询执行。

###### PreparedStatement有两个潜在的重用（复用）级别。

* JDBC驱动程序重新使用PreparedStatement。
* 数据库重用PreparedStatement。

首先，JDBC驱动程序可以在内部缓存PreparedStatement对象，从而重用PreparedStatement对象。这可能会节省一小部分PreparedStatement创建时间。

其次，高速缓存的解析和查询计划可能会跨Java应用程序（例如群集中的应用程序服务器）重复使用，并使用相同的数据库。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-1b23605f09e99f55.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



