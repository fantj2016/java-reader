>逻辑卷优点?可以动态扩展内存空间。

传统分区缺点：如果需要更改分区大小需要格式化分区然后重新分区。

VG：卷组

LV：逻辑卷

PV：物理卷

关系：物理卷组成卷组，然后在卷组上划分逻辑卷（PV->VG->LV）

#### 1. 将物理分区初始化为物理卷

1. `pvscan或者pvs` 查看物理卷

2. 开始转换分区类型

注意：不能直接更改扩展分区类型为8e(Lvm分区id)
```
You cannot change a partition into an extended one or vice versa.
Delete it first.
```
所以我们如果想将扩展分区改为LVM分区则先要删除扩展分区。

```
命令(输入 m 获取帮助)：d
分区号 (1-6，默认 6)：4
分区 4 已删除

命令(输入 m 获取帮助)：p

磁盘 /dev/sda：21.5 GB, 21474836480 字节，41943040 个扇区
Units = 扇区 of 1 * 512 = 512 bytes
扇区大小(逻辑/物理)：512 字节 / 512 字节
I/O 大小(最小/最佳)：512 字节 / 512 字节
磁盘标签类型：dos
磁盘标识符：0x00092946

   设备 Boot      Start         End      Blocks   Id  System
/dev/sda1   *        2048     1026047      512000   83  Linux
/dev/sda2         1026048     2050047      512000   83  Linux
/dev/sda3         2050048     3074047      512000   83  Linux

命令(输入 m 获取帮助)：n
Partition type:
   p   primary (3 primary, 0 extended, 1 free)
   e   extended
Select (default e): p
已选择分区 4
起始 扇区 (3074048-41943039，默认为 3074048)：
将使用默认值 3074048
Last 扇区, +扇区 or +size{K,M,G} (3074048-41943039，默认为 41943039)：
将使用默认值 41943039
分区 4 已设置为 Linux 类型，大小设为 18.5 GiB

命令(输入 m 获取帮助)：t
分区号 (1-4，默认 4)：3
Hex 代码(输入 L 列出所有代码)：8e
已将分区“Linux”的类型更改为“Linux LVM”
```
同理，将sda4也做类型转换
```
命令(输入 m 获取帮助)：p

磁盘 /dev/sda：21.5 GB, 21474836480 字节，41943040 个扇区
Units = 扇区 of 1 * 512 = 512 bytes
扇区大小(逻辑/物理)：512 字节 / 512 字节
I/O 大小(最小/最佳)：512 字节 / 512 字节
磁盘标签类型：dos
磁盘标识符：0x00092946

   设备 Boot      Start         End      Blocks   Id  System
/dev/sda1   *        2048     1026047      512000   83  Linux
/dev/sda2         1026048     2050047      512000   83  Linux
/dev/sda3         2050048     3074047      512000   8e  Linux LVM
/dev/sda4         3074048    41943039    19434496   8e  Linux LVM
```

然后执行`pvcreate /dev/sda{3,4}`即可创建物理卷，然后`pvs`查看。

```
[root@localhost home]# pvcreate /dev/sda{5,6}
  Physical volume "/dev/sda5" successfully created
  Physical volume "/dev/sda6" successfully created
[root@localhost home]# pvs
  PV         VG   Fmt  Attr PSize PFree
  /dev/sda5       lvm2 a--  1.00g 1.00g
  /dev/sda6       lvm2 a--  2.00g 2.00g
```


#### 2. 将物理卷创建为卷组
`vgcreate vg0 /dev/sda{3,4}`然后`vgscan`查看卷组，然后再执行`pvs`可以看到VG列有参数了，那就是卷组名vg0.

```
[root@localhost home]# vgcreate vg0 /dev/sda{5,6}
  Volume group "vg0" successfully created
```
```
[root@localhost home]# vgs
  VG   #PV #LV #SN Attr   VSize VFree
  vg0    2   0   0 wz--n- 2.99g 2.99g
```
```
[root@localhost home]# pvs
  PV         VG   Fmt  Attr PSize    PFree   
  /dev/sda5  vg0  lvm2 a--  1020.00m 1020.00m
  /dev/sda6  vg0  lvm2 a--     2.00g    2.00g
```
###### 查看所有卷组信息
`vgdisplay`
```
[root@localhost home]# vgdisplay
  --- Volume group ---
  VG Name               vg0
  System ID             
  Format                lvm2
  Metadata Areas        2
  Metadata Sequence No  1
  VG Access             read/write
  VG Status             resizable
  MAX LV                0
  Cur LV                0
  Open LV               0
  Max PV                0
  Cur PV                2
  Act PV                2
  VG Size               2.99 GiB
  PE Size               4.00 MiB
  Total PE              766
  Alloc PE / Size       0 / 0   
  Free  PE / Size       766 / 2.99 GiB
  VG UUID               FGEHCq-RI5V-DXOX-t9Io-b0v5-gEmx-1r08c3

```
其中，PE(物理扩展):组成卷组的最小单位。默认是4M
###### 创建PE大小为8的卷组
>什么是PE，PE是卷组的最小单位，可以理解成组成卷组的元素大小。默认是4M.可以通过`vgdisplay`命令查看

修改PE大小需要使用命令
`vgcreate -s 8 vg0 /dev/sda{3,4}`

其中，-s 8 的意思指定是PE为8MB

###### 查看某个卷组信息
`vgdisplay vgname`

###### 扩展卷组
`vgextend vg0 /dev/sda5`将sda5扩展到vg0卷组，然后在`vgdisplay vg0`查看vg size，会发现，变大了。同理，`pvs`命令结果也会同步的加上sda5的vg所属组。

###### 减小卷组
`vgreduce vg0 /dev/sda5`从vg0卷组中将sda5移除


###### vg重命名
`vgrename old new`

###### 删除卷组
`vgremove vgname`

###### 导入/导出卷组
>当我们需要将硬盘组成的LVM转移到另一台机器，并且希望这个LVM还照旧的话，就用此命令。

`vgexport`和`vgimport`分别为导出和导入命令



### 3. 将卷组创建为逻辑卷
>创建逻辑卷必须要在卷组上面创建。

###### 创建逻辑卷
1. 创建方式一：

`lvcreate -n lv0 -L 99M vg0`
```
[root@localhost home]# lvcreate -n lv0 -L 99M vg0
  Rounding up size to full physical extent 100.00 MiB
  Logical volume "lv0" created
```

-n name名字

-L length大小

执行该命令后提示创建了一个100M的lv0.为什么呢？因为PE默认为4M，100是99最接近的4的整数倍。也可以用`lvscan`来确认lv0的大小是100.

2. 创建方式二：

`lvcreate -n lv1 -l 25 vg0`

-l  是指多少个PE，25个PE就是100M

然后`lvscan`查看结果。
```
[root@localhost home]# lvcreate -n lv1 -l 25 vg0
  Logical volume "lv1" created
[root@localhost home]# lvs
  LV   VG   Attr       LSize   Pool Origin Data%  Move Log Cpy%Sync Convert
  lv0  vg0  -wi-a----- 100.00m                                             
  lv1  vg0  -wi-a----- 100.00m 
```
3. 创建方式三：

`lvcreate -n lv2 -l 100%free  vg0`

将vg0卷组中剩余的空间容量都分给lv2。当然，百分比可以是其他数值。
###### 扩展逻辑卷
注意三点：
1. 扩展lv时不需要卸载，即在线扩展
2. lv可以减小，但是变成xfs(文件系统)不能减小。
3. ext4是可以减小的。（ext4操作在后面展示）

如果一开始用的xfs文件系统的话，一开始就需要规划好。

操作步骤：

1. lv0扩展200M 

`lvextend -L +200M /dev/vg0/lv0`

此时这200M只是空间，没有文件系统。所以在`df -hT`查看lv0大小时，还是显示100M，因为这200M还不是文件系统。
```
[root@localhost home]# lvextend -L +200M /dev/vg0/lv0
  Extending logical volume lv0 to 500.00 MiB
  Logical volume lv0 successfully resized
[root@localhost home]# df -hT
文件系统            类型      容量  已用  可用 已用% 挂载点
/dev/sda2           xfs        15G  3.4G   12G   23% /
devtmpfs            devtmpfs  906M     0  906M    0% /dev
tmpfs               tmpfs     914M  136K  914M    1% /dev/shm
tmpfs               tmpfs     914M  8.9M  905M    1% /run
tmpfs               tmpfs     914M     0  914M    0% /sys/fs/cgroup
/dev/sda1           xfs       197M  101M   97M   52% /boot
/dev/mapper/vg0-lv0 xfs       297M   16M  282M    6% /home/html
```
2. 扩展文件系统:执行`xfs_growfs /dev/vg0/lv0`，然后在`df -hT`查看lv0大小，此时就变成了300M

```
[root@localhost home]# xfs_growfs /dev/vg0/lv0
meta-data=/dev/mapper/vg0-lv0    isize=256    agcount=4, agsize=19200 blks
         =                       sectsz=512   attr=2, projid32bit=1
         =                       crc=0
data     =                       bsize=4096   blocks=76800, imaxpct=25
         =                       sunit=0      swidth=0 blks
naming   =version 2              bsize=4096   ascii-ci=0 ftype=0
log      =internal               bsize=4096   blocks=853, version=2
         =                       sectsz=512   sunit=0 blks, lazy-count=1
realtime =none                   extsz=4096   blocks=0, rtextents=0
data blocks changed from 76800 to 128000
[root@localhost home]# df -hT
文件系统            类型      容量  已用  可用 已用% 挂载点
/dev/sda2           xfs        15G  3.4G   12G   23% /
devtmpfs            devtmpfs  906M     0  906M    0% /dev
tmpfs               tmpfs     914M  136K  914M    1% /dev/shm
tmpfs               tmpfs     914M  8.9M  905M    1% /run
tmpfs               tmpfs     914M     0  914M    0% /sys/fs/cgroup
/dev/sda1           xfs       197M  101M   97M   52% /boot
/dev/mapper/vg0-lv0 xfs       497M   16M  482M    4% /home/html
```


###### 删除逻辑卷
`lvremoce -f /dev/vg0/lv2`

-f    强制删除 


### 4. 挂载逻辑卷

`mount /dev/vg0/lv0  /home/fantj`

在挂载的时候可能会报错：
```
[root@localhost home]# mount /dev/vg0/lv0 /home/html/
mount: /dev/mapper/vg0-lv0 写保护，将以只读方式挂载
mount: 未知的文件系统类型“(null)”
```
此时需要对lv进行文件系统类型格式化`mkfs.xfs /dev/vg0/lv0`:
```
[root@localhost home]# mkfs.xfs /dev/vg0/lv0
meta-data=/dev/vg0/lv0           isize=256    agcount=4, agsize=19200 blks
         =                       sectsz=512   attr=2, projid32bit=1
         =                       crc=0
data     =                       bsize=4096   blocks=76800, imaxpct=25
         =                       sunit=0      swidth=0 blks
naming   =version 2              bsize=4096   ascii-ci=0 ftype=0
log      =internal log           bsize=4096   blocks=853, version=2
         =                       sectsz=512   sunit=0 blks, lazy-count=1
realtime =none                   extsz=4096   blocks=0, rtextents=0
```
然后执行挂载：
```
[root@localhost home]# mount /dev/vg0/lv0 /home/html/
[root@localhost home]# df -hT
文件系统            类型      容量  已用  可用 已用% 挂载点
/dev/sda2           xfs        15G  3.4G   12G   23% /
devtmpfs            devtmpfs  906M     0  906M    0% /dev
tmpfs               tmpfs     914M  136K  914M    1% /dev/shm
tmpfs               tmpfs     914M  8.9M  905M    1% /run
tmpfs               tmpfs     914M     0  914M    0% /sys/fs/cgroup
/dev/sda1           xfs       197M  101M   97M   52% /boot
/dev/mapper/vg0-lv0 xfs       297M   16M  282M    6% /home/html
```



### ext4文件系统的lvm设置

