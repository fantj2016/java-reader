本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

事务Transaction是一组要作为单一的原子动作进行的行为。 要么执行所有的操作，要么都不执行。

我们可以通过它来调用事务：
```
connection.setAutoCommit(false);
```
如果在事务中间出现失败，就需要对事务进行回滚
```
connection.rollback();
```
如果所有操作都没有失败，那最终需要提交。
```
connection.commit();
```
当然，我们需要借助try-catch 来帮我们捕获异常：
```
Connection connection = ...
try{
    connection.setAutoCommit(false);

    // create and execute statements etc.

    connection.commit();
} catch(Exception e) {
    connection.rollback();
} finally {
    if(connection != null) {
        connection.close();
    }
}
```

### 完整示例
```
Connection connection = ...
try{
    connection.setAutoCommit(false);


    Statement statement1 = null;
    try{
        statement1 = connection.createStatement();
        statement1.executeUpdate(
            "update user set username='aaa' where id=123");
    } finally {
        if(statement1 != null) {
            statement1.close();
        }
    }


    Statement statement2 = null;
    try{
        statement2 = connection.createStatement();
        statement2.executeUpdate(
            "update user set username='bbb' where id=456");
    } finally {
        if(statement2 != null) {
            statement2.close();
        }
    }

    connection.commit();
} catch(Exception e) {
    connection.rollback();
} finally {
    if(connection != null) {
        connection.close();
    }
}
```









