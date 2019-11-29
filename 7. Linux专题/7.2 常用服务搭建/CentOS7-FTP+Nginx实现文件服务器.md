>为什么要有文件服务器呢，如果把文件存放到web容器下肯定不是好的办法，因为你时刻需要注意覆盖后文件消失的问题。如果想完全的搬出web容器，项目不大的话我们可以选择ftp+nginx的方式来实现文件服务器。如果文件需求量大点的，建议用分布式文件服务器，它对横向扩展的支持比较好。
### 1. 环境准备
1. [CentOS 搭建 ftp 服务](https://www.jianshu.com/p/9b6389a26336)
2. [nginx环境搭建](https://www.jianshu.com/p/822ad05f33af)
### 2. 开始整合

##### 2.1 修改nginx.conf

我们需要再nginx.conf配置文件里增加一段配置来拦截所有的.jpg等图片请求
```
        location  ~ .*\.(jpg|gif|png|bit|jpeg){
                gzip on;
                root /home/ftpfile/ftp;
        }

```
`gzip on；`是开启图片压缩。不熟悉的可以看我的nginx专题https://www.jianshu.com/nb/18436827
`root  /xxx` 是重定向到本地路径。 注意ftp后面没有斜杠。
##### 2.2 重新加载配置
```
nginx -s reload
```
##### 2.3 访问 ip+图片名

此时可能会报错404或者403，那是因为nginx用户没有权限去ftp服务力拿东西。
所以我们需要配置
##### 2.4 配置nginx用户可访问
```
chown nginx /home/ftpfile/ftp
```
设置后的截图
![](https://upload-images.jianshu.io/upload_images/5786888-09bca82565498f91.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 2.5 成功截图
![](https://upload-images.jianshu.io/upload_images/5786888-de187258b16f99f7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
