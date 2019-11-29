我的安装环境是CentOS7.x


### 安装
安装命令：`wget -qO- https://get.docker.com | sh`或者`yum -y install docker-io`
![安装完成.png](http://upload-images.jianshu.io/upload_images/5786888-3ad2c962cd1b7815.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
图片中说，如果你想不用root用户来使用docker，你要考虑添加你的用户到docker 组，命令如下:`sudo usermod -aG docker your-user`，我在这里不演示，我用root用户。
### 版本查看
![version.png](http://upload-images.jianshu.io/upload_images/5786888-882798d6be513459.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 启动服务
`service docker start`
![](http://upload-images.jianshu.io/upload_images/5786888-5cce8eb8b3f7591c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


