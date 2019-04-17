### 页面缓存

##### 1. freemarker 的页面静态化
application.properties 配置实现浏览器缓存

```
# SPRING RESOURCES HANDLING ([ResourceProperties](https://github.com/spring-projects/spring-boot/tree/v1.5.4.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/ResourceProperties.java))
spring.resources.add-mappings=true # Enable default resource handling.
spring.resources.cache-period= # Cache period for the resources served by the resource handler, in seconds.
spring.resources.chain.cache=true # Enable caching in the Resource chain.
spring.resources.chain.enabled= # Enable the Spring Resource Handling chain. Disabled by default unless at least one strategy has been enabled.
spring.resources.chain.gzipped=false # Enable resolution of already gzipped resources.
spring.resources.chain.html-application-cache=false # Enable HTML5 application cache manifest rewriting.
spring.resources.chain.strategy.content.enabled=false # Enable the content Version Strategy.
spring.resources.chain.strategy.content.paths=/** # Comma-separated list of patterns to apply to the Version Strategy.
spring.resources.chain.strategy.fixed.enabled=false # Enable the fixed Version Strategy.
spring.resources.chain.strategy.fixed.paths=/** # Comma-separated list of patterns to apply to the Version Strategy.
spring.resources.chain.strategy.fixed.version= # Version string to use for the Version Strategy.
spring.resources.static-locations=classpath:/static/

```
这段配置是用来启用资源缓存处理。
借鉴官方文档：https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/

controller中实现页面静态化
```

/**
 * Created by Fant.J.
 */
@RestController
public class HelloController {

    @Autowired
    private Configuration configuration;


    @GetMapping("/hello")
    public String demo(Map<String, Object> map) {
        map.put("name", "demo");
        freeMarkerContent(map,"hello");
        return "hello";
    }

    private void freeMarkerContent(Map<String,Object> root,String ftl){
        try {
            Template temp = configuration.getTemplate(ftl+".ftl");
            //以classpath下面的static目录作为静态页面的存储目录，同时命名生成的静态html文件名称
            String path=this.getClass().getResource("/").getPath()+"templates/"+ftl+".html";
            Writer file = new FileWriter(path);
            temp.process(root, file);
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```
![](https://upload-images.jianshu.io/upload_images/5786888-e284b06f1dd44bb8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 2. thymeleaf 的页面静态化
核心代码的不同：
```
    	SpringWebContext ctx = new SpringWebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
    	String html = thymeleafViewResolver.getTemplateEngine().process("hello", ctx);
```
### 对象缓存
利用Redis实现对象的缓存：

后面我会添加SpringBoot 对 Redis 的集成开发。

### 静态资源优化

##### 1. JS/CSS压缩
取消空格。
##### 2. 多个JS/CSS组合
打包成一个js/css，减少请求次数
##### 3.CDN访问
js/css打包放到cdn上做提速
