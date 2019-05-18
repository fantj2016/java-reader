本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

通过java.sql.DatabaseMetaData 接口，我们能获取到数据库的列表、列等信息。

DatabaseMetaData 接口包含了许多方法，这里值介绍常用的。

### 获取 DatabaseMetaData 实例对象

```
DatabaseMetaData databaseMetaData = connection.getMetaData();
```

### 获取数据库名和版本
```
int    majorVersion   = databaseMetaData.getDatabaseMajorVersion();
int    minorVersion   = databaseMetaData.getDatabaseMinorVersion();

String productName    = databaseMetaData.getDatabaseProductName();
String productVersion = databaseMetaData.getDatabaseProductVersion();
```
```
数据库属性信息：5 6 MySQL 5.6.24
```
### 获取数据库驱动版本
```
int driverMajorVersion = databaseMetaData.getDriverMajorVersion();
int driverMinorVersion = databaseMetaData.getDriverMinorVersion();
```

### 获取数据库列表

```
String   catalog          = null;
String   schemaPattern    = null;
String   tableNamePattern = null;
String[] types            = null;

ResultSet result = databaseMetaData.getTables(
    catalog, schemaPattern, tableNamePattern, types );

while(result.next()) {
    String tableName = result.getString(3);
}
```
getTables()方法源码：
```
    ResultSet getTables(String catalog, String schemaPattern,
                        String tableNamePattern, String types[]) throws SQLException;
```
我在这里给四个参数都赋值null，则它会把所有数据库中的表信息 返回。

此ResultSet包含10列，每列包含有关给定表的信息。 索引3指的是表名称。
```
user
```
### 在表中列出列

```
String   catalog           = null;
String   schemaPattern     = null;
String   tableNamePattern  = "user";
String   columnNamePattern = null;


ResultSet result = databaseMetaData.getColumns(
    catalog, schemaPattern,  tableNamePattern, columnNamePattern);

while(result.next()){
    String columnName = result.getString(4);
    int    columnType = result.getInt(5);
}
```
getColumns（）方法返回的ResultSet包含给定表的列的列表。 索引为4的列包含列名称，索引为5的列包含列类型。 列类型是一个与java.sql.Types中的类型常量匹配的整数。
```
id 4 
username 12 
birthday 91 
sex 1 
address 12 
```
### 表的主键
```
String   catalog   = null;
String   schema    = null;
String   tableName = "user";

ResultSet result = databaseMetaData.getPrimaryKeys(
    catalog, schema, tableName);

while(result.next()){
    String columnName = result.getString(4);
}
```
调用getPrimaryKeys（）方法，向其传递3个参数。 在这个例子中，只有tableName是非空的。

getPrimaryKeys（）方法返回的ResultSet包含组成给定表主键的列表。 索引4指的是的列名称。

主键可以由多个列组成。 这样的密钥被称为复合密钥。 如果表包含复合键，则ResultSet将包含多行。 复合键中的每一列都有一行。
```
id
```


### 全部代码
```
package com.jdbc;

import java.sql.*;

/**
 * Created by Fant.J.
 * 2018/3/5 21:38
 */
public class DatabaseMetaDataTest {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/user";
        String user = "root";
        String password = "root";

        Connection connection =null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url,user,password);

            DatabaseMetaData databaseMetaData = connection.getMetaData();

            int    majorVersion   = databaseMetaData.getDatabaseMajorVersion();
            int    minorVersion   = databaseMetaData.getDatabaseMinorVersion();

            String productName    = databaseMetaData.getDatabaseProductName();
            String productVersion = databaseMetaData.getDatabaseProductVersion();

            System.out.println("数据库属性信息："+majorVersion+" "+minorVersion+" "+productName+" "+productVersion);

            int driverMajorVersion = databaseMetaData.getDriverMajorVersion();
            int driverMinorVersion = databaseMetaData.getDriverMinorVersion();

            System.out.println("驱动信息："+driverMajorVersion+" "+driverMinorVersion);

/*            String   catalog          = null;
            String   schemaPattern    = null;
            String   tableNamePattern = null;
            String[] types            = null;

            ResultSet result = databaseMetaData.getTables(
                    catalog, schemaPattern, tableNamePattern, types );

            while(result.next()) {
                String tableName = result.getString(3);
                System.out.println(tableName);
            }*/


 /*           String   catalog           = null;
            String   schemaPattern     = null;
            String   tableNamePattern  = "user";
            String   columnNamePattern = null;


            ResultSet result = databaseMetaData.getColumns(
                    catalog, schemaPattern,  tableNamePattern, columnNamePattern);

            while(result.next()){
                String columnName = result.getString(4);
                int    columnType = result.getInt(5);
                System.out.println(columnName+" "+columnType+" ");
            }*/

            String   catalog   = null;
            String   schema    = null;
            String   tableName = "user";

            ResultSet result = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);

            while(result.next()){
                String columnName = result.getString(4);
                System.out.println(columnName);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
    }
    }
}

```
