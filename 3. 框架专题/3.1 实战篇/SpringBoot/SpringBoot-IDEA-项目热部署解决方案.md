第一步：在项目pom文件中导入依赖
```
<dependency>
    <!--Spring 官方提供的热部署插件 -->
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <version>1.4.3.RELEASE</version>
</dependency>
```
第二步：修改Intellij IDEA的配置
`CTRL +SHIFT +A` 查找 `make project automatically` 并选中
`CTRL +SHIFT+A `查找`Registry` 找到选项`compile.automake.allow.when.app.running`
重启IDEA后启动项目就可以在IDEA中修改代码和静态页面模板，无需再重启SpringBoot项目了
