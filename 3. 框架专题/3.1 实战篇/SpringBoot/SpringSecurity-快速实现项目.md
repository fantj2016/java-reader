我的开发环境是springboot(下面简称sb)+freemarker+maven
>如大家所知道的，sb里面没有配置文件，需要通过类加载或者解析xml来配置一些东西，这也是初学sb的一个头痛点。（我当时就是，哈哈）

好的不多哔哔，直入正题。但是我还是想告诫一些没有sb读配置文件的经验的小司机。可能会不好理解，但是不要放弃，一步一步跟着我来，就可以
####1. pom依赖
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-taglibs</artifactId>
		</dependency>
```
第一个依赖是security的核心依赖包，第二个是security对freemarker的标签的支持包。
加了这两个东西之后，重启项目，它就会弹出让你登录的窗口。
![](http://upload-images.jianshu.io/upload_images/5786888-33a576fdf7d6c0a3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
你可能会迷惑，我什么都没配置，为什么就给我要密码？因为security依赖包只要引入了，它会默认给项目加入密码。用户名是user 默认密码在控制台找下。
![](https://upload-images.jianshu.io/upload_images/5786888-6a6814076befcca1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

实际开发中，我们肯定不是这样让用户认证。所以我们要重写security的实现类。
####2. 继承重写WebSecurityConfigurerAdapter类
```
/**
 *
 * Created by Fant.J.
 * 2017/10/26 18:48
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    //返回 BCrypt  加密对象
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
//    @Autowired
//    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
//    @Autowired
//    private MyAuthenticationFailHandler myAuthenticationFailHandler;


    @Bean
    protected UserDetailsService userDetailServiceImpl(){
        return new UserDetailServiceImpl();
    }
    @Override
    protected void configure(HttpSecurity http)throws Exception{
//        ValidateCodeFilter filter = new ValidateCodeFilter();
//        //写入自定义错误过滤器
//        filter.setAuthenticationFailureHandler(myAuthenticationFailHandler);
        //表单登录
       http.formLogin()         
 //        http.httpBasic()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();

        http
                .headers()
                .frameOptions()
                .sameOrigin();


    }
}
```
 http.formLogin()  表示以表单的形式登录，authorizeRequests()表示什么请求需要验证呢？anyRequest()嗯，所有的请求都需要验证。authenticated();证明是有效的情况下。
自己整段话连起来，大家就懂了这种写法的含义了。
相信一些有心人在上面的类中看到了这段代码
```
    @Bean
    protected UserDetailsService userDetailServiceImpl(){
        return new UserDetailServiceImpl();
    }
```
我们来看下这个UserDetailsService这个官方接口![image.png](http://upload-images.jianshu.io/upload_images/5786888-3caf26e728871478.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  根据方法名我们能看出来，它是用来通过username加载User信息的，这和form验证就关系上了。你只需要把这个类注入到类里，它会自动去找这个实现类，那么下面我们来看下我写的这个实现类。
####3. UserDetailServiceImpl 实现UserDetailsService接口
```

/**
 * Created by Fant.J.
 * 2017/10/26 20:54
 */
@Component
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService{
    @Autowired
    UserAdminMapper userAdminMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /** 超级用户 */
    private static final Integer USER_POWER_ADMIN = 1;
    /** 普通用户 */
    private static final Integer USER_POWER_USER = 2;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserAdmin userAdmin = userAdminMapper.selectByUsername(username);
            log.info("登录用户名" + username);
            log.info("数据库密码" + userAdmin.getPassword());
            if (userAdmin == null) {
                throw new UsernameNotFoundException("没有找到");
            }
            //获取用户使用状态
            boolean status = false;
            if (userAdmin.getUserStatus() == 1){
                status = true;
            }
            if (userAdmin.getUserPower() == 1) {
                log.info("ADMIN用户"+userAdmin.getUserAdminName()+"登录");
                return new User(username, passwordEncoder.encode(userAdmin.getPassword()),
                        status, true, true, true,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN"));
            } else if (userAdmin.getUserPower() == 2) {
                log.info("ADMIN用户"+userAdmin.getUserAdminName()+"登录");
                return new User(username, passwordEncoder.encode(userAdmin.getPassword()),
                        true, true, true, true,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
            } else {
                log.error("用户权限错误异常,默认为USER普通用户");
                return new User(username, passwordEncoder.encode(userAdmin.getPassword()),
                        true, true, true, true,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
            }
        }catch (Exception e){
            log.error("用户权限错误异常"+e.getMessage());
            throw new MyException("用户权限错误异常");
        }
    }
}
```
返回类型必须是UserDetails（官方提供类）,来看下它的源码
![image.png](http://upload-images.jianshu.io/upload_images/5786888-fdd6ad33b9b5c23d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到里面包含了账号密码，和是否过期，是否可用，是否被锁，是否有效四个状态。所以我代码里有
` return new User(username, passwordEncoder.encode(userAdmin.getPassword()),
                        status, true, true, true,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN"));`
大家就能看懂了，`AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN"));`这个是自定义添加的用户身份，再开发过程中可把它封装起来。（我在这里由于数据库设计原因定成死的），说明它是个admin身份。`passwordEncoder.encode`是security提供的加密，（挺高端的，随机生成盐然后插入到密码里。每次登录都不一样，还提供了passwordEncoder.match()方法，两者联合判断用户是否有效），不多说  看源码
![image.png](http://upload-images.jianshu.io/upload_images/5786888-d137a2e366784449.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后你返回去看我的SecurityConfig类，你会找到
```    //加密
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
```
这个注入类就是获取impl中从数据库里查出来的密码，然后match判断。所以就要求我们注册用户的时候，先encode()一下再存入数据库，然后读出来直接返回，我的这个impl是从数据库里读出密码然后加密的，因为我怕数据库密码我都会忘记。。
按照需求来吧。
####4. 启动项目
![image.png](http://upload-images.jianshu.io/upload_images/5786888-6d88b892b0990113.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
security默认有个form表单提交页面。我们输入错误的密码。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-f46b1addad79bb05.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
输入错误的账号
![image.png](http://upload-images.jianshu.io/upload_images/5786888-a657a862a52d3271.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
看上去没有提示，因为我们没有捕获这个异常。
我们输入正确的帐号密码
![image.png](http://upload-images.jianshu.io/upload_images/5786888-2c90c22d9317f99b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
进来了。但是测试的时候你会发现个问题。表单提交的时候会报403无权限访问异常
。好好想想我们引入了两个依赖包，第二个还没用呢！
在每个有form提交的地方都加上
`<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>`
这是security框架token认证需要的东西。




