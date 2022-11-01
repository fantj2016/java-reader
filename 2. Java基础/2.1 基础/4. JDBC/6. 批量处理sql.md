本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

批量更新是分组在一起的一批更新，并以“批量”方式发送到数据库，而不是逐个发送更新。

一次发送一批更新到数据库，比一个一个发送更快，等待每个更新完成。 发送一批更新（仅一次往返）涉及的网络流量较少，数据库可能能够并行执行一些更新。 与逐个执行更新相比，加速可能相当大。

有两种方法可以执行批量更新：

* 使用Statement
* 使用PreparedStatement

### Statement 批量更新

用Statement对象执行批量更新时，用到addBatch（）和executeBatch（）方法。例子：
```
Statement statement = null;

try{
    statement = connection.createStatement();

    statement.addBatch("update people set firstname='aaa' where id=123");
    statement.addBatch("update people set firstname='bbb' where id=456");
    statement.addBatch("update people set firstname='ccc'  where id=789");

    int[] recordsAffected = statement.executeBatch();
} finally {
    if(statement != null) statement.close();
}
```
使用addBatch（）方法添加要在批处理中执行的SQL语句。然后使用executeBatch（）执行SQL语句。

### PreparedStatement 批量更新
还可以使用PreparedStatement对象执行批量更新。 PreparedStatement可以重用相同的SQL语句，并只需插入新参数即可执行每个更新:
```
String sql = "update user set username=? where id=?";


PreparedStatement preparedStatement = null;
try{
    preparedStatement =
            connection.prepareStatement(sql);

    preparedStatement.setString(1, "aaa");
    preparedStatement.setLong  (2, 123);

    preparedStatement.addBatch();

    preparedStatement.setString(1, "bbb");
    preparedStatement.setLong  (2, 456);

    preparedStatement.addBatch();

    int[] affectedRecords = preparedStatement.executeBatch();

}finally {
    if(preparedStatement != null) {
        preparedStatement.close();
    }
}
```

将每组参数值插入到preparedStatement中，并调用addBatch（）方法。 这会将参数值添加到批处理内部。 现在可以添加另一组值，以便插入到SQL语句中。 将全部批次发送到数据库后，将每组参数插入到SQL中并分别执行。然后执行executeBatch（）方法，它执行所有的批量更新。 SQL语句和参数集一起发送到数据库。

### 注意

批量操作应该放到事务里进行，因为它会存在某条语句执行失败的情况。
