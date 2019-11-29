>为什么要弄私有仓库，大多是为了速度，我们再私有仓库中的push和pull的速度是特别快的。

### 利用registry快速搭建

https://hub.docker.com/_/registry/

```shell
Run a local registry: Quick Version
$ docker run -d -p 5000:5000 --restart always --name registry registry:2
Now, use it from within Docker:

$ docker pull ubuntu
$ docker tag ubuntu localhost:5000/ubuntu
$ docker push localhost:5000/ubuntu
```

这是官方的一个小demo。下面是我自己的实践。

```shell
# 拉取registry2.0版本
[root@FantJ ~]# docker pull registry:2
Trying to pull repository docker.io/library/registry ... 
2: Pulling from docker.io/library/registry
81033e7c1d6a: Pull complete 
b235084c2315: Pull complete 
c692f3a6894b: Pull complete 
ba2177f3a70e: Pull complete 
a8d793620947: Pull complete 
Digest: sha256:672d519d7fd7bbc7a448d17956ebeefe225d5eb27509d8dc5ce67ecb4a0bce54
Status: Downloaded newer image for docker.io/registry:2
# 后台启动运行
[root@FantJ ~]# docker run -d -p 5000:5000 --restart always --name registry registry:2
ce5b8bfa6d7f535906730ea3a058b00e7cfdaaa20ea0db3c49d700a4e2c8a645
[root@FantJ ~]# docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             SIZE
docker.io/openjdk       8-jre               bef23b4b9cac        2 weeks ago         443 MB
docker.io/fantj/nginx   latest              ae513a47849c        4 weeks ago         109 MB
docker.io/nginx         latest              ae513a47849c        4 weeks ago         109 MB
docker.io/registry      2                   d1fd7d86a825        4 months ago        33.3 MB
# tag 镜像
[root@FantJ ~]# docker tag docker.io/nginx 127.0.0.1:5000/nginx
# 上传到私有仓库
[root@FantJ ~]# docker push 127.0.0.1:5000/nginx
The push refers to a repository [127.0.0.1:5000/nginx]
7ab428981537: Pushed 
82b81d779f83: Pushed 
d626a8ad97a1: Pushed 
latest: digest: sha256:e4f0474a75c510f40b37b6b7dc2516241ffa8bde5a442bde3d372c9519c84d90 size: 948
[root@FantJ ~]# 

```
缺点：没有可视化管理工具，私有仓库服务宕机造成麻烦,没有用户管理机制、没有操作的记录功能。
优点：搭建操作简单


### 利用harbor搭建
下载地址：https://storage.googleapis.com/harbor-releases/release-1.5.0/harbor-offline-installer-v1.5.1.tgz
```
wget https://storage.googleapis.com/harbor-releases/release-1.5.0/harbor-offline-installer-v1.5.1.tgz

tar zxvf harbor-offline-installer-v1.5.1.tgz

cd harbor

```
##### 修改配置harbor.cfg
```
hostname = fantj.top:8888 

http还是https啥的自己看着改
```
保存退出，直接运行install.sh
```
cd ..
./install.sh
```

注意一点：harbor默认会占用80端口，所以请确保你的80端口不被占用，那怎么修改呢？

##### 修改配置docker-compose.yml（需要改端口再进行）

将80:80的第一个80改为自定义的端口号

我在这里改成8888端口。

然后运行install.sh

```
......
[Step 4]: starting Harbor ...
Creating network "harbor_harbor" with the default driver
Creating harbor-log
Creating redis
Creating registry
Creating harbor-db
Creating harbor-adminserver
Creating harbor-ui
Creating nginx
Creating harbor-jobservice

✔ ----Harbor has been installed and started successfully.----

Now you should be able to visit the admin portal at http://fantj.top:8888 . 
For more details, please visit https://github.com/vmware/harbor .

```
好的，成功了，我们来访问一下。
![](https://upload-images.jianshu.io/upload_images/5786888-74ef71845b73d766.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

默认账号密码：
admin
Harbor12345

如果想修改，去harbor.cfg修改。

```shell
[root@FantJ harbor]# docker ps
CONTAINER ID        IMAGE                                  COMMAND                  CREATED             STATUS                   PORTS                                                                NAMES
ea66b87c5d26        vmware/harbor-jobservice:v1.5.1        "/harbor/start.sh"       7 minutes ago       Up 7 minutes                                                                                  harbor-jobservice
5fecbc47ea68        vmware/nginx-photon:v1.5.1             "nginx -g 'daemon ..."   7 minutes ago       Up 7 minutes (healthy)   0.0.0.0:443->443/tcp, 0.0.0.0:4443->4443/tcp, 0.0.0.0:8888->80/tcp   nginx
9ccfa0d137de        vmware/harbor-ui:v1.5.1                "/harbor/start.sh"       7 minutes ago       Up 7 minutes (healthy)                                                                        harbor-ui
b1f6387545d6        vmware/harbor-db:v1.5.1                "/usr/local/bin/do..."   7 minutes ago       Up 7 minutes (healthy)   3306/tcp                                                             harbor-db
6bcd46635374        vmware/registry-photon:v2.6.2-v1.5.1   "/entrypoint.sh se..."   7 minutes ago       Up 7 minutes (healthy)   5000/tcp                                                             registry
c40db866f7d2        vmware/harbor-adminserver:v1.5.1       "/harbor/start.sh"       7 minutes ago       Up 7 minutes (healthy)                                                                        harbor-adminserver
8d0ee20abfbf        vmware/redis-photon:v1.5.1             "docker-entrypoint..."   7 minutes ago       Up 7 minutes             6379/tcp                                                             redis
17c002dd8b98        vmware/harbor-log:v1.5.1               "/bin/sh -c /usr/l..."   7 minutes ago       Up 7 minutes (healthy)   127.0.0.1:1514->10514/tcp                                            harbor-log
[root@FantJ harbor]# docker images
REPOSITORY                    TAG                 IMAGE ID            CREATED             SIZE
vmware/redis-photon           v1.5.1              19245c7a4f51        3 days ago          207 MB
vmware/clair-photon           v2.0.1-v1.5.1       e7f0ab982469        3 days ago          303 MB
vmware/notary-server-photon   v0.5.1-v1.5.1       611385e920c3        3 days ago          211 MB
vmware/notary-signer-photon   v0.5.1-v1.5.1       f9e01495db0e        3 days ago          209 MB
vmware/registry-photon        v2.6.2-v1.5.1       2efae6b250b1        3 days ago          198 MB
vmware/nginx-photon           v1.5.1              90d35cd72a68        3 days ago          135 MB
vmware/harbor-log             v1.5.1              67000769dfac        3 days ago          200 MB
vmware/harbor-jobservice      v1.5.1              3f7a7987ca5b        3 days ago          194 MB
vmware/harbor-ui              v1.5.1              8dbe945233a8        3 days ago          212 MB
vmware/harbor-adminserver     v1.5.1              a11b8eb3f9d8        3 days ago          183 MB
vmware/harbor-db              v1.5.1              afa780d73279        3 days ago          526 MB
vmware/mariadb-photon         v1.5.1              59ed57632415        3 days ago          526 MB
vmware/postgresql-photon      v1.5.1              41b693c0ce50        3 days ago          221 MB
docker.io/openjdk             8-jre               bef23b4b9cac        2 weeks ago         443 MB
vmware/harbor-migrator        v1.5.0              466c57ab0dc3        4 weeks ago         1.16 GB
127.0.0.1:5000/nginx          latest              ae513a47849c        4 weeks ago         109 MB
docker.io/fantj/nginx         latest              ae513a47849c        4 weeks ago         109 MB
docker.io/nginx               latest              ae513a47849c        4 weeks ago         109 MB
vmware/photon                 1.0                 4b481ecbef2a        5 weeks ago         130 MB
docker.io/registry            2                   d1fd7d86a825        4 months ago        33.3 MB

```
可以看到我们的进程和镜像都多了很多的以vmware开头的（harbor是vmware公司的开源工具）.

好了，开始push测试

```
[root@FantJ harbor]# docker tag docker.io/nginx fantj.top:8888/internet-plus/nginx
[root@FantJ harbor]# docker push fantj.top:8888/internet-plus/nginx
The push refers to a repository [fantj.top:8888/internet-plus/nginx]
Get https://fantj.top:8888/v1/_ping: dial tcp: lookup fantj.top: no such host

```
它提示需要用https请求才安全，解决方式有两种：
第一种：docker启动的时候添加对域名+端口的信任    --insecure-registry=xxxx.xx.xx.xx:8888
第二种：添加ssl证书,改天更新

Harbor重启
```shell
#docker-compose stop
# ./install.sh
```
### 遇到过的问题
如果你的服务上挂了https，也就是SA证书，它和docker的harbor会有冲突，因为你访问的harbor端口会被
