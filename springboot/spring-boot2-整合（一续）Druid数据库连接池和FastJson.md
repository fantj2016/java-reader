本项目教程环境建立在[spring boot2 整合（一）Mybatis （特别完整！）](https://www.jianshu.com/p/c15094bd1965)
的基础上。

###1. Druid配置

######1.1 修改pom.xml
```
		<!-- alibaba的druid数据库连接池 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.0.11</version>
		</dependency>
```
######1.2. 添加yml配置属性

```
# 使用druid数据源
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.jdbc.Driver
    platform: mysql
    # 下面为连接池的补充设置，应用到上面所有数据源中
    # 初始化大小，最小，最大
    initialSize: 1
    minIdle: 3
    maxActive: 20
    # 配置获取连接等待超时的时间
    maxWait: 60000
    # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    timeBetweenEvictionRunsMillis: 60000
    # 配置一个连接在池中最小生存的时间，单位是毫秒
    minEvictableIdleTimeMillis: 30000
    validationQuery: select 'x'
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    # 打开PSCache，并且指定每个连接上PSCache的大小
    poolPreparedStatements: true
    maxPoolPreparedStatementPerConnectionSize: 20
    # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
    filters: stat,wall,slf4j
    # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
    # 合并多个DruidDataSource的监控数据
    #useGlobalDataSourceStat: true
```
注意这些属性都是spring.datasource下的子属性。
######1.3. 添加druid的配置类文件
DruidConfiguration .java
```
package com.fantj.config;

/**
 * druid 连接池配置类
 * Created by Fant.J.
 */
@Configuration
public class DruidConfiguration {
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.initialSize}")
    private int initialSize;
    @Value("${spring.datasource.minIdle}")
    private int minIdle;
    @Value("${spring.datasource.maxActive}")
    private int maxActive;
    @Value("${spring.datasource.maxWait}")
    private int maxWait;
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;
    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;
    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;
    @Value("${spring.datasource.filters}")
    private String filters;
    @Value("{spring.datasource.connectionProperties}")
    private String connectionProperties;

    @Bean   //声明其为Bean实例
    @Primary //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(this.dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            System.err.println("druid configuration initialization filter: "+ e);
        }
        datasource.setConnectionProperties(connectionProperties);
        return datasource;
    }
    @Bean
    public ServletRegistrationBean statViewServlet(){
        //创建servlet注册实体
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        //设置ip白名单
        servletRegistrationBean.addInitParameter("allow","127.0.0.1");
        //设置ip黑名单
        servletRegistrationBean.addInitParameter("deny","192.168.0.2");
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername","druid");
        servletRegistrationBean.addInitParameter("loginPassword","123456");
        //是否可以重置数据
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean statFilter(){
        //创建过滤器
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //设置过滤器过滤路径
        filterRegistrationBean.addUrlPatterns("/*");
        //忽略过滤的形式
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}

```

注意该代码段中有
```
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername","druid");
        servletRegistrationBean.addInitParameter("loginPassword","123456");
```
这是druid监控端的账号和密码，其登录入口的官方url为：[127.0.0.1:8080/druid/login.html](http://127.0.0.1:8080/druid/login.html)
![](https://upload-images.jianshu.io/upload_images/5786888-ee89e6b5e5e954ab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


###2. FastJson配置
######2.1 添加pom依赖
```
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.39</version>
		</dependency>
```
######2.2 增加fastJson配置文件
方法一：继承extends WebMvcConfigurerAdapter
FastJsonConfiguration .java
```
package com.fantj.config;
/**
 * fastJson 配置类
 * Created by Fant.J.
 */
@Configuration
public class FastJsonConfiguration extends WebMvcConfigurerAdapter{
    /**
     * 修改自定义消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
        super.configureMessageConverters(converters);
        //1.创建一个convert消息转换对象
        FastJsonHttpMessageConverter fastConvert = new FastJsonHttpMessageConverter();
        //2.创建一个fastJson的配置对象,然后配置格式化信息
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //3.在convert中添加配置信息
        fastConvert.setFastJsonConfig(config);
        //4.将convert添加到converts里面
        converters.add(fastConvert);
    }
}

```
方法二：覆盖方法configureMessageConverters
```
public HttpMessageConverters fastJsonHttpMessageConverters(){
//1.创建一个convert消息转换对象
FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//2.创建一个fastJson的配置对象,然后配置格式化信息
FastJsonConfig fastJsonConfig = new FastJsonConfig();
fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
//3.在convert中添加配置信息
fastConverter.setFastJsonConfig(fastJsonConfig);
HttpMessageConverter<?> converters = fastConverter;
return new HttpMessageConverters(converters);
}
```

######2.3 修改pojo

```
@Data
public class User {
    private Integer id;

    private String username;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date birthday;

    private String sex;

    private String address;
}
```
注意` @JSONField(format="yyyy-MM-dd HH:mm:ss")`，如果它能显示出效果，证明druid配置成功。
![](https://upload-images.jianshu.io/upload_images/5786888-60822b4eccac4e03.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
