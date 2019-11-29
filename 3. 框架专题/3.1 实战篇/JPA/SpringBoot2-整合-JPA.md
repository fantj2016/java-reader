>JPA全称Java Persistence API.JPA通过JDK 5.0注解或XML描述对象－关系表的映射关系，并将运行期的实体对象持久化到数据库中。

>JPA 的目标之一是制定一个可以由很多供应商实现的API，并且开发人员可以编码来实现该API，而不是使用私有供应商特有的API。

>JPA是需要Provider来实现其功能的，Hibernate就是JPA Provider中很强的一个，应该说无人能出其右。从功能上来说，JPA就是Hibernate功能的一个子集。

本教程大概流程：
1. 借助idea实现springboot 和 spring data jpa 整合
2. 实现JpaRepository接口快捷开发
3. 自定义Mapper查询接口方法
4. MVC架构+分页功能实战
5. QueryDSL工具与之的整合
6. EntityManager的使用
首先我的开发环境：
jdk1.8+maven3+IDEA

####1. 完善pom文件
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>springboot-jpa</groupId>
	<artifactId>springboot-jpa</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>springboot-jpa</name>
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
			<artifactId>spring-boot-starter-test</artifactId>
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
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.16.18</version>
		</dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-test</artifactId>
            <version>1.4.5.RELEASE</version>
            <scope>test</scope>
        </dependency>
		<!--querydsl依赖-->
		<dependency>
			<groupId>com.querydsl</groupId>
			<artifactId>querydsl-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>com.querydsl</groupId>
			<artifactId>querydsl-apt</artifactId>
			<scope>provided</scope>
		</dependency>
		<!--阿里巴巴数据库连接池，专为监控而生 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.0.26</version>
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
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<!--添加QueryDSL插件支持-->
			<plugin>
				<groupId>com.mysema.maven</groupId>
				<artifactId>apt-maven-plugin</artifactId>
				<version>1.1.3</version>
				<executions>
					<execution>
						<configuration>
							<outputDirectory>target/generated-sources/java</outputDirectory>
							<processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>


</project>

```

#### 2. 完善application.properties 文件
```
spring.datasource.url=jdbc:mysql://localhost:3306/user
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.type: com.alibaba.druid.pool.DruidDataSource
spring.datasource.filters:stat
spring.datasource.maxActive: 20
spring.datasource.initialSize: 1
spring.datasource.maxWait: 60000
spring.datasource.minIdle: 1
spring.datasource.timeBetweenEvictionRunsMillis: 60000
spring.datasource.minEvictableIdleTimeMillis: 300000
spring.datasource.validationQuery: select 'x'
spring.datasource.testWhileIdle: true
spring.datasource.testOnBorrow: false
spring.datasource.testOnReturn: false
spring.datasource.poolPreparedStatements: true
spring.datasource.maxOpenPreparedStatements: 20


spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.show-sql=true
```

####3.  编写实体类 User.java
```
package com.fantj.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {
    public User(){
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private Date birthday;
    @Column(nullable = false)
    private String sex;
    @Column(nullable = false)
    private String address;
}
```

@Data注解是 lombok 依赖包下的注解，它可以自动帮我们生成set/getter方法，简化代码量。有兴趣的可以详细了解，这里不做多解释。

####4. 实现DAO层

```
package com.fantj.repostory;

/**
 * Created by Fant.J.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //自定义repository。手写sql
    @Query(value = "update user set name=?1 where id=?4",nativeQuery = true)   //占位符传值形式
    @Modifying
    int updateById(String name,int id);

    @Query("from User u where u.username=:username")   //SPEL表达式
    User findUser(@Param("username") String username);// 参数username 映射到数据库字段username
}

```

注意：只有@Query 的注解下不能使用insert，我们需要在上面再添加个@Modify注解，我习惯都加，nativeQuery 是询问是否使用原生sql语句。多表查询也是在这里手写sql，不做演示。因为后面我们用更好的支持多表查询的工具框架 QueryDSL来帮助我们更简洁的实现它。

#### 5.实现Service层
UserService .java
```
package com.fantj.service;

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
UserServiceImpl.java
```
package com.fantj.service.impl;


/**
 * Created by Fant.J.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    /**
     * 增加
     *
     * @param user
     */
    @Override
    public void insert(User user) {
        userRepository.save(user);
    }

    /**
     * 更新
     *
     * @param user
     */
    @Override
    public int update(User user) {
        userRepository.save(user);
        return 1;
    }

    /**
     * 查询单个
     *
     * @param id
     */
    @Override
    public User selectById(int id) {
        Optional<User> optional = userRepository.findById(id);
        User user = optional.get();
        return user;
    }

    /**
     * 查询全部列表,并做分页
     *  @param pageNum 开始页数
     * @param pageSize 每页显示的数据条数
     */
    @Override
    public Iterator<User> selectAll(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageNum, pageSize, sort);
        Page<User> users = userRepository.findAll(pageable);
        Iterator<User> userIterator =  users.iterator();
        return  userIterator;
    }
}

```
分页不止可以这样做，也可以在Controller层进行实例化和初始化然后将Pageable对象传给Service。
当然也可以对分页进行封装,封装后的展示。
```
    Page<User> datas = userRepository.findAll(PageableTools.basicPage(1, 5, new SortDto("id")));
```
是不是很简洁。大家可以自己尝试一下。

#####6. 实现Controller
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

![](https://upload-images.jianshu.io/upload_images/5786888-a880a5f69bca5442.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### QueryDSL工具与上文的整合

可以参考恒宇少年的四篇文章：
* Maven环境下如何配置QueryDSL环境 https://www.jianshu.com/p/a22447c0897c
* 使用QueryDSL与SpringDataJPA实现单表普通条件查询 https://www.jianshu.com/p/4e9d8adaeeb2
* 使用QueryDSL与SpringDataJPA完成Update&Delete https://www.jianshu.com/p/ac388c3c36c2
* 使用QueryDSL与SpringDataJPA实现多表关联查询 https://www.jianshu.com/p/6199e76a5485

注意一点，目前springboot2.0 版本对JPA支持有误，如果你用springboot2 来配置querydsl，application启动类会运行不起来，正确的依赖包或者是配置类我还没有找到，希望有点子的朋友可以和我联系。 本人QQ：844072586

### 管理器EntityManager--执行数据库更新
转载一篇博客：
https://blog.csdn.net/moridehuixiang/article/details/47005335
