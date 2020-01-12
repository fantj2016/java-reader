LDAP：轻量型目录访问协议

RHDS：红帽目录服务，客户端工具
IPA：

>使用nmcli命令会同步写到配置文件中。
##### 查看链接信息

```
[root@localhost ~]# nmcli connection 
名称   UUID                                  类型            设备        
ens33  13756690-ac77-b776-4fc1-f5535cee6f16  802-3-ethernet  eno16777736 
```
##### 查看活跃网络信息
```
[root@localhost ~]# nmcli connection show --active 
名称   UUID                                  类型            设备        
ens33  13756690-ac77-b776-4fc1-f5535cee6f16  802-3-ethernet  eno16777736 
```

### 添加新网卡

在vm中设置>网络适配器>添加>网络适配器>NET>确定

##### 方法一：复制配置文件

复制修改`/etc/sysconfig/network-scripts/ifcfg-enxxxx`
##### 方法二：命令行添加


显示网络信息：
`nmcli connection show`

添加网络信息：

`nmcli connection add con-name eth1 type ethernet ifname eth1`

con-name 是指定名字

ifname   是接口名

tpye  因特网

```
[root@localhost ~]# nmcli connection add con-name eth1 type ethernet ifname eth1
Connection 'eth1' (bd5b981e-3518-4dee-8aad-0b756622ab55) successfully added.
```

检查结果：
```
[root@localhost ~]# ip a
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN 
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: eno16777736: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP qlen 1000
    link/ether 00:0c:29:f3:47:3c brd ff:ff:ff:ff:ff:ff
    inet 192.168.27.100/24 brd 192.168.27.255 scope global eno16777736
       valid_lft forever preferred_lft forever
    inet6 fe80::20c:29ff:fef3:473c/64 scope link 
       valid_lft forever preferred_lft forever
3: eno33554992: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP qlen 1000
    link/ether 00:0c:29:f3:47:46 brd ff:ff:ff:ff:ff:ff
```
```
[root@localhost ~]# nmcli connection show 
名称   UUID                                  类型            设备        
eth1   bd5b981e-3518-4dee-8aad-0b756622ab55  802-3-ethernet  --          
ens33  13756690-ac77-b776-4fc1-f5535cee6f16  802-3-ethernet  eno16777736 
```

### 配置网络

##### 1. 先查看是否活跃
```
[root@localhost ~]# nmcli connection show --active 
名称   UUID                                  类型            设备        
ens33  13756690-ac77-b776-4fc1-f5535cee6f16  802-3-ethernet  eno16777736 
```
可以看到eth1并没有激活。
所以我们需要先激活。

激活网络连接：`nmcli connection up eth1`

关闭网络连接:`nmcli connection down eth1`

去掉网络连接(拔掉网线)：`nmcli device disconnect eth1`

接上网络连接(插入网线)：`nmcli device connect eth1`

查看eth1所有参数：`nmcli connection show eth1`

修改ip地址：`nmcli connection modify  eth1 ipv4.addresses 192.168.27.110`，改完重启NetworkManager服务，不然不能及时生效。

修改ip获取方式：`nmcli connection eth1 ipv4.method manua` 修改为手动获取。









### 设置主机名

##### 传统方式：

修改/etc/hostname

或者临时修改`hostname xxx`

##### rhel7方式：

查看hostname信息:`hostnamectl status`
```
[root@localhost ~]# hostnamectl status
   Static hostname: localhost.localdomain
Transient hostname: xxx
         Icon name: computer
           Chassis: n/a
        Machine ID: dace283296e141d6b6eaa41d03184eeb
           Boot ID: 0c39622521a64fcf86c5d9297f1e8a17
    Virtualization: vmware
  Operating System: Red Hat Enterprise Linux Server 7.0 (Maipo)
       CPE OS Name: cpe:/o:redhat:enterprise_linux:7.0:GA:server
            Kernel: Linux 3.10.0-123.el7.x86_64
      Architecture: x86_64
```
命令永久修改hostname：`hostnamectl set-hostname fantj`

```
[root@localhost ~]# hostnamectl set-hostname fantj
[root@localhost ~]# cat /etc/hostname 
fantj
```


### ipv6设置
>ipv6地址举例：(128比特,16进制) 2001:250:c01:6188:655b:1bd0:b59a:9ef

ipv6的公网地址：2000::1/64

ipv6的私有地址：fec0::1/64

回还地址:  ::1

fe80::  一般表示没有获取ipv6地址。

查看ipv6地址信息：
`nmcli connection show eth1 |grep ipv6`


设置ipv6地址：`nmcli connection modify eth1 ipv6.addresses "fec0::2/64 fec0::1"`前者是地址，后者是网关。注意修改获取方式为手工manual.和网段/64保持一致(掩码保持一致)。

ping ipv6的方法:
ping6   200exxx




