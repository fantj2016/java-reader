>这种报错一般是因为nginx用户权限不足引起的。

### 1. 查看日志

打开nginx.conf 配置文件，查看日志位置。
![](https://upload-images.jianshu.io/upload_images/5786888-b4db235694e1148e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2. 访问让它报错：

![](https://upload-images.jianshu.io/upload_images/5786888-cf121dcb03a69b9a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 3. 修改目录权限
我们看到了它报错无权限，因为我的nginx用户是ftpuser，所以我在/var/lib下执行`chown -R ftpuser:ftpuser nginx/`修改目录所属用户。

![](https://upload-images.jianshu.io/upload_images/5786888-58f4c59313d764a7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 4. 收工

好了重启nginx:`nginx -s reload `

测试ok
