##方法一：设置fallback属性
>Feign Hystrix Fallbacks 官网解释
Hystrix supports the notion of a fallback: a default code path that is executed when they circuit is open or there is an error. To enable fallbacks for a given @FeignClient set the fallback attribute to the class name that implements the fallback.



它说你想要给Feign fallback功能，就给@FeignClient注解设置fallback属性，并且回退类要继承@FeignClient所注解的接口
* 官方实例：
```
@FeignClient(name = "hello", fallback = HystrixClientFallback.class)
protected interface HystrixClient {
    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello iFailSometimes();
}

static class HystrixClientFallback implements HystrixClient {
    @Override
    public Hello iFailSometimes() {
        return new Hello("fallback");
    }
}
```
* 但是如果`HystrixClientFallback `类拿出去单独作为一个类的话，我们就得在该类上添加注解@Component
![UserFeignClient.java.png](http://upload-images.jianshu.io/upload_images/5786888-3e261feb976722f2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![HystrixFallBack.java.png](http://upload-images.jianshu.io/upload_images/5786888-2aa8fe86531b4e7a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

* 最后访问feign端口7901调用的服务
![image.png](http://upload-images.jianshu.io/upload_images/5786888-41e4b1d6ddb0e987.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
访问/health ![image.png](http://upload-images.jianshu.io/upload_images/5786888-17608fddf951dc47.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
访问正常。

* 然后我关闭user服务再进行访问
![image.png](http://upload-images.jianshu.io/upload_images/5786888-90b938d557061c08.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7043a83fc191af5d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

* To disable Hystrix support for Feign, `set feign.hystrix.enabled=false`.
想关闭feign对hystrix的支持的话，在application里配置`feign.hystrix.enabled=false`.
>To disable Hystrix support on a per-client basis create a vanilla Feign.Builder with the "prototype" scope, e.g.:
想要以每个Feign客户端为基础来禁用或开启Hystrix
请在需要禁用的类中添加
```
@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder() {
		return Feign.builder();
	}
```
原理：官网上有
`Feign.Builder feignBuilder: HystrixFeign.Builder`
说明了 Feign 默认是开启HystrixFeign的，默认Builder对象是HystrixFeign，我们在这里只Builder  Feign，所以会达到禁用Hystrix的效果
##方法二：设置fallfactory属性
**提示：**如果fallback默认优先级比fallfactory优先级高。所以二者都存在的话，会访问fallback的回退方法。
这里不做演示。
那么fallback和fallfactory有什么区别呢

不多哔哔，先看代码后加分析。
```
@FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
protected interface HystrixClient {
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	Hello iFailSometimes();
}

@Component
static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
	@Override
	public HystrixClient create(Throwable cause) {
		return new HystrixClientWithFallBackFactory() {
			@Override
			public Hello iFailSometimes() {
				return new Hello("fallback; reason was: " + cause.getMessage());
			}
		};
	}
}
```
这是官网给的demo。我在这里把类都提了出来。
UserFeignClient  接口：
```
package com.fantj.moviefeign.feign;

import com.fantj.fantjconsumermovie.entity.User;
import feign.hystrix.FallbackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fant.J.
 * 2017/12/1 19:33
 */
@FeignClient(value = "provider-user3",fallbackFactory = HystrixFallbackFactory.class)
public interface UserFeignClient {
    @RequestMapping(value = "/simple/{id}",method = RequestMethod.GET)
    User findById(@PathVariable("id") Long id);
}
```
HystrixFallbackFactory 实现FallbackFactory接口
```
package com.fantj.moviefeign.feign;

import com.fantj.fantjconsumermovie.entity.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by Fant.J.
 * 2017/12/3 14:04
 */
@Component
@Slf4j
public class HystrixFallbackFactory implements FallbackFactory<UserFeignClient>{
    @Override
    public UserFeignClient create(Throwable throwable) {
        log.info("fallback; reason was: " + throwable.getMessage());
        return id -> {
            User user = new User();
            user.setId(0L);
            return user;
        };
    }
}
```
注意我在重写create方法中把id返回值固定为0。如果它返回的id是0，证明fallbackfactory启用。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-a6b3c627b2a7dd73.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后create方法中也有获取异常并打印日志。我们看下日志。提示我们访问被拒绝。（之前我手动把3`provider-user3`服务停止）
![image.png](http://upload-images.jianshu.io/upload_images/5786888-0a4bf97581d997fb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####fallback和fallfactory区别：
* fallback**只是重写**了回退**方法**。
* fallfactory层面比较深，因为它**可以调出底层异常**。

