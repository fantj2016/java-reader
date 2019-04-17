1，安装软件
```
yum install ppp
yum install pptp
yum install pptp-setup
```
2，创建VPN连接
`
pptpsetup --create vpn1 --server xxx.xx.xxx.xx --username xxxxxxxx --encrypt`

3，连接VPN
`pppd call vpn1`

4，断开VPN
`killall pppd`

5，更改路由
`ip route replace default dev ppp0`
