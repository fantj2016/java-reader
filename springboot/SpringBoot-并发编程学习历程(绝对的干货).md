>如果一个项目总用单线程来跑，难免会遇到一些性能问题，所以再开发中，我们应该尽量适量的使用多线程（在保证线程安全的情况下）。

本教程大概目录：
1. 模拟单线程情节
2. 用Callable实现 并发编程
3. 用DeferedResult实现异步处理


###模拟单线程情节

```
/**
 * Created by Fant.J.
 */
@RestController
@Slf4j
public class AsyncController {

    /**
     * 单线程测试
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/order")
    public String order() throws InterruptedException {
        log.info("主线程开始");
        Thread.sleep(1000);
        log.info("主线程返回");
        return "success";
    }
}
```
我们把线程休息一秒当作模拟处理业务所花费的时间。很明显能看出来，这是个单线程。
![](https://upload-images.jianshu.io/upload_images/5786888-37af9e86ac5785cf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
`nio-8080-exec-1`表示主线程的线程1。
![](https://upload-images.jianshu.io/upload_images/5786888-fd64232446f15343.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 用Callable实现 并发编程
```
    /**
     * 用Callable实现异步
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/orderAsync")
    public Callable orderAsync() throws InterruptedException {
        log.info("主线程开始");
        Callable result = new Callable() {
            @Override
            public Object call() throws Exception {
                log.info("副线程开始");
                Thread.sleep(1000);
                log.info("副线程返回");
                return "success";
            }
        };
        log.info("主线程返回");
        return result;
    }

```
![](https://upload-images.jianshu.io/upload_images/5786888-60ec91ba57723b27.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-10eee7e882588257.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们可以看到，主线程的开始和返回（结束处理）是首先执行的，然后副线程才执行真正的业务处理。说明主线程在这里的作用是调用(唤醒)子线程，子线程处理完会返回一个Object对象，然后返回给用户。

这样虽然实现了并发处理，但是有一个问题，就是主线程和副线程没有做到完全分离，毕竟是一个嵌套进去的副线程。

所以为了优化我们的实现，我在这里模拟 消息中间件 来实现主线程副线程的完全分离。

### 用DeferedResult实现异步处理
因为本章主要讲的是并发编程原理，所以这里我们不用现成的消息队列来搞，我们模拟一个消息队列来处理。
###### MockQueue .java
```
/**
 * 模拟消息队列 类
 * Created by Fant.J.
 */
@Component
@Slf4j
public class MockQueue {

    //下单消息
    private String placeOrder;
    //订单完成消息
    private String completeOrder;

    public String getPlaceOrder() {
        return placeOrder;
    }

    public void setPlaceOrder(String placeOrder) throws InterruptedException {
        new Thread(()->{
            log.info("接到下单请求"+placeOrder);
            //模拟处理
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //给completeOrder赋值
            this.completeOrder = placeOrder;
            log.info("下单请求处理完毕"+placeOrder);
        }).start();
    }

    public String getCompleteOrder() {
        return completeOrder;
    }

    public void setCompleteOrder(String completeOrder) {
        this.completeOrder = completeOrder;
    }
}

```
注意再setPlaceOrder(String placeOrder)方法里，我创建了一个新的线程来处理接单的操作（为什么要建立新线程，怕主线程在这挂起，此段逻辑也没有线程安全问题，况且异步处理更快）。传进来的参数是个 订单号 ，经过1s的处理成功后，把订单号传给completeOrder 字段，说明用户下单成功，我在下面付controller调用该方法的代码
```
    //注入模拟消息队列类
    @Autowired
    private MockQueue mockQueue;
    @Autowired
    private DeferredResultHolder deferredResultHolder;
    ....
    @RequestMapping("/orderMockQueue")
    public DeferredResult orderQueue() throws InterruptedException {
        log.info("主线程开始");

        //随机生成8位数
        String orderNumber = RandomStringUtils.randomNumeric(8);
        mockQueue.setPlaceOrder(orderNumber);

        DeferredResult result = new DeferredResult();
        deferredResultHolder.getMap().put(orderNumber,result);
        Thread.sleep(1000);
        log.info("主线程返回");

        return result;
    }
```

好了，然后我们还需要一个中介类来存放订单号和处理结果。为什么需要这么一个类呢，因为我们之前说过要实现主线程和副线程分离，所以需要一个中介来存放处理信息（比如：这个订单号信息，和处理结果信息），我们判断处理结果是否为空就知道该副线程执行了没有。所以我们写一个中介类DeferredResultHolder 。

######DeferredResultHolder .java
```

/**
 *  订单处理情况 中介/持有者
 * Created by Fant.J.
 */
@Component
public class DeferredResultHolder {

    /**
     * String： 订单号
     * DeferredResult：处理结果
     */
    private Map<String,DeferredResult> map = new HashMap<>();

    public Map<String, DeferredResult> getMap() {
        return map;
    }

    public void setMap(Map<String, DeferredResult> map) {
        this.map = map;
    }
}

```
在重复一次-.-，为什么需要这么一个类呢，因为我们之前说过要实现主线程和副线程分离，所以需要一个中介来存放处理信息（比如：这个订单号信息，和处理结果信息）,一个订单肯定要对应一个结果。不然岂不是乱了套。

DeferredResult是用来放处理结果的对象。

好了，那新问题又来了，我们怎么去判断订单处理成功了没有，我们此时就需要写一个监听器，过100毫秒监听一次MockQueue类中的completeOrder中是否有值，如果有值，那么这个订单就需要被处理。我们写一个监听器。


######QueueListener .java
```

/**
 * Queue监听器
 * Created by Fant.J.
 */
@Component
@Slf4j
public class QueueListener implements ApplicationListener<ContextRefreshedEvent>{

    @Autowired
    private MockQueue mockQueue;

    @Autowired
    private DeferredResultHolder deferredResultHolder;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        new Thread(()->{
            while(true){
                //判断CompleteOrder字段是否是空
                if (StringUtils.isNotBlank(mockQueue.getCompleteOrder())){

                    String orderNumber = mockQueue.getCompleteOrder();

                    deferredResultHolder.getMap().get(orderNumber).setResult("place order success");

                    log.info("返回订单处理结果");

                    //将CompleteOrder设为空，表示处理成功
                    mockQueue.setCompleteOrder(null);
                }else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

```
![](https://upload-images.jianshu.io/upload_images/5786888-9faf2aa241a46579.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我们可以看到一共有三个不同的线程来处理。
![](https://upload-images.jianshu.io/upload_images/5786888-07da274940b2c7a5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


-------------------------
----------------------------

分割线后，我再给大家带来一批干货，自定义线程池 https://www.jianshu.com/p/832f2b162450


学完这个后，再看下面的。。


我们前面的代码中，有两部分有用new Thread()来创建线程，我们有自己的线程池后，就可以用线程池来分配线程任务了，我在自定义线程里有讲，我用的是第二种配置方法(用@Async注解来给线程 ）。
修改如下：
```
    @Async
    public void setPlaceOrder(String placeOrder) throws InterruptedException {
            log.info("接到下单请求"+placeOrder);
            //模拟处理
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //给completeOrder赋值
            this.completeOrder = placeOrder;
            log.info("下单请求处理完毕"+placeOrder);
    }
```

我们看看效果：
![](https://upload-images.jianshu.io/upload_images/5786888-b34a9ad0649dd668.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

圈红圈的就是我们自己定义的线程池里分配的线程。


谢谢大家！


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
