这几天一直在整合SSM框架,虽然网上有很多已经整合好的,但是对于里面的配置文件并没有进行过多的说明,很多人知其然不知其所以然,经过几天的搜索和整理,今天总算对其中的XML配置文件有了一定的了解,所以拿出来一起分享一下,希望有不足的地方大家批评指正~~~

首先   这篇文章暂时只对框架中所要用到的配置文件进行解释说明,而且是针对注解形式的,框架运转的具体流程过两天再进行总结.

[spring](http://lib.csdn.net/base/javaee "Java EE知识库")+springmvc+mybatis框架中用到了三个XML配置文件:web.xml,spring-mvc.xml,spring-mybatis.xml.第一个不用说,每个web项目都会有的也是关联整个项目的配置.第二个文件spring-mvc.xml是springmvc的一些相关配置,第三个是mybatis的相关配置.

项目中还会用到两个资源属性文件jdbc.properties和log4j.properties.一个是关于jdbc的配置,提取出来方便以后的修改.另一个是日志文件的配置.

以上是我这篇文章中所要讲的内容,比较简单,也很容易懂.希望大牛不要鄙视~~接下来进入正题:

一  web.xml

关于这个配置文件我以前一直都是朦朦胧胧的状态,刚好借着这次整合框架的机会将它了解清楚.在下面的代码中我对每一个标签都进行了详细的注释,大家一看就懂,主要理解<servlet>中的配置,因为其中配置了前端控制器,在SSM框架中,

前端控制器

起着最主要的作用.下面贴上代码
```
<?xml version="1.0" encoding="UTF-8"?>  
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
  xmlns="http://java.sun.com/xml/ns/javaee"  
  xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"  
  version="3.0">  
      
    <context-param> <!--全局范围内环境参数初始化-->  
        <param-name>contextConfigLocation</param-name>          <!--参数名称-->  
        <param-value>classpath:spring-mybatis.xml</param-value>     <!--参数取值-->  
    </context-param>  
      
         <!--以下配置的加载顺序:先 ServletContext >> context-param >> listener >> filter >> servlet >>  spring-->  
                                  
    <!---------------------------------------------------过滤器配置------------------------------------------------------>  
    <!--例:编码过滤器-->  
    <filter>      <!-- 用来声明filter的相关设定,过滤器可以截取和修改一个Servlet或JSP页面的请求或从一个Servlet或JSP页面发出的响应-->  
        <filter-name>encodingFilter</filter-name>     <!--指定filter的名字-->  
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class> <!--定义filter的类的名称-->  
        <async-supported>true</async-supported>     <!--设置是否启用异步支持-->  
        <init-param><!--用来定义参数,若在Servlet可以使用下列方法来获得:String param_name=getServletContext().getInitParamter("param-name里面的值");-->  
            <param-name>encoding</param-name>   <!--参数名称-->  
            <param-value>UTF-8</param-value> <!--参数值-->  
        </init-param>  
    </filter>  
    <filter-mapping><!--用来定义filter所对应的URL-->  
        <filter-name>encodingFilter</filter-name>     <!--指定对应filter的名字-->  
        <url-pattern>/*</url-pattern>       <!--指定filter所对应的URL-->  
    </filter-mapping>  
      
    <!---------------------------------------------------监听器配置------------------------------------------------------>  
    <!--例:spring监听器-->  
    <listener>        <!--用来设定Listener接口-->  
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class><!--定义Listener的类名称-->  
    </listener>  
    <!-- 防止Spring内存溢出监听器  -->  
    <listener>  
        <listener-class>org.springframework.web.util.IntrospectorCleanupListener</listener-class>  
    </listener>  
      
    <!---------------------------------------------------servlet配置------------------------------------------------------>  
    <servlet>     <!--用来声明一个servlet的数据 -->    
        <servlet-name>SpringMVC</servlet-name>  <!--指定servlet的名称-->  
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class> <!--指定servlet的类名称,这里配置了前端控制器-->  
        <init-param><!--用来定义参数,可有多个init-param。在servlet类中通过getInitParamenter(String name)方法访问初始化参数    -->  
            <param-name>contextConfigLocation</param-name>  <!--参数名称-->  
            <param-value>classpath:spring-mvc.xml</param-value> <!--参数值-->  
        </init-param>  
        <load-on-startup>1</load-on-startup><!--当值为正数或零时：Servlet容器先加载数值小的servlet，再依次加载其他数值大的servlet.-->  
        <async-supported>true</async-supported><!--设置是否启用异步支持-->  
    </servlet>  
    <servlet-mapping><!--用来定义servlet所对应的URL-->  
        <servlet-name>SpringMVC</servlet-name>  <!--指定servlet的名称-->  
        <url-pattern>/</url-pattern>        <!--指定servlet所对应的URL-->  
    </servlet-mapping>  
      
    <!-----------------------------------------------会话超时配置（单位为分钟）------------------------------------------------->  
    <session-config><!--如果某个会话在一定时间未被访问，则服务器可以扔掉以节约内存-->  
        <session-timeout>120</session-timeout>  
    </session-config>  
    <!---------------------------------------------------MIME类型配置   ------------------------------------------------------>  
    <mime-mapping><!--设定某种扩展名的文件用一种应用程序来打开的方式类型，当该扩展名文件被访问的时候，浏览器会自动使用指定应用程序来打开-->  
        <extension>*.ppt</extension>            <!--扩展名名称-->  
        <mime-type>application/mspowerpoint</mime-type>         <!--MIME格式-->  
    </mime-mapping>  
    <!---------------------------------------------------欢迎页面配置  ------------------------------------------------------>  
    <welcome-file-list><!--定义首页列单.-->  
        <welcome-file>/index.jsp</welcome-file> <!--用来指定首页文件名称.我们可以用<welcome-file>指定几个首页,而服务器会依照设定的顺序来找首页.-->  
        <welcome-file>/index.html</welcome-file>  
    </welcome-file-list>  
    <!---------------------------------------------------配置错误页面------------------------------------------------------>  
    <error-page>  <!--将错误代码(Error Code)或异常(Exception)的种类对应到web应用资源路径.-->  
        <error-code>404</error-code>        <!--HTTP Error code,例如: 404、403-->  
        <location>error.html</location>         <!--用来设置发生错误或异常时要显示的页面-->  
    </error-page>  
    <error-page>  
        <exception-type>java.lang.Exception</exception-type><!--设置可能会发生的java异常类型,例如:java.lang.Exception-->  
        <location>ExceptionError.html</location>            <!--用来设置发生错误或异常时要显示的页面-->  
    </error-page>  
</web-app>  
```
二  spring-mvc.xml
需要实现基本功能的配置 
1 配置 <mvc:annotation-driven/>
2 配置 <context:component-scan base-package="com.springmvc.controller"/> //配置controller的注入
3 配置视图解析器
<mvc:annotation-driven/>   相当于注册了DefaultAnnotationHandlerMapping(映射器)和AnnotationMethodHandlerAdapter(适配器)两个bean.即解决了@Controller注解的使用前提配置。 
context:component-scan  对指定的包进行扫描，实现注释驱动Bean定义，同时将bean自动注入容器中使用。即解决了@Controller标识的类的bean的注入和使用。
```
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns:aop="http://www.springframework.org/schema/aop"  
    xmlns:mvc="http://www.springframework.org/schema/mvc"  
    xmlns:context="http://www.springframework.org/schema/context"  
    xmlns:tx="http://www.springframework.org/schema/tx"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans-4.1.xsd  
        http://www.springframework.org/schema/tx  
        http://www.springframework.org/schema/tx/spring-tx-4.1.xsd  
        http://www.springframework.org/schema/aop  
        http://www.springframework.org/schema/aop/spring-aop-4.1.xsd  
        http://www.springframework.org/schema/context  
        http://www.springframework.org/schema/context/spring-context-4.1.xsd  
        http://www.springframework.org/schema/mvc  
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">  
     
   <!-- 1、配置映射器与适配器 -->  
   <mvc:annotation-driven></mvc:annotation-driven>  
     
   <!-- 2、视图解析器 -->  
   <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">  
   <span style="white-space:pre"> </span><!-- 前缀和后缀 -->  
     <property name="prefix" value="/"/>  
     <property name="suffix" value=".jsp"/>  
   </bean>  
     
   <!-- 3、自动扫描该包，使SpringMVC认为包下用了@controller注解的类是控制器  -->  
   <context:component-scan base-package="com.rhzh.controller"/>  
</beans>  
```
三  spring-mybatis.xml

需要实现基本功能的配置 

1 配置 <context:component-scan base-package="com.rhzh"/> //自动扫描,将标注Spring注解的类自动转化Bean，同时完成Bean的注入

2 加载数据资源属性文件

3 配置数据源  三种数据源的配置方式 [http://blog.csdn.net/yangyz_love/article/details/8199207](http://blog.csdn.net/yangyz_love/article/details/8199207)

4 配置sessionfactory

5 装配Dao接口

6 声明式事务管理 

7 注解事务切面
```
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
    xmlns:aop="http://www.springframework.org/schema/aop"  
    xmlns:context="http://www.springframework.org/schema/context"  
    xmlns:tx="http://www.springframework.org/schema/tx"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans-4.1.xsd  
        http://www.springframework.org/schema/tx  
        http://www.springframework.org/schema/tx/spring-tx-4.1.xsd  
        http://www.springframework.org/schema/aop  
        http://www.springframework.org/schema/aop/spring-aop-4.1.xsd  
        http://www.springframework.org/schema/context  
        http://www.springframework.org/schema/context/spring-context-4.1.xsd">  
  <!--1 自动扫描 将标注Spring注解的类自动转化Bean-->  
  <context:component-scan base-package="com.rhzh" />  
  <!--2 加载数据资源属性文件 -->  
  <bean id="propertyConfigurer"  
    class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">  
    <property name="location" value="classpath:jdbc.properties" />  
  </bean>  
  <span style="white-space:pre"><!-- 3 配置数据源 --></span>  
  <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"  
    destroy-method="close">  
    <property name="driverClassName" value="${driver}" />  
    <property name="url" value="${url}" />  
    <property name="username" value="${username}" />  
    <property name="password" value="${password}" />  
    <!-- 初始化连接大小 -->  
    <property name="initialSize" value="${initialSize}"></property>  
    <!-- 连接池最大数量 -->  
    <property name="maxActive" value="${maxActive}"></property>  
    <!-- 连接池最大空闲 -->  
    <property name="maxIdle" value="${maxIdle}"></property>  
    <!-- 连接池最小空闲 -->  
    <property name="minIdle" value="${minIdle}"></property>  
    <!-- 获取连接最大等待时间 -->  
    <property name="maxWait" value="${maxWait}"></property>  
  </bean>  
  <!-- 4   配置sessionfactory -->  
  <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">  
    <property name="dataSource" ref="dataSource" />  
    <!-- 自动扫描mapping.xml文件 -->  
    <property name="mapperLocations" value="classpath:com/rhzh/mapping/*.xml"></property>  
  </bean>  
  <!-- 5  装配dao接口 -->  
  <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">  
    <property name="basePackage" value="com.rhzh.dao" /> <!-- DAO接口所在包名，Spring会自动查找其下的类 -->  
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>  
  </bean>  
  <!-- 6、声明式事务管理 -->  
  <bean id="transactionManager"  
    class="org.springframework.jdbc.datasource.DataSourceTransactionManager">  
    <property name="dataSource" ref="dataSource" />  
  </bean>  
  <!-- 7、注解事务切面 --><span style="font-family: Arial, Helvetica, sans-serif;"></span><pre name="code" class="html">   <tx:annotation-driven transaction-manager="transactionManager"/>  
</beans>  
```

下面是需要引入的两个资源属性文件:
jdbc.properties
```
driver=com.mysql.jdbc.Driver  
url=jdbc:mysql://127.0.0.1:3306/ecdatabase?characterEncoding=utf-8  
username=root  
password=admin  
#定义初始连接数  
initialSize=0  
#定义最大连接数  
maxActive=20  
#定义最大空闲  
maxIdle=20  
#定义最小空闲  
minIdle=1  
#定义最长等待时间  
maxWait=60000  
```
log4j.properties
```#定义LOG输出级别  
log4j.rootLogger=INFO,Console,File  
#定义日志输出目的地为控制台  
log4j.appender.Console=org.apache.log4j.ConsoleAppender  
log4j.appender.Console.Target=System.out  
#可以灵活地指定日志输出格式，下面一行是指定具体的格式  
log4j.appender.Console.layout = org.apache.log4j.PatternLayout  
log4j.appender.Console.layout.ConversionPattern=[%c] - %m%n  
  
#文件大小到达指定尺寸的时候产生一个新的文件  
log4j.appender.File = org.apache.log4j.RollingFileAppender  
#指定输出目录  
log4j.appender.File.File = logs/ssm.log  
#定义文件最大大小  
log4j.appender.File.MaxFileSize = 10MB  
# 输出所以日志，如果换成DEBUG表示输出DEBUG以上级别日志  
log4j.appender.File.Threshold = ALL  
log4j.appender.File.layout = org.apache.log4j.PatternLayout  
log4j.appender.File.layout.ConversionPattern =[%p] [%d{yyyy-MM-dd HH\:mm\:ss}][%c]%m%n  
```
以上仅仅是对于框架中的配置文件进行解释说明,整合框架并不是能够运行就OK了,了解其中的原理以便于以后发现问题和解决问题,不要急于求成,一步一步走踏实了,你会发现你比别人走的更快!!!

这是从我的csdn搬过来的文章。csdn用来做算法了.
