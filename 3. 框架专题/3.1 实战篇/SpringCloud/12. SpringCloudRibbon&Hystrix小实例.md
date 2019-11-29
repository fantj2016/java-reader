###1.添加pom
```
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix</artifactId>
		</dependency>
```
###2.启动类上添加注解
`@EnableCircuitBreaker`
附图：
      ![image.png](http://upload-images.jianshu.io/upload_images/5786888-0985ad4e8e390c87.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###3.在@RequestMapping上添加注解`@HystrixCommand(fallbackMethod = "findByIdFallback")`
附图：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-498e541363298356.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

源码：
```
package com.fantj.fantjconsumermovieribbon.controller;

import com.fantj.fantjconsumermovieribbon.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Fant.J.
 * 2017/11/11 17:43
 */
@RestController
public class MovieController {
    @Autowired
    private RestTemplate template;

@HystrixCommand(fallbackMethod = "findByIdFallback")
@RequestMapping("/movie/{id}")
public User findById(@PathVariable Long id){
        return this.template.getForObject("http://provider-user3/simple/"+id,User.class);
    }

    public User findByIdFallback(Long id){
        User user = new User();
        user.setId(id);
        return user;
    }
}

```
其中，`fallbackMethod = "findByIdFallback"`表示断路器启用后调用`findByIdFallback`方法。
从代码中可以看出，我在这里是通过ribbon访问`provider-user3/simple/"+id`这个服务内容，如果正常访问的话，会调用`provider-user3`服务中的`/simple/"+id`方法。
eureka：![image.png](http://upload-images.jianshu.io/upload_images/5786888-7890fa3cffe53b9c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######1. 访问provider-user3/：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7e420d5d9c3abbaa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

######2. 访问hystrix：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-70e9d9cc1287e367.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

######3. 然后我停止`provider-user3/`服务：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-6900acdc76d8306f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######4. 最后访问hystrix:
![image.png](http://upload-images.jianshu.io/upload_images/5786888-6489905ddbf82955.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

说明我们的hystrix起到了作用。（调用了fallback方法）

####小知识点
访问hystrix服务下的  `/health`  可以查看健康启动状况.
![image.png](http://upload-images.jianshu.io/upload_images/5786888-41150b3fbd661de2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* 我在这里开启eureka、provider-user3/、Hystrix 三个服务，请求hystrix的health
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7f5ea0b6b8933299.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* 然后我关掉provider-user3服务，再请求/health
![image.png](http://upload-images.jianshu.io/upload_images/5786888-2818a6ad9fccd585.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
说明了hystrix已启用。
```
 "hystrix": {
    "status": "CIRCUIT_OPEN",
    "openCircuitBreakers": Array[1][
      "MovieController::findById"           //说明断路器回调到MovieController的findById方法。
    ]
  }
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-70d243e174fcb11d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
还有`/hystrix.stream`监控信息（不用，会被dashboard代替）
![image.png](http://upload-images.jianshu.io/upload_images/5786888-386ac9704c2c3418.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**注意**：
/health  必须要有actuator的依赖支持
```
        <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```



