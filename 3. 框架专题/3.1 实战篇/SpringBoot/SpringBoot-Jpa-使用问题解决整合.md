##### 1. 启动项目报错 Not a managed type: class com.xzxx
这种问题一般出在多模块开发中的依赖传递导致的问题，该问题的原因是启动类找不到对应的bean。所以需要在启动类上添加注解`@EntityScan("com.xxxx`.

##### 2. 启动项目报错 Cannot determine embedded database driver class for database type NONE  
springboot启动时会自动注入数据源和配置jpa，但是web项目里不需要配置 数据库连接信息。所以需要在启动注解上修改`@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})`
