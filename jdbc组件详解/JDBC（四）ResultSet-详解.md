本问翻译自：http://tutorials.jenkov.com/jdbc/index.html

### 结果集类型，并发性和可持续性

当创建一个ResultSet时，你可以设置三个属性：

* 类型
* 并发
* 可保存性
在创建Statement或PreparedStatement时已经设置了这些值，如下所示：
```
Statement statement = connection.createStatement(
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_READ_ONLY,
    ResultSet.CLOSE_CURSORS_OVER_COMMIT
   );

PreparedStatement statement = connection.prepareStatement(sql,
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_READ_ONLY,
    ResultSet.CLOSE_CURSORS_OVER_COMMIT
   );
```
###### 1. 最基本的ResultSet.
之所以说是最基本的ResultSet是因为,这个ResultSet他起到的作用就是完成了查询结果的存储功能,而且只能读去一次,不能够来回的滚动读取.这种结果集的创建方式如下:

```java
Statement st = conn.CreateStatement
ResultSet rs = Statement.excuteQuery(sqlStr);
```

　　由于这种结果集不支持,滚动的读去功能所以,如果获得这样一个结果集,只能使用它里面的next()方法,逐个的读去数据.

###### 2. 可滚动的ResultSet类型.

　　这个类型支持前后滚动取得纪录next(),previous(),回到第一行first(),同时还支持要去的ResultSet中的第几行absolute(int n),以及移动到相对当前行的第几行relative(int n),要实现这样的ResultSet在创建Statement时用如下的方法.
```
Statement st = conn.createStatement(int resultSetType, int resultSetConcurrency)
ResultSet rs = st.executeQuery(sqlStr)
```

　　其中两个参数的意义:
* resultSetType是设置ResultSet对象的类型可滚动,或者是不可滚动.取值如下:
* ResultSet.TYPE_FORWARD_ONLY只能向前滚动

　　ResultSet.TYPE_SCROLL_INSENSITIVE和Result.TYPE_SCROLL_SENSITIVE这两个方法都能够实现任意的前后滚动,使用各种移动的ResultSet指针的方法.二者的区别在于前者对于修改不敏感,而后者对于修改敏感.

　　resultSetConcurency是设置ResultSet对象能够修改的,取值如下:

　　ResultSet.CONCUR_READ_ONLY 设置为只读类型的参数.

　　ResultSet.CONCUR_UPDATABLE 设置为可修改类型的参数.

　　所以如果只是想要可以滚动的类型的Result只要把Statement如下赋值就行了.
```
　　Statement st = conn.createStatement(Result.TYPE_SCROLL_INSENITIVE,ResultSet.CONCUR_READ_ONLY);

　　ResultSet rs = st.excuteQuery(sqlStr);
```
　　用这个Statement执行的查询语句得到的就是可滚动的ResultSet.

###### 3. 可更新的ResultSet

这样的ResultSet对象可以完成对数据库中表的修改,但是我知道ResultSet只是相当于数据库中表的视图,所以并不时所有的ResultSet只要设置了可更新就能够完成更新的,能够完成更新的ResultSet的SQL语句必须要具备如下的属性:

　　a,只引用了单个表.

　　b,不含有join或者group by子句.

　　c,那些列中要包含主关键字.

　　具有上述条件的,可更新的ResultSet可以完成对数据的修改,可更新的结果集的创建方法是:
```
　　Statement st = createstatement(Result.TYPE_SCROLL_INSENSITIVE,Result.CONCUR_UPDATABLE)
```
　　这样的Statement的执行结果得到的就是可更新的结果集.更新的方法是,把ResultSet的游标移动到你要更新的行,然后调用updateXXX(),这个方法XXX的含义和getXXX()是相同的.updateXXX()方法,有两个参数,第一个是要更新的列,可以是列名或者序号.第二个是要更新的数据,这个数据类型要和XXX相同.每完成对一行的update要调用updateRow()完成对数据库的写入,而且是在ResultSet的游标没有离开该修改行之前,否则修改将不会被提交.

　　使用updateXXX方法还可以完成插入操作.但是首先要介绍两个方法:

　　moveToInsertRow()是把ResultSet移动到插入行,这个插入行是表中特殊的一行,不需要指定具体那一行,只要调用这个方法系统会自动移动到那一行的.

　　moveToCurrentRow()这是把ResultSet移动到记忆中的某个行,通常当前行.如果没有使用insert操作,这个方法没有什么效果,如果使用了insert操作,这个方法用于返回到insert操作之前的那一行,离开插入行,当然也可以通过next(),previous()等方法离开插入行.

　　要完成对数据库的插入,首先调用moveToInsertRow()移动到插入行,然后调用updateXXX的方法完成对,各列数据的更新,完成更新后和更新操作一样,要写到数据库,不过这里使用的是insertRow(),也要保证在该方法执行之前ResultSet没有离开插入列,否则插入不被执行,并且对插入行的更新将丢失.

###### 4. 可保持的ResultSet 
　　正常情况下如果使用Statement执行完一个查询，又去执行另一个查询时这时候第一个查询的结果集就会被关闭，也就是说，所有的Statement的查询对应的结果集是一个，如果调用Connection的commit()方法也会关闭结果集。可保持性就是指当ResultSet的结果被提交时，是被关闭还是不被关闭。JDBC2.0和1.0提供的都是提交后ResultSet就会被关闭。不过在JDBC3.0中，我们可以设置ResultSet是否关闭。要完成这样的ResultSet的对象的创建，要使用的Statement的创建要具有三个参数，这个Statement的创建方式也就是，我所说的 Statement的第三种创建方式。 
　　当使用ResultSet的时候，当查询出来的数据集记录很多，有一千万条的时候，那rs所指的对象是否会占用很多内存，如果记录过多，那程序会不会把系统的内存用光呢 ?
　　不会的，ResultSet表面看起来是一个记录集，其实这个对象中只是记录了结果集的相关信息，具体的记录并没有存放在对象中，具体的记录内容知道你通过next方法提取的时候，再通过相关的getXXXXX方法提取字段内容的时候才能从数据库中得到，这些并不会占用内存，具体消耗内存是由于你将记录集中的数据提取出来加入到你自己的集合中的时候才会发生，如果你没有使用集合记录所有的记录就不会发生消耗内存厉害的情况。

### 遍历resultSet

遍历ResultSet跟打印读取流内容一样，它有一个next()方法，它会判断是否该对象里还有数据。示例：
```
while(result.next()) {
    // ... get column values from this record
}
```
### 从ResultSet接受数据表中列数据
上文有详细的讲解，这里只提一下
```
while(result.next()) {

    result.getString    ("name");
    result.getInt       ("age");
    // 等等
}
```
或者根据数据表中列的index值获取
```
while(result.next()) {

    result.getString    (1);
    result.getInt       (2);
    // 等等
}
```
但是请注意列中的数据类型和java数据类型要相对应，以免报错。














