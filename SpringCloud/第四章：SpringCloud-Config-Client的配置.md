>This is the default behaviour for any application which has the Spring Cloud Config Client on the classpath. When a config client starts up it binds to the Config Server (via the bootstrap configuration property spring.cloud.config.uri) and initializes Spring Environment with remote property sources.
The net result of this is that all client apps that want to consume the Config Server need a bootstrap.yml (or an environment variable) with the server address in spring.cloud.config.uri (defaults to "http://localhost:8888").

文档大意: configclient 服务启动后，默认会先访问bootstrap.yml，然后绑定configserver，然后获取application.yml 配置。如果仅仅在application.yml 配置了`url:http://127.0.0.1:8080` 这样默认会使用8888端口，（配置无效）。
所以，
#####1. 我们将绑定configserver的配置属性应该放在bootstrap.yml文件里。
![image.png](http://upload-images.jianshu.io/upload_images/5786888-a840f2eae4051fe7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
 #####2. pom
```
            <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
```
因为用到mvc，所以添加web依赖
#####3.application
![image.png](http://upload-images.jianshu.io/upload_images/5786888-c8aad5656f4fbcd8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####4. 写一个获取配置信息的controller
![xxx](http://upload-images.jianshu.io/upload_images/5786888-85c960278d181899.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####5. 测试是否连接上server
![image.png](http://upload-images.jianshu.io/upload_images/5786888-8e8cc513a97ff5cc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-d8ffe9e654202c5b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-aa8ed05e7793f5b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####注意：
一般来说git上放的yml名称都以 ${application.name}-dev.yml 命名。
因为：如果client服务**没有声明**application.name的话，如果去configserver里拉取配置，**cloud会默认拉取application.yml**。
当然：你**按照规范**，给**application.name:laojiao**   然后git上放个**对应配置文件laojiao.yml** 或者 laojiao-xxxxx.yml  注意**命名唯一**，这样client就能找到对应的配置文件。**否则默认去找application.yml**
##高性能配置：
###一、git repo 仓库设置 （将上面的替换。隔离性高）Placeholders in Git URI
>or a "one repo per profile" policy using a similar pattern but with {profile}.
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/{application}
```
意思是： 一个项目 创建一个专门的git仓库，该仓库里只存放对应服务名称的配置文件。
例如： 我有个laojiao和tiantian模块，我就创建两个 git仓库，一个叫 laojiao,一个叫tiantian,然后在模块里的spring.application.name 就是laojiao / tiantian。最后，在config server里的配置文件`git:
          uri: https://github.com/myorg/{application}`  这个{application}通配符会自动获取模块对应的仓库去获取配置文件。
###二、Placeholders in Git Search Paths 获取一个git仓库下的文件夹名称，来找配置
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: '{application}'
```
在`https://github.com/spring-cloud-samples/config-repo`路径下，找对应的laojiao / tiantian 文件夹，然后获取配置文件
