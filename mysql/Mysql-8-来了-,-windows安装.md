### 1. 下载安装

先贴一个官网大轮播图：
![](https://upload-images.jianshu.io/upload_images/5786888-fe7af466fafc4b30.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


8.0文档官方：https://dev.mysql.com/doc/refman/8.0/en/document-store.html

8版本的特性和使用我很快会出一片教程。

![](https://upload-images.jianshu.io/upload_images/5786888-145b2b7524430045.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

安装规规矩矩，一路next，有一部让选安装组件，只选server就行。

### 2. Navicat 破解

baidu云链接: https://pan.baidu.com/s/15b0qpvNngdFXFQr_UeQ4lw 提取码: 78mx

1. 先安装`Navicat.exe`
2. 执行`patchNavicat.exe` 然后选择你安装的`navicat.exe`文件， 即可完成破解。
### 3. mysql登录

如果完成了上面两步，你在用Navicat连接本地数据库会报错:`Client does not support authentication protocol requested by server`

所以需要重置密码(即使安装的时候让你填了密码)。

```
mysql> use mysql;
Database changed
mysql> alter user 'root'@'localhost' identified with mysql_native_password by 'root';
Query OK, 0 rows affected (0.03 sec)

mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
```

然后再使用Navicat连接。
