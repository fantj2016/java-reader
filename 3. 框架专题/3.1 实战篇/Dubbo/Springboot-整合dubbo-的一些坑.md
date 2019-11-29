>最近在做一个分布式的web系统，用的boot+dubbo，中间碰到不少坑，最近碰到的坑可是坑了我三四天，把坑都给大家捎带分享一下，希望能少走弯路。

### 1. 坑一：与jpa的不兼容

如果你想写这样的jpasql
```
    @Query("select g.userIdentity from  GroupMembers g where g.userId=?1 and g.groupId=?2")
    int selectIndentity(Integer userId,Integer groupId);
```
恭喜你，你可以成功的运行它。但是如果你想要这样
```
    @Transactional
    @Modifying
    @Query("update GroupMembers g set g.userIdentity=1 where g.userId=?1 and g.groupId=?2")
    int updateIdentity(Integer userId,Integer groupId);
```
那么，对不起！你不可以。如果这样做，你会发现你注册不了服务，消费者也相应的得到nullpointexception异常，这一点你可以在dubbo-admin中清晰的看到--没有提供服务。但是单独一个模块跑起来是没有问题的，测试也没有问题的，就是注册不进去~~

想知道为啥的请看第二点~

### 2. 坑二：service层加事务报空指针
截图我不发了，我可不想把事发场景还原，毕竟花了我几天的精力和头皮营养。

之所以说这个bug费时费力，是因为这个项目可以运行，测试全都通过，但是就是注册不进去dubbo，配置和别的模块一模一样也无动于衷！就是注册不进去dubbo,就是注册不进去dubbo,就是注册不进去dubbo.

为什么呢？为什么呢？

如果给service层加事务，同样服务也注册不进dubbo，那web层的调用当然也是返回空指针。那原因是为什么呢？

我们看一下dubbo的@Service这个注解源码：

```

package com.alibaba.dubbo.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Service {
    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "";

    String group() default "";

    String path() default "";

    boolean export() default false;

    String token() default "";

    boolean deprecated() default false;

    boolean dynamic() default false;

    String accesslog() default "";

    int executes() default 0;

    boolean register() default false;

    int weight() default 0;

    String document() default "";

    int delay() default 0;

    String local() default "";

    String stub() default "";

    String cluster() default "";

    String proxy() default "";

    int connections() default 0;

    int callbacks() default 0;

    String onconnect() default "";

    String ondisconnect() default "";

    String owner() default "";

    String layer() default "";

    int retries() default 0;

    String loadbalance() default "";

    boolean async() default false;

    int actives() default 0;

    boolean sent() default false;

    String mock() default "";

    String validation() default "";

    int timeout() default 0;

    String cache() default "";

    String[] filter() default {};

    String[] listener() default {};

    String[] parameters() default {};

    String application() default "";

    String module() default "";

    String provider() default "";

    String[] protocol() default {};

    String monitor() default "";

    String[] registry() default {};
}

```

发现它不支持被子类继承，所以dubbo扫描的时候不能够扫到被注解的服务。

##### 解决方案：修改Service源码
怎么修改呢。我们找到Service这个注解类，位于`com.alibaba.dubbo.config.annotation;` 
###### 1. 给它添加一个注解`@Inherited`
```
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
```
###### 2. 重编译生成class文件
这个不用说了吧  `javac Service.java`.
然后把class覆盖上去。

###### 3. maven项目扔到服务器又变了啊

所以，我们需要把这个单独作为jar包放到项目中。然后maven引用本地jar，而不是从仓库中获取。
```
        <dependency>
            <groupId>com.xxx</groupId>
            <!--自定义-->
            <artifactId>xxxx</artifactId>
            <!--自定义-->
            <version>1.0</version>
            <!--自定义-->
            <scope>system</scope>
            <!--system，类似provided，需要显式提供依赖的jar以后，Maven就不会在Repository中查找它-->
            <systemPath>${pom.basedir}/src/main/webapp/WEB-INF/lib/xxx-xxx.jar</systemPath>
            <!--项目根目录下的lib文件夹下-->
        </dependency>
```
###### 4. 修改后遗症
如果你真的像我上面所讲的修改了dubbo的Service类，那你注册的时候会发现一个问题，也是找不到服务，但是这次不一样了，你如果在dubbo-admin上看就很清楚。
![](https://upload-images.jianshu.io/upload_images/5786888-5805391f30411c02.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

天呐，服务名是springProxy，所以我们修改了源码后必须要手动生命Service名称。`@Service(interfaceName="com.xx.xxx.service.xxx")`



#### 坑一坑二的总结
个人认为坑一坑二是同一个问题，jpa出了问题后因为时间关系我还是尽快选择了mybatis替代的方案，所以解决了坑二后并没有去试坑一是否能解决。不过想想，jpa的修改必须要@Modifying 和事务，在原生的dubbo的@Service注解下都是不认识的，可能会有用。试过的小伙伴可以在评论分享下你的成果，谢谢！


### 3. 坑三：service层缓存报异常

我忘了是什么错了，没有及时记录下来，当时在网上查，前辈说的是因为加了redis后service接口不符合dubbo规范，也就是dubbo不能识别这个接口，所以我最终把cache的实现放在了controller里。


### 最完美的解决方案：[最新官方版的SpringBoot 整合 Dubbo](https://www.jianshu.com/p/b462b8cb99ce)
