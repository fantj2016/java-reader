>前言：相信很多朋友都会遇到一些maven打包失败的问题，这里我把我自己再打包过程中遇到的问题都分享出来，希望能帮到大家。

##本教程大概内容如下：
###1. 依赖传递失效问题解决方案
* **注意父类继承的格式**
* **pom里面有没有错**
###2. 打包报错问题总结以及解决方案
* **Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.20.4:test (default-test)**
* **ERROR信息,是不是你写的代码运行不通导致的报错**
###3. 通过父类pom打包子类项目可能出现的问题
* **error 打包失败**
* **打出的子类包不能运行**


好了，开始正文。如果上面没有列出你所要找的问题，请再评论区评论下下，我们共同维护总结maven的问题，打造最新最全的解决办法。谢谢大家的支持！

####1. 依赖传递失效问题

碰到这类问题，莫慌，慌

注意几个点。

######1.1 注意父类继承的格式完整（尤其是relativePath属性，它不抱错，但是没他不行！）

```
	<parent>
		<groupId>com.laojiao</groupId>
		<artifactId>xxx</artifactId>
		<version>1.0-SNAPSHOT</version>
		<relativePath>../xxx</relativePath>    #这里写父类pom路径
	</parent>
```
######1.2 注意pom里面有没有错

有时候，你把某个依赖的groupId写错，pom里面是不会有报错提示的。idea会在Maven Managerment 插件里有检测。
![](https://upload-images.jianshu.io/upload_images/5786888-1a97d5819c28ea2e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

你可以去这里查看是否哪个pom有问题，如果父类的pom有问题，子类的引入是无效的。所以你会看到你甚至连个spring-boot-starter-web 包都没有 -。-   如果你父类pom有，子类报错，就去找父类的问题吧！

###2 打包报错问题

#####2.1 看第一个ERROR信息，是不是 Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.20.4:test (default-test)

如果是，那就是因为你测试类打包出错，所以我在这里把测试类忽略掉（测试类打包一般也不放在jar里）

```
	<!-- Package as an executable jar -->
	<build>
		<finalName>springboot-mybatis</finalName>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<configuration>
					<testFailureIgnore>true</testFailureIgnore>
				</configuration>
			</plugin>
		</plugins>
	</build>
```
######2.2 看第一个ERROR信息,是不是你写的代码运行不通导致的报错

如果是，先去在本地运行，通过了再打包吧。。或者把报错的地方注释掉，然后 clean -> package


###3. 通过父类pom打包子类项目可能出现的问题

######3.1 报错信息可以参考情况 2
如果出现error 打包失败，可以参考上文中的2提到的一些解决方案
#####3.2 打出的子类包不能运行（找不到main方法）
如果出现此类错误，说明maven  build工具配置有问题，它没有识别出来你需要打可执行包。所以添加下面的配置

```
	<build>
		<finalName>xxxx</finalName>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<executions>
					<execution>
						<goals>
							<goal>repackage</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

######那什么是repackage呢？
Spring Boot Maven plugin的最主要goal就是repackage，其在Maven的package生命周期阶段，能够将mvn package生成的软件包，再次打包为可执行的软件包。在执行上述命令的过程中，Maven首先在package阶段打包生成*.jar文件；然后执行spring-boot:repackage重新打包，查找Manifest文件中配置的Main-Class属性

更多详情可以参考官方文档：
http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/maven-plugin/
https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html

注意packing标签里面要写 jar ，因为我们要的是jar包（war包也类似）。

finalName 是告诉 打包插件，打包后的jar包名字叫啥。

######最后，那怎样运行jar包呢。
cmd 或者terminal下 `java -jar xxx.jar` ，当然，首(其)先(实)你(就)得(是)有(废)jdk(话)环境。
