###一、静态资源WEB服务
>非服务器动态运行生成的文件

配置语法-文件读取
```
Synatax: sendfile on| off
Default: sendfile off;
Context: http,server,location,if in location
``` 
配置语法-tcp_noposh（sendfile开启时，提高网络传输效率）
```
Syntax: tcp_nopush on|off;
Default: tcp_nopush off;
Context: http,server,location
简单点说，就是批量收集再push
```
配置语法-tcp_nodelay
```
Syntax: tcp_nodelay on|off;
Default: tcp_nodelay off;
Context: http,server,location
及时push，实时性传输
```
配置语法-压缩
```
Syntax: gzip on|off;
Default: gzip  off;
Context: http,server,location,if in location
压缩传输，服务器端进行压缩，浏览器上自动解压
Syntax: gzip_comp_level level;
Default: gzip_comp_level 1;
Context: http,server,location
调试压缩比例。级别
Syntax:	gzip_http_version 1.0 | 1.1;
Default:	gzip_http_version 1.1;
Context:	http, server, location
版本号
```
扩展Nginx压缩模块

  1. http_gzip_static_module  -预读gzip功能
```
Syntax:	gzip_static on | off | always;
Default:	
gzip_static off;
Context:	http, server, location
先会去磁盘查找是否有已经压缩过的文件，再确定要不要进行压缩操作。如果磁盘有，直接就发送了压缩文件给客户端。
```
  2. http_gunzip_module -应用支持gunzip的压缩方式(不多用)

######实战：
配置文件（注释gzip功能）
```
    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
    }

    location ~ .*\.(jpg|gif|png) {
        #gzip on;
        #gzip_http_version 1.1;
        #gzip_comp_level 2;
        #gzip_types image/jpeg image/gif image/png;
        root /opt/app/code/images;
    }
    location ~ .*\.(txt|xml) {
        #gzip on;
        #gzip_http_version 1.1;
        #gzip_comp_level 2;
        #gzip_types image/jpeg image/gif image/png text/javascript text/plain;
        root /opt/app/code/doc;
    }
    location ~ ^/download {
        gzip_static on;
        tcp_nopush on;
        root /opt/app/code;
    }

```
![没有使用gzip.png](http://upload-images.jianshu.io/upload_images/5786888-f80178e08e93700c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后我们开启gzip,并重启nginx服务
emmmm我给虚拟机配置了，请求头里有gzip信息，但是size没有压缩。我再找找原因,这里先不放成功后的截图。

配置语法 -expires(静态资源过期设置)
```
添加Cache-Control、Expires头,
优点：可以跟服务器做实时交互
缺点：每次都会访问服务器看是否有更新
Syntax: expires [modified] time;
             expires epoch | max| off;
Default: expires off;
Context: http,server,location,if in location
```
![expires.png](http://upload-images.jianshu.io/upload_images/5786888-bab14fb8f1c0736f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######跨域访问
允许跨域访问
```
Syntax: add_header Access-Control-Allow-Origin *;    #星号代表所有ip都能跨域
        add_header Access-Control-Allow-Methods GET,POST,PUT,DELETE,OPTIONS; #可以跨域的方法
Default: --
Context: http,server,location,in in location
```
######防盗链--防止资源被盗用-http_refer
首先，最主要的是区别那些请求是非正常的用户请求
```
valid_referers none blocked server_names
               *.example.com example.* www.example.org/galleries/
               ~\.google\.;

if ($invalid_referer) {
    return 403;
}
```
http://nginx.org/en/docs/http/ngx_http_referer_module.html
###二、代理服务
正向代理
* 对象是客户端

反向代理
- 对象是服务端
```
location / {
    proxy_pass       http://localhost:8000;    #将localhost的8080端口代理到监听的server
    proxy_set_header Host      $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```
其他配置语法-缓冲区
```
Syntax:	proxy_buffering on | off;
Default:	proxy_buffering on;
Context:	http, server, location
```
其他配置语法-跳转重定向
```
Syntax:	proxy_redirect default;proxy_redirect off;proxy_redirect redirect replacement;
Default:	proxy_redirect default;
Context:	http, server, location
```
其他配置语法-头信息
```
Syntax:	proxy_set_header field value;
Default:	proxy_set_header Host $proxy_host;proxy_set_header Connection close;
Context:	http, server, location
```
其他配置语法-超时
```
Syntax:	proxy_connect_timeout time;
Default:	proxy_connect_timeout 60s;
Context:	http, server, location
```
实战：
```
location / {
      proxy_pass http://127.0.0.1:8080;
      proxy_redirect default;
      
      proxy_set_header Host $http_host;
      proxy_set_header X-Real_IP $remote_addr;
      
      proxy_connect_timeout 30;
      proxy_send_timeout 60;
      proxy_read_timeout 60;

      proxy_buffer_size 32k;
      proxy_buffering on;
      proxy_buffers 4 128k;
      proxy_busy_buffers_size 256k;
      proxy_max_temp_file_size 256k;
}
```
###三、负载均衡调度器SLB
```
upstream dynamic {
    zone upstream_dynamic 64k;
    hash $request_uri;   #采用hash策略，获取uri，确保以下操作在同一服务器

    server backend1.example.com      weight=5;
    server backend2.example.com:8080 fail_timeout=5s slow_start=30s;  #max_fail失败后服务暂停的时间
    server 192.0.2.1                 max_fails=3;   #允许请求失败次数
    server backend3.example.com      resolve;
    server backend4.example.com      service=http resolve;

    server backup1.example.com:8080  backup;
    server backup2.example.com:8080  backup;   #备用服务器
}

server {
    location / {
        proxy_pass http://dynamic;
        health_check;
    }
}
```
|策略|介绍|
|:------:|:------:|
|轮询|按时间顺序逐一分配到不通的后端服务器|
|加权轮询|weight值越大，分配到的访问几率越高|
|ip_hash|每个请求按访问IP的hash结果分配，这样癞子同一个IP的固定访问一个沟段服务器|
|least_conn|最少连接数，哪个机器连接数少就分发|
|url_hash|按照访问的URL的hash结果来分配氢气，是每个URL丁香到同一个后端服务器|
|hash关键数值|hash自定义的key|
只有最后一个hash关键数值策略能保证用户访问后，一直停留在该服务器。
###四、动态缓存
######客户端缓存
 * 浏览器缓存
######代理缓存nginx
配置语法-proxy_cache
```
Syntax:	proxy_cache zone | off;
Default:	proxy_cache off;
Context:	http, server, location
```
配置语法-缓存过期周期
```
Syntax:	proxy_cache_valid [code ...] time;
Default:	—
Context:	http, server, location
```
配置语法-缓存维度
```
Syntax:	proxy_cache_key string;
Default:	proxy_cache_key $scheme$proxy_host$request_uri;
Context:	http, server, location
```
配置语法-缓存路径
```
Syntax:	proxy_cache_path path [levels=levels] [use_temp_path=on|off] keys_zone=name:size [inactive=time] [max_size=size] [manager_files=number] [manager_sleep=time] [manager_threshold=time] [loader_files=number] [loader_sleep=time] [loader_threshold=time] [purger=on|off] [purger_files=number] [purger_sleep=time] [purger_threshold=time];
Default:	—
Context:	http
```
配置语法-部分页面不缓存
```
Syntax:	proxy_no_cache string ...;   #string 是
Default:	—
Context:	http, server, location
```
实战：
```
upstream fantj{
      server 192.168.0.1:8081;
      server 192.168.0.1:8082;
      server 192.168.0.1:8083;
}
proxy_cache_path /opt/app/cache levels=1:2 keys_zone=fantj_cache:10m max_size=10g inactive=60m use_temp_path=off;

server {
      listen 80;
      server_name localhost fantj.com;
       ......
      location / {
            proxy_cache fantj_cache;
            proxy_pass http://fantj;
            proxy_cache_valid 200 304 12h;
            proxy_cache_valid any 10m;
            proxy_cache_key $host$uri$is_args$args;
            add_header Nginx-Cache "$upstream_cache_status";
            
            proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;

            include my_proxy_params;   #读取自定义的proxy配置文件
      }
}
```
如何清理指定缓存
1. rm -rf 缓存目录
2. 第三方模块`ngx_cache_purge`
