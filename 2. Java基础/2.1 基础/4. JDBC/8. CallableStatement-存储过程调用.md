本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

CallableStatement 和 PreparedStatement用法特别相似，只是CallableStatement 可以用来调用存储过程。

### 存储过程简介调用简介
>SQL语句需要先编译然后执行，而存储过程（Stored Procedure）是一组为了完成特定功能的SQL语句集，经编译后存储在数据库中，用户通过指定存储过程的名字并给定参数（如果该存储过程带有参数）来调用执行它。

>存储过程是可编程的函数，在数据库中创建并保存，可以由SQL语句和控制结构组成。当想要在不同的应用程序或平台上执行相同的函数，或者封装特定功能时，存储过程是非常有用的。数据库中的存储过程可以看做是对编程中面向对象方法的模拟，它允许控制数据的访问方式。

##### 存储过程的优点：

(1). 增强SQL语言的功能和灵活性：存储过程可以用控制语句编写，有很强的灵活性，可以完成复杂的判断和较复杂的运算。

(2). 标准组件式编程：存储过程被创建后，可以在程序中被多次调用，而不必重新编写该存储过程的SQL语句。而且数据库专业人员可以随时对存储过程进行修改，对应用程序源代码毫无影响。

(3). 较快的执行速度：如果某一操作包含大量的Transaction-SQL代码或分别被多次执行，那么存储过程要比批处理的执行速度快很多。因为存储过程是预编译的。在首次运行一个存储过程时查询，优化器对其进行分析优化，并且给出最终被存储在系统表中的执行计划。而批处理的Transaction-SQL语句在每次运行时都要进行编译和优化，速度相对要慢一些。

(4). 减少网络流量：针对同一个数据库对象的操作（如查询、修改），如果这一操作所涉及的Transaction-SQL语句被组织进存储过程，那么当在客户计算机上调用该存储过程时，网络中传送的只是该调用语句，从而大大减少网络流量并降低了网络负载。

(5). 作为一种安全机制来充分利用：通过对执行某一存储过程的权限进行限制，能够实现对相应的数据的访问权限的限制，避免了非授权用户对数据的访问，保证了数据的安全。

### MySQL的存储过程
存储过程是数据库的一个重要的功能，MySQL 5.0以前并不支持存储过程，这使得MySQL在应用上大打折扣。好在MySQL 5.0开始支持存储过程，这样即可以大大提高数据库的处理速度，同时也可以提高数据库编程的灵活性。

### 1. 创建MySQL存储过程
```
DELIMITER //
create procedure findById(IN pid INTEGER)
BEGIN
SELECT * FROM `user` WHERE id= pid;
END //
DELIMITER;
```
### 2. 调用存储过程
```
package com.jdbc;

import java.sql.*;

/**
 * Created by Fant.J.
 * 2018/3/5 20:14
 */
public class CallableStatementTest {
    static String url = "jdbc:mysql://localhost:3306/user";
    static String user = "root";
    static String password = "root";

    public static void main(String[] args) {


        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            String sql = "CALL findById(?)";
            CallableStatement stmt = connection.prepareCall(sql);
            stmt.setInt(1,27);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String birthday = resultSet.getString("birthday");
                String sex = resultSet.getString("sex");
                String address = resultSet.getString("address");

                System.out.println("  " + username + "  " + birthday + " " + sex
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

```
 Fant.J reUseTest  2017-04-20 男 xxxx
```

### 3. 全面科普
通过前面的讲解和例子，我想你肯定已经了解调用存储过程到底是个什么东西，但是你肯定也能看到，这例子只是一个特例，所以我在这里把 调用存储代码中的sql做个详解。

##### 存储过程参数详解
* in：往过程里传参。    （参考我创建存储过程中的代码(IN pid INTEGER)）
* out：往过程外传参。
* inout：in and out

相对于oracle数据库来说，MySQL的存储过程相对功能较弱，使用较少。

创建的存储过程保存在数据库的数据字典中。
