>在以前的版本，我们都用bind来设置DNS，在Rhel7中，我们用unbound来配置。

### DHCP&MAC地址绑定
>为客户端分配必要的网络信息。


###### 工作流程
>客户端新生成后会在局域网发广播，DHCP收到后从地址池里拿出ip作回应。然后客户端再发广播拿到的ip地址，然后DHCP回应放出ip。


##### 安装DHCP服务器

1. `yum install dhcp -y`
2. 配置文件`/etc/dhcp/dhcpd.conf`,发现里面是空的，所以找个模版`rpm -ql dhcp |grep example`
```
[root@fantj dhcp]# rpm -ql dhcp |grep example
/usr/share/doc/dhcp-4.2.5/dhcpd.conf.example
/usr/share/doc/dhcp-4.2.5/dhcpd6.conf.example
```

复制内容:`cat /usr/share/doc/dhcp-4.2.5/dhcpd.conf.example  >  /etc/dhcp/dhcpd.conf`

dhcpd.conf文件配置解析

```
# dhcpd.conf
option domain-name "fantj.cc";  域名
option domain-name-servers 192.168.27.3; 编址服务器

default-lease-time 600; 租约：租期
max-lease-time 7200; 租约：最大续期

#ddns-update-style none; 根据客户端ip地址的改变来动态dns
log-facility local7;
subnet 192.168.27.0(作用域) netmask 255.255.255.0 {
  range 192.168.27.169   192.168.27.250;定义地址池
  option domain-name "internal.example.org";
  option routers 192.168.27.2;网关地址
  option broadcast-address 192.168.27.255;广播地址
}  subnet配置网络信息，一个网段一个作用域，


绑定mac地址

：服务器在运行时，如果没有给服务器配静态地址，
但是我们希望每次获取的地址是一样的，
就需要我们对mac地址做绑定。
host fantasia {
  hardware ethernet 08:00:07:26:c0:a5;  mac地址(可通过ip a 查看)
  fixed-address 192.168.27.88;分配的地址不一定要在上面的地址池里。
}

```

3. 开启服务:`systemctl start dhcpd`
4. 开启防火墙:`firewall-cmd  --add-service=dhcp`允许dhcp服务。


### DNS
>


`www.baidu.com `是主机名 

`baidu.com ` 是域名  

`mail.baidu.com` 是`baidu.com`域中的主机


###### DNS解析路径追踪`

`dig +trace www.baidu.com`

```
[root@fantj ~]# dig +trace www.baidu.com

; <<>> DiG 9.9.4-RedHat-9.9.4-14.el7 <<>> +trace www.baidu.com
;; global options: +cmd
.			221744	IN	NS	m.root-servers.net.
.			221744	IN	NS	b.root-servers.net.
.			221744	IN	NS	c.root-servers.net.
.			221744	IN	NS	d.root-servers.net.
.			221744	IN	NS	e.root-servers.net.
.			221744	IN	NS	f.root-servers.net.
.			221744	IN	NS	g.root-servers.net.
.			221744	IN	NS	h.root-servers.net.
.			221744	IN	NS	i.root-servers.net.
.			221744	IN	NS	j.root-servers.net.
.			221744	IN	NS	a.root-servers.net.
.			221744	IN	NS	k.root-servers.net.
.			221744	IN	NS	l.root-servers.net.

上面是13个.域服务器


com.			172800	IN	NS	a.gtld-servers.net.
com.			172800	IN	NS	b.gtld-servers.net.
com.			172800	IN	NS	c.gtld-servers.net.
com.			172800	IN	NS	d.gtld-servers.net.
com.			172800	IN	NS	e.gtld-servers.net.
com.			172800	IN	NS	f.gtld-servers.net.
com.			172800	IN	NS	g.gtld-servers.net.
com.			172800	IN	NS	h.gtld-servers.net.
com.			172800	IN	NS	i.gtld-servers.net.
com.			172800	IN	NS	j.gtld-servers.net.
com.			172800	IN	NS	k.gtld-servers.net.
com.			172800	IN	NS	l.gtld-servers.net.
com.			172800	IN	NS	m.gtld-servers.net.


com域服务器


baidu.com.		172800	IN	NS	dns.baidu.com.
baidu.com.		172800	IN	NS	ns2.baidu.com.
baidu.com.		172800	IN	NS	ns3.baidu.com.
baidu.com.		172800	IN	NS	ns4.baidu.com.
baidu.com.		172800	IN	NS	ns7.baidu.com.


上面的是百度域服务器


www.baidu.com.		1200	IN	CNAME	www.a.shifen.com.
a.shifen.com.		1200	IN	NS	ns2.a.shifen.com.
a.shifen.com.		1200	IN	NS	ns3.a.shifen.com.
a.shifen.com.		1200	IN	NS	ns1.a.shifen.com.
a.shifen.com.		1200	IN	NS	ns4.a.shifen.com.
a.shifen.com.		1200	IN	NS	ns5.a.shifen.com.


上面是baidu.com别名以后a.shifen.com的dns服务器
```


###### 1. 安装unbound

`yum install unbound -y`


###### 2. 开启dns服务
>默认53端口，支持tcp协议和udp协议。

`firewall-cmd  --add-service=dns`

```
[root@fantj ~]# firewall-cmd  --add-service=dns
success
```


###### 3. 配置DNS

主配置文件：
`/etc/unbound/unbound.conf`

```
interface 0.0.0.0   监听接口
# 访问控制
access-control: 127.0.0.0/8 allow
access-control: 192.168.0.0/24 allow
# 区域配置，本地区域配置主要分为local-zone、local-data 和 local-data-ptr 。这里假设配置一个域名为rhce.com 的域名，其A记录、MX记录配置结果如下

local-zone: "rhce.com" static
local-data: "www.rhce.com. IN A 192.168.0.103"
local-data-ptr: "192.168.0.103 www.rhce.com"
local-data: "rhce.com. IN MX 5 mail.rhce.com"
local-data: "mail.rhce.com. IN A 192.168.0.103"

#转发配置，指定上一级DNS转发
forward-zone:
        name: .
        forward-addr: 114.114.114.114
        forward-addr: 8.8.8.8

```
###### 4. 重启服务
`systemctl restart unbound`

`systemctl enable unbound`
###### 5. 防火墙配置

`firewall-cmd --permanent --add-service=dns`




