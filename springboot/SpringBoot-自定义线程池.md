>我们都知道spring只是为我们简单的处理线程池，每次用到线程总会new 一个新的线程，效率不高，所以我们需要自定义一个线程池。

本教程目录：
1. 自定义线程池
2. 配置spring默认的线程池


###1. 自定义线程池

######1.1 修改application.properties

```
task.pool.corePoolSize=20
task.pool.maxPoolSize=40
task.pool.keepAliveSeconds=300
task.pool.queueCapacity=50
```
######1.2 线程池配置属性类TaskThreadPoolConfig .java
```
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池配置属性类
 * Created by Fant.J.
 */
@ConfigurationProperties(prefix = "task.pool")
public class TaskThreadPoolConfig {
    private int corePoolSize;

    private int maxPoolSize;

    private int keepAliveSeconds;

    private int queueCapacity;
    ...getter and setter methods...
}
```
######1.3 创建线程池  TaskExecutePool .java
```
/**
 * 创建线程池
 * Created by Fant.J.
 */
@Configuration
@EnableAsync
public class TaskExecutePool {
    @Autowired
    private TaskThreadPoolConfig config;

    @Bean
    public Executor myTaskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(config.getCorePoolSize());
        //最大线程数
        executor.setMaxPoolSize(config.getMaxPoolSize());
        //队列容量
        executor.setQueueCapacity(config.getQueueCapacity());
        //活跃时间
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        //线程名字前缀
        executor.setThreadNamePrefix("MyExecutor-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

```
######1.4 创建线程任务
```
/**
 * Created by Fant.J.
 */
@Component
public class AsyncTask {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async("myTaskAsyncPool")  //myTaskAsynPool即配置线程池的方法名，此处如果不写自定义线程池的方法名，会使用默认的线程池
    public void doTask1(int i) throws InterruptedException{
        logger.info("Task"+i+" started.");
    }
}

```
######1.5 修改启动类
给启动类添加注解
```
@EnableAsync
@EnableConfigurationProperties({TaskThreadPoolConfig.class} ) // 开启配置属性支持
``` 
######1.6 测试
```
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AsyncTask asyncTask;

    @Test
    public void AsyncTaskTest() throws InterruptedException, ExecutionException {

        for (int i = 0; i < 100; i++) {
            asyncTask.doTask1(i);
        }
        logger.info("All tasks finished.");
    }
``` 


###2. 配置默认的线程池

我本人喜欢用这种方式的线程池，因为上面的那个线程池使用时候总要加注解`@Async("myTaskAsyncPool")`，而这种重写spring默认线程池的方式使用的时候，只需要加`@Async`注解就可以，不用去声明线程池类。

######2.1 获取属性配置类
这个和上面的TaskThreadPoolConfig类相同，这里不重复

######2.2 NativeAsyncTaskExecutePool.java 装配线程池
```
/**
 * 原生(Spring)异步任务线程池装配类
 * Created by Fant.J.
 */
@Slf4j
@Configuration
public class NativeAsyncTaskExecutePool implements AsyncConfigurer{


    //注入配置类
    @Autowired
    TaskThreadPoolConfig config;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(config.getCorePoolSize());
        //最大线程数
        executor.setMaxPoolSize(config.getMaxPoolSize());
        //队列容量
        executor.setQueueCapacity(config.getQueueCapacity());
        //活跃时间
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        //线程名字前缀
        executor.setThreadNamePrefix("MyExecutor-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    /**
     *  异步任务中异常处理
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {

            @Override
            public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
                log.error("=========================="+arg0.getMessage()+"=======================", arg0);
                log.error("exception method:"+arg1.getName());
            }
        };
    }
}

```
######2.3 线程任务类AsyncTask .java
```
@Component
public class AsyncTask {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    public void doTask2(int i) throws InterruptedException{
        logger.info("Task2-Native"+i+" started.");
    }
}
```
######2.4 测试
```
    @Test
    public void AsyncTaskNativeTest() throws InterruptedException, ExecutionException {

        for (int i = 0; i < 100; i++) {
            asyncTask.doTask2(i);
        }

        logger.info("All tasks finished.");
    }
```
```
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-8] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native6 started.
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-3] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native1 started.
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native7 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native21 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native22 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native23 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native24 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native25 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native26 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native27 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native28 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native29 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native30 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native31 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native32 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native33 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native34 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native35 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native36 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native37 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native38 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native39 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native40 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native41 started.
2018-03-25 21:23:07.657  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native42 started.
2018-03-25 21:23:07.657  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native43 started.
2018-03-25 21:23:07.657  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native44 started.
2018-03-25 21:23:07.657  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native45 started.
2018-03-25 21:23:07.657  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native46 started.
2018-03-25 21:23:07.658  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native47 started.
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-7] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native5 started.
2018-03-25 21:23:07.658  INFO 4668 --- [   MyExecutor-7] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native49 started.
2018-03-25 21:23:07.658  INFO 4668 --- [   MyExecutor-7] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native50 started.
2018-03-25 21:23:07.658  INFO 4668 --- [  MyExecutor-11] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native9 started.
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native4 started.
2018-03-25 21:23:07.659  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native53 started.
2018-03-25 21:23:07.659  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native54 started.
2018-03-25 21:23:07.659  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native55 started.
2018-03-25 21:23:07.659  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native56 started.
2018-03-25 21:23:07.659  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native57 started.
2018-03-25 21:23:07.659  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native58 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native59 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native60 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native61 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native62 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native63 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native64 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native65 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native66 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native67 started.
2018-03-25 21:23:07.660  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native68 started.
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-5] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native3 started.
2018-03-25 21:23:07.655  INFO 4668 --- [   MyExecutor-4] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native2 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-8] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native19 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-2] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native0 started.
2018-03-25 21:23:07.656  INFO 4668 --- [   MyExecutor-3] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native20 started.
2018-03-25 21:23:07.657  INFO 4668 --- [  MyExecutor-10] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native8 started.
2018-03-25 21:23:07.658  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native48 started.
2018-03-25 21:23:07.658  INFO 4668 --- [   MyExecutor-7] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native51 started.
2018-03-25 21:23:07.658  INFO 4668 --- [  MyExecutor-11] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native52 started.
2018-03-25 21:23:07.658  INFO 4668 --- [  MyExecutor-12] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native10 started.
2018-03-25 21:23:07.661  INFO 4668 --- [  MyExecutor-13] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native11 started.
2018-03-25 21:23:07.662  INFO 4668 --- [  MyExecutor-14] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native12 started.
2018-03-25 21:23:07.662  INFO 4668 --- [  MyExecutor-15] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native13 started.
2018-03-25 21:23:07.663  INFO 4668 --- [  MyExecutor-16] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native14 started.
2018-03-25 21:23:07.663  INFO 4668 --- [  MyExecutor-17] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native15 started.
2018-03-25 21:23:07.663  INFO 4668 --- [  MyExecutor-18] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native16 started.
2018-03-25 21:23:07.663  INFO 4668 --- [  MyExecutor-19] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native17 started.
2018-03-25 21:23:07.664  INFO 4668 --- [  MyExecutor-20] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native18 started.
2018-03-25 21:23:07.664  INFO 4668 --- [  MyExecutor-21] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native69 started.
2018-03-25 21:23:07.664  INFO 4668 --- [           main] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native89 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-6] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native90 started.
2018-03-25 21:23:07.664  INFO 4668 --- [  MyExecutor-22] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native70 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-5] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native91 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-5] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native92 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-8] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native93 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-2] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native94 started.
2018-03-25 21:23:07.664  INFO 4668 --- [  MyExecutor-10] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native95 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-3] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native96 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-7] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native98 started.
2018-03-25 21:23:07.664  INFO 4668 --- [   MyExecutor-9] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native97 started.
2018-03-25 21:23:07.664  INFO 4668 --- [  MyExecutor-11] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native99 started.
2018-03-25 21:23:07.664  INFO 4668 --- [           main] com.laojiao.securitydemo.ControllerTest  : All tasks finished.
2018-03-25 21:23:07.666  INFO 4668 --- [  MyExecutor-23] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native71 started.
2018-03-25 21:23:07.667  INFO 4668 --- [  MyExecutor-24] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native72 started.
2018-03-25 21:23:07.667  INFO 4668 --- [  MyExecutor-25] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native73 started.
2018-03-25 21:23:07.669  INFO 4668 --- [  MyExecutor-26] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native74 started.
2018-03-25 21:23:07.669  INFO 4668 --- [  MyExecutor-27] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native75 started.
2018-03-25 21:23:07.673  INFO 4668 --- [  MyExecutor-28] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native76 started.
2018-03-25 21:23:07.674  INFO 4668 --- [  MyExecutor-29] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native77 started.
2018-03-25 21:23:07.674  INFO 4668 --- [  MyExecutor-30] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native78 started.
2018-03-25 21:23:07.676  INFO 4668 --- [  MyExecutor-31] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native79 started.
2018-03-25 21:23:07.677  INFO 4668 --- [  MyExecutor-32] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native80 started.
2018-03-25 21:23:07.677  INFO 4668 --- [  MyExecutor-33] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native81 started.
2018-03-25 21:23:07.677  INFO 4668 --- [  MyExecutor-34] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native82 started.
2018-03-25 21:23:07.678  INFO 4668 --- [  MyExecutor-35] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native83 started.
2018-03-25 21:23:07.679  INFO 4668 --- [  MyExecutor-36] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native84 started.
2018-03-25 21:23:07.679  INFO 4668 --- [  MyExecutor-37] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native85 started.
2018-03-25 21:23:07.679  INFO 4668 --- [  MyExecutor-38] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native86 started.
2018-03-25 21:23:07.680  INFO 4668 --- [  MyExecutor-39] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native87 started.
2018-03-25 21:23:07.680  INFO 4668 --- [  MyExecutor-40] c.l.securitydemo.mythreadpool.AsyncTask  : Task2-Native88 started.
```






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








