### 1. 安装Shadowsocks客户端

```
yum -y install epel-release
yum -y install python-pip
pip install shadowsocks
```

### 2. 配置客户端
##### 新建配置文件
```
 mkdir /etc/shadowsocks
 vi /etc/shadowsocks/shadowsocks.json
```
添加如下信息
```
{
    "server":"xx.xx.xx.xx",
    "server_port":xxxx,
    "local_address": "127.0.0.1",
    "local_port":1080,
    "password":"password",
    "timeout":300,
    "method":"aes-256-cfb",
    "fast_open": false,
    "workers": 1
}
```
##### 配置自启动 
新建启动脚本文件/etc/systemd/system/shadowsocks.service
```
[Unit]
Description=Shadowsocks
[Service]
TimeoutStartSec=0
ExecStart=/usr/bin/sslocal -c /etc/shadowsocks/shadowsocks.json
[Install]
WantedBy=multi-user.target
```
##### 启动Shadowsocks并开机自启
```
systemctl enable shadowsocks.service
systemctl start shadowsocks.service
systemctl status shadowsocks.service
```

##### 验证socks5是否配置成功
`curl --socks5 127.0.0.1:1080 http://httpbin.org/ip`

如果成功，则返回
```
{
  "origin": "x.x.x.x"       #你的Shadowsock服务器IP
}
```
此时你的系统还不能FQ，因为只是socks5服务搭建好了，还没有映射到http服务上。所以需要继续设置代理。

### 3. 安装配置Privoxy

##### 安装
```
yum -y install privoxy
systemctl enable privoxy
systemctl start privoxy
systemctl status privoxy
```

##### 配置Privoxy
1. 修改配置文件/etc/privoxy/config
```
确保如下内容没有被注释掉
listen-address 127.0.0.1:8118 # 8118 是默认端口，不用改
forward-socks5t / 127.0.0.1:1080 . #转发到本地端口，注意最后有个点
```
2. vim /etc/profile 添加如下信息
```
export http_proxy=http://127.0.0.1:8118       #这里的端口和上面 privoxy 中的保持一致
export https_proxy=http://127.0.0.1:8118
```
请记住这个端口，这就是你的本地http代理的端口，若有需要，需要在chrome中设置，火狐不需要。推荐chrom的一个插件：`智能代理`，然后将自己的本地代理添加进去即可。
![](https://upload-images.jianshu.io/upload_images/5786888-99a2fb9c444b5b87.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3. 刷新设置
```
source /etc/profile
```
4. 测试是否成功
```
[fantj@localhost ~]$ curl -I www.google.com
HTTP/1.1 200 OK
Date: Tue, 20 Nov 2018 17:11:35 GMT
Expires: -1
Cache-Control: private, max-age=0
Content-Type: text/html; charset=ISO-8859-1
P3P: CP="This is not a P3P policy! See g.co/p3phelp for more info."
Server: gws
X-XSS-Protection: 1; mode=block
X-Frame-Options: SAMEORIGIN
Set-Cookie: GMT; path=/; domain=.google.com
Set-Cookie: NID=146=WjgWbAavrnMqF3RRthCOkS18kpSV_-tKFGrckiL7EzovOU9wTIqCJhksR-PoI3lufSPRCxCroweE9sbxcJ3Bqv28KkjEqWy-ZNFGcS_wZ90Oe2SF8QxEC-_VCTZoKScv2v928iYfElBSfVjd075U_3hE16uYlNKFkzKYJjJTq1Q; expires=Wed, 22-May-2019 17:11:35 GMT; path=/; domain=.google.com; HttpOnly
Transfer-Encoding: chunked
Accept-Ranges: none
Vary: Accept-Encoding
Proxy-Connection: keep-alive
```
5. 取消使用代理
```
while read var; do unset $var; done < <(env | grep -i proxy | awk -F= '{print $1}')
```
