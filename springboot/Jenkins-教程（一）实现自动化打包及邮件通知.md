>个人不喜欢装腔作势一堆专业术语放上去，让大多数人看不懂来提升逼格(所谓的专家)，所以我简单的介绍jenkins是干啥的。本文使用jenkins，就是让它把git仓库里的东西取出来，然后在jenkins容器中编译打包，然后执行脚本，可以是运行jar等，根据自身情况量身定制脚本。

### 下载
官方下载链接：https://jenkins.io/download/

windows可以直接下载msi安装，linux的话rpm或者直接下载war包。我在这里用的war包。

### 启动

因为它是个jar包，所以我们用命令`java -jar jenkins.war `来启动。默认8080端口。

如果你希望它不在8080端口上启动，那使用命令`java -jar jenkins.war httpPort=8888`

### 启动后续步骤

1. 启动后浏览器访问8888端口。

2. 他会让你填写一个密匙，并告诉你密匙在哪个文件。

3. 然后让你安装一些工具，直接点击默认按照即可。（可按需求来按照）

4. 创建用户

![](https://upload-images.jianshu.io/upload_images/5786888-c16bfab63716c553.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

5. 一路next
![](https://upload-images.jianshu.io/upload_images/5786888-8cd716adb078626f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 配置环境

因为我们需要编译java项目，所以jdk和maven也必须在容器里进行安装。

我们进入 ：系统管理->全局工具配置

###### jdk安装： 
![](https://upload-images.jianshu.io/upload_images/5786888-600e4a85d696cf8e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### maven安装： 
![](https://upload-images.jianshu.io/upload_images/5786888-978af61977a70941.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


###### 什么？想在项目编译失败了给你发送邮件提示？

进入： 系统管理->系统设置

首先我们看到的是
![](https://upload-images.jianshu.io/upload_images/5786888-0618ebc8093f32e7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

主目录是什么呢？你编译的项目就放在这里面。

进入正题，配置邮箱。

首先配置管理员邮箱：
![第一步](https://upload-images.jianshu.io/upload_images/5786888-7da6f607c3567d09.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
配置邮箱扩展：

![第二步](https://upload-images.jianshu.io/upload_images/5786888-60a1e174099a3c38.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

配置通知邮箱：

![第三步](https://upload-images.jianshu.io/upload_images/5786888-c8d9e25844e858a3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

测试配置是否成功：
![](https://upload-images.jianshu.io/upload_images/5786888-85b873349cd86136.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

至于没有实现过邮箱发送邮件的朋友，可以看我之前的文章了解一下。[Java 发送qq邮件基础和封装](https://www.jianshu.com/p/f66553af9a04)

### 开始项目

![](https://upload-images.jianshu.io/upload_images/5786888-961154918017f864.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-d530893f50d97bea.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-95d7860d39669939.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

应用并保存

![](https://upload-images.jianshu.io/upload_images/5786888-d15c8eee4fbac9cf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-e3ade2ef6d3cd528.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

点这个小红点可以查看构建日志。


![](https://upload-images.jianshu.io/upload_images/5786888-e67dc9e947536009.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

成功部署邮件：

![](https://upload-images.jianshu.io/upload_images/5786888-dccb66227e01eaed.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


部署失败邮件：

![](https://upload-images.jianshu.io/upload_images/5786888-1fc9b401d56d1527.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 建议
后面的一小点建议，尽量在本地上部署jenkins，然后将可运行jar包上传到服务器。因为把jenkins部署在服务器可能会存在maven下载失败的问题，需要搭建maven私服来解决，很麻烦。

至于如何上传到服务器，会在下文介绍。


####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[Spring Security](https://www.jianshu.com/nb/23842307)
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

