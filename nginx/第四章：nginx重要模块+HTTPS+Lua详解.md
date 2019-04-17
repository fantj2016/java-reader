###官方模块*
nginx -V 
显示的信息就是加载的模块信息
* #####Module（1）ngx_http_stub_status_module  本机状态
  >该ngx_http_stub_status_module模块提供对基本状态信息的访问。
此模块不是默认生成的，应该使用--with-http_stub_status_module 配置参数启用 。
  配置
  ```
  location / basic_status { 
      stub_status; 
  }
  ```
  Syntax(句法)：	stub_status;
  Default(默认)：	-
  Context(语境)：	server， location
  实例：
   1.增加配置   
        ![image.png](http://upload-images.jianshu.io/upload_images/5786888-282d5103c53c9739.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
  2.验证语法
  `nginx -tc /etc/nginx/nginx.conf`     检查文件是否有错
  3.重载服务
  `nginx -s reload -c /etc/nginx/nginx.conf`
  4.效果查看
  访问59.110.243.88/mystatus   
  ![image.png](http://upload-images.jianshu.io/upload_images/5786888-19e0f5b89f2f8abe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####Module（2） ngx_http_random_index_module  主页随机
  >该ngx_http_random_index_module模块处理以斜线字符（' /'）结尾的请求，并选取一个目录中的随机文件作为索引文件。该模块在ngx_http_index_module模块之前进行处理 。
此模块不是默认生成的，应该使用--with-http_random_index_module 配置参数启用 。
  ```
  location / {
      random_index on;
  }
  ```
*  Directives(指令):
    Syntax(语法):	random_index on | off;
    Default（默认）:	 random_index off;
    Context（语境）:	location
#####Module（3） ngx_http_sub_module  字符替换
  >The ngx_http_sub_module module is a filter that modifies a response by replacing one specified string by another.
This module is not built by default, it should be enabled with the --with-http_sub_module configuration parameter.

* Example Configuration
```
  location / {
    sub_filter '<a href="http://127.0.0.1:8080/'  '<a href="https://$host/';
    sub_filter '<img src="http://127.0.0.1:8080/' '<img src="https://$host/';
    sub_filter_once on;
  }
```
  * Directives
Syntax:	sub_filter string replacement;
Default:	—
Context:	http, server, location
* #####Module（4） ngx_http_limit_conn_module 连接限制
  >该ngx_http_limit_conn_module模块用于**限制**每个定义的密钥的连接数量，特别是来自单个IP地址的连接数量。
并非所有连接都被计算在内 只有在服务器处理请求并且已经读取了整个请求头时才计算连接。
  * 一次只允许每个IP地址一个连接。
  ```
  limit_conn_zone $binary_remote_addr zone=addr:10m;
  server {
    location /download/ {
        limit_conn addr 1;
    }
  ```
  * 可能有几个limit_conn指令。例如，以下配置将限制每个客户端IP到服务器的连接数，同时限制连接到虚拟服务器的总数：
  ```
  limit_conn_zone $ binary_remote_addr zone = perip：10m; 
  limit_conn_zone $ server_name zone = perserver：10m; 
  server{ 
      ... 
      limit_conn perip 10; 
      limit_conn perserver 100; 
    }
  ```
  * Directives
  Syntax:	limit_conn zone number;
Default:	—
Context:	http, server, location

  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_limit_conn_module.html)
* #####Module（5） ngx_http_limit_req_module 请求限制
  >所述ngx_http_limit_req_module模块（0.7.21）用于**限制每一个定义的密钥的请求的处理速率**，特别是从一个单一的IP地址的请求的处理速率。限制是使用“泄漏桶”方法完成的。

  * 平均每秒不超过1个请求，连发数不超过5个请求。
  ```
  limit_req_zone $binary_remote_addr zone=one:10m rate=1r/s;
  server {
    location /search/ {
        limit_req zone=one burst=5;
    }
  ```
  * 可能有几个limit_req指令。例如，以下配置将限制来自单个IP地址的请求的处理速率，同时限制虚拟服务器的请求处理速率：
  ```
  limit_req_zone $binary_remote_addr zone=perip:10m rate=1r/s;
  limit_req_zone $server_name zone=perserver:10m rate=10r/s;
  server {
    ...
    limit_req zone=perip burst=5 nodelay;
    limit_req zone=perserver burst=10;
  }
  ```
  * Directives
   Syntax:	limit_req zone=name [burst=number] [nodelay];
Default:	—
Context:	http, server, location
  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_limit_req_module.html)

* #####Module（6） ngx_http_access_module 限制访问某些客户端地址。
  >该ngx_http_access_module模块允许限制访问某些客户端地址。访问也可以通过密码子请求结果或JWT来限制满足控制地址和密码的同时访问限制

  **具有局限性，因为用户一旦通过代理来访问的话，是阻止不了的。http_x_forwarded_for头信息控制访问 会更好的解决该问题,它要求访问时必须带上所有用到的ip的地址信息**
######局限性解决方法总结：
  方法一： 采用http头信息控制访问，如HTTP_X_FORWARD_FOR
  方法二： 结合geo模块
  方法三： 通过HTTP自定义变量传递  
  * Example Configuration
  ```
  location / {
      deny  192.168.1.1;
      allow 192.168.1.0/24;
      allow 10.1.1.0/16;
      allow 2001:0db8::/32;
      deny  all;
  }

  实例：
        location ~ ^/admin.html{
                root /usr/share/nginx;
                deny 192.168.1.1;#访问admin.html需要限制的ip
                allow all;
                index index.html index.htm;
        }
  ```
  * 规则按顺序检查，直到找到第一个匹配。在本例中，只允许IPv4网络访问 `10.1.1.0/16``192.168.1.0/24` 不包括地址`192.168.1.1`和IPv6网络访问`2001:0db8::/32`在很多规则的情况下，使用模块变量是可取的。
  ```
  Syntax:	allow address | CIDR | unix: | all;
  Default:	—
  Context:	http, server, location, limit_except
  ```
  * 允许访问指定的网络或地址。如果unix:指定了特殊值（1.5.1），则允许访问所有UNIX域套接字。
   句法：	deny address | CIDR | unix: | all;
  默认：	-
  语境：	http，server，location，limit_except

  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_access_module.html)

* #####Module（7） ngx_http_auth_basic_module
  >该ngx_http_auth_basic_module模块允许通过使用“HTTP基本认证”协议验证用户名和密码来限制对资源的访问。访问也可以通过地址，jwt。满足指令控制地址和密码的同时访问限制。

在这里首先安装`htpasswd`
######局限性：
  一： 用户信息依赖文件
  二： 操作管理机械，效率低
######解决方式：
  一： nginx结合LUA实现高效验证
  二： nginx配合LDAP打通，利用nginx-auth-ldap模块
  * Example Configuration
  ```
      location / {
      auth_basic           "closed site";
      auth_basic_user_file conf/htpasswd;
    }
    实例：

  location ~ ^/admin.html{
                root /usr/share/nginx;
                auth_basic "please input your password!"#这里随便写字符串
                auth_basic_user_file /etc/nginx/password; #htpasswd生成的密码所在文件路径
                index index.html index.htm;
        }
 然后wq ->语法检查 nginx -t -c /etc/nginx/nginx.conf
  ```
  * 使用“HTTP基本验证”协议验证用户名和密码。指定的参数用作a realm。参数值可以包含变量（1.3.10,1.2.7）。特殊值off允许取消auth_basic从先前配置级别继承的指令的效果。
  ```
  Syntax:	auth_basic string | off;
  Default:	
  auth_basic off;
  Context:	http, server, location, limit_except

  ```
  * 指定一个保存用户名和密码的文件，格式如下：
\# comment
name1:password1
name2:password2:comment
name3:password3
```
    Syntax:	auth_basic_user_file file;
    Default:	—
    Context:	http, server, location, limit_except
```

  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_auth_basic_module.html)

* #####Module（8） ngx_http_addition_module
  >该ngx_http_addition_module模块是一个过滤器，在响应之前和之后添加文本。此模块不是默认生成的，应该使用--with-http_addition_module 配置参数启用 。

  * Example Configuration
  ```
      location / {
        add_before_body /before_action;
        add_after_body  /after_action;
      }
  ```
  * 在响应主体之前添加处理给定子请求的结果返回的文本。""作为参数的空字符串（）会取消从前一个配置级别继承的加法。
  ```
  句法：	add_before_body uri;
  默认：	-
  语境：	http，server，location
  ```
  * 在响应主体之后添加由于处理给定的子请求而返回的文本。""作为参数的空字符串（）取消了从以前的配置级别继承的加法。
  句法：	add_after_body uri;
  默认：	-
  语境：	http，server，location
  * 允许在指定的MIME类型的响应中添加文本，除了“ text/html”。特殊值“ *”匹配任何MIME类型（0.8.29）。
    句法：	addition_types mime-type ...;
    默认：	addition_types text / html;
    语境：	http，server，location
    该指令出现在0.7.9版本中。
  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_addition_module.html)

* #####Module(9) ngx_http_slice_module-大文件分片请求
* 优势：每个自请求收到的数据都会形成一个独立文件，一个请求断了，其他请求不受影响。
* 缺点：当文件很大或者slice很小的时候，可能会导致文件描述符耗尽等情况
```
location / {
    slice             1m;
    proxy_cache       cache;
    proxy_cache_key   $uri$is_args$args$slice_range;
    proxy_set_header  Range $slice_range;
    proxy_cache_valid 200 206 1h;
    proxy_pass        http://localhost:8000;
}
```
###高级模块篇
* #####Module（10） ngx_http_secure_link_module
  >该ngx_http_secure_link_module模块（0.7.18）用于检查请求链接的真伪，保护资源免受未经授权的访问，并限制连杆的寿命。
请求的链接的真实性通过将请求中传递的校验和值与为请求计算的值进行比较来验证。如果链接的使用期限有限且时间已过，则认为该链接已过时。这些检查的状态在$secure_link变量中可用 。
该模块提供两种替代操作模式。第一种模式由secure_link_secret指令启用，用于检查请求的链接的真实性，并保护资源免受未经授权的访问。第二种模式（0.8.50）由secure_link和secure_link_md5指令启用， 也用于限制链接的生存期。

此模块不是默认生成的，应该使用--with-http_secure_link_module 配置参数启用 。

  * Example Configuration
  ```
location /s/ {
    secure_link $arg_md5,$arg_expires;
    secure_link_md5 "$secure_link_expires$uri$remote_addr secret";

    if ($secure_link = "") {
        return 403;
    }

    if ($secure_link = "0") {
        return 410;
    }

    ...
}
--------------------------------分割线----------------------------------------------
location /p/ {
    secure_link_secret secret;

    if ($secure_link = "") {
        return 403;
    }

    rewrite ^ /secure/$secure_link;
}

location /secure/ {
    internal;
}
  ```
  * 定义一个带有变量的字符串，从中提取链接的校验值和生存期。 
  ```
句法：	secure_link expression;
默认：	-
语境：	http，server，location
  ```
  * 定义word用于检查所请求的链接的真实性的秘密。
```
句法：	secure_link_secret word;
默认：	-
语境：	location
```
  * 定义一个计算MD5哈希值的表达式，并将其与请求中传递的值进行比较。
```
句法：	secure_link_md5 expression;
默认：	-
语境：	http，server，location
```

  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_secure_link_module.html)
* #####Module（11） ngx_http_geoip_module
  * 使用场景：
  1. 区别国内外作HTTP访问规则
  2. 区别国内城市地域作HTTP访问规则
  * 该模块需要下载MaxMind库（存放世界的ip信息）
```
location / {
    if ($geoip_country_code != CN){
          return 403;      #如果ip不是中国的，就返回403
    }
}
```
  * [详细官方链接 ](http://nginx.org/en/docs/http/ngx_http_geoip_module.html)
* #####Module（12） 配置HTTPS服务器
  * 为什莫要用HTTPS？：
  1. HTTP传输数据可以被中间人盗用
  2. 数据被劫持篡改
  3. 同时用到对称加密+非对称加密
  * 该模块需要安装openssl （用于生成密钥和CA证书）
```
cd /etc/nginx
mkdir ssl_key
openssl genrsa -idea -out fantj.key 1024
      enter pass phrase for fantj.key:
------
openssl req -new -key fantj.key -out fant.csr
------
openssl x509 -req -days 3650 -in fantj.csr -signkey fantj.key -out fantj.crt #打包成crt文件

server {
    listen              443 ssl;
    server_name         127.0.0.1：8080;
    ssl_certificate     /etc/nginx/ssl_key/fantj.crt;
    ssl_certificate_key /etc/nginx/ssl_key/fantj.key;
    ssl_protocols       TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers         HIGH:!aNULL:!MD5;
    ...
}
```
   * HTTPS服务优化
        1. 激活keepalive长连接
        2. 设置ssl session缓存 
```
http {
    ssl_session_cache   shared:SSL:10m; #设置缓存
    ssl_session_timeout 10m;   #设置过期时间

    server {
        listen              443 ssl;
        server_name         www.example.com;
        keepalive_timeout   70;   #保持连接时间

        ssl_certificate     www.example.com.crt;
        ssl_certificate_key www.example.com.key;
        ssl_protocols       TLSv1 TLSv1.1 TLSv1.2;
        ssl_ciphers         HIGH:!aNULL:!MD5;
        ...
```
  * [详细官方链接 ](http://nginx.org/en/docs/http/configuring_https_servers.html)
* #####Module（13） nginx+Lua 实现灰度发布
  >灰度发布（又名金丝雀发布）是指在黑与白之间，能够平滑过渡的一种发布方式。在其上可以进行A/B testing，即让一部分用户继续用产品特性A，一部分用户开始用产品特性B，如果用户对B没有什么反对意见，那么逐步扩大范围，把所有用户都迁移到B上面来。灰度发布可以保证整体系统的稳定，在初始灰度的时候就可以发现、调整问题，以保证其影响度。
  * 安装lua `yum install lua`
  * 运行lua  `lua;  print("hello");`
  * 注释   `--行注释     --[[   --]]块注释`
  * 基础语法 http://www.runoob.com/lua/lua-tutorial.html
  * nginx&lua wiki https://www.nginx.com/resources/wiki/modules/lua/
```
location / {
      default_type "text/html";
      content_by_lua_file /opt/app/lua/dep.lua;   #将根目录请求交给dep.lua过滤处理
}
至于dep.lua源码，大家尽可在百度中得到，在这里我不做重点，以后可能会单独作为一章。
bacause重点是模块讲解，so这里提纲挈领下。
~sorry
```
