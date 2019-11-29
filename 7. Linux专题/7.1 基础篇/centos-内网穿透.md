1. 
`echo 1 > /proc/sys/net/ipv4/ip_forward`

让它立即生效，就执行

`[root@shx-web150 ~]# sysctl -p`
 
2、紧接着我就把内网机器网关指向A的内网IP地址，结果，还是不能连接到外网，纠结许久。后面想到，才想起要在A做一个NAT转发
`[root@shx-web150 ~]#iptables -t nat -A POSTROUTING -s 10.1.2.0/24 -j MASQUERADE`
