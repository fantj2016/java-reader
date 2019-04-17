>FastDFS是一个开源的轻量级分布式文件系统，它对文件进行管理，功能包括：文件存储、文件同步、文件访问（文件上传、文件下载）等，解决了大容量存储和负载均衡的问题。特别适合以文件为载体的在线服务，如相册网站、视频网站等等。
FastDFS为互联网量身定制，充分考虑了冗余备份、负载均衡、线性扩容等机制，并注重高可用、高性能等指标，使用FastDFS很容易搭建一套高性能的文件服务器集群提供文件上传、下载等服务。
### 1. 下载安装 libfastcommon
>libfastcommon是从 FastDFS 和 FastDHT 中提取出来的公共 C 函数库，基础环境，安装即可 。

##### 1.1 下载：
```
wget https://github.com/happyfish100/libfastcommon/archive/V1.0.7.tar.gz
```
##### 1.2 解压：
```
tar -zxvf V1.0.7.tar.gz
```
##### 1.3 安装：
```
cd libfastcommon-1.0.7
./make.sh
./make.sh install
```
##### 1.4 复制文件：
>解决FastDFS中lib配置文件路径问题。
```
cp /usr/lib64/libfastcommon.so /usr/local/lib/libfastcommon.so
cp /usr/lib64/libfastcommon.so /usr/lib/libfastcommon.so
```

### 2. 下载安装FastDFS

###### 2.1 下载：
```
wget https://github.com/happyfish100/fastdfs/archive/V5.05.tar.gz
```
###### 2.2 解压：
```
tar -zxvf V5.05.tar.gz
```
###### 2.3 安装：
```
cd fastdfs-5.05
./make.sh   
./make.sh install
```

安装完成后，默认配置文件目录为：`/etc/fdfs/`，默认命令放在`/usr/bin/`中，以`fdfs_`开头。

### 3. 配置FastDFS
>首先将配置文件复制到`/etc/fdfs`目录下。

```
cd /home/fantj/download/fastdfs-5.05/conf
cp * /etc/fdfs/ 

如下：
[root@s168 conf]# cd /etc/fdfs/
[root@s168 fdfs]# ls
anti-steal.jpg  client.conf.sample  mime.types    storage.conf.sample  tracker.conf
client.conf     http.conf           storage.conf  storage_ids.conf     tracker.conf.sample
```

#### 3.1. 配置tracker.conf
>FastDFS跟踪器

修改如下配置：
```
#Tracker 数据和日志目录地址(根目录必须存在,子目录会自动创建)
base_path=/fastdfs/tracker
port=22122
```
###### 3.1.1 创建该目录：
`mkdir -p /fastdfs/tracker`

###### 3.1.2 启动/关闭：

`fdfs_trackerd /etc/fdfs/tracker.conf start/stop`

>默认使用`/etc/fdfs`下的配置文件，如有指定配置可在后面追加参数。如有防火墙，开放防火墙规则。


###### 3.1.3 设置开机启动：
```
# systemctl enable fdfs_trackerd

或者：
# vim /etc/rc.d/rc.local
加入配置：
/etc/init.d/fdfs_trackerd start 
```

###### 3.1.4 tracker server 目录及文件结构 :
>Tracker服务启动成功后，会在base_path下创建data、logs两个目录。目录结构如下：
```
${base_path}
  |__data
  |   |__storage_groups.dat：存储分组信息
  |   |__storage_servers.dat：存储服务器列表
  |__logs
  |   |__trackerd.log： tracker server 日志文件 
```

#### 3.2. 配置storage
>下面只是基本配置，如有更细微的需要，则请查看所有配置。
```
group_name=fantj     #组名
base_path=/fastdfs/storage   #日志目录
store_path0=/fastdfs/storage     #存储目录
tracker_server=192.168.27.168:22122    #tracker节点
http.server_port=8888    #端口
```

###### 3.2.1 启动
```
可以用这种方式启动
fdfs_storaged /etc/fdfs/storage.conf start

也可以用这种方式，后面都用这种
# service fdfs_storaged start
```
###### 3.2.2 检查进程
`netstat -unltp|grep fdfs`

######  3.2.3 Storage 开机启动
```
# systemctl enable fdfs_storaged

或者：
# vim /etc/rc.d/rc.local
加入配置：
/etc/init.d/fdfs_storaged start
```

###### 3.2.4 Storage 目录
Storage 启动成功后，在base_path 下创建了data、logs目录，记录着 Storage Server 的信息。

在 store_path0 目录下，创建了N*N个子目录：
```
[root@s168 data]# pwd
/fastdfs/storage/data
[root@s168 data]# ls
00  07  0E  15  1C  23  2A  31  38  3F  46  4D  54  5B  62  69  70  77  7E  85  8C  93  9A  A1  A8  AF  B6  BD  C4  CB  D2  D9  E0  E7  EE  F5  FC
01  08  0F  16  1D  24  2B  32  39  40  47  4E  55  5C  63  6A  71  78  7F  86  8D  94  9B  A2  A9  B0  B7  BE  C5  CC  D3  DA  E1  E8  EF  F6  FD
02  09  10  17  1E  25  2C  33  3A  41  48  4F  56  5D  64  6B  72  79  80  87  8E  95  9C  A3  AA  B1  B8  BF  C6  CD  D4  DB  E2  E9  F0  F7  fdfs_storaged.pid
03  0A  11  18  1F  26  2D  34  3B  42  49  50  57  5E  65  6C  73  7A  81  88  8F  96  9D  A4  AB  B2  B9  C0  C7  CE  D5  DC  E3  EA  F1  F8  FE
04  0B  12  19  20  27  2E  35  3C  43  4A  51  58  5F  66  6D  74  7B  82  89  90  97  9E  A5  AC  B3  BA  C1  C8  CF  D6  DD  E4  EB  F2  F9  FF
05  0C  13  1A  21  28  2F  36  3D  44  4B  52  59  60  67  6E  75  7C  83  8A  91  98  9F  A6  AD  B4  BB  C2  C9  D0  D7  DE  E5  EC  F3  FA  storage_stat.dat
06  0D  14  1B  22  29  30  37  3E  45  4C  53  5A  61  68  6F  76  7D  84  8B  92  99  A0  A7  AE  B5  BC  C3  CA  D1  D8  DF  E6  ED  F4  FB  sync
```



#### 3.3. 配置client
```
vim client.conf

tracker_server=192.168.27.168:22122   #tracker节点
base_path=/fastdfs/client   #日志路径
```


#### 3.4. 本地图片上传测试
```
[root@s168 fdfs]# fdfs_upload_file /etc/fdfs/client.conf /home/test.png 
fantj/M00/00/00/wKgbqFu7T7iAJh7lAABDYbhAMC4812.png

或者：
[root@s168 fdfs]# fdfs_test /etc/fdfs/client.conf /home/test.png 
This is FastDFS client test program v5.05

Copyright (C) 2008, Happy Fish / YuQing

FastDFS may be copied only under the terms of the GNU General
Public License V3, which may be found in the FastDFS source kit.
Please visit the FastDFS Home Page http://www.csource.org/ 
for more detail.

[2018-5-08 20:41:15] DEBUG - base_path=/fastdfs/client, connect_timeout=30, network_timeout=60, tracker_server_count=1, anti_steal_token=0, anti_steal_secret_key length=0, use_connection_pool=0, g_connection_pool_max_idle_time=3600s, use_storage_id=0, storage server id count: 0

invalid operation: /home/test.png

```


### 4. 安装nginx
>之前有写过nginx的安装文章，在这里不重复。没有安装过的请点击：


点击查看我的文章：[nginx安装](https://www.jianshu.com/p/822ad05f33af)


### 5. FastDFS 配置 Nginx 模块
>`fastdfs-nginx-module` 可以重定向文件链接到源服务器取文件，避免客户端由于复制延迟导致的文件无法访问错误。


###### 5.1 下载安装
```
wget https://github.com/happyfish100/fastdfs-nginx-module/archive/5e5f3566bbfa57418b5506aaefbe107a42c9fcb1.zip
unzip 5e5f3566bbfa57418b5506aaefbe107a42c9fcb1.zip
mv fastdfs-nginx-module-5e5f3566bbfa57418b5506aaefbe107a42c9fcb1/ fastdfs-nginx-module
```
###### 5.2 配置Nginx
```
ngix -s stop
cd nginx-1.12.1
#添加模块
./configure --add-module=../fastdfs-nginx-module/src
(如果你是yum安装的nginx，我暂时还没找到解决办法，目前的方法是nginx -V 查看nginx版本，然后下载一个同版本的nginx，
然后将/etc/nginx 下数据整体备份，make && make install 完成后再做恢复)
#重新编译和安装
make
make install

#拷贝配置文件
[root@s168 src]# pwd
/home/fantj/download/fastdfs-nginx-module/src
[root@s168 src]# cp mod_fastdfs.conf /etc/fdfs/
```
配置 /etc/fdfs/mod_fastdfs.conf文件
```
#配置 /etc/fdfs/mod_fastdfs.conf文件

base_path=/fastdfs/tmp  #日志目录
store_path0=/fastdfs/storage
tracker_server=192.168.27.168:22122
# the group name of the local storage server
group_name=fantj   #和storage的groupname一一对应
url_have_group_name = true   #开启url中附带group_name
```
配置nginx
```
# 配置nginx
[root@s168 fdfs]# cd /usr/local/nginx/conf/
[root@s168 conf]# vim nginx.conf
#添加如下配置
server {
    listen 88;
    server_name 192.168.27.168;

    location /fantj/M00{
            ngx_fastdfs_module;
    }
}

# 进入/usr/local/nginx/sbin目录执行配置检测
[root@s168 sbin]# ./nginx  -t
ngx_http_fastdfs_set pid=6431
nginx: the configuration file /usr/local/nginx/conf/nginx.conf syntax is ok
nginx: configuration file /usr/local/nginx/conf/nginx.conf test is successful
# 启动nginx
[root@s168 sbin]# ./nginx 
ngx_http_fastdfs_set pid=6436
```

服务重启并测试
```
[root@s168 fdfs]# fdfs_trackerd tracker.conf restart
waiting for pid [1202] exit ...
starting ...
[root@s168 fdfs]# fdfs_storaged storage.conf restart
waiting for pid [1211] exit ...
starting ...
[root@s168 fdfs]# /usr/local/nginx/sbin/nginx -s reload
ngx_http_fastdfs_set pid=6463
```

### 6. 配置成功测试

![](https://user-gold-cdn.xitu.io/2018/10/8/16653f8d9182a20e?w=1319&h=709&f=png&s=60068)


### 配置nginx_image_filter 实现图片大小切割
1. 查看nginx -V是否有image_filter模块
```
 --with-http_image_filter_module=dynamic 
```
我查出来是上面的(dynami)，所以我需要在全局配中加载该模块
```
load_module "/usr/lib64/nginx/modules/ngx_http_image_filter_module.so";
```
如果不知道自己的modules文件夹在哪，可在`nginx -V`查看。

2. 配置nginx转发规则
```
location ~ /yunding/M00/(.*)_([0-9]+)x([0-9]+)\.png{
        root /data/fastdfs/storage/data;
        ngx_fastdfs_module;
        set $w $2;
        set $h $3;
        if ($h != "0"){
                rewrite /yunding/M00/(.+)_(\d+)x(\d+)\.(jpg|gif|png)$ /yunding/M00/$1.$4 break;
        }

        if ($w != "0") {
                rewrite /yunding/M00/(.+)_(\d+)x(\d+)\.(jpg|gif|png)$ /yunding/M00/$1.$4 break;
        }
        image_filter resize $w $h;
        image_filter_buffer 2M;
}

location ~ yunding/M00/(.+)\.?(.+){
        alias /data/fastdfs/storage/data;
        ngx_fastdfs_module;
}
```
![](https://upload-images.jianshu.io/upload_images/5786888-b7603797238268ab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-f046c4bf1bb46f9b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
