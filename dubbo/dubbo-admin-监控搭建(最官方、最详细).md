>因为我们不能直观的看到dubbo和zk上到底有什么服务（提供者），所以我们需要一个可视化工具来方便我们管理每一个服务和每一个节点。

先上个成功后的监控图：
![](https://upload-images.jianshu.io/upload_images/5786888-47beea7849345a1e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 示例环境
1. maven 3.x
2. jdk 1.8
3. tomcat 1.8
4. idea 2018
5. git

### 1.克隆项目

apache 下的dubbo-admin git仓库 ：https://github.com/apache/incubator-dubbo-ops

我们需要先把这个项目用git克隆到本地中。

我在这里用idea直接进行克隆并打开。

![](https://upload-images.jianshu.io/upload_images/5786888-1cf34d7de47144d9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-00370793b5be532b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

因为我已经克隆过一个项目，所以它提示我项目已经存在。

### 2.配置属性

我们再dubbo-admin 项目下找到dubbo.properties 配置文件
`src->main->webapp->WEB-INF->dubbo.properties`
![](https://upload-images.jianshu.io/upload_images/5786888-7d309ebd1fb4bd44.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
报错不用理会（嫌烦的可以选择忽略或者fetch），不影响项目启动。
![](https://upload-images.jianshu.io/upload_images/5786888-532f0eff84b62e5e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

dubbo.properties
```
dubbo.registry.address=zookeeper://xx.xxx.xxx.xxx:2181  
dubbo.admin.root.password=root
dubbo.admin.guest.password=guest
```
dubbo-admin 默认两个登陆账号，一个是root，一个是guest
第一行是填写dubbo注册的zk地址
第二行是填写root的密码
第三行是填写guest的密码

### 3.放入tomcat容器并启动
##### 3.1 配置启动项

![](https://upload-images.jianshu.io/upload_images/5786888-0720d3f487d92973.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.2 添加Tomcat服务

![](https://upload-images.jianshu.io/upload_images/5786888-081ddd6704a2fd70.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

根据自己条件看是添加远程的tomcat还是本地的。

##### 3.3 配置tomcat

1. 配置tomcat路径和端口
我配置的是10000端口。
![](https://upload-images.jianshu.io/upload_images/5786888-0198ce0c6dd68f54.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2. 配置部署war包
![](https://upload-images.jianshu.io/upload_images/5786888-981389940929dc7d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.4 启动
略

### 后续工作
>我们不可能每次想看dubbo监控的时候总得打开idea甚至是打开项目才能来运行。所以我们还需要做一些后续工作。

##### 打包项目

![](https://upload-images.jianshu.io/upload_images/5786888-15162b79be7ed531.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

成功的话我们会发现一个war包：

![](https://upload-images.jianshu.io/upload_images/5786888-7ee18f7e48bf74a7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

但是请记住，这个war包是1.8jdk版本下的，并不是通用的war包。(请不要乱分享)，每个人都应该打包一份属于自己环境的war包。

有了war包，我们就可以直接放到tomcat的webapp目录下，启动tomcat，它会自动解析该war包并提供服务。
