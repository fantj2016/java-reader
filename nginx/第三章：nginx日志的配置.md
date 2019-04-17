#####配置文件路径
`vim  /etc/nginx/nginx.conf`
#####日志模块
关系我在这个图中都标记出来了
* ![image.png](http://upload-images.jianshu.io/upload_images/5786888-17c4f1c861715a31.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####error.log 和 access_log 区别  
* error.log  按不同级别记录运行状态
* access_log   记录请求访问状态

在main中可以选择自己想打印的**变量**，**变量**详情查看官网文档http://nginx.org/en/docs/syslog.html
