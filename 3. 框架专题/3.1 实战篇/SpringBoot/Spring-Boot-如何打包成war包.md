因为springboot内嵌tomcat，所以直接打的war包肯定不能使用，所以我们必须做以下操作。
###1. 修改pom的package 为war
```
    <packaging>war</packaging>
```
###2. 增加ServletInitializer 类
```
import org.springframework.boot.builder.SpringApplicationBuilder;  
import org.springframework.boot.context.web.SpringBootServletInitializer;  

public class ServletInitializer extends SpringBootServletInitializer {  

    @Override  
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {  
        return application.sources(Application.class);  
    }  

}  
```
注意：Application.class 是你的springboot项目的启动类
