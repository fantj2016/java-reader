### U盘启动

略

### 挂载设备
>在挂载设备之前，需要先了解自己硬盘的各个分区都是什么。

`fdisk -l `查看分区表
```
fdisk -l
Disk /dev/nvme0n1：477 GiB，512110190592 字节，1000215216 个扇区
单元：扇区 / 1 * 512 = 512 字节
扇区大小(逻辑/物理)：512 字节 / 512 字节
I/O 大小(最小/最佳)：512 字节 / 512 字节
磁盘标签类型：gpt
磁盘标识符：ED987A6A-2997-422F-956E-A2A1B8B09A29

设备                起点       末尾      扇区  大小 类型
/dev/nvme0n1p1      2048     923647    921600  450M Windows 恢复环境
/dev/nvme0n1p2    923648    1128447    204800  100M EFI 系统
/dev/nvme0n1p3   1128448    1161215     32768   16M Microsoft 保留
/dev/nvme0n1p4   1161216  159685601 158524386 75.6G Microsoft 基本数据
/dev/nvme0n1p5 159686656  161351679   1665024  813M Windows 恢复环境
/dev/nvme0n1p6 161353728  580782079 419428352  200G Microsoft 基本数据
/dev/nvme0n1p7 580782080  582879231   2097152    1G Linux 文件系统
/dev/nvme0n1p8 582879232 1000214527 417335296  199G Linux LVM


Disk /dev/mapper/fedora-root：50 GiB，53687091200 字节，104857600 个扇区
单元：扇区 / 1 * 512 = 512 字节
扇区大小(逻辑/物理)：512 字节 / 512 字节
I/O 大小(最小/最佳)：512 字节 / 512 字节


Disk /dev/mapper/fedora-swap：7.9 GiB，8417968128 字节，16441344 个扇区
单元：扇区 / 1 * 512 = 512 字节
扇区大小(逻辑/物理)：512 字节 / 512 字节
I/O 大小(最小/最佳)：512 字节 / 512 字节


Disk /dev/mapper/fedora-home：141.2 GiB，151569563648 字节，296034304 个扇区
单元：扇区 / 1 * 512 = 512 字节
扇区大小(逻辑/物理)：512 字节 / 512 字节
I/O 大小(最小/最佳)：512 字节 / 512 字节
```
可以看出，我的root分区是:` /dev/mapper/fedora-root`(逻辑分区),

我的boot分区是:`/dev/nvme0n1p7`,

我的EFI分区是:`/dev/nvme0n1p2`

好了，开始挂载，记住一定要将dev proc  sys  efivars  pts 同步到U盘上，否则`grub2-install`命令报错。
```
挂载根目录
mount /dev/mapper/fedora-root /mnt

挂载boot目录
mount /dev/nvme0n1p7 /mnt/boot

绑定dev
mount --bind /dev /mnt/dev

挂载efi
mount /dev/nvme0n1p2 /mnt/boot/efi

挂载pts(控制台设备文件所在的目录)
mount -t devpts  /dev/devtps /mnt/dev/pts

挂载proc (系统信息目录)
mount -t proc /proc /mnt/proc

挂载sys(驱动和设备)
mount -t sysfs /sys /mnt/sys

挂载efivars(里面放的是efi的环境变量,有它才能使用efibootmgr命令)
        例子：
            [fantj@localhost pts]$ efibootmgr 
            BootCurrent: 0000
            Timeout: 2 seconds
            BootOrder: 0000,0004
            Boot0000* fedora  #我的linux
            Boot0004* Windows Boot Manager  #我的winin10

mount -t efivars /sys/firmware/efi/efivars  /mnt/sys/firmware/efi/efivars
```

### 切换根目录&&执行grub安装
切换根目录：
`chroot /mnt`


重新安装grub2：`grub2-install /dev/nvme0n1` 注意后面是你所要安装到硬盘名。

不出意外会提示你No Error，修复成功。重启机器。

### 善后，两个如果
###### 情景1
重启机器后，如果成功进入图形界面，执行`grub2-mkconfig -o /boot/grub2/grub.cfg`重写grub配置。grub2会自动将你电脑中的所有引导倒入，包括win10。

```
[fantj@localhost pts]$ sudo grub2-mkconfig -o /boot/grub2/grub.cfg
[sudo] fantj 的密码：
Generating grub configuration file ...
Found linux image: /boot/vmlinuz-4.18.16-300.fc29.x86_64
Found initrd image: /boot/initramfs-4.18.16-300.fc29.x86_64.img
Found linux image: /boot/vmlinuz-0-rescue-8e1d87ce6b9c43e0847ebd0fbe40a68b
Found initrd image: /boot/initramfs-0-rescue-8e1d87ce6b9c43e0847ebd0fbe40a68b.img
Found Windows Boot Manager on
 #Win10
/dev/nvme0n1p2@/EFI/Microsoft/Boot/bootmgfw.efi  
Adding boot menu entry for EFI firmware configuration
done
```

###### 情景2
如果进入乱码报错界面`you are in emergency mode ...`，证明没有找到你的硬盘上ESP分区，我就是这样，因为我之前格式化过ESP分区，所以uuid会有变化，但是`/etc/fstab`里还是旧的uuid挂载该分区，所以需要将该分区手动挂载上。

报错日志一般是：`Starting File System Check on /dev/disk/by-uuid/xxxx-xxxx...timed out...`文件系统检测超时。

手动修改`/etc/fstab`,将ESP分区挂到`/boot/efi`下。
```
/dev/nvme0n1p2          /boot/efi               vfat    umask=0077,shortname=winnt 0 2
```