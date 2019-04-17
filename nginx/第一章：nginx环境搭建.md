[TOC]
### 1.四项确认(本次实例用redhat系列系统)
1. 确认系统网络    ping www.baidu.com
2. 确认yum可用    yum list |grep gcc
3. 确认关闭iptables规则  `  iptables -L`/`iptables -t nat -L `  如果有的话  `iptables -F`/`iptables -t nat -F` 关闭规则
4. 确认停用selinux    getenforce   显示应该为 Disabled , 如果不是。`setenforce 0`
### 2.两项安装(yum list | grep gcc )
1. `yum -y install gcc gcc-c++ autoconf pcre pcre-devel make automake `  系统基本库
2. `yum -y install wget httpd-tools vim`     一些基本工具
### 3.目录介绍
`cd /opt;` 
`mkdir app(src) download(src package) logs(log) work(shell脚本) backup(配置文件)`
![image.png](http://upload-images.jianshu.io/upload_images/5786888-117f21b5834363bc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### nginx 中间件架构介绍（了解）
1. nginx是一个开源且高性能、可靠的HTTP中间件，代理服务。
2. 常见的HTTP服务
          HTTPD -Apache
          IIS - ms
          GWS -Google
3. 为什么选择Nginx
          1. IO多路复用epoll（select模型和epoll模型）    多路复用：一个线程内并发交替的顺序完成
          2. 功能模块少 （只保留了核心代码，轻量级）          代码模块化（易读，阿里借鉴开发）
          3. CPU亲和（affinity）
               是一种把CPU核心和Nginx工作进程绑定方式，把每个worker进程固定在一个cpu上执行，减少切换cpu的cache miss，
               获得更好的性能。
          4. sendfile工作机制（借鉴与linux的0拷贝）
               直接通过内核空间来将静态资源拷贝到socket中（原始需要先通过用户空间）
###4. Nginx快读搭建与基本参数使用
>Mainline version  -开发版
Stable version -稳定版
Legacy version -历史版本
Changes  有哪些改变
######1. 修改yum源（添加nginx依赖）
* 在 etc/yum.repos.d/下创建一个nginx.repo
```
[nginx]
name=nginx repo
baseurl=http://nginx.org/packages/centos/7/$basearch/
gpgcheck=0
enabled=1
```

* 然后` yum list | grep nginx `测试是否成功添加yum源 
成功后的页面：![image.png](http://upload-images.jianshu.io/upload_images/5786888-a908df1b37b1fdee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* 然后 `yum install nginx`安装。
* 测试是否安装成功
  1. nginx -V  编译信息
          nginx -v  版本信息
  2. 安装时的编译参数
          --user=nginx      
          --group=nginx      设定nginx进程启动的用户和用户组
  3. nginx.conf 介绍
          systemctl restart nginx.service 重启服务
#####[ 拓展 ] 一些目录的介绍
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ec855623076f1c7e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-afcfa4ee3d24a346.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-c9e6d5c351feed57.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
[^1]: 脚注内容
