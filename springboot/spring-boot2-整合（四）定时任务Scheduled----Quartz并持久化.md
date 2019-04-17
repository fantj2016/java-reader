>在进入正文前，我想把所有java可以实现的定时任务介绍一下，其实这个也是底层实现思路。


**本教程大概目录：**
1. 线程等待实现定时任务
2. 用Timer实现定时任务
3. 用ScheduledExecutorService实现定时任务
4. Quartz 定时任务框架单机应用
5. spingboot2 整合 Scheduled
6. spingboot2 整合 Quartz框架持久化定时任务

###1. 线程等待实现定时任务

```
package com.fantj.myScheduled;

/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            while (true) {
                System.out.println("假设我是个定时任务");
                try {
                    Thread.sleep(1000 * 10);   //线程休息十秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

```
大家看代码也能看出来，如果任务复杂时，是相当的麻烦，而且还存在内存泄露风险，而且是一发不可收拾（不可控）。

下面看一个相对简单的。

###2. Timer

```
package com.fantj.myScheduled;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("TimerTask is called!");
            }
        };

        Timer timer = new Timer();
        /*
         *  参数：1、任务体    2、延时时间（可以指定执行日期）3、任务执行间隔时间
         */
        timer.schedule(task, 0, 1000 * 3);
        timer.scheduleAtFixedRate(task, 0, 1000 * 3);
    }
}

```
注意上面timer调用的两个方法：
1、schedule，如果第一次执行被延时，随后的任务执行时间将以上一次任务实际执行完成的时间为准
2、scheduleAtFixedRate，如果第一次执行被延时，随后的任务执行时间将以上一次任务开始执行的时间为准（需考虑同步）

如果让我粗俗的讲，第一个是等任务进行完了才开始计时，第二个是任务开始运行的时候就计时。

那我们稍微瞄一眼 Timer底层实现
```
public void schedule(TimerTask task, Date firstTime, long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, firstTime.getTime(), -period);
    }
```
然后看看TimerTask类 
```
public abstract class TimerTask implements Runnable {
      public abstract void run();
    ...
}
```

大概也能看出来，也是线程实现。

######Timer也有缺点：

多线程并行处理定时任务时，Timer运行多个TimeTask时，只要其中之一没有捕获抛出的异常，其它任务便会自动终止运行，使用ScheduledExecutorService则没有这个问题。


###3. 那我们就来研究ScheduledExecutorService

```
package com.fantj.myScheduled;

/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("ScheduledExecutorService Task is called!");
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 参数：1、任务体 2、首次执行的延时时间
        //      3、任务执行间隔 4、间隔时间单位
        service.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }
}

```
这个。。就不多说实现原理了Executors线程池都出现了。


###Quartz 定时任务框架
为什么会有定时任务框架呢，大家仔细观察前面的实现案例，没有一个定时任务是可控的，这对开发者来说特别不友好。Quartz就比较nb了，我带大家稍微看看它的一部分方法：
我先大概介绍下Quartz工作原理，JobDetail是写定时任务逻辑，Trigger是一个触发器，用来定义cron和执行次数等。
```
    String getSchedulerName() throws SchedulerException;

    String getSchedulerInstanceId() throws SchedulerException;

    SchedulerContext getContext() throws SchedulerException;

    void start() throws SchedulerException;

    void startDelayed(int var1) throws SchedulerException;

    boolean isStarted() throws SchedulerException;

    void standby() throws SchedulerException;

    boolean isInStandbyMode() throws SchedulerException;

    void shutdown() throws SchedulerException;

    void shutdown(boolean var1) throws SchedulerException;

    boolean isShutdown() throws SchedulerException;

    SchedulerMetaData getMetaData() throws SchedulerException;

    List<JobExecutionContext> getCurrentlyExecutingJobs() throws SchedulerException;

    void setJobFactory(JobFactory var1) throws SchedulerException;

    ListenerManager getListenerManager() throws SchedulerException;

    Date scheduleJob(JobDetail var1, Trigger var2) throws SchedulerException;

    Date scheduleJob(Trigger var1) throws SchedulerException;

    void scheduleJobs(Map<JobDetail, Set<? extends Trigger>> var1, boolean var2) throws SchedulerException;

    void scheduleJob(JobDetail var1, Set<? extends Trigger> var2, boolean var3) throws SchedulerException;

    boolean unscheduleJob(TriggerKey var1) throws SchedulerException;

    boolean unscheduleJobs(List<TriggerKey> var1) throws SchedulerException;

    Date rescheduleJob(TriggerKey var1, Trigger var2) throws SchedulerException;

    void addJob(JobDetail var1, boolean var2) throws SchedulerException;

    void addJob(JobDetail var1, boolean var2, boolean var3) throws SchedulerException;

    boolean deleteJob(JobKey var1) throws SchedulerException;

    boolean deleteJobs(List<JobKey> var1) throws SchedulerException;

    void triggerJob(JobKey var1) throws SchedulerException;

    void triggerJob(JobKey var1, JobDataMap var2) throws SchedulerException;

    void pauseJob(JobKey var1) throws SchedulerException;

    void pauseJobs(GroupMatcher<JobKey> var1) throws SchedulerException;

    void pauseTrigger(TriggerKey var1) throws SchedulerException;

    void pauseTriggers(GroupMatcher<TriggerKey> var1) throws SchedulerException;

    void resumeJob(JobKey var1) throws SchedulerException;

    void resumeJobs(GroupMatcher<JobKey> var1) throws SchedulerException;

    void resumeTrigger(TriggerKey var1) throws SchedulerException;

    void resumeTriggers(GroupMatcher<TriggerKey> var1) throws SchedulerException;

    void pauseAll() throws SchedulerException;

    void resumeAll() throws SchedulerException;

    List<String> getJobGroupNames() throws SchedulerException;

    Set<JobKey> getJobKeys(GroupMatcher<JobKey> var1) throws SchedulerException;

    List<? extends Trigger> getTriggersOfJob(JobKey var1) throws SchedulerException;

    List<String> getTriggerGroupNames() throws SchedulerException;

    Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> var1) throws SchedulerException;

    Set<String> getPausedTriggerGroups() throws SchedulerException;

    JobDetail getJobDetail(JobKey var1) throws SchedulerException;

    TriggerState getTriggerState(TriggerKey var1) throws SchedulerException;

    void resetTriggerFromErrorState(TriggerKey var1) throws SchedulerException;

    boolean interrupt(String var1) throws UnableToInterruptJobException;

    boolean checkExists(JobKey var1) throws SchedulerException;

```
可以看到，基本上对定时任务的控制可以说是很全了。包括增删改查等。
下面是个实例，当然，你需要下载必要的依赖jar，这个可以自行百度下载一下，不做重点解释。
```
package com.fantj.myScheduled;

/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail job = JobBuilder.newJob(Job.class)
                    .withIdentity("job", "group").build();

            // 休眠时长可指定时间单位，此处使用秒作为单位（withIntervalInSeconds）
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger", "group").startNow()
                    .withSchedule(simpleSchedule().withIntervalInSeconds(3).repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);

            // scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}

    class Job implements org.quartz.Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Quartz task is called!");
        }
}
```

### spingboot2 整合 Scheduled
SpringBoot内置了定时任务Scheduled，操作可谓特别简单。
正常引入`spring-boot-starter-web`依赖包即可实现。

##### Scheduled 第一步

再启动类上添加注解`@EnableScheduling`
```
package com.fantj;

@SpringBootApplication
@MapperScan("com.fantj.mapper")
@EnableScheduling  //启动定时任务
public class MybatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisApplication.class, args);
	}
}
```

##### Scheduled 第二步
写Task。即定时任务。
```
package com.fantj.myScheduled;

/**
 * scheduled 定时任务类
 * Created by Fant.J.
 */
@Component
public class Task {


    @Scheduled(cron = "5 0 0 * * ?")
    public void scheduledTask1(){
        System.out.println("scheduledTask method run..");
    }

    @Scheduled(initialDelay =  1000 * 10,fixedDelay = 1000 * 5)
    public void scheduledTask2(){
        System.out.println("scheduledTask method run..");
    }

    @Scheduled(initialDelay =  1000 * 10,fixedDelay = 1000 * 5)
    public void test() throws Exception {
        for (int i = 0;i<20;i++){
            new MailSender()
                    .title("FantJ给你发送的邮件")
                    .content("嘻嘻")
                    .contentType(MailContentTypeEnum.TEXT)
                    .targets(new ArrayList<String>(){{
                        add("xxxxxx@qq.com");
                    }})
                    .send();
            System.out.println("第"+i+"次发送成功!");
        }
    }
}

```

第三个方法是我写的发邮件的一个接口。可以参考我的一篇文章[Java 发送qq邮件 ](https://www.jianshu.com/p/f66553af9a04)


######我介绍下`@Scheduled`注解的三（四）个属性：

* cron： 懂点linux的都知道，没听说过的可以自己百度一下，不难。

* fixedRate和fixedDelay: 这和Timer的两个方法（rate和delay）很相似，如果让我粗俗的讲，第一个是任务开始运行的时候就计时，第二个是等任务进行完了才开始计时。

* initialDelay：该属性的作用是  设置第一次执行延迟时间 。需要配合fixedDelay、fixedRate、crom来使用。


###新问题的思考

虽然上面的方式一直在改进，但是试想一种情况，如果正在执行定时任务的服务器挂掉，那该如何去寻找它之前执行了多少次呢。如果我们把定时任务持久化到数据库，像维护普通逻辑数据那样维护任务，就会避免项目中遇到的种种的特殊情况。


### spingboot2 整合 Quartz框架持久化定时任务
提前声明啊，很麻烦，因为需要加入ioc管理（有部分注释，可尝试看懂），还需要创建quartz需要让我们创建的数据表。
之后就像玩单机（见上文：Quartz 定时任务框架单机应用）一样，玩quartz了。
##### 1. 导入依赖
```
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
        </dependency>

```
提示，本项目是基于springboot2整合mybatic项目下：https://www.jianshu.com/p/c15094bd1965

##### 2. Quartz 注入Spring IOC配置
QuartzConfiguration.java
```
package com.fantj.quartz;

/**
 * quartz定时任务配置
 * Created by Fant.J.
 */
@Configuration
@EnableScheduling
public class QuartzConfiguration
{
    /**
     * 继承org.springframework.scheduling.quartz.SpringBeanJobFactory
     * 实现任务实例化方式
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
            ApplicationContextAware {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        /**
         * 将job实例交给spring ioc托管
         * 我们在job实例实现类内可以直接使用spring注入的调用被spring ioc管理的实例
         * @param bundle
         * @return
         * @throws Exception
         */
        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            /**
             * 将job实例交付给spring ioc
             */
            beanFactory.autowireBean(job);
            return job;
        }
    }

    /**
     * 配置任务工厂实例
     * @param applicationContext spring上下文实例
     * @return
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext)
    {
        /**
         * 采用自定义任务工厂 整合spring实例来完成构建任务
         * see {@link AutowiringSpringBeanJobFactory}
         */
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * 配置任务调度器
     * 使用项目数据源作为quartz数据源
     * @param jobFactory 自定义配置任务工厂
     * @param dataSource 数据源实例
     * @return
     * @throws Exception
     */
    @Bean(destroyMethod = "destroy",autowire = Autowire.NO)
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource) throws Exception
    {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //将spring管理job自定义工厂交由调度器维护
        schedulerFactoryBean.setJobFactory(jobFactory);
        //设置覆盖已存在的任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //项目启动完成后，等待2秒后开始执行调度器初始化
        schedulerFactoryBean.setStartupDelay(2);
        //设置调度器自动运行
        schedulerFactoryBean.setAutoStartup(true);
        //设置数据源，使用与项目统一数据源
        schedulerFactoryBean.setDataSource(dataSource);
        //设置上下文spring bean name
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        //设置配置文件位置
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));
        return schedulerFactoryBean;
    }
}

```
看代码倒数第二行，需要一个配置文件，那么...
###### quartz.properties配置

```
#调度器实例名称
org.quartz.scheduler.instanceName = quartzScheduler

#调度器实例编号自动生成
org.quartz.scheduler.instanceId = AUTO

#持久化方式配置
org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX

#持久化方式配置数据驱动，MySQL数据库
org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate

#quartz相关数据表前缀名
org.quartz.jobStore.tablePrefix = QRTZ_

#开启分布式部署
org.quartz.jobStore.isClustered = true
#配置是否使用
org.quartz.jobStore.useProperties = false

#分布式节点有效性检查时间间隔，单位：毫秒
org.quartz.jobStore.clusterCheckinInterval = 20000

#线程池实现类
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool

#执行最大并发线程数量
org.quartz.threadPool.threadCount = 10

#线程优先级
org.quartz.threadPool.threadPriority = 5

#配置为守护线程，设置后任务将不会执行
#org.quartz.threadPool.makeThreadsDaemons=true

#配置是否启动自动加载数据库内的定时任务，默认true
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread = true

```

######创建数据表
说出来你可能不信，它需要十一个数据表的支持。
```
#  
# In your Quartz properties file, you'll need to set   
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate  
#  
#  
# By: Ron Cordell - roncordell  
#  I didn't see this anywhere, so I thought I'd post it here. This is the script from Quartz to create the tables in a MySQL database, modified to use INNODB instead of MYISAM.  
  
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;  
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;  
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;  
DROP TABLE IF EXISTS QRTZ_LOCKS;  
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;  
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;  
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;  
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;  
DROP TABLE IF EXISTS QRTZ_TRIGGERS;  
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;  
DROP TABLE IF EXISTS QRTZ_CALENDARS;  
  
CREATE TABLE QRTZ_JOB_DETAILS(  
SCHED_NAME VARCHAR(120) NOT NULL,  
JOB_NAME VARCHAR(200) NOT NULL,  
JOB_GROUP VARCHAR(200) NOT NULL,  
DESCRIPTION VARCHAR(250) NULL,  
JOB_CLASS_NAME VARCHAR(250) NOT NULL,  
IS_DURABLE VARCHAR(1) NOT NULL,  
IS_NONCONCURRENT VARCHAR(1) NOT NULL,  
IS_UPDATE_DATA VARCHAR(1) NOT NULL,  
REQUESTS_RECOVERY VARCHAR(1) NOT NULL,  
JOB_DATA BLOB NULL,  
PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_TRIGGERS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
JOB_NAME VARCHAR(200) NOT NULL,  
JOB_GROUP VARCHAR(200) NOT NULL,  
DESCRIPTION VARCHAR(250) NULL,  
NEXT_FIRE_TIME BIGINT(13) NULL,  
PREV_FIRE_TIME BIGINT(13) NULL,  
PRIORITY INTEGER NULL,  
TRIGGER_STATE VARCHAR(16) NOT NULL,  
TRIGGER_TYPE VARCHAR(8) NOT NULL,  
START_TIME BIGINT(13) NOT NULL,  
END_TIME BIGINT(13) NULL,  
CALENDAR_NAME VARCHAR(200) NULL,  
MISFIRE_INSTR SMALLINT(2) NULL,  
JOB_DATA BLOB NULL,  
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)  
REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_SIMPLE_TRIGGERS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
REPEAT_COUNT BIGINT(7) NOT NULL,  
REPEAT_INTERVAL BIGINT(12) NOT NULL,  
TIMES_TRIGGERED BIGINT(10) NOT NULL,  
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_CRON_TRIGGERS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
CRON_EXPRESSION VARCHAR(120) NOT NULL,  
TIME_ZONE_ID VARCHAR(80),  
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_SIMPROP_TRIGGERS  
  (            
    SCHED_NAME VARCHAR(120) NOT NULL,  
    TRIGGER_NAME VARCHAR(200) NOT NULL,  
    TRIGGER_GROUP VARCHAR(200) NOT NULL,  
    STR_PROP_1 VARCHAR(512) NULL,  
    STR_PROP_2 VARCHAR(512) NULL,  
    STR_PROP_3 VARCHAR(512) NULL,  
    INT_PROP_1 INT NULL,  
    INT_PROP_2 INT NULL,  
    LONG_PROP_1 BIGINT NULL,  
    LONG_PROP_2 BIGINT NULL,  
    DEC_PROP_1 NUMERIC(13,4) NULL,  
    DEC_PROP_2 NUMERIC(13,4) NULL,  
    BOOL_PROP_1 VARCHAR(1) NULL,  
    BOOL_PROP_2 VARCHAR(1) NULL,  
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)   
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_BLOB_TRIGGERS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
BLOB_DATA BLOB NULL,  
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),  
INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),  
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_CALENDARS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
CALENDAR_NAME VARCHAR(200) NOT NULL,  
CALENDAR BLOB NOT NULL,  
PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_FIRED_TRIGGERS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
ENTRY_ID VARCHAR(95) NOT NULL,  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
INSTANCE_NAME VARCHAR(200) NOT NULL,  
FIRED_TIME BIGINT(13) NOT NULL,  
SCHED_TIME BIGINT(13) NOT NULL,  
PRIORITY INTEGER NOT NULL,  
STATE VARCHAR(16) NOT NULL,  
JOB_NAME VARCHAR(200) NULL,  
JOB_GROUP VARCHAR(200) NULL,  
IS_NONCONCURRENT VARCHAR(1) NULL,  
REQUESTS_RECOVERY VARCHAR(1) NULL,  
PRIMARY KEY (SCHED_NAME,ENTRY_ID))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_SCHEDULER_STATE (  
SCHED_NAME VARCHAR(120) NOT NULL,  
INSTANCE_NAME VARCHAR(200) NOT NULL,  
LAST_CHECKIN_TIME BIGINT(13) NOT NULL,  
CHECKIN_INTERVAL BIGINT(13) NOT NULL,  
PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))  
ENGINE=InnoDB;  
  
CREATE TABLE QRTZ_LOCKS (  
SCHED_NAME VARCHAR(120) NOT NULL,  
LOCK_NAME VARCHAR(40) NOT NULL,  
PRIMARY KEY (SCHED_NAME,LOCK_NAME))  
ENGINE=InnoDB;  
  
CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);  
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);  
  
CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);  
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);  
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);  
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);  
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);  
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);  
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);  
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);  
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);  
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);  
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);  
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);  
  
CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);  
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);  
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);  
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);  
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);  
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);  
  
commit;   
```
![](https://upload-images.jianshu.io/upload_images/5786888-6363ad395ae68c58.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####好了，开始写定时任务业务

首先我们需要自定义一个Job的子类来写JobDetail
```
package com.fantj.quartz.notSpringFrame;

/**
 * quartz增删改查方法
 */
public class MyJob implements Job {

    public MyJob(){}
    @Override
    //把要执行的操作，写在execute方法中
    public void execute(JobExecutionContext arg0) throws JobExecutionException
    {
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        System.out.println("测试Quartz"+ df.format(Calendar.getInstance().getTime()));
    }
}

```

其次，我在这里演示 如何再 ServiceImpl 里注入并使用 定时任务。

```
package com.fantj.service.impl;

/**
 * Created by Fant.J.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 注入任务调度器
  @Autowired
  private Scheduler scheduler;

//    测试quartz 框架 定时任务
    public void sendMail() throws Exception {
        //设置开始时间为1分钟后
        long startAtTime = System.currentTimeMillis() + 1000 * 60;
        //任务名称
        String name = UUID.randomUUID().toString();
        //任务所属分组
        String group = MyJob.class.getName();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(name,group).build();
        //创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name,group).startAt(new Date(startAtTime)).build();
        //将触发器与任务绑定到调度器内
        scheduler.scheduleJob(jobDetail, trigger);
    }
}

```
还是像在单机下玩quartz一样，需要传入两个对象（一个JobDetail，一个Trigger）任务详情和触发器。不懂的翻上文有做详细介绍。好了  此时我们启动项目，就可以看到控制台打印，并且数据库里quar_等表也自动填入了定时任务的信息。

我们大概只是完成了quartz的持久化，上文我们说过，quartz的亮点主要再对定时任务的可控性，那么如果需要再后台管理页面上完成 增删改查 定时任务。我推荐一个博客给大家，写的很好http://blog.csdn.net/u012907049/article/details/73801122

也谢谢这位作者。

最后谢谢大家，这篇文章挺长了。






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
