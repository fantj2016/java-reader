>与git相似，docker也有自己的镜像仓库，官方仓库网站是https://hub.docker.com/，其实我们平时docker pull xxx就是从该仓库得到的镜像（在不设置国内镜像加速的情况下）。它和git仓库很相似。

### 创建账号
DockerHub：https://hub.docker.com/
很多人在这里就出了问题，为什么都填完了但是不能点注册按钮呢？因为该网站目前来说注册是需要翻墙的，注册成功后再关闭翻墙。

![](https://upload-images.jianshu.io/upload_images/5786888-f28388b432283eae.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


我重新上传一个项目做示范，该镜像是从hub上pull到的一个nginx镜像，我把它上传到我的公开仓库。

### 上传镜像
先登录docker hub账号。
```shell
docker login


[root@FantJ ~]# docker login
Login with your Docker ID to push and pull images from Docker Hub. If you don't have a Docker ID, head over to https://hub.docker.com to create one.
Username (fantj): fantj
Password: 
Login Succeeded

```


```shell
[root@FantJ ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
docker.io/openjdk   8-jre               bef23b4b9cac        2 weeks ago         443 MB
docker.io/nginx     latest              ae513a47849c        4 weeks ago         109 MB
[root@FantJ ~]# docker tag docker.io/nginx fantj/nginx
[root@FantJ ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
docker.io/openjdk   8-jre               bef23b4b9cac        2 weeks ago         443 MB
fantj/nginx         latest              ae513a47849c        4 weeks ago         109 MB
docker.io/nginx     latest              ae513a47849c        4 weeks ago         109 MB
[root@FantJ ~]# docker push fantj/nginx
The push refers to a repository [docker.io/fantj/nginx]
7ab428981537: Mounted from library/nginx 
82b81d779f83: Mounted from library/nginx 
d626a8ad97a1: Mounted from library/nginx 
latest: digest: sha256:e4f0474a75c510f40b37b6b7dc2516241ffa8bde5a442bde3d372c9519c84d90 size: 948
[root@FantJ ~]# 

```

流程大概是：登录->tag操作->push
**注：tag 的第二个参数的前缀是你的hub账户名**

![](https://upload-images.jianshu.io/upload_images/5786888-b397584985a2728e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 拉取镜像
我先把服务器上的镜像删除掉，然后再从hub中拉取镜像。

``` shell

# 删除本地fantj/nginx镜像
[root@FantJ ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
docker.io/openjdk   8-jre               bef23b4b9cac        2 weeks ago         443 MB
fantj/nginx         latest              ae513a47849c        4 weeks ago         109 MB
docker.io/nginx     latest              ae513a47849c        4 weeks ago         109 MB
[root@FantJ ~]# docker rmi fantj/nginx 
Untagged: fantj/nginx:latest
Untagged: fantj/nginx@sha256:e4f0474a75c510f40b37b6b7dc2516241ffa8bde5a442bde3d372c9519c84d90
[root@FantJ ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
docker.io/openjdk   8-jre               bef23b4b9cac        2 weeks ago         443 MB
docker.io/nginx     latest              ae513a47849c        4 weeks ago         109 MB



# 从hub中拉取fantj/nginx镜像
[root@FantJ ~]# docker pull fantj/nginx
Using default tag: latest
Trying to pull repository docker.io/fantj/nginx ... 
latest: Pulling from docker.io/fantj/nginx
Digest: sha256:e4f0474a75c510f40b37b6b7dc2516241ffa8bde5a442bde3d372c9519c84d90
Status: Downloaded newer image for docker.io/fantj/nginx:latest
[root@FantJ ~]# docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             SIZE
docker.io/openjdk       8-jre               bef23b4b9cac        2 weeks ago         443 MB
docker.io/fantj/nginx   latest              ae513a47849c        4 weeks ago         109 MB
docker.io/nginx         latest              ae513a47849c        4 weeks ago         109 MB
[root@FantJ ~]# 

```
