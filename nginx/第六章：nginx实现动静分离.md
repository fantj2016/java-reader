> 为什么要做动静分离呢？
减少不必要的请求消耗，减少请求延时。

>怎么才能做到动静分离呢？
首先我们得想什么是静态东西，什么需要动态获取。我想大家心里肯定都很清楚，图片、影视、音乐等文件一般属于静态文件，带有.jsp   .ftl   .do 等后缀请求的应该都是动态获取。所以我们在nginx里这样配置:

* 假设我启动了一个tomcat服务
```
upstream tomcat_server{
        server 127.0.0.1:8080'
}

server{
        listen 80;
        server_name localhost;
        ......
        location ~ \.jsp$ {
              proxy_pass http://tomcat_server;  #对.jsp请求做重定向到tomcat服务
              index index.html index.htm; 
        }
        location ~ \.(jpg|png|gif)$ {
              expires 1h;  #静态缓存过期时间
              gzip  on;    #压缩开启
        }
        ......
}
```
