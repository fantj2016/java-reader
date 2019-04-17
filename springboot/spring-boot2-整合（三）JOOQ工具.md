###先来介绍下jooq
jOOQ是一个基于Java编写SQL的工具包，具有：简单、轻量、函数式编程写SQL等独特优势，非常适合敏捷快速迭代开发。

SQL语句：
```
SELECT AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, COUNT(*)
    FROM AUTHOR
    JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID
   WHERE BOOK.LANGUAGE = 'DE'
     AND BOOK.PUBLISHED > DATE '2008-01-01'
GROUP BY AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME
  HAVING COUNT(*) > 5
ORDER BY AUTHOR.LAST_NAME ASC NULLS FIRST
   LIMIT 2
  OFFSET 1  
```
Java代码：
```
create.select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, count())
      .from(AUTHOR)
      .join(BOOK).on(AUTHOR.ID.equal(BOOK.AUTHOR_ID))
      .where(BOOK.LANGUAGE.eq("DE"))
      .and(BOOK.PUBLISHED.gt(date("2008-01-01")))
      .groupBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
      .having(count().gt(5))
      .orderBy(AUTHOR.LAST_NAME.asc().nullsFirst())
      .limit(2)
      .offset(1)
```


###那从这里开始正式开始整合

#####1. pom修改
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>springboot-jooq</groupId>
	<artifactId>springboot-jooq</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>springboot-jooq</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.0.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jooq</artifactId>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.16.18</version>
		</dependency>

		<!-- 阿里巴巴fastjson，解析json视图 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.15</version>
		</dependency>
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.3</version>
		</dependency>
		<dependency>
			<groupId>org.jooq</groupId>
			<artifactId>jooq-meta</artifactId>
		</dependency>
		<dependency>
			<groupId>org.jooq</groupId>
			<artifactId>jooq-codegen</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
			</plugin>

			<plugin>
				<groupId>org.jooq</groupId>
				<artifactId>jooq-codegen-maven</artifactId>
				<version>${jooq.version}</version>
				<executions>
					<execution>
						<goals>
							<goal>generate</goal>
						</goals>
					</execution>
				</executions>
				<dependencies>
					<dependency>
						<groupId>mysql</groupId>
						<artifactId>mysql-connector-java</artifactId>
						<version>${mysql.version}</version>
					</dependency>
				</dependencies>
				<configuration>
					<configurationFile>src/main/resources/JooqConfig.xml</configurationFile>
				</configuration>
			</plugin>
		</plugins>
	</build>


</project>

```
##### 2. 逆向工程

##### 2.1 逆向配置文件
JooqConfig.xml
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<configuration>
    <jdbc>
        <driver>com.mysql.jdbc.Driver</driver>
        <url>jdbc:mysql://localhost:3306/user</url>
        <user>root</user>
        <password>root</password>
    </jdbc>
    <generator>
        <!-- 代码生成器 -->
        <name>org.jooq.util.JavaGenerator</name>
        <database>
            <!--下面这两行是为view也生成代码的关键-->
            <!--force generating id'sfor everything in public schema, that has an 'id' field-->
            <syntheticPrimaryKeys>public\..*\.id</syntheticPrimaryKeys>
            <!--name for fake primary key-->
            <overridePrimaryKeys>override_primmary_key</overridePrimaryKeys>

            <name>org.jooq.util.mysql.MySQLDatabase</name>

            <!--include和exclude用于控制为数据库中哪些表生成代码-->
            <includes>.*</includes>
            <!--<excludes></excludes>-->

            <!--数据库名称-->
            <inputSchema>user</inputSchema>
        </database>

        <generate>
            <!--生成dao和pojo-->
            <daos>true</daos>
            <pojos>true</pojos>
            <!--把数据库时间类型映射到java 8时间类型-->
            <javaTimeTypes>true</javaTimeTypes>
            <!--<interfaces>true</interfaces>-->
            <!--不在生成的代码中添加spring注释，比如@Repository-->
            <springAnnotations>false</springAnnotations>
        </generate>

        <target>
            <!--生成代码文件的包名及放置目录-->
            <packageName>com.generator</packageName>
            <directory>src/main/java</directory>
        </target>
    </generator>
</configuration>
```
##### 2.2 mvn执行逆向工程
![](https://upload-images.jianshu.io/upload_images/5786888-bb65c26b6bb77bb3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

先进行clean 再compile，然后我们就会发现，生成了很多个类
![](https://upload-images.jianshu.io/upload_images/5786888-b39e14acbd401270.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果你看到了这些，那么恭喜。你的jooq环境搭建成功了！

#### 三层架构
###### service接口
UserService .java
```
package com.fantj.service;

import com.fantj.pojos.User;

import java.util.Iterator;

/**
 * Created by Fant.J.
 */
public interface UserService {
    /** 删除 */
    public void delete(int id);
    /** 增加*/
    public void insert(User user);
    /** 更新*/
    public int update(User user);
    /** 查询单个*/
    public User selectById(int id);
    /** 查询全部列表*/
    public Iterator<User> selectAll(int pageNum, int pageSize);
}

```
###### serviceImpl
UserServiceImpl .java
```
package com.fantj.service.impl;

/**
 * Created by Fant.J.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    DSLContext dsl;
/*
    @Autowired
    private UserDao userDao;*/

    com.generator.tables.User u =  User.USER_.as("u");
    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(int id) {
        dsl.delete(u).where(u.ID.eq(id));
    }

    /**
     * 增加
     *
     * @param user
     */
    @Override
    public void insert(com.fantj.pojos.User user) {
        dsl.insertInto(u).
                columns(u.ADDRESS,u.BIRTHDAY,u.SEX,u.USERNAME).
                values(user.getAddress(),user.getBirthday(),user.getSex(),user.getUsername())
                .execute();
    }

    /**
     * 更新
     *
     * @param user
     */
    @Override
    public int update(com.fantj.pojos.User user) {
        dsl.update(u).set((Record) user);
        return 0;
    }

    /**
     * 查询单个
     *
     * @param id
     */
    @Override
    public com.fantj.pojos.User selectById(int id) {
        Result result =  dsl.select(u.ADDRESS,u.BIRTHDAY,u.ID,u.SEX,u.USERNAME)
                .from(u)
                .where(u.ID.eq(id)).fetch();
        System.out.println(result.get(0).toString());
        String className = result.get(0).getClass().getName();
        System.out.println(className);
        com.fantj.pojos.User user = new com.fantj.pojos.User();
        return null;
        /*com.fantj.pojos.User user1 = userDao.findById(id);
        return user1;*/
    }

    /**
     * 查询全部列表
     *  @param pageNum
     * @param pageSize
     */
    @Override
    public Iterator<com.fantj.pojos.User> selectAll(int pageNum, int pageSize) {
        Result result = dsl.select().from(u).fetch();

        return result.iterator();
    }
}

```
我对几段代码做点解释
``` 
  @Autowired
    DSLContext dsl;
```
这里是注入DSL上下文对象，DSLContextl里面有connect对象，大概猜测的话应该是与数据库连接交互的一个对象。

```
com.generator.tables.User u =  User.USER_.as("u");
```
这段代码的意思是给User表 重命名 u 。（类似sql语句中的  user as u）。
但是注意一点，这个User类是逆向生成的tables包下的，不是pojos包下的User实体类。
（逆向工程它会生成两个User类。一个在pojos下，一个再tables下）。

还有，我在这里只测试了一个方法`selectById()`，别的我没有测试，大概应该差不多。
好了，让大家看看方法`selectById()`的运行结果。
![](https://upload-images.jianshu.io/upload_images/5786888-fffb5f43bd7aabcd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
控制台打印分别对应上面代码中的这两个语句
```
System.out.println(result.get(0).toString());  //获取result对象中的第一个对象并打印toString
String className = result.get(0).getClass().getName();  //获取Result第一个对象的类类型
System.out.println(className);
```
我目前还没有把org.jooq.impl.RecordImpl这个对象转换成我们想要的pojos包下的User实体类。
但是查询的功能是实现了，希望有能力大佬再研究和试试。

我也试过用它逆向生成的UserDao（我再上面代码中注释掉的）。结果报错：
`org.jooq.exception.DetachedException: Cannot execute query. No Connection configured`
意思是没有获取到连接配置信息。这块也没能搞懂。希望大佬也能在下面评论。

######controller
UserController .java
```
package com.fantj.controller;

/**
 * Created by Fant.J.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET,value = "/delete/{id}")
    public void delete(@PathVariable("id")int id){
        userService.delete(id);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/insert")
    public void insert(User user){
        userService.insert(user);
    }
    @RequestMapping(method = RequestMethod.POST,value = "/update/{id}")
    public void update(@RequestParam User user){
        userService.update(user);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}/select")
    public User select(@PathVariable("id")int id){
        return userService.selectById(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/selectAll/{pageNum}/{pageSize}")
    public List<User> selectAll(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
        Iterator<User> userIterator = userService.selectAll(pageNum, pageSize);
        List<User> list = new ArrayList<>();
        while(userIterator.hasNext()){
            list.add(userIterator.next());
        }
        return list;
    }

}

```

好了，这是我8个小时琢磨的结果，因为ssm整合jooq资料真的很少。springboot更是少。但是可以说完成了一大半。再总结一下遗留的两个问题：
1. UserDao.java 是干什么的
2. Result 对象怎么 转换成 javabean 实体类

#####问题2已得道解决
谢谢QQ号为523309375的朋友的提示。

自己也是耐心耗到极限，没有仔细再研究`Result`这个接口，它里面有info方法。可以转换成很多格式，我在这里选择`<E> List<E> into(Class<? extends E> var1) throws MappingException;`方法来返回一个`List<com.fantj.pojos.User>`。

修改之后的两个ServiceImpl里的方法。
```
    /**
     * 查询单个
     *
     * @param id
     */
    @Override
    public com.fantj.pojos.User selectById(int id) {
        List<com.fantj.pojos.User> result =  dsl.select(u.ADDRESS,u.BIRTHDAY,u.ID,u.SEX,u.USERNAME)
                .from(u)
                .where(u.ID.eq(id))
                .fetch()
                .into(com.fantj.pojos.User.class);

        return result.get(0);
        /*com.fantj.pojos.User user1 = userDao.findById(id);
        return user1;*/
    }

    /**
     * 查询全部列表
     * @param pageNum
     * @param pageSize
     */
    @Override
    public List<com.fantj.pojos.User> selectAll(int pageNum, int pageSize) {
        List<com.fantj.pojos.User> list = dsl.select().from(u)
                .orderBy(u.ID.desc())   //id倒序
                .limit(0)   //分页
                .offset(10)   //分页
                .fetch()
                .into(com.fantj.pojos.User.class);  //数据类型格式转化

        return list;
    }
```
![](https://upload-images.jianshu.io/upload_images/5786888-794ef1acdd3ec80b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-34b7dcf37876dae3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


再说说我现在对jooq的理解。我个人觉得，如果再对jooq进行一些与ssm整合上的优化。我们可以在serviceImpl里写sql伪代码，达到快速开发。仔细想想，JPA和Mybatis对复杂sql的支持还是挺不方便的。如果能再serviceImpl层里直接写sql语句岂不更好。个人认为jooq很接近。
哈哈，不得不说这种代码格式看上去很舒服。但是有点打破经典三层架构的意思，当然自然的也失去了代码耦合性（对于大型项目而言），希望大家能提出自己好的想法。

谢谢大家赏脸！

源码地址：https://github.com/jiaofanting/springboot-integration/tree/master/springboot-jooq
