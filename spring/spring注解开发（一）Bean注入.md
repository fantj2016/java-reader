>传统的Spring做法是使用.xml文件来对bean进行注入,这样做既麻烦又难维护。所以Spring后来引入了注解，大大减少配置文件，增加了web代码的可读性。

本文将对比配置注入bean和注解注入bean两种方式。

因为本文主要比较这两种开发模式，所以不介绍项目的构建。

### 1. xml配置注入bean
##### 1.1 创建bean
```
public class Student {

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Student() {
    }

    private String name;
    private int age;
    
    setter and getter ...
```
##### 1.2 添加xml配置
```
    <bean id="student" class="com.fantj.bean.Student">
        <property name="age" value="18"/>
        <property name="name" value="FantJ"/>
    </bean>
```
`id`表示bean的唯一标识，从bean工厂里获取实例化对象就是靠它，一般是类首字母小写。
`property` 用来初始化类的字段值，比如我初始化`name=FantJ,age=18`。
##### 1.3 执行查看结果
```
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean.xml");
        Student person = (Student) applicationContext.getBean("student");
        System.out.println(person);
    }
```
这个`getBean`方法返回的是Object对象，但是我们很确定根据student这个id取出的就是Student的实例化对象，所以我们可以直接在这里做强制类型转换。
```
Student{name='FantJ', age=18}
```


### 2. 注解注入bean
##### 2.1 创建配置类
```
@Configuration
public class MyConfig {

    @Bean
    public Student student(){
        return new Student("FantJ",20);
    }
}
```
1. 先看`@Bean`注解，这个注解就是把Student这个类加载到IOC容器，我们看不到它的`id`和`property `，id默认是方法名，当然也可以手动设置（不建议），以这样的方式设置：`@Bean("xxx")`。那如何初始化它的字段值呢，我们返回一个带有值的实例化对象就行。
2. 那`@Configuration`注解是干嘛的呢，它用来声明这是一个配置类，Spring容器启动要来加载我这个类，如果不加这个配置，spring启动不会加载到它。既然Spring能来加载这个类，就会发现Bean注解，自然也把bean就加载了。当然这样的注解有很多，比如`@Compoment、@Controller、@Servic`e等都能完成，只不过它们有它们的特定使用的规范。最好不要乱用，造成代码可读性差。

##### 2.2 测试
```
ApplicationContext applicationContext1 = new AnnotationConfigApplicationContext(MyConfig.class);
Student bean = applicationContext1.getBean(Student.class);
System.out.println(bean);
```
```
Student{name='FantJ', age=20}
```
##### 2.3 @Component加载bean
>写这个的意思是告诉读者，千万不要以为注解加载Bean只有`@Configuration`能做到。

我们看下`@Configuration`源码
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {.....}
```
可以看到它里面包含这`Component`注解。那下来我们写个`Component`加载bean的例子：

```
@Component
public class ComponentTest {
    @Bean
    public Student student(){
        return new Student("FantJ",21);
    }
}
```
```
ApplicationContext applicationContext2 = new AnnotationConfigApplicationContext(ComponentTest.class);
Student bean1 = applicationContext2.getBean(Student.class);
System.out.println(bean1);
```
```
Student{name='FantJ', age=21}
```


