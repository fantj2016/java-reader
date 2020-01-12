

### 1. 安装
`yum install httpd -y`

#### 开启端口

`firewall-cmd --permanent --add-service=http`

### 2. 配置
>配置文件都在`/etc/httpd/conf`下。


##### 查看已经加载的模块

`httpd -M`
##### 主配置文件

`/etc/httpd/conf/httpd.conf`

```
ServerRoot "/etc/httpd"  根目录
Listen 80  监听80端口
Include conf.modules.d/*.conf   扫描配置文件目录
User apache  所有者
Group apache   所属组
ServerAdmin root@localhost  设置管理员邮箱
ServerName www.example.com:80  域名
<Directory />    访问目录
    AllowOverride none  不允许覆盖
    Require all denied  所有都拒绝访问
</Directory>

DocumentRoot "/var/www/html"  html目录

<Directory "/var/www">   
    AllowOverride None    
    # Allow open access:
    Require all granted   所有请求都授权
</Directory>

<Directory "/var/www/html">
    Options Indexes FollowSymLinks  Indexes:类似ftp功能，FollowSymLinks：支持软连接
    AllowOverride None   配置不被覆盖
    Require all granted
</Directory>

Alias  /path  /realpath  访问/path的时候实则访问/realpath
当然，完成上面的alias功能还需要添加访问权限,如下：
<Directory "/www">
    Options Indexes FollowSymLinks  Indexes:类似ftp功能，FollowSymLinks：支持软连接
    AllowOverride None   配置不被覆盖
    Require all granted
</Directory>

<IfModule alias_module>
    ScriptAlias /cgi-bin/ "/var/www/cgi-bin/"   动态脚本支持，访问/cgi-bi时，实际访问本地/var/www/cgi-bin/
</IfModule>

ErrorLog "logs/error_log"   错误日志

EnableSendfile on
```

##### 简单配置虚拟主机

从`/usr/share/doc/httpd-2.4.6/httpd-vhosts.conf`中拷贝一份模版到`/etc/httpd/conf.d/`中

```
<VirtualHost *:80>
    DocumentRoot "/var/www/html"
    ServerName fantj1.com
</VirtualHost>
```
然后在`/var/www/html/`下添加index.html文件。

重启服务请求`fantj1.com`即可访问到index.html。(前提是hosts认识fantj1.com这个域名)

### 3.  配置CA


##### 3.1 生成自签名CA证书
`cd /etc/pki/tls/certs/`然后执行`make caname.crt`

```
[root@s168 certs]# make fantj.crt
umask 77 ; \
/usr/bin/openssl genrsa -aes128 2048 > fantj.key
Generating RSA private key, 2048 bit long modulus
...............+++
....................+++
e is 65537 (0x10001)
Enter pass phrase:
Verifying - Enter pass phrase:
```
然后目录里多了两个文件：`fantj.crt`和`fantj.key`
##### 3.2 将私钥公钥拷贝
`cp fantj.* /etc/httpd/conf/`

##### 3.3 安装https模块
`yum install mod_ssl -y`

编辑`/etc/httpd/conf.d/ssl.conf`文件。
```
Listen 443 https   监听端口


SSLPassPhraseDialog exec:/usr/libexec/httpd-ssl-pass-dialog

SSLSessionCache         shmcb:/run/httpd/sslcache(512000)
SSLSessionCacheTimeout  300

SSLRandomSeed startup file:/dev/urandom  256
SSLRandomSeed connect builtin

SSLCryptoDevice builtin


<VirtualHost _default_:443>   虚拟主机配置


ErrorLog logs/ssl_error_log
TransferLog logs/ssl_access_log
LogLevel warn    日志信息

SSLEngine on    ssl开关

SSLProtocol all -SSLv2 -SSLv3   支持ssl协议

SSLCipherSuite HIGH:3DES:!aNULL:!MD5:!SEED:!IDEA


SSLCertificateFile   证书 /etc/pki/tls/certs/localhost.crt

SSLCertificateKeyFile /etc/pki/tls/private/localhost.key   私钥





<Files ~ "\.(cgi|shtml|phtml|php3?)$">
    SSLOptions +StdEnvVars
</Files>
<Directory "/var/www/cgi-bin">
    SSLOptions +StdEnvVars
</Directory>

BrowserMatch "MSIE [2-5]" \
         nokeepalive ssl-unclean-shutdown \
         downgrade-1.0 force-response-1.0

CustomLog logs/ssl_request_log \
          "%t %h %{SSL_PROTOCOL}x %{SSL_CIPHER}x \"%r\" %b"

</VirtualHost>    
```

其中我们需要修改`SSLCertificateFile`和`SSLCertificateKeyFile`这两个配置，它们分别用来设置crt文件目录和key文件目录。其他保持默认配置即可。

```
SSLCertificateFile /etc/httpd/conf/fantj.crt
SSLCertificateKeyFile /etc/httpd/conf/fantj.key
```
###### 重启httpd服务
```
[root@s168 conf.d]# systemctl restart httpd
Enter SSL pass phrase for www.example.com:443 (RSA) : ******
```
好了，设置成功.



