### 1. 配置docker-compose
>这里用到了一个中文版的docker镜像。https://hub.docker.com/r/twang2218/gitlab-ce-zh/
```
version: '3'
services:
    gitlab:
      image: 'twang2218/gitlab-ce-zh:9.4'
      restart: unless-stopped
      hostname: 'fantj.gitlab.top'
      environment:
        TZ: 'Asia/Shanghai'
        GITLAB_OMNIBUS_CONFIG: |
          external_url 'http://fantj.gitlab.top:9999'
          gitlab_rails['time_zone'] = 'Asia/Shanghai'
          # 需要配置到 gitlab.rb 中的配置可以在这里配置，每个配置一行，注意缩进。
          # 比如下面的电子邮件的配置：
          # gitlab_rails['smtp_enable'] = true
          # gitlab_rails['smtp_address'] = "smtp.exmail.qq.com"
          # gitlab_rails['smtp_port'] = 465
          # gitlab_rails['smtp_user_name'] = "xxxx@xx.com"
          # gitlab_rails['smtp_password'] = "password"
          # gitlab_rails['smtp_authentication'] = "login"
          # gitlab_rails['smtp_enable_starttls_auto'] = true
          # gitlab_rails['smtp_tls'] = true
          # gitlab_rails['gitlab_email_from'] = 'xxxx@xx.com'
      ports:
        - '9999:9999'
          #- '443:443'
          #- '22:22'
      volumes:
        - /home/fantj/app/docker/compose/gitlab/config:/etc/gitlab
        - /home/fantj/app/docker/compose/gitlab/data:/var/opt/gitlab
        - /home/fantj/app/docker/compose/gitlab/logs:/var/log/gitlab
```
### 2. 配置hosts
```
[fantj@lalala gitlab]$ cat /etc/hosts
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
127.0.0.1   www.xmind.net
127.0.0.1   fantj.gitlab.top
```
### 3. 创建目录
```
[fantj@lalala gitlab]$ pwd
/home/fantj/app/docker/compose/gitlab
[fantj@lalala gitlab]$ ll
总用量 16
drwxrwxr-x  3 root    root  4096 9月  4 11:37 config
drwxr-xr-x 18 root    root  4096 9月  4 11:42 data
-rw-rw-r--  1 fantj   fantj 1286 9月  4 11:42 docker-compose.yml
drwxr-xr-x 19 polkitd fantj 4096 9月  4 11:38 logs
```
### 4. 启动
```
sudo docker-compose up -d
```
##### 访问`http://fantj.gitlab.top:9999`
>![](https://upload-images.jianshu.io/upload_images/5786888-ec301376f262a34a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 5. 设置密码并登录
设置完密码后，超级用户账号默认是root。
##### 登录
>![](https://upload-images.jianshu.io/upload_images/5786888-e31c975ef55e588d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 6. 设置和使用
```
设置->设置
    设置ssh和http服务
    设置注册服务开关
    设置项目限制
    设置头像来源等等
```
>![](https://upload-images.jianshu.io/upload_images/5786888-a3a8635f09f054d5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>![](https://upload-images.jianshu.io/upload_images/5786888-62582942c6ff18bb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>![新建项目](https://upload-images.jianshu.io/upload_images/5786888-df485d7f71e9eaf0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>![](https://upload-images.jianshu.io/upload_images/5786888-fac5e0d508195a78.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 内存修改
改之前先看看它有多吃内存。。。
```
[fantj@lalala workspace]$ free -h
              total        used        free      shared  buff/cache   available
Mem:           15Gi       8.9Gi       246Mi       1.5Gi       6.4Gi       4.8Gi
Swap:         7.8Gi       4.0Mi       7.8Gi
```
修改方法有两种，一种是进入docker交互模式修改，一种是在docker-compose中设置参数。

**注意:**因为我做了docker数据卷映射在本地的`/home/fantj/app/docker/compose/gitlab/config`目录中，所以只需要修改`config/gitlab.rb`的参数即可。

##### 1、减少进程数
>修改配置文件`gitlab.rb`中的`worker_processes`:

```
unicorn['worker_processes'] = 2
```

默认是被注释掉的，官方建议该值是CPU核心数加一，可以提高服务器的响应速度，如果内存只有4G，或者服务器上有其它业务，就不要改了，以免内存不足。另外，这个参数最小值是2，设为1，服务器可能会卡死。

##### 2、减少数据库缓存
>默认为256MB，可适当改小
```
postgresql['shared_buffers'] = "256MB"
```
##### 3、减少数据库并发数
>默认为8，可适当改小
```
postgresql['max_worker_processes'] = 8
```
##### 4、减少sidekiq并发数
>默认是25，可适当改小
```
sidekiq['concurrency'] = 25
```

##### 重启服务
```
sudo gitlab-ctl reconfigure
sudo gitlab-ctl restart
```
