>什么是jwt，即 json web token。JWT是一种用于双方之间传递安全信息的简洁的、URL安全的表述性声明规范。也是一种token，但是和token有一些不同。

### jwt优点：
1. 自包含
2. 防篡改
3. 可自定义扩展

### JWT的结构
JWT包含了使用.分割的三部分：
1. Header 头部
2. Payload 负载
3. Signature 签名

比如：`eyJpc3MiOiJKb2huIFd1IEpXVCIsImlhdCI6MTQ0MTU5MzUwMiwiZXhwIjoxNDQxNTk0NzIyLCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJqcm9ja2V0QGV4YW1wbGUuY29tIiwiZnJvbV91c2VyIjoiQiIsInRhcmdldF91c2VyIjoiQSJ9`

### 三个新东西
1. 什么是自包含？
字符串里包含用户信息。
2. 什么是防篡改？
签名用于验证消息的发送者以及消息是没有经过篡改的。
3. 可扩展是什么意思？
你可以在Payload部分加入自己想加入的json字符串


##### 那我们既然token的实现方式那么优秀，为什么还要有jwt呢，这就需要了解他们的生成机制。
token生成的其实就是一个UUID，和业务没有丝毫的关系，这样带来最大的问题，就是需要人工持久化处理token（像处理分布式下的sessionId一样）。但是jwt就不需要，因为自包含，所以token里有身份验证信息，不需要做后台持久化处理，前端每次请求被保护的资源时请求头里带上该token就可以实现。


好了开始正文。

本文建立在第三节的基础上。[这里是顺风车](https://www.jianshu.com/p/19059060036b)

### 1. 新增JwtTokenConfig
```
    @Configuration
    public class JwtTokenConfig{

        @Bean
        public TokenStore jwtTokenStore(){
            return new JwtTokenStore(jwtAccessTokenConverter());
        }

        /**
         * token生成处理：指定签名
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter(){
            JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
            accessTokenConverter.setSigningKey("internet_plus");
            return accessTokenConverter;
        }
    }
```
jwtTokenStore方法返回一个TokenStore对象的子对象JwtTokenStore，供给认证服务器取来给授权服务器端点配置器，通俗点就是让MyAuthorizationServerConfig能注入到值。

jwtAccessTokenConverter方法是根据签名生成JwtToken，同样也需要在MyAuthorizationServerConfig类里注入。

### 修改MyAuthorizationServerConfig 的 configure方法
```
/**
 * 认证服务器
 * Created by Fant.J.
 */
@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;



    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
    }

    /**
     * 客户端配置（给谁发令牌）
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(ConsParams.Auth.GET_CLIENT_ID)
                .secret(ConsParams.Auth.GET_SECRET)
                //有效时间 2小时
                .accessTokenValiditySeconds(ConsParams.Auth.GET_TOKEN_VALIDITY_SECONDS)
                //密码授权模式和刷新令牌
                .authorizedGrantTypes(ConsParams.Auth.GE_TAUTHORIZED_GRANT_TYPES)
                .scopes(ConsParams.Auth.GE_TSCOPES);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .accessTokenConverter(jwtAccessTokenConverter);
        }
    }
}

```
和第三节不同的是 endpoints添加了accessTokenConverter属性，它规定了token生成器是jwtAccessTokenConverter，并按照我们设置的签名来生成。

### 启动项目 
###### 给/oauth/token  发送post请求获取token
请求头：Authorization:Basic +clientid:secret 的base64加密字符串  (认证服务器中设置的client信息)
请求参数：username     password    (用户登陆账号密码)
![](https://upload-images.jianshu.io/upload_images/5786888-c152035a87ecfb17.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 2. JWT扩展

在前文我们介绍过jwt的可扩展性，一般情况下实现jwt就可以了，但是有一些特殊需求，比如你想在返回的token中附带一些别的信息，这就需要我们对jwt进行扩展。


#### 2.1 修改JwtTokenConfig
新增jwtTokenEnhancer方法 
```
@Configuration
    public class JwtTokenConfig{

        @Bean
        public TokenStore jwtTokenStore(){
            return new JwtTokenStore(jwtAccessTokenConverter());
        }

        /**
         * token生成处理：指定签名
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter(){
            JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
            accessTokenConverter.setSigningKey("internet_plus");
            return accessTokenConverter;
        }

        @Bean
        public TokenEnhancer jwtTokenEnhancer(){
                return new JwtTokenEnhancer();
        }
    }
```

jwtTokenEnhancer方法 返回一个JwtTokenEnhancer并交给bean工厂。

### 2.2 增加JwtTokenEnhancer类
```
/**
 * Jwt token 扩展
 * Created by Fant.J.
 */
public class JwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        Map<String,Object> info = new HashMap<>();
        info.put("provider","Fant.J");
        //设置附加信息
        ((DefaultOAuth2AccessToken)oAuth2AccessToken).setAdditionalInformation(info);
        return oAuth2AccessToken;
    }
}

```

重写TokenEnhancer的enhance方法，根据需求扩展jwt。

### 2.3 修改MyAuthorizationServerConfig类
```
/**
 * 认证服务器
 * Created by Fant.J.

 */
@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private TokenEnhancer jwtTokenEnhancer;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
    }

    /**
     * 客户端配置（给谁发令牌）
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(ConsParams.Auth.GET_CLIENT_ID)
                .secret(ConsParams.Auth.GET_SECRET)
                //有效时间 2小时
                .accessTokenValiditySeconds(ConsParams.Auth.GET_TOKEN_VALIDITY_SECONDS)
                //密码授权模式和刷新令牌
                .authorizedGrantTypes(ConsParams.Auth.GE_TAUTHORIZED_GRANT_TYPES)
                .scopes(ConsParams.Auth.GE_TSCOPES);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancerList = new ArrayList<>();
            enhancerList.add(jwtTokenEnhancer);
            enhancerList.add(jwtAccessTokenConverter);
            enhancerChain.setTokenEnhancers(enhancerList);


            endpoints
                    .tokenEnhancer(enhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter);
    }
}
```
endpoints的tokenEnhancer方法需要我们提供一个token增强器链对象TokenEnhancerChain，所以我们需要在链中加入我们重写的TokenEnhancer和jwtAccessTokenConverter，然后放入endpoints。

### 启动项目
和上文获取token方式相同。

下图红框便是我添加的扩展。

![](https://upload-images.jianshu.io/upload_images/5786888-dcf7f44ca18c7950.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 解析jwt，获取扩展信息

如果我们需要获取jwt本来就有的信息，我们直接请求方法中吧Authentication当做参数，就可以获取到jwt原始信息。

如果我们需要获取jwt扩展的信息，即我们自定义添加的信息，我们需要做这几个操作：
1. pom导入依赖
```
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.7.0</version>
        </dependency>

```
2. UserController.java
```
    @GetMapping("/me")
    public ServerResponse getCurrentUser(Authentication user, HttpServletRequest request) throws UnsupportedEncodingException {

        String s = user.getPrincipal().toString();
        String name = user.getName();
        String header = request.getHeader("Authorization");
        String token = StringUtils.substringAfter(header,"bearer ");
        Claims body = Jwts.parser().setSigningKey(ConsParams.Auth.GET_SIGNING_KEY.getBytes("UTF-8"))
                .parseClaimsJws(token).getBody();

        String username = (String) body.get("username");
        log.info("解析token获取到的username为{}",username);
        log.info("从Authentication里获取到的username为{}",s);
        log.info("从Authentication里获取到的username为{}",name);
        return ServerResponse.createBySuccess(user);
    }
```

![](https://upload-images.jianshu.io/upload_images/5786888-0f843e1fed05e2bf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

###### 底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)









                                                                                                                                              
