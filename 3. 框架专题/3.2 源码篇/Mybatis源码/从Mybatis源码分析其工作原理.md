>Mybatis将sql与代码分离、将查询的结果集与java对象自动映射, 大大方便了研发人员的开发和维护成本, 那它是如何做到的呢?

我将其工作流程分为六个部分：
1. 读取核心配置文件并返回`InputStream`流对象。
2. 根据`InputStream`流对象解析出`Configuration`对象，然后创建`SqlSessionFactory`工厂对象
3. 根据一系列属性从`SqlSessionFactory`工厂中创建`SqlSession`
4. 从`SqlSession`中调用`Executor`执行数据库操作&&生成具体SQL指令
5. 对执行结果进行二次封装
6. 提交与事务

接下来一步步跟进看下源码：

先给大家看看我的实体类:
```
/**
 * 图书实体
 */
public class Book {

	private long bookId;// 图书ID

	private String name;// 图书名称

	private int number;// 馆藏数量

        getter and setter ...
}
```
### 1. 读取核心配置文件
#### 1.1 配置文件mybatis-config.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
		<environment id="development">
			<transactionManager type="JDBC"/>
			<dataSource type="POOLED">
				<property name="driver" value="com.mysql.jdbc.Driver"/>
				<property name="url" value="jdbc:mysql://xxx.xxx:3306/ssm" />
				<property name="username" value="root"/>
				<property name="password" value="root"/>
			</dataSource>
		</environment>
	</environments>
        <mappers>
		<mapper resource="BookMapper.xml"/>
	</mappers>
</configuration>
```
当然，还有很多可以在XML 文件中进行配置，上面的示例指出的则是最关键的部分。要注意 XML 头部的声明，用来验证 XML 文档正确性。environment 元素体中包含了事务管理和连接池的配置。mappers 元素则是包含一组 mapper 映射器（这些 mapper 的 XML 文件包含了 SQL 代码和映射定义信息）。
#### 1.2 BookMapper.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="Book">
	<!-- 目的：为dao接口方法提供sql语句配置 -->
    <insert id="insert" >
		insert into book (name,number) values (#{name},#{number})
	</insert>
</mapper>
```
就是一个普通的mapper.xml文件。
#### 1.3 Main方法
>从 XML 文件中构建 SqlSessionFactory 的实例非常简单，建议使用类路径下的资源文件进行配置。但是也可以使用任意的输入流(InputStream)实例，包括字符串形式的文件路径或者 file:// 的 URL 形式的文件路径来配置。MyBatis 包含一个名叫 Resources 的工具类，它包含一些实用方法，可使从 classpath 或其他位置加载资源文件更加容易。
```
public class Main {
    public static void main(String[] args) throws IOException {
        // 创建一个book对象
        Book book = new Book();
        book.setBookId(1006);
        book.setName("Easy Coding");
        book.setNumber(110);
        // 加载配置文件 并构建SqlSessionFactory对象
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        // 从SqlSessionFactory对象中获取 SqlSession对象
        SqlSession sqlSession = factory.openSession();
        // 执行操作
        sqlSession.insert("insert", book);
        // 提交操作
        sqlSession.commit();
        // 关闭SqlSession
        sqlSession.close();
    }
}
```
这个代码是根据Mybatis官方提供的一个[不使用 XML 构建 SqlSessionFactory](http://www.mybatis.org/mybatis-3/zh/getting-started.html)的一个Demo改编的。


注意：是官方给的一个`不使用 XML 构建 SqlSessionFactory`的例子，那么我们就从这个例子中查找入口来分析。

### 2. 根据配置文件生成SqlSessionFactory工厂对象

#### 2.1 Resources.getResourceAsStream(resource);源码分析
>`Resources `是mybatis提供的一个加载资源文件的工具类。
![](https://upload-images.jianshu.io/upload_images/5786888-61fe3a71960cb343.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们只看getResourceAsStream方法:
```
public static InputStream getResourceAsStream(String resource) throws IOException {
    return getResourceAsStream((ClassLoader)null, resource);
}
```
getResourceAsStream调用下面的方法：
```
public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
    InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
    if (in == null) {
        throw new IOException("Could not find resource " + resource);
    } else {
        return in;
    }
}
```
交给ClassLoader来加载, 如果classLoader为null, 则去"/" + resource 路径下装载流:
```
InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
    ClassLoader[] arr$ = classLoader;
    int len$ = classLoader.length;

    for(int i$ = 0; i$ < len$; ++i$) {
        ClassLoader cl = arr$[i$];
        if (null != cl) {
            InputStream returnValue = cl.getResourceAsStream(resource);
            if (null == returnValue) {
                returnValue = cl.getResourceAsStream("/" + resource);
            }

            if (null != returnValue) {
                return returnValue;
            }
        }
    }
```
值的注意的是，它返回了一个`InputStream`对象。

#### 2.2 new SqlSessionFactoryBuilder().build(inputStream);源码分析
```
public SqlSessionFactoryBuilder() {
}
```
所以` new SqlSessionFactoryBuilder()`只是创建一个对象实例,而没有对象返回(建造者模式)，对象的返回交给`build()`方法。

```
public SqlSessionFactory build(InputStream inputStream) {
    return this.build((InputStream)inputStream, (String)null, (Properties)null);
}
```
这里要传入一个inputStream对象，就是将我们上一步获取到的InputStream对象传入。

>InputStream里面有什么数据呢?

jdbc信息、和关联mapper.xml(BookMapper.xml)
```
public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    SqlSessionFactory var5;
    try {
        // 进行XML配置文件的解析
        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
        var5 = this.build(parser.parse());
    } catch (Exception var14) {
        throw ExceptionFactory.wrapException("Error building SqlSession.", var14);
    } finally {
        ErrorContext.instance().reset();

        try {
            inputStream.close();
        } catch (IOException var13) {
            ;
        }

    }

    return var5;
}
```
可以看到, XMLConfigBuilder对象对输入流进行了解析, 如何解析的就大概说下，通过`Document`对象来解析，然后返回`InputStream`对象，然后交给`XMLConfigBuilder`构造成`org.apache.ibatis.session.Configuration`对象，然后交给build()方法构造程SqlSessionFactory：
```
public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
}
```
```
public DefaultSqlSessionFactory(Configuration configuration) {
    this.configuration = configuration;
}
```
### 3. 创建SqlSession
>SqlSession 完全包含了面向数据库执行 SQL 命令所需的所有方法。你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句。
```
public SqlSession openSession() {
    return this.openSessionFromDataSource(this.configuration.getDefaultExecutorType(), (TransactionIsolationLevel)null, false);
}
```
调用自身的`openSessionFromDataSource`方法：
1. getDefaultExecutorType()默认是SIMPLE。
2. 注意TX等级是 Null， autoCommit是false。
```
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;

    DefaultSqlSession var8;
    try {
        Environment environment = this.configuration.getEnvironment();
        // 根据Configuration的Environment属性来创建事务工厂
        TransactionFactory transactionFactory = this.getTransactionFactoryFromEnvironment(environment);
        // 从事务工厂中创建事务，默认等级为null，autoCommit=false
        tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
        // 创建执行器
        Executor executor = this.configuration.newExecutor(tx, execType);
        // 根据执行器创建返回对象 SqlSession
        var8 = new DefaultSqlSession(this.configuration, executor, autoCommit);
    } catch (Exception var12) {
        this.closeTransaction(tx);
        throw ExceptionFactory.wrapException("Error opening session.  Cause: " + var12, var12);
    } finally {
        ErrorContext.instance().reset();
    }
    return var8;
}
```
构建步骤：
`Environment`>>`TransactionFactory`+`autoCommit`+`tx-level`>>`Transaction`+`ExecType`>>`Executor`+`Configuration`+`autoCommit`>>`SqlSession`

其中，`Environment`是`Configuration`中的属性。
### 4. 调用Executor执行数据库操作&&生成具体SQL指令
>在拿到SqlSession对象后，我们调用它的insert方法。
```
public int insert(String statement, Object parameter) {
    return this.update(statement, parameter);
}
```
它调用了自身的update(statement, parameter)方法：
```
public int update(String statement, Object parameter) {
    int var4;
    try {
        this.dirty = true;
        MappedStatement ms = this.configuration.getMappedStatement(statement);
        // wrapCollection(parameter)判断 param对象是否是集合
        var4 = this.executor.update(ms, this.wrapCollection(parameter));
    } catch (Exception var8) {
        throw ExceptionFactory.wrapException("Error updating database.  Cause: " + var8, var8);
    } finally {
        ErrorContext.instance().reset();
    }

    return var4;
}
```


`mappedStatements`就是我们平时说的sql映射对象.

源码如下：
` protected final Map<String, MappedStatement> mappedStatements;`

可见它是一个Map集合，在我们加载xml配置的时候，`mapping.xml`的`namespace`和`id`信息就会存放为`mappedStatements`的`key`，对应的，sql语句就是对应的`value`.

然后调用BaseExecutor中的update方法：

```
public int update(MappedStatement ms, Object parameter) throws SQLException {
    ErrorContext.instance().resource(ms.getResource()).activity("executing an update").object(ms.getId());
    if (this.closed) {
        throw new ExecutorException("Executor was closed.");
    } else {
        this.clearLocalCache();
        // 真正做执行操作的方法
        return this.doUpdate(ms, parameter);
    }
}
```
doUpdate才是真正做执行操作的方法：
```
public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
    Statement stmt = null;

    int var6;
    try {
        Configuration configuration = ms.getConfiguration();
        // 创建StatementHandler对象，从而创建Statement对象
        StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, (ResultHandler)null, (BoundSql)null);
        // 将sql语句和参数绑定并生成SQL指令
        stmt = this.prepareStatement(handler, ms.getStatementLog());
        var6 = handler.update(stmt);
    } finally {
        this.closeStatement(stmt);
    }

    return var6;
}
```
先来看看`prepareStatement`方法，看看mybatis是如何将sql拼接合成的：
```
private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Connection connection = this.getConnection(statementLog);
    // 准备Statement
    Statement stmt = handler.prepare(connection);
    // 设置SQL查询中的参数值
    handler.parameterize(stmt);
    return stmt;
}
```
来看看`parameterize`方法：
```
public void parameterize(Statement statement) throws SQLException {
    this.parameterHandler.setParameters((PreparedStatement)statement);
}
```
这里把statement转换程PreparedStatement对象，它比Statement更快更安全。
这都是我们在JDBC中熟用的对象，就不做介绍了，所以也能看出来Mybatis是对JDBC的封装。


从ParameterMapping中读取参数值和类型，然后设置到SQL语句中：
```
public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(this.mappedStatement.getParameterMap().getId());
    List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
    if (parameterMappings != null) {
        for(int i = 0; i < parameterMappings.size(); ++i) {
            ParameterMapping parameterMapping = (ParameterMapping)parameterMappings.get(i);
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                if (this.boundSql.hasAdditionalParameter(propertyName)) {
                    value = this.boundSql.getAdditionalParameter(propertyName);
                } else if (this.parameterObject == null) {
                    value = null;
                } else if (this.typeHandlerRegistry.hasTypeHandler(this.parameterObject.getClass())) {
                    value = this.parameterObject;
                } else {
                    MetaObject metaObject = this.configuration.newMetaObject(this.parameterObject);
                    value = metaObject.getValue(propertyName);
                }

                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    jdbcType = this.configuration.getJdbcTypeForNull();
                }

                try {
                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                } catch (TypeException var10) {
                    throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + var10, var10);
                } catch (SQLException var11) {
                    throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + var11, var11);
                }
            }
        }
    }

}
```

### 5. 对查询结果二次封装
>为什么要二次封装, 因为要将数据库返回数据转换成java对象类型。

>在doUpdate方法中，解析生成完新的SQL后，然后执行var6 = handler.update(stmt);我们来看看它的源码。

```
public int update(Statement statement) throws SQLException {
    PreparedStatement ps = (PreparedStatement)statement;
     // 执行sql
    ps.execute();
    // 获取返回值
    int rows = ps.getUpdateCount();
    Object parameterObject = this.boundSql.getParameterObject();
    KeyGenerator keyGenerator = this.mappedStatement.getKeyGenerator();
    keyGenerator.processAfter(this.executor, this.mappedStatement, ps, parameterObject);
    return rows;
}
```
因为我们是插入操作，返回的是一个int类型的值，所以这里mybatis给我们直接返回int。

如果是query操作，返回的是一个ResultSet，mybatis将查询结果包装程`ResultSetWrapper`类型，然后一步步对应java类型赋值等...有兴趣的可以自己去看看。

### 6. 提交与事务
>mybatis的事务也是基于jdbc做的, 不了解jdbc事务可以去补一下这一块的知识。

>最后，来看看commit()方法的源码。


```
public void commit() {
    this.commit(false);
}
```
调用其对象本身的commit()方法：
```
public void commit(boolean force) {
    try {
        // 是否提交（判断是提交还是回滚）
        this.executor.commit(this.isCommitOrRollbackRequired(force));
        this.dirty = false;
    } catch (Exception var6) {
        throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + var6, var6);
    } finally {
        ErrorContext.instance().reset();
    }
}
```
如果dirty是false，则进行回滚；如果是true，则正常提交。
```
private boolean isCommitOrRollbackRequired(boolean force) {
    return !this.autoCommit && this.dirty || force;
}
```
调用CachingExecutor的commit方法：
```
public void commit(boolean required) throws SQLException {
    this.delegate.commit(required);
    this.tcm.commit();
}
```
调用BaseExecutor的commit方法：
```
public void commit(boolean required) throws SQLException {
    if (this.closed) {
        throw new ExecutorException("Cannot commit, transaction is already closed");
    } else {
        this.clearLocalCache();
        this.flushStatements();
        if (required) {
            this.transaction.commit();
        }

    }
}
```
最后调用JDBCTransaction的commit方法：
> 注意这里的this.connection, 保证了jdbc的同一个连接下的操作(同一次对话中完成事务)。这里面还是大有学问的, 有兴趣的可以继续钻研, 我的理解是开启连接依赖于java提供的javax.sql.DataSource对象, 然后后续是将这个connect对象一直持有着的直到销毁连接。

```
public void commit() throws SQLException {
    if (this.connection != null && !this.connection.getAutoCommit()) {
        if (log.isDebugEnabled()) {
            log.debug("Committing JDBC Connection [" + this.connection + "]");
        }
        // 提交连接
        this.connection.commit();
    }
}
```

Demo参考文档：http://www.mybatis.org/mybatis-3/zh/getting-started.html
