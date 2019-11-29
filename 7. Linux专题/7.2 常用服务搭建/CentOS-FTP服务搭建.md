vsftpd 安装
### 1. 安装
1. 执行`yum -y install vsftpd`进行安装，默认配置文件再/etc/vsftpd/vsftpd.conf下
2. 添加匿名用户:` useradd ftpuser -d /ftpfile -s /sbin/nologin`
3. 修改ftpfile权限 :`chown -R ftpuser.ftpuser /ftpfile`
4. 重设ftpuser 密码：`passwd ftpuser `
5. 给/ftpfile(ftpuser的用户目录)下创建子目录/ftp，并赋予777权限。为什么这样做呢？因为新版本的vsftpd不允许用户对用户的加目录进行写的权限，所以我们给用户加目录755权限，给家目录下的子目录777的权限。
```
#执行权限授予命令
chmod 755 -R /ftpfile
chmod 777 -R /ftpfile/ftp
#两个目录的所有者和权限
dr-x------ 3 ftpuser ftpuser 4096 Apr 27 20:47 ftpfile
drwxrwxrwx 3 ftpuser ftpuser 4096 Apr 27 20:47 ftp

```
### 2. 配置
1. 进入`etc/vsftpd `, `vim chroot_list` ,把刚刚新建的用户添加到此配置中，保存退出
![](https://upload-images.jianshu.io/upload_images/5786888-875369f5d3a018f3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
2. `vim /etc/selinux/config `,修改为 SELINUX = disabled .保存退出
    * 如果碰到550拒绝访问，请执行`sudo setsebool -P ftp_home_dir 1`，然后重启服务器，执行reboot命令
3. `vim /etc/vsftpd/vsftpd.conf `
```
#(当本地用户登入时，将被更换到定义的目录下，默认值为各用户的家目录) 
local_root=/ftpfile/ftp
#(使用匿名登入时，所登入的目录) 
anon_root=/ftpfile/ftp
#(默认是GMT时间，改成使用本机系统时间)
use_localtime=YES
#(不允许匿名用户登录)
anonymous_enable=NO
#(允许本地用户登录)
local_enable=YES
#(本地用户可以在自己家目录中进行读写操作)
write_enable=YES
#(本地用户新增档案时的umask值)
local_umask=022
#(如果启动这个选项，那么使用者第一次进入一个目录时，会检查该目录下是否有.message这个档案，如果有，则会出现此档案的内容，通常这个档案会放置欢迎话语，或是对该目录的说明。默认值为开启)
dirmessage_enable=YES
#(是否启用上传/下载日志记录。如果启用，则上传与下载的信息将被完整纪录在xferlog_file 所定义的档案中。预设为开启。)
xferlog_enable=YES
#(指定FTP使用20端口进行数据传输，默认值为YES)
connect_from_port_20=YES
#(如果启用，则日志文件将会写成xferlog的标准格式)
xferlog_std_format=YES
#(这里用来定义欢迎话语的字符串)
ftpd_banner=Welcome to mmall FTP Server
#(用于指定用户列表文件中的用户是否允许切换到上级目录)
chroot_local_user=NO
#(设置是否启用chroot_list_file配置项指定的用户列表文件)
chroot_list_enable=YES
#(用于指定用户列表文件)
chroot_list_file=/etc/vsftpd/chroot_list
#(设置vsftpd服务器是否以standalone模式运行，以standalone模式运行是一种较好的方式，此时listen必须设置为YES，此为默认值。建议不要更改，有很多与服务器运行相关的配置命令，需要在此模式下才有效，若设置为NO，则vsftpd不是以独立的服务运行，要受到xinetd服务的管控，功能上会受到限制)
listen=YES
#(虚拟用户使用PAM认证方式，这里是设置PAM使用的名称，默认即可，与/etc/pam.d/vsftpd对应) userlist_enable=YES(是否启用vsftpd.user_list文件，黑名单,白名单都可以
pam_service_name=vsftpd
#(被动模式使用端口范围最小值)
pasv_min_port=61001
#(被动模式使用端口范围最大值)
pasv_max_port=62000
#(pasv_enable=YES/NO（YES）
pasv_enable=YES
#允许用户有写的权限
allow_writeable_chroot=YES
```

4. 防火墙配置`vim /etc/sysconfig/iptables`

```
-A INPUT -p TCP --dport 61001:62000 -j ACCEPT
-A OUT -p TCP --dport 61001:62000 -j ACCEPT
-A INPUT -p TCP --dport 20 -j ACCEPT
-A OUT -p TCP --dport 20 -j ACCEPT
-A INPUT -p TCP --dport 21 -j ACCEPT
-A OUT -p TCP --dport 21 -j ACCEPT

```
重启： `service iptables restart`

### 3. 测试
1. 启动 service vsftpd restart
2. 查看服务器ip地址，ifconfig
3. 安装 ftp 命令，`yum install ftp -y `
4. ftp登录
```
[root@FantJ vsftpd]# ftp 127.0.0.1
Connected to 127.0.0.1 (127.0.0.1).
220 Welcome to FTP Server
Name (127.0.0.1:root): ftpuser
331 Please specify the password.
Password:
230 Login successful.
Remote system type is UNIX.
Using binary mode to transfer files.
ftp> ls
227 Entering Passive Mode (127,0,0,1,239,251).
150 Here comes the directory listing.
drwxrwxrwx    2 1002     1002         4096 Apr 27 20:47 image
226 Directory send OK.
ftp> exit
221 Goodbye.

```
5. 浏览器登录
  你可能会碰到一个问题，就是你用ftp和cmd里的ftp命令可以连接ftp服务，为啥浏览器就不行呢？
  
  其实我们想想，用ftp来连接，默认访问的就是21/20端口，但是用浏览器访问的时候就不一定了(被动模式)，它有可能用其他的端口来访问，那到底是什么端口呢？我们回到我们vsftpd.conf的配置文件，有这两行
```
#(被动模式使用端口范围最小值)
pasv_min_port=61001
#(被动模式使用端口范围最大值)
pasv_max_port=62000
```
所以我们把这段端口开放就好。
![](https://upload-images.jianshu.io/upload_images/5786888-22f8c0ac0c572793.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
