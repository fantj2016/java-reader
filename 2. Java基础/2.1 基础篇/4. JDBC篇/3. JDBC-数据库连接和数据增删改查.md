本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

### 加载JDBC驱动
只需要在第一次连接数据库时加载，java6以后我们可以直接这样加载：
*我在本系列教程中用mysql示例。
1. 需要导入jar包：mysql-connector-java-5.0.8-bin.jar(版本和下载网站自己挑)
2. 如果是web程序，把jar包放到WebRoot/WEB-INF/lib/下
3. 如果是普通java项目，将jar包导入到自己项目的lib库中。
4. 然后加载驱动如下
```
Class.forName("com.mysql.jdbc.Driver");
```

### 打开连接
打开连接的话需要调用DriverManager类中的getConnection()方法,该方法有三个重载方法。如下所示
```
public static Connection getConnection(String url,
        java.util.Properties info) throws SQLException {

        return (getConnection(url, info, Reflection.getCallerClass()));
    }

public static Connection getConnection(String url,
        String user, String password) throws SQLException {
        java.util.Properties info = new java.util.Properties();

        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }

        return (getConnection(url, info, Reflection.getCallerClass()));
}


    public static Connection getConnection(String url)
        throws SQLException {

        java.util.Properties info = new java.util.Properties();
        return (getConnection(url, info, Reflection.getCallerClass()));
    }
```
大概看下它的参数名字应该知道它需要什么吧。在这里我只解释第一个方法。Properties info 这个参数其实也是user和password的打包。其实和方法二一样。

###### 实例：

```
public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/user";
        String user = "root";
        String password = "root";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // 1.加载驱动//com.mysql.jdbc.Driver
            Class.forName("com.mysql.jdbc.Driver");
            // 2.获取连接
            connection = DriverManager.getConnection(url, user, password);

            // 3.获取用于向数据库发送SQL的Statement对象
            statement = connection.createStatement();

            // 4.执行sql,获取数据
            resultSet = statement.executeQuery("SELECT * FROM user;");

            // 解析数据
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("username");
                String psd = resultSet.getString("birthday");
                String email = resultSet.getString("sex");
                String birthday = resultSet.getString("address");

                System.out.println(" " + name + " " + psd + " " + email
                        + " " + birthday);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            //5.关闭连接，释放资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                resultSet = null;
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                statement = null;
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                connection = null;
            }
        }
    }
}
```
### 数据查询
数据查询需要我们将sql发送给数据库，我们需要创建一个Statement对象。
```
Statement statement = connection.createStatement();
```
因为Statement对象可以执行sql
```
String sql = "select * from user";

ResultSet resultSet = statement.executeQuery(sql);
```
当执行一个sql查询的时候，会得道一个ResultSet对象，它里面放着查询的结果。
```
ResultSet resultSet = statement.executeQuery("SELECT * FROM user;");
```

那怎样获取user表中对应列的数据呢？
```
    resultSet .getString    ("columnName");
    resultSet .getLong      ("columnName");
    resultSet .getInt       ("columnName");
    resultSet .getDouble    ("columnName");
    resultSet .getBigDecimal("columnName");
```
或者通过第几列进行查询。
```
    resultSet .getString    (1);
    resultSet .getLong      (2);
    resultSet .getInt       (3);
    resultSet .getDouble    (4);
    resultSet .getBigDecimal(5);
```

如果你想知道对应列名的index值（位于第几列），可以这样
```
 int columnIndex = resultSet .findColumn("columnName");
```
### 数据修改/删除

为什么要把修改和删除放一块说呢，因为他们和查询调用不一样的方法。
查询我们调用executeQuery()方法，修改和删除我们需要用executeUpdate()方法。
举个简单的例子：
```
//更新数据（也叫修改数据）
String    sql       = "update user set name='fantj' where id=1";

int rowsAffected    = statement.executeUpdate(sql);    //rowsAffected是影响行数的意思

//删除数据
String    sql       = "delete from user where id=123";

int rowsAffected    = statement.executeUpdate(sql);
```




### 关闭连接
为了安全性和项目性能，我们尽量在执行完操作之后关闭连接（虽然高版本jvm会自动关闭它，但是这也需要检测浪费cpu资源）。
关闭连接分为三个部分。
* resultSet.close();
* statement.close();
* connection.close();

注意先后问题。
