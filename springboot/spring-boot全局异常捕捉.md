>Spring有帮我们做异常处理（页面跳转）有兴趣的可以研究研究`BasicErrorController`这个类，这是springboot处理异常的源码，它的原理很简单，就是判断请求头：Accept 是否是text/html，如果是返回view，如果不是返回json。但是我们发现它的报错信息不太符合开发，所以我们需要自定义报错信息。

###1. 首先创建自定义异常

创建一个自定义异常。

```
package com.laojiao.securitydemo.myexception;

/**
 * Created by Fant.J.
 */
public class UserNotExistException extends RuntimeException {

    private String id;

    public UserNotExistException(String id) {
        super("user not exist");
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

```

###2. 创建异常处理类

```
package com.laojiao.securitydemo.myexception;

/**
 * 错误处理器:处理其他Controller出的异常
 * Created by Fant.J.
 */
@ControllerAdvice
public class ControllerException {

    @ExceptionHandler(UserNotExistException.class)   //需要处理的 异常类
    @ResponseBody   //json格式
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //响应状态：服务器内部错误异常
    public Map<String,Object> handleUserNotExistException(UserNotExistException ex){

        Map<String,Object> result = new HashMap<>();
        result.put("id",ex.getId());
        result.put("message",ex.getMessage());
        return result;
    }
}

```

我在这里返回json，所以用了` @ResponseBody `注解。

![](https://upload-images.jianshu.io/upload_images/5786888-f62c4b2066698c9e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
