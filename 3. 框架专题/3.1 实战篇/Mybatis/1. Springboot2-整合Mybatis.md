>整合Mybatis分为两种模式，一种是xml配置，一种是注解。（类似JPA）
我在这里重点放在xml配置上，因为如果想用注解的话，建议直接用jpa代替，因为Jpa有更成熟的CRUD接口更方便开发。我在后文中也会把注解方式说清楚。

大概介绍下流程：
1. 借助idea实现mybatis逆向工程
2. 用xml配置实现整合
3. 用cmd命令行实现mybatis逆向工程
4. 用mapping.xml配置实现数据交互
5. 用注解的方式实现数据交互 


首先我的开发环境：
jdk1.8+maven3+IDEA
###1. mybatis逆向攻城
逆向工程方式很多，我目前接触到的就两种，一种是借助于ide开发工具，一种是在cmd中执行命令。（其实二者原理都一样，都是执行maven的generator命令，具体请看下文）。
######1. 完善pom文件
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>springboot-mybatis</groupId>
	<artifactId>springboot-mybatis</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>springboot-mybatis</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>springboot-integration</groupId>
		<artifactId>springboot-integration</artifactId>
		<version>1.0-SNAPSHOT</version>
	</parent>

	<!-- Add typical dependencies for a web application -->
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>1.3.0</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.35</version>
		</dependency>
		<!-- alibaba的druid数据库连接池 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.0.11</version>
		</dependency>
		<!-- alibaba的druid数据库连接池 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid-spring-boot-starter</artifactId>
			<version>1.1.0</version>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- 分页插件 -->
		<dependency>
			<groupId>com.github.pagehelper</groupId>
			<artifactId>pagehelper</artifactId>
			<version>5.0.4</version>
		</dependency>
	</dependencies>

	<!-- Package as an executable jar -->
	<build>
		<finalName>springboot-mybatis</finalName>
		<plugins>
			<plugin>

				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<configuration>
					<testFailureIgnore>true</testFailureIgnore>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.mybatis.generator</groupId>
				<artifactId>mybatis-generator-maven-plugin</artifactId>
				<version>1.3.2</version>
				<configuration>
					<verbose>true</verbose>
					<overwrite>true</overwrite>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>

```
######2. 逆向所需配置文件generatorConfig.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <!-- 数据库驱动:选择你的本地硬盘上面的数据库驱动包-->
    <classPathEntry  location="E:\lib\mysql_driver\mysql-connector-java-5.1.26-bin.jar"/>
    <context id="DB2Tables"  targetRuntime="MyBatis3">
        <commentGenerator>
            <property name="suppressDate" value="true"/>
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true"/>
        </commentGenerator>
        <!--数据库链接URL，用户名、密码 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver" connectionURL="jdbc:mysql://127.0.0.1/user" userId="root" password="root">
        </jdbcConnection>
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>
        <!-- 生成模型的包名和位置-->
        <javaModelGenerator targetPackage="com.fantj.sbmybatis.model" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>
        <!-- 生成映射文件的包名和位置-->
        <sqlMapGenerator targetPackage="mapping" targetProject="src/main/resources">
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>
        <!-- 生成DAO的包名和位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.fantj.sbmybatis.mapper" targetProject="src/main/java">
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>
        <!-- 要生成的表 tableName是数据库中的表名或视图名 domainObjectName是实体类名-->
        <table tableName="user" domainObjectName="User" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false"></table>
    </context>
</generatorConfiguration>
```
######3. 利用IDE创建逆向工程启动类
![](https://upload-images.jianshu.io/upload_images/5786888-d1a0d44f736b173b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######4. add一个Maven configuration
![](https://upload-images.jianshu.io/upload_images/5786888-69d75a2469eed01e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-06dd1e5a1ecf042d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

######5. 我的数据库和表结构
![](https://upload-images.jianshu.io/upload_images/5786888-df4403640a519a5e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

######6. application配置文件

```
server:
  port: 8080

spring:
  datasource:
    name: test
    url: jdbc:mysql://127.0.0.1:3306/user
    username: root
    password: root
    # druid 连接池
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    filters: stat
    maxActive: 20
    initialSize: 1
    maxWait: 60000
    minIdle: 1
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: select 'x'
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true
    maxOpenPreparedStatements: 20

mybatis:
  mapper-locations: classpath:mapping/*.xml
  type-aliases-package: com.fant.model

#pagehelper分页插件
pagehelper:
    helperDialect: mysql
    reasonable: true
    supportMethodsArguments: true
    params: count=countSql
```
**运行generator工程 自动生成代码**

####生成的文件
######User.java
```
package com.fantj.model;

import java.util.Date;

public class User {
    private Integer id;

    private String username;

    private Date birthday;

    private String sex;

    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }
}
```
######UserMapper .java
```
package com.fantj.mapper;

import com.fantj.model.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectAll();
}
```

######UserMapper.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.fantj.mapper.UserMapper" >
  <resultMap id="BaseResultMap" type="com.fantj.model.User" >
    <id column="id" property="id" jdbcType="INTEGER" />
    <result column="username" property="username" jdbcType="VARCHAR" />
    <result column="birthday" property="birthday" jdbcType="DATE" />
    <result column="sex" property="sex" jdbcType="CHAR" />
    <result column="address" property="address" jdbcType="VARCHAR" />
  </resultMap>
  <sql id="Base_Column_List" >
    id, username, birthday, sex, address
  </sql>
  <select id="selectAll" resultMap="BaseResultMap" >
    select
    <include refid="Base_Column_List" />
    from user
  </select>
  <select id="selectByPrimaryKey" resultMap="BaseResultMap" parameterType="java.lang.Integer" >
    select 
    <include refid="Base_Column_List" />
    from user
    where id = #{id,jdbcType=INTEGER}
  </select>
  <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer" >
    delete from user
    where id = #{id,jdbcType=INTEGER}
  </delete>
  <insert id="insert" parameterType="com.fantj.model.User" >
    insert into user (id, username, birthday, 
      sex, address)
    values (#{id,jdbcType=INTEGER}, #{username,jdbcType=VARCHAR}, #{birthday,jdbcType=DATE}, 
      #{sex,jdbcType=CHAR}, #{address,jdbcType=VARCHAR})
  </insert>
  <insert id="insertSelective" parameterType="com.fantj.model.User" >
    insert into user
    <trim prefix="(" suffix=")" suffixOverrides="," >
      <if test="id != null" >
        id,
      </if>
      <if test="username != null" >
        username,
      </if>
      <if test="birthday != null" >
        birthday,
      </if>
      <if test="sex != null" >
        sex,
      </if>
      <if test="address != null" >
        address,
      </if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides="," >
      <if test="id != null" >
        #{id,jdbcType=INTEGER},
      </if>
      <if test="username != null" >
        #{username,jdbcType=VARCHAR},
      </if>
      <if test="birthday != null" >
        #{birthday,jdbcType=DATE},
      </if>
      <if test="sex != null" >
        #{sex,jdbcType=CHAR},
      </if>
      <if test="address != null" >
        #{address,jdbcType=VARCHAR},
      </if>
    </trim>
  </insert>
  <update id="updateByPrimaryKeySelective" parameterType="com.fantj.model.User" >
    update user
    <set >
      <if test="username != null" >
        username = #{username,jdbcType=VARCHAR},
      </if>
      <if test="birthday != null" >
        birthday = #{birthday,jdbcType=DATE},
      </if>
      <if test="sex != null" >
        sex = #{sex,jdbcType=CHAR},
      </if>
      <if test="address != null" >
        address = #{address,jdbcType=VARCHAR},
      </if>
    </set>
    where id = #{id,jdbcType=INTEGER}
  </update>
  <update id="updateByPrimaryKey" parameterType="com.fantj.model.User" >
    update user
    set username = #{username,jdbcType=VARCHAR},
      birthday = #{birthday,jdbcType=DATE},
      sex = #{sex,jdbcType=CHAR},
      address = #{address,jdbcType=VARCHAR}
    where id = #{id,jdbcType=INTEGER}
  </update>
</mapper>
```
### 修改启动类

逆向生成代码后，我们还需要在启动类上添加一个`@MapperScan("com.fantj.mapper")`注解，告诉我们的Mapper需要扫描的包，这样就不用每个Mapper上都添加@Mapper注解了。

```
@SpringBootApplication
@MapperScan("com.fantj.mapper")
public class MybatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisApplication.class, args);
	}

}

```
### 完善controller和service
###### UserController.java
```
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
        return userService.selectAll(pageNum,pageSize);
    }

}

```
###### UserService.java
```
package com.fantj.service;

import com.fantj.model.User;

import java.util.List;

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
    public List<User> selectAll(int pageNum, int pageSize);
}

```
######UserServiceImpl .java
```
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(int id) {
        userMapper.deleteByPrimaryKey(id);
    }

    /**
     * 增加
     *
     * @param user
     */
    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    /**
     * 更新
     *
     * @param user
     */
    @Override
    public int update(User user) {
        return userMapper.updateByPrimaryKey(user);
    }

    /**
     * 查询单个
     *
     * @param id
     */
    @Override
    public User selectById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询全部列表,并做分页
     *
     * @param pageNum 开始页数
     * @param pageSize 每页显示的数据条数
     */
    @Override
    public List<User> selectAll(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        PageHelper.startPage(pageNum,pageSize);
        return userMapper.selectAll();
    }
}

```
######浏览器访问127.0.0.1/user/selectAll/0/1
![](https://upload-images.jianshu.io/upload_images/5786888-a76859c6557bcc09.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

说明我们成功了。
######一个小甜点
有人问，get方法可以直接从浏览器地址中的url来测试，那post请求怎么测试呢？
个人建议用postman工具，也可以写测试类用代码来完成测试。也可以使用idea的一个测试工具Test RESTful Web Service
![](https://upload-images.jianshu.io/upload_images/5786888-55cafea621c1eb87.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-54b858f78cfd90a1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#注解方式
好了，配置方式我们介绍完了，我在这里稍微聊一聊注解开发方式，个人建议如果想用注解开发，直接用jpa，可以更方便自己的开发。

######mybatis java api ：http://www.mybatis.org/mybatis-3/zh/java-api.html
######UserMapper.java
```
package com.fantj.mapper;

import com.fantj.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserMapper {
    /*int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectAll();*/
    //如果实例对象中的属性名和数据表中字段名不一致，用@Result注解进行说明映射关系，我在这里只是告诉你怎么写
    @Select("SELECT * FROM user")
    @Results({
            @Result(property = "username", column = "username"),
            @Result(property = "sex",  column = "sex"),
            @Result(property = "address",column = "address")
    })
    List<User> selectAll();

    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
            @Result(property = "sex",  column = "sex"),
            @Result(property = "username", column = "username")
    })
    User selectByPrimaryKey(int id);

    @Insert({"INSERT INTO user(username,birthday,sex,address}) VALUES(#{userName}, #{birthday}, #{sex},#{address})"})
    void insert(User user);

    @Update("UPDATE user SET userName=#{userName} WHERE id =#{id}")
    int updateByPrimaryKey(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    int deleteByPrimaryKey(int id);
}
```
######注解的一些解释
* @Select 是查询类的注解，所有的查询均使用这个
* @Result 修饰返回的结果集，关联实体类属性和数据库字段一一对应，如果实体类* 属性和数据库属性名保持一致，就不需要这个属性来修饰。
* @Insert 插入数据库使用，直接传入实体类会自动解析属性到对应的值
* @Update 负责修改，也可以直接传入对象
* @delete 负责删除

######注意将application配置文件中的mybatis.mapper-locations:属性注视掉再启动项目。否则它会报错：Mapped Statements collection already contains value for com.fantj.mapper.UserMapper.insert 。意思是mapper中方法重复。

###最后一个甜点
还有一个东西忘说了 -.-,就是用cmd来逆向生成代码。
![](https://upload-images.jianshu.io/upload_images/5786888-6ce55ef0b8af8089.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######声称语句
`java -jar mybatis-generator-core-1.3.2.jar -configfile generator.xml -overwrite`
src目录请忽略，这是生成的目录。
好了  干货都分享了，谢谢大家。0.0
