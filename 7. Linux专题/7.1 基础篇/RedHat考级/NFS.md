>两个linux系统可以通过NFS系统实现文件共享。

其中，`showmount`是连接linux系统中的nfs。如果想连接windows上的共享文件，需要用`smbclient`工具来实现。


### 1. 搭建nfs服务
>我在ip地址为192.168.27.100上部署NFS服务。

默认端口2049

##### 1.1 修改配置文件
>该文件是共享目录设置，格式为 共享目录+参数(在尾巴详解)


`vim /etc/exports`
```
添加一行

home/fantj   *(rw,sync,no_root_squash)
```
###### 刷新文件
`exports -r`
###### 全部参数介绍：
```
rw 可读写的权限 
ro 只读的权限 
no_root_squash 登入NFS主机，使用该共享目录时相当于该目录的拥有者，如果是root的话，那么对于这个共享的目录来说，他就具有root的权 
               限，这个参数『极不安全』，不建议使用

root_squash 登入NFS主机，使用该共享目录时相当于该目录的拥有者。但是如果是以root身份使用这个共享目录的时候，那么这个使用者（root）
             的权限将被压缩成为匿名使用者，即通常他的UID与GID都会变成nobody那个身份

all_squash 不论登入NFS的使用者身份为何，他的身份都会被压缩成为匿名使用者，通常也就是nobody
anonuid 可以自行设定这个UID的值，这个UID必需要存在于你的/etc/passwd当中
anongid 同anonuid，但是变成groupID就是了 
sync 资料同步写入到内存与硬盘当中 
async 资料会先暂存于内存当中，而非直接写入硬盘 
insecure 允许从这台机器过来的非授权访问
sec 加密  如：sec=krb5p
```

##### 1.2 启动nfs
`systemctl start nfs`

`systmctl status nfs`

```
[root@localhost home]# systemctl start nfs
[root@localhost home]# systemctl status nfs
nfs-server.service - NFS Server
   Loaded: loaded (/usr/lib/systemd/system/nfs-server.service; disabled)
   Active: active (exited) since 二 2018-09-18 13:54:01 CST; 5s ago
  Process: 46909 ExecStart=/usr/sbin/rpc.nfsd $RPCNFSDARGS $RPCNFSDCOUNT (code=exited, status=0/SUCCESS)
  Process: 46905 ExecStartPre=/usr/sbin/exportfs -r (code=exited, status=0/SUCCESS)
  Process: 46903 ExecStartPre=/usr/libexec/nfs-utils/scripts/nfs-server.preconfig (code=exited, status=0/SUCCESS)
 Main PID: 46909 (code=exited, status=0/SUCCESS)
   CGroup: /system.slice/nfs-server.service

9月 18 13:54:01 localhost.localdomain systemd[1]: Starting NFS Server...
9月 18 13:54:01 localhost.localdomain systemd[1]: Started NFS Server.
```



### 2. 客户端连接nfs
>我在ip地址为192.168.27.169上部署客户端。

###### showmount命令
```
语法:showmount [-aed] [hostname]
-a:显示目前以及连上主机的client机器的使用目录的状态
-e:显示hostname的/etc/exports里面共享的目录
-d:只显示被client机器挂载的目录
```
---------------------


###### 2.1 测试连通性
`showmount -e xx.xx.xx.xx`
如果没有showmount命令的话 先yum安装。
```
[root@s169 ~]# showmount -e 192.168.27.100
Export list for 192.168.27.100:
/home/fantj *
```
如果这一步出现报错，则需要设置服务端的防火墙规则，不正规的做法: `iptables -F`，清除所有防火墙规则。

##### 2.2 挂载
>使用网络设备的时候，也需要挂载才可以使用。


挂载命令:`mount 192.168.27.100:/home/fantj /home/nfs`

```
[root@s169 home]# mount 192.168.27.100:/home/fantj /home/nfs
[root@s169 home]# df -h
Filesystem                  Size  Used Avail Use% Mounted on
/dev/mapper/centos-root      13G  2.4G   11G  19% /
devtmpfs                    476M     0  476M   0% /dev
tmpfs                       488M     0  488M   0% /dev/shm
tmpfs                       488M  7.7M  480M   2% /run
tmpfs                       488M     0  488M   0% /sys/fs/cgroup
/dev/sda1                  1014M  159M  856M  16% /boot
tmpfs                        98M     0   98M   0% /run/user/0
192.168.27.100:/home/fantj   15G  3.4G   12G  23% /home/nfs
```
可以看到，已经成功挂载(自动识别文件系统)。


然后写入`/etc/fstab`文件中。切记文件系统的格式是nfs。

那如果nfs文件是加密的呢?👇
###### 带加密挂载到fstab
>如果nfs经过krb5加密，则需要在挂载的时候声明它的参数。

`192.168.27.100:home/fantj  /home/nfs  defaults,v4.2,sec=krb5p  0 0`

##### 2.3 测试写入读取

```
[root@s169 home]# cd /home/nfs/
[root@s169 nfs]# touch test
[root@s169 nfs]# ls
test
```


### 3. linux连接windows共享文件

##### 3.1 建立连接
`smbclient  -L //192.168.0
1 -U username%passwd`

username是账号，passwd是密码。

-L 是登录

##### 3.2 挂载

直接使用mount挂载的话会报错显示文件系统类型错误。

此时我们需要安装`samba、*和cifs\*`(使其支持cifs文件格式)

然后再挂载`mount -o username=xxx,password=xxx  //192.168.0.1/D  /windisk`。文件类型是cifs。


然后
