>在日常开发中，我们项目的开发环境和生产环境以及测试环境往往是不同的，比如:数据库的url等。在项目上生产环境时，就需要修改这些参数，给开发造成不便。 为了解决该问题，Maven 2.0引入了构建配置文件的概念(build profiles)。

### 它能干什么呢?
>假如你的生产环境和开发环境所需环境配置不同,生产环境配置文件是`pro.properties`,开发环境配置文件是`dev.properties`,那么用maven profile ， 你可以实现打包开发环境jar包的时候只将`dev.properties`打包并使用，生产环境打包同理。
### 在哪里声明呢?
>它可以在每个项目的`pom.xml`文件中声明，也可以在maven的用户`setting.xml`下声明，也可以在maven全局环境下设置`setting.xml`，详情如下。

###### 1. Per Project
 Defined in the POM itself (`pom.xml`).
###### 2. Per User
Defined in the [Maven-settings](http://maven.apache.org/ref/current/maven-settings/settings.html) (`%USER_HOME%/.m2/settings.xml`)
###### 3. Global
Defined in the [global Maven-settings](http://maven.apache.org/ref/current/maven-settings/settings.html) `(${maven.home}/conf/settings.xml)`
###### 4. Profile descriptor
不支持3.0,详情请看： https://cwiki.apache.org/MAVEN/maven-3x-compatibility-notes.html#Maven3.xCompatibilityNotes-profiles.xml


虽然有这么多define的方式，但是我们一般使用的是第一种`defined in the pom`，因为不见得每个项目的生产环境都一模一样，当然这个也是因个人情况而异。

### 实战
#### 1. 项目结构
```
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── fantj
│   │   └── resources
│   │       └── conf
│   │           ├── dev
│   │           │   └── application.properties
│   │           ├── pro
│   │           │   └── application.properties
│   │           └── test
│   │               └── application.properties
│   └── test
│       └── java
```
#### 2. pom.xml 
```
    <profiles>
        <profile>
            <id>dev</id>
            <properties>
                <profile.env>dev</profile.env>
            </properties>
            <activation>
                <activeByDefault>dev</activeByDefault>
            </activation>
        </profile>
        <profile>
            <id>pro</id>
            <properties>
                <profile.env>pro</profile.env>
            </properties>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <profile.env>test</profile.env>
            </properties>
        </profile>
    </profiles>

    <build>
        <resources>
            <resource>
                <directory>${basedir}/src/main/resources</directory>
                <excludes>
                    <exclude>conf/**</exclude>
                </excludes>
            </resource>
            <resource>
                <directory>src/main/resources/conf/${profile.env}</directory>
            </resource>
        </resources>
    </build>
```

#### 3. 三个application.properties
dev/application.properties
```
env=dev
db.url=192.168.0.166  
db.username=db-dev 
db.password=db-dev
```
pro/application.properties
```
env=pro
db.url=47.xxx.xxx.xxx  
db.username=db-pro
db.password=db-pro
```
test/application.properties
```
env=test
db.url=127.0.0.1 
db.username=db-test
db.password=db-test
```

#### 4. 打包
```
mvn clean install -P pro
```
>![](https://upload-images.jianshu.io/upload_images/5786888-0017cd1622283eac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到只将`pro/application.properties`进行了编译。
