### keepalived是什么
>Keepalived 是一种高性能的服务器高可用或热备解决方案， Keepalived 可以用来防止服务器单点故障的发生，通过配合 Nginx 可以实现 web 前端服务的高可用。虽然nginx的抗压性很强，很少出现宕机，但是如果不做热备，nginx一挂服务都会挂掉，所以热备是必须的，当然，根据自己的实际业务需求来决定。

### keepalived 原理
>keepalived是以VRRP协议为实现基础的，VRRP全称Virtual Router Redundancy Protocol([虚拟路由冗余协议](http://en.wikipedia.org/wiki/VRRP))

keepalived是以VRRP协议为实现基础的，VRRP全称Virtual Router Redundancy Protocol，即[虚拟路由冗余协议](http://en.wikipedia.org/wiki/VRRP)。

虚拟路由冗余协议，可以认为是实现路由器高可用的协议，即将N台提供相同功能的路由器组成一个路由器组，这个组里面有一个master和多个backup，master上面有一个对外提供服务的vip（该路由器所在局域网内其他机器的默认路由为该vip），master会发组播，当backup收不到vrrp包时就认为master宕掉了，这时就需要根据[VRRP的优先级](http://tools.ietf.org/html/rfc5798#section-5.1)来[选举一个backup当master](http://en.wikipedia.org/wiki/Virtual_Router_Redundancy_Protocol#Elections_of_master_routers)。这样的话就可以保证路由器的高可用了。

keepalived主要有三个模块，分别是core、check和vrrp。core模块为keepalived的核心，负责主进程的启动、维护以及全局配置文件的加载和解析。check负责健康检查，包括常见的各种检查方式。vrrp模块是来实现VRRP协议的。


### keepalived 结构
>keepalived只有一个配置文件`keepalived.conf`。里面主要包括以下几个配置区域，分别是`global_defs`、`vrrp_instance`、和`virtual_server`。


##### global_defs区域
>主要是配置故障发生时的通知对象以及机器标识,通俗点说就是出状况后发邮件通知的一个配置。
```
global_defs {
    notification_email {    故障发生时给谁发邮件通知
        a@abc.com
        b@abc.com
        ...
    }
    notification_email_from alert@abc.com    通知邮件从哪个地址发出
    smtp_server smtp.abc.com        smpt_server 通知邮件的smtp地址。
    smtp_connect_timeout 30       连接smtp服务器的超时时间
    enable_traps      开启SNMP陷阱
    router_id host163      标识本节点的字条串，通常为hostname
}
```

##### vrrp_instance区域
>vrrp_instance用来定义对外提供服务的VIP区域及其相关属性
```
vrrp_instance VI_1 {
    state MASTER         state 可以是MASTER或BACKUP
    interface ens33        本机网卡的名字
    virtual_router_id 51      取值在0-255之间，用来区分多个instance的VRRP组播
    priority 100            权重
    advert_int 1       发VRRP包的时间间隔，即多久进行一次master选举
    authentication {        身份认证区
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {        虚拟ip地址
        192.168.27.160
    }
}
```


##### virtual_server 
>超大型的LVS中用到,我在这里不用它。
```
virtual_server 192.168.200.100 443 {
    delay_loop 6                                延迟轮询时间（单位秒）
    lb_algo rr                                 后端调试算法
    lb_kind NAT                               LVS调度类型
    persistence_timeout 50 
    protocol TCP

    real_server 192.168.201.100 443 {                              真正提供服务的服务器
        weight 1
        SSL_GET {
            url {
              path /
              digest ff20ad2481f97b1754ef3e12ecd3a9cc         表示用genhash算出的结果
            }
            url {
              path /mrtg/
              digest 9b3a0c85a887a256d6939da88aabd8cd
            }
            connect_timeout 3
            nb_get_retry 3                                                           重试次数
            delay_before_retry 3                                                下次重试的时间延迟
        }
    }
}
```
### keepalived安装
```
yum install keepalived -y 
```
### 环境模拟
我准备了四个主机，ip是`192.168.27.166-169`,都搭建nginx服务，然后把166和167分别当主备机。

##### `nginx配置`
```
upstream centos_pool{
        server s168:80;
        server s169:80;
}
server {
    listen       80;
    server_name  localhost;

    #charset koi8-r;
    #access_log  /var/log/nginx/host.access.log  main;

    location / {
       # root   /usr/share/nginx/html;
       # index  index.html index.htm;
        proxy_pass http://centos_pool;
    }
```
四个主机都用该配置启动，看起来是4个nginx服务，在这个实例里不是这样的，不同的是166和167是nginx服务，168和169是web服务(用nignx开放80端口来模仿服务)。

换句话说，166和167用来做负载均衡，168和169是web服务主机。

我在168和169的主机`/usr/share/nginx/html/index.html`里做了简单的标识:

![](https://upload-images.jianshu.io/upload_images/5786888-3c923bab98fe4dbf.gif?imageMogr2/auto-orient/strip)
![](https://upload-images.jianshu.io/upload_images/5786888-687dac1709191580.gif?imageMogr2/auto-orient/strip)
![](https://upload-images.jianshu.io/upload_images/5786888-5f5a1557a57cfe35.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-abb806703021ef1a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
注意对照地址栏看变化。就把168和169当作普通的web服务。

好了，下来配置keepalived
##### `配置keepalived`
166主机配置：

```
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL
}

vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.27.160
    }
}
```
可以说是最核心的配置了，也是最简配置，想要配置邮件服务可以对照上文中的模块介绍注释去弄。lvs配置也一样。

167热备配置:
```
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL
}

vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.27.160
    }
}
```
可以看出，除了`state MASTER/BACKUP`和`priority 100`属性不同，其他都相同也必须相同。
好了，现在启动keepalived，从ip路由就能看出谁是主机（接管节点的网卡会绑定VIP地址192.168.27.160）

### 测试
##### 1. 访问虚拟ip：
![](https://upload-images.jianshu.io/upload_images/5786888-03237ef208620612.gif?imageMogr2/auto-orient/strip)

##### 2. 查看主机路由
![166主机](https://upload-images.jianshu.io/upload_images/5786888-209d43624977d7e1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![167主机](https://upload-images.jianshu.io/upload_images/5786888-d05c13964b9512f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3. 情景演练
>一切配置正常后，我把166上的nginx停了，会发生什么呢？

167会接手虚拟ip地址，完成双机热备任务吗？答案是不会，因为你回头看看，keepalived没有一点是和nginx有关系的，两服务互不影响。keepalived其实是监控master上的keepalived的心跳的。所以，我把keepalived服务也关掉。

```
[root@s166 keepalived]# nginx -s stop
[root@s166 keepalived]# service keepalived stop
Redirecting to /bin/systemctl stop keepalived.service
```
然后再查看166的ip路由
```
[root@s166 keepalived]# ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether 00:0c:29:7b:59:07 brd ff:ff:ff:ff:ff:ff
    inet 192.168.27.166/24 brd 192.168.27.255 scope global noprefixroute ens33
       valid_lft forever preferred_lft forever
    inet6 fe80::83ee:6998:a0d4:7974/64 scope link tentative dadfailed 
       valid_lft forever preferred_lft forever
    inet6 fe80::2513:4c77:5da7:f031/64 scope link tentative dadfailed 
       valid_lft forever preferred_lft forever
    inet6 fe80::99b3:c79:5377:c3fe/64 scope link tentative dadfailed 
       valid_lft forever preferred_lft forever
```
可以看到是没有`192.168.27.160`这个虚拟ip了。那我们再看一下167是否有，如果有，证明配置生效。
![](https://upload-images.jianshu.io/upload_images/5786888-5293b5a0e7b15341.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
那我们继续刷新`192.168.27.160`这个链接，会发现运行正常。
![](https://upload-images.jianshu.io/upload_images/5786888-3b4f6c2a5be7c3f6.gif?imageMogr2/auto-orient/strip)

### 脚本优化

既然keepalived和nginx没有关联，那我们可以写个脚本监听nginx，如果nginx挂了，然后用命令把keepalived也停掉，这样就会完成双机热备的任务。

创建脚本`check_nginx.sh`
```
#!/bin/bash
A=`ps -C nginx --no-header | wc -l`
if [ $A -eq 0 ];then
    echo "restart the nginx server" >> /etc/keepalived/keepalived_error.log
    /usr/sbin/nginx
    sleep 2
    if [ `ps -C nginx --no-header | wc -l` -eq 0 ];then
	echo "keepalived is closed" >> /etc/keepalived/keepalived_error.log
 	/usr/bin/ps -ef | grep "keepalived" | grep -v "grep" | cut -c 9-15 | xargs kill -9
	echo /usr/bin/ps -ef | grep "keepalived" >> /etc/keepalived/keepalived_error.log
    fi
fi
```
对了，记住修改脚本可执行权限。为什么要重定向呢，因为`echo`不会打印在控制台上，我们可以跟踪`keepalived_error.log`来判断脚本是否执行。
![](https://upload-images.jianshu.io/upload_images/5786888-5dfef46197761144.gif?imageMogr2/auto-orient/strip)

那既然这样，我们的脚本如何控制时间呢？sleep的时间也得控制好，在保持高性能下转换越快越好。所以，我们把脚本加载到keepalived配置中，keepalived每进行一次选举，就执行一次脚本。

`把脚本添加到keepalived任务中`
```
! Configuration File for keepalived 
 
global_defs { 
   router_id LVS_DEVEL 
} 
 
vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.27.160
    }
    track_script {
       chk_nginx  # nginx存活状态检测脚本
    }
}

vrrp_script chk_nginx {
       script "/etc/keepalived/check_nginx.sh"
       interval 2 
       weight -20
}
```
同理，BACKUP主机也要配置
```
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL
}

vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.27.160
    }
    track_script {
       chk_nginx
    }
}
vrrp_script chk_nginx {
       script "/etc/keepalived/check_nginx.sh"
       interval 2 
       weight -20
}
```

##### 优化后测试

![](https://upload-images.jianshu.io/upload_images/5786888-99d893faa2ca4941.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
日志也会发现一直有在执行脚本。

那如何测试不重启nginx，让它直接关keepalived，然后启用BACKUP呢。我把那行重启nginx脚本注释掉。然后再跑。
```
#!/bin/bash
A=`ps -C nginx --no-header | wc -l`
if [ $A -eq 0 ];then
#    echo "restart the nginx server" >> /etc/keepalived/keepalived_error.log
#    /usr/sbin/nginx
#    sleep 2
#    if [ `ps -C nginx --no-header | wc -l` -eq 0 ];then
	echo "keepalived is closed" >> /etc/keepalived/keepalived_error.log
 	/usr/bin/ps -ef | grep "keepalived" | grep -v "grep" | cut -c 9-15 | xargs kill -9
	echo /usr/bin/ps -ef | grep "keepalived" >> /etc/keepalived/keepalived_error.log

#    fi
fi
```

![](https://upload-images.jianshu.io/upload_images/5786888-ed4b4e405c1b6890.gif?imageMogr2/auto-orient/strip)

说明脚本和keepalived整合没问题，将注释去掉。任务完成。


### 思考
```
[root@s166 keepalived]# ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether 00:0c:29:7b:59:07 brd ff:ff:ff:ff:ff:ff
    inet 192.168.27.166/24 brd 192.168.27.255 scope global noprefixroute ens33
       valid_lft forever preferred_lft forever
    inet 192.168.27.160/32 scope global ens33
       valid_lft forever preferred_lft forever
    inet6 fe80::83ee:6998:a0d4:7974/64 scope link tentative dadfailed 
       valid_lft forever preferred_lft forever
    inet6 fe80::2513:4c77:5da7:f031/64 scope link tentative dadfailed 
       valid_lft forever preferred_lft forever
    inet6 fe80::99b3:c79:5377:c3fe/64 scope link tentative dadfailed 
       valid_lft forever preferred_lft forever
```

```
[root@s167 keepalived]# ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether 00:0c:29:d4:26:34 brd ff:ff:ff:ff:ff:ff
    inet 192.168.27.167/24 brd 192.168.27.255 scope global noprefixroute ens33
       valid_lft forever preferred_lft forever
    inet 192.168.27.160/32 scope global ens33
       valid_lft forever preferred_lft forever
    inet6 fe80::99b3:c79:5377:c3fe/64 scope link noprefixroute 
       valid_lft forever preferred_lft forever
```
虽然我们把s166都关闭了keepalived，但是ip路由还会有虚拟ip `192.168.27.160`，这个可能是keepalived并没有完全终止。但是我在刷新的时候没有出现错误页面，证明并没有影响到服务的正常运行。不属于裂脑问题。我将脚本中的强制kill`keepalived`的操作换成更委婉的`/usr/sbin/service keepalived stop`,解决了该问题.

拓展：[高可用之裂脑问题](https://www.jianshu.com/p/83d0dda60c55)







