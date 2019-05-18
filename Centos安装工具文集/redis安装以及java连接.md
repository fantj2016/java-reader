### 1.什么是redis
>Redis是一个开源的使用ANSI [C语言](https://baike.baidu.com/item/C%E8%AF%AD%E8%A8%80)编写、支持网络、可基于内存亦可持久化的日志型、Key-Value[数据库](https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E5%BA%93)，并提供多种语言的API。从2010年3月15日起，Redis的开发工作由VMware主持。从2013年5月开始，Redis的开发由Pivotal赞助。

### 2.redis用在什么场景
   简括四个字：读多写少
### 3.Redis安装(Linux)
http://download.redis.io/releases/redis-4.0.2.tar.gz
```
$ wget http://download.redis.io/releases/redis-4.0.2.tar.gz
$ tar xzf redis-4.0.2.tar.gz
$ cd redis-4.0.2
$ make
```
### 4.redis启动
![image.png](http://upload-images.jianshu.io/upload_images/5786888-9378c775e186f5d1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
启动成功界面：
![image.png](http://upload-images.jianshu.io/upload_images/5786888-dd32d3558c0eabf9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我们来给redis里面加值。  `redis-cli`进入redis命令行
![image.png](http://upload-images.jianshu.io/upload_images/5786888-b9465e2e0ca8ecdf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* java代码实现增加查看
![image.png](http://upload-images.jianshu.io/upload_images/5786888-26e62828a5a3e163.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![redis服务验证.png](http://upload-images.jianshu.io/upload_images/5786888-278e5597ad7bac31.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 5.redis瓶颈及解决方案
1. 单线程   -- 主从复制
2. 主从管理 -- 哨兵机制
