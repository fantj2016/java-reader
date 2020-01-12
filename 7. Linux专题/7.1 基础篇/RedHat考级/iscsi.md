>iSCSI技术是一种由IBM公司研究开发的，是一个供硬件设备使用的可以在IP协议的上层运行的SCSI指令集，这种指令集合可以实现在IP网络上运行SCSI协议，使其能够在诸如高速千兆以太网上进行路由选择。iSCSI技术是一种新储存技术，该技术是将现有SCSI接口与以太网络(Ethernet)技术结合，使服务器可与使用IP网络的储存装置互相交换资料。

###### iSCSI的工作过程：
1. 当iSCSI主机应用程序发出数据读写请求后，操作系统会生成一个相应的SCSI命令，
2. 该SCSI命令在iSCSI initiator层被封装成ISCSI消息包并通过TCP/IP传送到设备侧，
3. 设备侧的iSCSI target层会解开iSCSI消息包，得到SCSI命令的内容，然后传送给SCSI设备执行；
4. 设备执行SCSI命令后的响应，在经过设备侧iSCSI target层时被封装成ISCSI响应PDU，通过TCP/IP网络传送给主机的ISCSI initiator层，
5. iSCSI initiator会从ISCSI响应PDU里解析出SCSI响应并传送给操作系统，操作系统再响应给应用程序。


共享设备：可以是磁盘，也可以是本地设备。

# 服务端
### 1. 安装

###### 1.1 查找targetcli依赖
`yum list targetcli\*`

###### 1.2 安装
`yum install targetcli\* -y`

###### 1.3 开放端口
`firewall-cmd --permanent --add-port=3260/tcp`

### 2. 使用

#### 2.1 使用targetcli
进入cli：`targetcli`

如果是共享磁盘，则放到block下

如果是共享文件，则放到fileio下


```
cd backstores/

创建磁盘：block/ create block1 /dev/sdb1

创建fileio：fileio/ create file1  /xxx/file

查看创建项目：ls 

删除项目：fileio/ delete file1


```

#### 2.2 创建target
>需要进入到iscsi目录下操作。

```
cd /

cd iscsi

ls

创建target：create ign.2018-09.com.fantj:disk

删除target：delete ign.2018-09.com.fantj:disk
```

#### 2.3 建立设备和target的联系

```
cd ign.2018-09.com.fantj:disk/

cd tpg1/

ls

创建ACL(相当于密码)： acls/ create ign.2018-09.com.fantj:xx

删除ACL：/acl delete ign.2018-09.com.fantj:xx

创建LUN(创建联系)：luns/ create /backstores/fileio/file1

ls

设置portals(访问地址):portals/ create 192.168.27.100

ls 查看自动分配的端口

退出 ：exit
```
默认配置文件保存在`/etc/target/saveconfig.json`文件中。

###### 重启服务/开机启动
`systemctl restart target`

`systemctl enable target`

---
# 客户端

### 1. 安装
`yum install iscsi\*`

##### 启动服务
`systemctl start iscsi`

### 2. 使用

###### 2.1 寻找/扫描服务
>扫描某个ip下的共享设备列表。

`iscsiadm -m discovery -t st -p 192.168.27.100`

```
[root@fantj]# iscsiadm -m discovery -t st -p 192.168.27.100
192.168.27.100:3260 ign.2018-09.com.fantj:disk
```
```
iscsiadm参数：

-m  模式model
-t  类型type
-T  target名字
-p  地址portal
```

###### 2.2 加载设备
```
[root@fantj]# iscsiadm -m node -T ign.2018-09.com.fantj:disk -p 192.168.27.100 -l
此时是加载不成功的，因为在服务端有配置ACL加密信息。

解决ACL加密：
1. 编辑/etc/iscsi/initiatorname.iscsi文件，
2. 将值改为我们服务端设置的ACL：ign.2018-09.com.fantj:xx

重启服务: systemctl restart iscsi

重复上面步骤：扫描->加载
```

###### 2.3 查看加载结果

`cat /etc/proc/partitions`
或者
`iscsiadm -m session`

开机自动连接，只需要做好挂载就行。

如果不想自动连接：修改配置`/etc/iscsi/iscsi.conf`将`node.startup=automatic 改成 manual`