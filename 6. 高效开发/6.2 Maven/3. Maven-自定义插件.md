### 1. provide
>插件提供者 项目结构
```
├── pom.xml
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── fantj
│       │           └── mypluginprovide
│       │               └── MyMojo.java
│       └── resources
│           └── application.properties
└── target
    │  
    └── my-plugin-provide-0.0.1-SNAPSHOT.jar

```
##### 1.1 修改packaging
```
 <packaging>maven-plugin</packaging>
```

##### 1.2. 修改pom
```
<dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-plugin-api</artifactId>
    <version>3.5.0</version>
</dependency>
<dependency>
    <groupId>org.apache.maven.plugin-tools</groupId>
    <artifactId>maven-plugin-annotations</artifactId>
    <version>3.5</version>
</dependency>
```
这两个依赖是自定义插件必须的依赖，代表了它是一个Mojo工程，里面包含了一些Mojo的接口和抽象类以及注解。
##### 1.3 coding业务逻辑
```
@Mojo(name = "fantj",defaultPhase = LifecyclePhase.PACKAGE)
public class MyMojo extends AbstractMojo {

    @Parameter
    private String msg;

    @Parameter
    private List<String> list;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("hello plugin: "+msg + "list: "+ list);
    }
}
```
注意这里面的`@Parameter``@Mojo`` LifecyclePhase.PACKAGE`都是`org.apache.maven.plugins.annotations`包下的：
```
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
```
`@Parameter`注解会获取消费者配置文件中的变量值并赋值。

`defaultPhase = LifecyclePhase.PACKAGE`声明了该插件触发的生命周期。

`@Mojo`定义插件的`goal`名字。
##### 1.3. clean and install
执行`mvn clean install`，在target目录下会生成这样一个jar包，这就是插件包。

![](https://upload-images.jianshu.io/upload_images/5786888-a86c99a93d8e6aab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 2. consume
>插件消费者 :  项目结构
```
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── fantj
        │           └── mypluginuse
        └── resources
            └── application.properties
```
##### 2.1 修改pom
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fantj</groupId>
    <artifactId>my-plugin-consume</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <build>
        <plugins>
            <plugin>
                <groupId>com.fantj</groupId>
                <artifactId>my-plugin-provide</artifactId>
                <version>0.0.1-SNAPSHOT</version>
                <configuration>
                    <msg>hello plugin</msg>
                    <list>
                        <list>one</list>
                        <list>two</list>
                    </list>
                </configuration>

                <!--在执行package时候自动执行自定义插件 将插件挂到 phase 中 -->
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>fantj</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

如果不加`<executions>`,我们只能通过执行插件或者执行命令的方式来执行，如果想让它在执行`package`的时候自动执行，就需要设置该属性,可以把它理解成hook。

##### 2.2 如何传递参数给plugin
在插件提供者中，有个`MyMojo`的类，有这样一段代码:
```
    @Parameter
    private String msg;

    @Parameter
    private List<String> list;
```
它和你用过的spring注解一样，也是用来以注解的形式获取参数的值。

相对应的，在插件消费者的配置中我们就应该相应的给出参数的定义:
```
<configuration>
    <msg>hello plugin</msg>
    <list>
         <list>one</list>
         <list>two</list>
    </list>
</configuration>
```
上面的配置与变量名一一对应即可。这时候你会发现maven插件中自动会添加一个`plugins`选项:
![](https://upload-images.jianshu.io/upload_images/5786888-953a0a49dfad21b8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

执行该插件：`mvn myprovide:fantj` 或者直接点击
```
[fantj@lalala my-plugin-consume]$ mvn myprovide:fantj
[INFO] Scanning for projects...
[INFO] 
[INFO] --------------------< com.fantj:my-plugin-consume >---------------------
[INFO] Building my-plugin-consume 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- my-plugin-provide:0.0.1-SNAPSHOT:fantj (default-cli) @ my-plugin-consume ---
hello plugin: hello pluginlist: [one, two]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.347 s
[INFO] Finished at: 2018-11-01T19:59:04+08:00
[INFO] ------------------------------------------------------------------------
```
