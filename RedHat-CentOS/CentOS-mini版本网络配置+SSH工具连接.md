### 1. VM网络设置


![图1.png](https://upload-images.jianshu.io/upload_images/5786888-565a162cbca07fa6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
点击NAT设置
![图2](https://upload-images.jianshu.io/upload_images/5786888-991e7fb546867fbe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

记住网关和子网ip，后面会用

### 2. CentOs网络设置
```
[root@localhost download]# cd /etc/sysconfig/network-scripts/
[root@localhost network-scripts]# ls
ifcfg-ens33  ifdown-eth   ifdown-post    ifdown-Team      ifup-aliases  ifup-ipv6   ifup-post    ifup-Team      init.ipv6-global
ifcfg-lo     ifdown-ippp  ifdown-ppp     ifdown-TeamPort  ifup-bnep     ifup-isdn   ifup-ppp     ifup-TeamPort  network-functions
ifdown       ifdown-ipv6  ifdown-routes  ifdown-tunnel    ifup-eth      ifup-plip   ifup-routes  ifup-tunnel    network-functions-ipv6
ifdown-bnep  ifdown-isdn  ifdown-sit     ifup             ifup-ippp     ifup-plusb  ifup-sit     ifup-wireless

```
用vim打开 `ifcfg-ens33`
![](https://upload-images.jianshu.io/upload_images/5786888-36c9bc0cd9e0fa37.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
修改我圈中的这几项，网关和子网ip与之前一一对应起来。

###### 重启网络模块
`service network restart`
### 3. 检测网络是否配置成功
###### ping百度
```
[root@localhost network-scripts]# ping baidu.com
PING baidu.com (220.181.57.216) 56(84) bytes of data.
64 bytes from 220.181.57.216 (220.181.57.216): icmp_seq=1 ttl=128 time=18.5 ms
64 bytes from 220.181.57.216 (220.181.57.216): icmp_seq=2 ttl=128 time=18.7 ms
64 bytes from 220.181.57.216 (220.181.57.216): icmp_seq=3 ttl=128 time=17.7 ms

```

###### 查看ip
此命令需要先安装net工具包`yum install net-tools -y`
```
[root@localhost network-scripts]# ifconfig
ens33: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.27.132  netmask 255.255.255.0  broadcast 192.168.27.255
        inet6 fe80::99b3:c79:5377:c3fe  prefixlen 64  scopeid 0x20<link>
        ether 00:0c:29:7b:59:07  txqueuelen 1000  (Ethernet)
        RX packets 288077  bytes 405462136 (386.6 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 14968  bytes 2189388 (2.0 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        inet6 ::1  prefixlen 128  scopeid 0x10<host>
        loop  txqueuelen 1000  (Local Loopback)
        RX packets 83  bytes 8297 (8.1 KiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 83  bytes 8297 (8.1 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
```
###### 更换yum源
[CentOS 7.x 安装阿里yum源](https://www.jianshu.com/p/4dc117fb00b3)

###### 安装vim 
`yum install vim -y`

### 4. SSH工具连接
###### 安装openssh-server
`yum install openssh-server -y`
###### 开放22端口或者关闭防火墙(二选一)
开放端口
```
firewall-cmd --zone=public --add-port=22/tcp --permanent
其中：
–zone #作用域
–add-port=80/tcp #添加端口，格式为：端口/通讯协议
–permanent #永久生效，没有此参数重启后失效
```
关闭防火墙
```
//临时关闭
systemctl stop firewalld
//禁止开机启动
systemctl disable firewalld
```
关闭SELinux
```
#临时关闭
setenforce 0
#永久关闭
vi /etc/selinux/config
```
###### 开始连接
我用的是XShell工具
![](https://upload-images.jianshu.io/upload_images/5786888-85b29f12f94b3395.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![](https://upload-images.jianshu.io/upload_images/5786888-442fcf301896f45e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 什么？ssh还连接不上？
在VM里还原默认网络配置，然后注意把NAT模式的子网ip也对应做修改。
![](https://upload-images.jianshu.io/upload_images/5786888-ede0b5dbcefc5b40.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/5786888-52344b473bdf8f51.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

