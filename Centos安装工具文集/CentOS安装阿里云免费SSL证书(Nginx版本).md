### 申请免费证书

![](https://upload-images.jianshu.io/upload_images/5786888-acd610123247039d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 神奇？为什么我找不到你这个页面？别着急

正确的姿势是：倒着往上点，symantec---单个域名----免费型

申请完后需要填写表（点击补全）
如果你的域名就是阿里云解析的，那么打个勾会更方便
![](https://upload-images.jianshu.io/upload_images/5786888-289edd286206e969.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

填写完后提交，等待几分钟就ok了。

### 下载证书


![](https://upload-images.jianshu.io/upload_images/5786888-b5b511fe0925799a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

把证书下载下来，然后传到服务器。

我把他放在了/etc/nginx/cert 下
```
[root@FantJ cert]# pwd
/etc/nginx/cert
[root@FantJ cert]# ls
214743286220329.key  214743286220329.pem
[root@FantJ cert]# 

```

### nginx添加支持
```
server {
        listen       80 default_server;
        listen       [::]:80 default_server;
        listen       443 ssl;
        server_name  localhost;
        root         /home/html;

        ssl_certificate   /etc/nginx/cert/214743286220329.pem;
        ssl_certificate_key  /etc/nginx/cert/214743286220329.key;
        ssl_session_timeout 5m;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
        ssl_prefer_server_ciphers on;

```

记住，如果你想http和https都可以访问，就按照我的配置（也是官方推荐的配置）。
如果不符合你的模式，详情请看官方：http://nginx.org/en/docs/http/configuring_https_servers.html#single_http_https_server


### 如果你死活发现配置都对，但是访问报错400

我在部署的时候也出现了这个问题，然后我查看日志发现443端口被占用，进一步查看得知是docker的harbor占用了此端口，然后我把harbor的配置文件做了端口修改，重启harbor，重启nginx。ok！

所以问题大同小异，你需要追踪日志或者查看是否有程序占用了443端口。
