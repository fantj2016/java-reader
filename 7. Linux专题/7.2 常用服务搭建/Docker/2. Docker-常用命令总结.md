### 列出镜像列表
```
docker images
```
### 获取新的镜像
```
docker pull centos:7.0
```

### 查找镜像
```
docker search httpd
```

### 更新镜像
```
docker commit -m="提交的信息" -a="作者" e218edb10161 更改后的镜像名
```
### 删除镜像
```shell
docker rmi 49c614fbbea8

Error response from daemon: conflict: unable to delete 49c614fbbea8 (must be forced) - image is referenced in multiple repositories

# 这个报错说明它被其他的镜像依赖，所以需要先删除依赖的image


[root@FantJ ~]# docker rmi 49c614fbbea8
Error response from daemon: conflict: unable to delete 49c614fbbea8 (must be forced) - image is being used by stopped container ffd74d6603e0

#这个报错说明它被容器所使用，所以需要先删除容器
[root@FantJ ~]# docker rm ffd74d6603e0
ffd74d6603e0


#如果发现它被很多个容器占用，就用批量命令
[root@FantJ ~]# docker rm $(docker ps -a -q)
27298d317f59
955bbef03be9
95889edc40c8
518eff5fed00
c0b7ca7b267b
ce5935dfa127
63c212771b1e
906780ebb103
[root@FantJ ~]# docker rmi 49c614fbbea8
Untagged: learn/pingtag:latest
Deleted: sha256:49c614fbbea80b328834ecde9a39d8f5bb812c32851e0e5ae39b514642426984
Deleted: sha256:1de562b8b13c8b7af429a9072ef19a35e3c9085a202ffee5ed5c8aef4046cad4

```


### 运行容器

我们通过docker的两个参数 -i -t，让docker运行的容器实现"对话"的能力

* -t:在新容器内指定一个伪终端或终端。

* -i:允许你对容器内的标准输入 (STDIN) 进行交互。

```shell
[root@FantJ ~]# docker run -i -t REPOSITORY:TAG /bin/bash
root@dc0050c79503:/#
```
REPOSITORY:TAG  表示的是 镜像仓库源:镜像版本标签

##### 在容器中执行ls
![](https://upload-images.jianshu.io/upload_images/5786888-85cd5e7cadc64bf7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###### 在完成操作之后，输入 exit命令来退出这个容器
### 后台启动

* -d：让容器在后台启动

![](https://upload-images.jianshu.io/upload_images/5786888-08be1d253202fbe4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 查看正在运行的容器

```shell
[root@FantJ ~]# docker ps
```



### 查看容器内控制台打印
```
[root@FantJ ~]# docker logs id
```
```
docker logs -f 7a38a1ad55c6
```
跟踪查看日志
### 停止容器
```shell
[root@FantJ ~]# docker stop 112f9e089c33
```

### 运行容器并设置端口
```
docker run -d -p 5000:5000 training/webapp python app.py
```
容器内部的 5000 端口映射到我们本地主机的 5000 端口上。

```
docker run -d -p 127.0.0.1:5001:5000 training/webapp python app.py
```
这样我们就可以通过访问127.0.0.1:5001来访问容器的5000端口
### 查看 容器端口
```
docker port 7a38a1ad55c6
```

### 查看容器运行进程
```
docker top determined_swanson
```

### 构建镜像
后面章节讲
### 设置镜像标签
```
docker tag 860c279d2fec fantj/ip-web:1.0
```

### 使用本地配置运行nginx镜像
```shell
docker run -p 80:80 --name mynginx -v $PWD/www:/www -v $PWD/conf/nginx.conf:/etc/nginx/nginx.conf -v $PWD/logs:/wwwlogs  -d nginx 
```
* -p 80:80：将容器的80端口映射到主机的80端口

* --name mynginx：将容器命名为mynginx

* -v $PWD/www:/www：将主机中当前目录下的www挂载到容器的/www

* -v $PWD/conf/nginx.conf:/etc/nginx/nginx.conf：将主机中当前目录下的nginx.conf挂载到容器的/etc/nginx/nginx.conf

* -v $PWD/logs:/wwwlogs：将主机中当前目录下的logs挂载到容器的/wwwlogs

### 进到docker里的某一服务
```
docker exec -it id bash
```
