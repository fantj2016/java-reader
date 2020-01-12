>Samba是一种在局域网上共享文件和打印机的一种通信协议，它为局域网内的不同计算机之间提供文件及打印机等资源的共享服务。

如果是windows的文件共享，需要安装cifs包来支持此文件系统。



### 1. 安装
`yum install samba -y`

### 2. 配置

###### 配置文件目录
`/etc/samba/`


###### 修改文件安全上下文
>chcon命令是修改对象（文件）的安全上下文，比如：用户、角色、类型、安全级别。也就是将每个文件的安全环境变更至指定环境。


```
chcon [选项]... 环境 文件...
chcon [选项]... [-u 用户] [-r 角色] [-l 范围] [-t 类型] 文件...
chcon [选项]... --reference=参考文件 文件...


-h, --no-dereference：影响符号连接而非引用的文件。
    --reference=参考文件：使用指定参考文件的安全环境，而非指定值。
-R, --recursive：递归处理所有的文件及子目录。
-v, --verbose：为处理的所有文件显示诊断信息。
-u, --user=用户：设置指定用户的目标安全环境。
-r, --role=角色：设置指定角色的目标安全环境。
-t, --type=类型：设置指定类型的目标安全环境。
-l, --range=范围：设置指定范围的目标安全环境。


以下选项是在指定了-R选项时被用于设置如何穿越目录结构体系。如果您指定了多于一个选项，那么只有最后一个会生效。

-H：如果命令行参数是一个通到目录的符号链接，则遍历符号链接。
-L：遍历每一个遇到的通到目录的符号链接。
-P：不遍历任何符号链接（默认）。
--help：显示此帮助信息并退出。
--version：显示版本信息并退出。
```

如果你希望将samba目录共享给其他用户，你需要设置：`chcon -R -t samba_sharee_t /common`

如果你想把这个ftp共享给匿名用户的话，需要开启以下：`chcon -R -t public_content_t /var/ftp`

如果你想让你设置的FTP目录可以上传文件的话，SELINUX需要设置：`chcon -t public_content_rw_t /var/ftp/incoming`

###### 主配置文件`smb.conf`
```
workgroup = SMBGROUP     #设置工作组
[common]           #与文件路径同名
     comment = linhut     #描述
     path = /common     #共享资源名
     #read list= natasha        #读权限用户,添加这个名单就只有里面的可以读
     browseable = yes   #共享资源是否允许用户浏览
     hosts allow = 172.25.12.     #允许访问的（这里题目DNS域解析为这个段）
[miscellaneous]           #与文件路径同名
     comment = linhut     #描述
     path = /miscellaneous     #共享资源名
     #read list= silene        #有读权限用户
     write list = akira       #读写权限用户
     browseable = yes   #共享资源是否允许用户浏览
     hosts allow = 172.25.12.     #允许访问的（这里题目DNS域解析为这个段）

```

###### 查看SMB分享

`smbclietn -L //192.168.0.1`

###### 设置smb密码

`smbpasswd -a fantj`

###### 服务重启
`systemctl restart smb;systemctl restart nmb`
#### 在防火墙中开启服务支持

`firewall-cmd --permanent --add-rich-rule 'rule family=ipv4 source address=172.25.xx.xx/24 service name="samba" reject`

### 3. 挂载

#### 3.1 安装cifs支持

`yum install cifs-utils -y`

#### 3.2 设置密码文件

`echo "username"=xxx >> /root/smb-multiuser.txt`
`echo "password"=xxx >> /root/smb-multiuser.txt`

#### 3.3 挂载
`vim /etc/fstab`

```
//serverX/miscellaneous   /mnt/multi  cifs  credentials=/root/smb-multiuser.txt,multiuser,sec=ntlmssp  0 0
```