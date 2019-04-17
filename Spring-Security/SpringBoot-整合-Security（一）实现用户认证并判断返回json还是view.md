>看这篇文章的我默认都认为是有security基本基础的，因为封装的缘故，没有基础的话很容易被绕晕。但是只要认真看，我相信没什么大问题，如果仅仅是会用，该教程让你更熟悉底层实现，更优雅的写代码。


第一章顺风车：[SpringBoot 整合 Security（一）实现用户认证并判断返回json还是view](https://www.jianshu.com/p/18875c2995f1)
第二章顺风车：[SpringBoot 整合 Security（二）实现验证码登录](https://www.jianshu.com/p/9d08c767b33e)

本教程大概目录：
1. 实现用户认证
2. 实现json请求返回json，网页请求返回网页。



### 1. 添加依赖
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```
### 2. 封装以及实现
因为封装的比较多，实现比较复杂，我想先把项目结构贴出来，然后我说明每个类之间的关系，然后把类一个一个再贴出来，这样大家更容易理解一些（我每个类的注释很全）。
###### 2.1 结构
![](https://upload-images.jianshu.io/upload_images/5786888-b1d3f17472992179.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-fc31f308631e1c35.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

他们都在我的 `com.fantJ`包下。总共也就这么多类，但是关系比较复杂，我先把每个类介绍一下，按照从上到下的顺序。
* MyAuthenticationFailHandler.java 自定义登录失败处理器，如果登录认证失败，会跳到这个类上来处理。
* MyAuthenticationSuccessHandler  自定义登录成功处理器，如果登录认证成功，会运行这个类。
* 我们可以看出，不论登录成功还是失败，都会路过我们自定义的处理器，所以我们可以在这里重写原来的方法，实现根据请求头类型返回相应的 json/view。
* SimpleResponse   返回类类型 POJO（可返回任意类型的结果）（封装字符串、数字、集合等返回类型）
* BrowerSecurityController  登录路径请求类，`.loginPage("/authentication/require")`，是个controller请求类。判断json/html 请求 返回不同的登录认证结果
* BrowserSecurityConfig  Security 配置类，它里面会说明登录方式、登录页面、哪个url需要认证、注入登录失败/成功过滤器
* MyUserDetailsService  加载用户数据 , 返回UserDetail 实例 （里面包含用户信息）。
* BrowserProperties  读取配置文件里的： `fantJ.security.browser.loginPage`等 属性类
* LoginType  登录类型  枚举类
* SecurityProperties  Security 属性 类，读取配置文件里的： `fantJ.security`等属性，里面包含了BrowserProperties 对象。

###### 2.2 代码
我稍微改变下顺序，尽量的有条理性，方便大家理清思路。

1. 我们先写核心配置类，BrowserSecurityConfig .
```
package com.fantJ.browser;
/**
 * Security 配置类
 * Created by Fant.J.
 */
@Configuration
public class BrowserSecurityConfig  extends WebSecurityConfigurerAdapter {

    /**
     * 注入 Security 属性类配置
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 重写PasswordEncoder  接口中的方法，实例化加密策略
     * @return 返回 BCrypt 加密策略
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 注入 自定义的  登录成功处理类
     */
    @Autowired
    private MyAuthenticationSuccessHandler mySuccessHandler;
    @Autowired
    private MyAuthenticationFailHandler myFailHandler;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String redirectUrl = securityProperties.getBrowser().getLoginPage();
        //basic 登录方式
//      http.httpBasic()

        //表单登录 方式
        http.formLogin()
                .loginPage("/authentication/require")
                //登录需要经过的url请求
                .loginProcessingUrl("/authentication/form")
                .successHandler(mySuccessHandler)
                .failureHandler(myFailHandler)
                .and()
                //请求授权
                .authorizeRequests()
                //不需要权限认证的url
                .antMatchers("/authentication/require",redirectUrl).permitAll()
                //任何请求
                .anyRequest()
                //需要身份认证
                .authenticated()
                .and()
                //关闭跨站请求防护
                .csrf().disable();
    }
}

```
我们可以看到，它里面声明了登录页面url、哪个请求不需要认证就能访问、调用自定义成功/失败处理过滤器等，可是说是security的核心配置。

这里面有非常需要注意的一点，就是必须要给loginPage 设置不需要权限认证，否则项目会陷入死锁，调用loginPage受到权限限制，然后返回loginPage，然后又受到限制...循环下去。

2. 上面代码首先就 找loginPage，那我先把.loginPage("/authentication/require")相关的 视图控制器 贴出来

```
package com.fantJ.browser;

/**
 * 判断json/html 请求 返回不同的结果
 * @ 注解@ResponseStatus ：响应状态码 UNAUTHORIZED(401, "Unauthorized")
 * Created by Fant.J.
 */

/**
 * 响应状态码 UNAUTHORIZED(401, "Unauthorized")
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
@RestController
public class BrowerSecurityController {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 重定向 策略
     */
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * 把当前的请求缓存到 session 里去
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    /**
     * 注入 Security 属性类配置
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 当需要身份认证时 跳转到这里
     */
    @RequestMapping("/authentication/require")
    public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //拿到请求对象
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null){
            //获取 跳转url
            String targetUrl = savedRequest.getRedirectUrl();
            logger.info("引发跳转的请求是:"+targetUrl);

            //判断 targetUrl 是不是 .html　结尾, 如果是：跳转到登录页(返回view)
            if (StringUtils.endsWithIgnoreCase(targetUrl,".html")){
                String redirectUrl = securityProperties.getBrowser().getLoginPage();
                redirectStrategy.sendRedirect(request,response,redirectUrl);
            }

        }
        //如果不是，返回一个json 字符串
        return new SimpleResponse("访问的服务需要身份认证，请引导用户到登录页");
    }
}

```
其中最主要的逻辑是判断request请求对象中的getRedirectUrl() 的结果是不是.html 结尾，如果是，则调用sendRedirect(request,response,redirectUrl)方法重定向到redirectUrl页面，其中redirectUrl是我们自定义的登录页面。

如果不是.html 结尾，那就是json请求，我们返回json 字符串提示信息。SimpleResponse 其实就是一个Object对象，然后实现了它的getter setter方法，为的是结构化封装，返回的是对象，而不仅仅是个字符串（返回对象的话，响应格式是：content:访问的服务需要身份认证，请引导用户到登录页 。返回字符串就只是"访问的服务需要身份认证，请引导用户到登录页"，你让接json数据的工作者怎么去接这段字符）。

3. 我就先贴下SimpleResponse 代码，很简单，扫一眼就行
```
package com.fantJ.browser.support;

/**
 * 返回类 工具 （可返回任意类型的结果）
 * Created by Fant.J.
 */
public class SimpleResponse {

    /**
     * 返回 内容 （json格式）
     */
    private Object content;

    public SimpleResponse(Object content) {
        this.content = content;
    }
  ...getter and  setter...
}

```
4. 我们在代码2中的controller中，也调用了 我们自己写的一个类 SecurityProperties，它是用来获取 application.properties 中的配置属性的。
```
package com.fantJ.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security 属性 类
 * Created by Fant.J.
 */
//获取配置属性前缀
@ConfigurationProperties(prefix = "fantJ.security")
public class SecurityProperties {
    /**
     * 浏览器 属性类
     */
    private BrowserProperties browser = new BrowserProperties();

    public BrowserProperties getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserProperties browser) {
        this.browser = browser;
    }
}

```
可以看到。它里面包含了一个对象BrowserProperties ，它也是读取配置属性的一个类
```
package com.fantJ.core.properties;

/**
 * browser(浏览器)配置文件里的： fantJ.security.browser.loginPage 属性类
 * Created by Fant.J.
 */
public class BrowserProperties {

    /**
     *  loginPage 默认值  是login.html
     *  如果 application.properties 里有对 fantJ.security.browser.loginPage 的声明，则获取该值
     */
    private String loginPage = "/browser-login.html";

    /**
     * 默认 返回 json 类型
     */
    private LoginType loginType = LoginType.JSON;

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }
}

```
然后我把配置文件贴出来。
```
#登录页 配置
fantJ.security.browser.loginPage = /demo-signIn.html

# 返回 类型设置（view 还是 json）
fantJ.security.browser.loginType = REDIRECT

```
总的来说，SecurityProperties可以获取到前缀为`fantJ.security`的所有属性，BrowserProperties可以获取到`fantJ.security.browser`下的所有属性，所以BrowserProperties中会有对应的两个字段loginPage 、loginType 。
其中loginType 也是一个封装枚举类，特简单的枚举
```
package com.fantJ.core.properties;

/**
 * 登录类型  枚举类
 * Created by Fant.J.
 */
public enum LoginType {
    REDIRECT,
    JSON
}

```
5. 配置都有了，那接下来应该要写如何去认证用户。**MyUserDetailsService里面可以用来获取数据库中的密码然后打包返回用户信息给security做用户校验使用**，后者校验如果与登录的密码match，如果成功，返回UserDetail对象（用户信息对象）,进入自定义登录成功后处理类MyAuthenticationSuccessHandler。如果失败，直接进入登录失败处理类MyAuthenticationFailHandler。

MyUserDetailsService .java
```
package com.fantJ.browser;
/**
 * UserDetail 类
 * Created by Fant.J.
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

//    @Autowired
//    private //在这里注入mapper，再想ia面根据用户名做信息查找

    /**
     * 重写PasswordEncoder  接口中的方法，实例化加密策略
     * @return 返回 BCrypt 加密策略
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;



    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 加载用户数据 , 返回UserDetail 实例
     * @param username  用户登录username
     * @return  返回User实体类 做用户校验
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("登录用户名:"+username);
        String password = passwordEncoder.encode("123456");
        //User三个参数   (用户名+密码+权限)
        //根据查找到的用户信息判断用户是否被冻结
        logger.info("数据库密码:"+password);
        return new User(username,password,
                true,true,true,true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));

    }
}

```
上面有段代码
```
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
```
PasswordEncoder 是个接口，该接口下有两个方法，一个是encoder 一个是matches，前者用于加密，后者用于匹配校验，我们这里使用的是BCrypt加密算法来实现加密和匹配，所以在这里实现该接口的encoder方法，进行加密。

其次我想说的是重写的loadUserByUsername方法，该方法将用户登录使用的username传进来，然后我们把该用户的密码从数据库取出来，一同打包成User对象，返回给security框架做下一步校验。其中User的几个参数介绍见下面源码：
```
    public User(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
...
}
```
依次是：用户名+密码+可用？+没过期？+授权过期？+不被锁？+用户权限（我在这里是手动加了个权限和密码，自己根据业务修改下）

然后带大家看下security内部是怎样校验用户身份的。
![首先是将身份加入权限列表中](https://upload-images.jianshu.io/upload_images/5786888-abf0a73baa243fa5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-6fc3d6865528841e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-ad41d6cc445813e0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-0aeb7aa1305c69af.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

省略了 很多个步骤，具体的大家可以自己打断点 调试下。

6. 最后我把成功/失败处理器代码贴出来
MyAuthenticationSuccessHandler .java
```
package com.fantJ.browser.authentication;

/**
 * 自定义登录成功处理类
 * Created by Fant.J.
 */
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * json 转换工具类
     */
    private ObjectMapper objectMapper;
    @Autowired
    private SecurityProperties securityProperties;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功");

        //判断是json 格式返回 还是 view 格式返回
        if (LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
            //将 authention 信息打包成json格式返回
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(authentication));
        }else {
            //返回view
            super.onAuthenticationSuccess(request,response,authentication);
        }

    }
}

```
MyAuthenticationFailHandler .java 
```
package com.fantJ.browser.authentication;

/**
 * 自定义登录失败处理器
 * Created by Fant.J.
 */
@Component
public class MyAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * json 转换工具类
     */
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        logger.info("登录失败");

        //如果是json 格式
        if (LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
            //设置状态码
            response.setStatus(500);
            //将 登录失败 信息打包成json格式返回
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(e));
        }else{
            //如果不是json格式，返回view
            super.onAuthenticationFailure(request,response,e);
        }

    }
}

```

他俩再哪里被调用了呢，再BrowserSecurityConfig类里，也就是security启动核心配置类中，注入并
![](https://upload-images.jianshu.io/upload_images/5786888-351008be05e9c3ce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

写起来好麻烦，希望大家能看懂，有什么疑问的可以在下方留言。谢谢大家！


####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

######底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)
