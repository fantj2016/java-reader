>为什么要出这篇文章呢，相信很多redhat粉一直对桌面级系统念念不忘，但是桌面级的它却需要大量的时间去折腾，下载这下载那的，遍地找应用。因为他没有ubuntu和debian那么使用方便和部署，今后会陆续出关于redhat系列的桌面级使用工具下载和使用。当然本章对其他非redhat发行版只有参考价值，不建议手操。

### 1. flatpak
>Linux 上应用程序的未来,Flatpak是用于在Linux上构建和分发桌面应用程序的下一代技术,使用Flatpak，每个应用程序都在隔离的环境中构建和运行。
官方网站：https://flatpak.org/
应用程序库：https://flathub.org/home

![](https://upload-images.jianshu.io/upload_images/5786888-6b8738fa662816c0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

基本上支持所有发行版的linux.你在官方的app store 中可能发现不了TIM，微信，迅雷，百度云等，有些大佬将深度的包重打包成flatpak，我们可以直接安装，不过需要安装深度的flatpak依赖，具体看下面的贴吧。

http://tieba.baidu.com/p/5759912059

注意安装完后应用列表里会自动添加图标，第一次运行尽量用命令来运行`flatpak run xxx`，因为能看到报错(如果未安装成功的话，避免一脸蒙蔽).
##### 常用命令：
1. 安装：`flatpak install xxxx.flatpak`
2. 查看列表：`flatpak list`
3. 运行：`flatpak run com.tencen.tim`
4. 删除: `flatpak remove com.tencen.tim`
##### 全部命令
```
Usage:
  flatpak [OPTION…] COMMAND

Builtin Commands:
 Manage installed apps and runtimes
  install             Install an application or runtime 
  update              Update an installed application or runtime
  uninstall           Uninstall an installed application or runtime
  list                List installed apps and/or runtimes
  info                Show info for installed app or runtime
  config              Configure flatpak
  repair              Repair flatpak installation
  create-usb          Put apps and/or runtimes onto removable media

 Finding applications and runtimes
  search              Search for remote apps/runtimes

 Running applications
  run                 Run an application
  override            Override permissions for an application
  make-current        Specify default version to run
  enter               Enter the namespace of a running application
  ps                  Enumerate running applications

 Manage file access
  document-export     Grant an application access to a specific file
  document-unexport   Revoke access to a specific file
  document-info       Show information about a specific file
  document-list       List exported files

 Manage dynamic permissions
  permission-remove   Remove item from permission store
  permission-list     List permissions
  permission-show     Show app permissions
  permission-reset    Reset app permissions

 Manage remote repositories
  remotes             List all configured remotes
  remote-add          Add a new remote repository (by URL)
  remote-modify       Modify properties of a configured remote
  remote-delete       Delete a configured remote
  remote-ls           List contents of a configured remote
  remote-info         Show information about a remote app or runtime

 Build applications
  build-init          Initialize a directory for building
  build               Run a build command inside the build dir
  build-finish        Finish a build dir for export
  build-export        Export a build dir to a repository
  build-bundle        Create a bundle file from a ref in a local repository
  build-import-bundle Import a bundle file
  build-sign          Sign an application or runtime
  build-update-repo   Update the summary file in a repository
  build-commit-from   Create new commit based on existing ref
  repo                Print information about a repo

Help Options:
  -h, --help             Show help options

Application Options:
  --version              Print version information and exit
  --default-arch         Print default arch and exit
  --supported-arches     Print supported arches and exit
  --gl-drivers           Print active gl drivers and exit
  -v, --verbose          Print debug information during command processing, -vv for more detail
  --ostree-verbose       Print OSTree debug information during command processing
```


### 2. crossover
> Linux 上运行 Windows 软件,是 wine的发行版，可以破解。

##### 下载
官方链接：http://www.crossoverchina.com/xiazai.html
![](https://upload-images.jianshu.io/upload_images/5786888-72d81cac5ab34ca1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

直接运行rpm包或者命令`rpm -ivh crossoverxxxx.rpm`进行安装。

##### 破解

破解文件下载：https://pan.baidu.com/s/1KZP3lEZI9SJ2HTgmxbkVgw

然后将`/opt/cxoffice/lib/wine/winewrapper.exe.so`这个文件替换即可。

##### 使用
http://www.crossoverchina.com/rumen/

![](https://upload-images.jianshu.io/upload_images/5786888-584fc4687b077137.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

软件本身就有应用市场，可以直接搜。也可以安装本地的exe文件，但是不能保证可用，自己要琢磨，多百度。

### 3. fedy
>由于我现在用的29 版本，目前该版本对fedy的支持还没做，之前我用28版本的时候也是有点bug，也不知道是28版本的bug还是fedy的，这个想玩的可以试试。
安装官网：cms-admin.bd.xkenmon.cn


### 4. Snapcraft
>napcraft 是一个正在为其在 Linux 中的地位而奋斗的包管理系统，它为你重新设想了分发软件的方式。这套新的跨发行版的工具可以用来帮助你构建和发布 snap 软件包。

>snap 软件包被设计成用来隔离并封装整个应用程序。这些概念使得 snapcraft 提高软件安全性、稳定性和可移植性的目标得以实现，其中可移植性允许单个 snap 软件包不仅可以在 Ubuntu 的多个版本中安装，而且也可以在 Debian、Fedora 和 Arch 等发行版中安装

##### 安装
官方文档:https://docs.snapcraft.io/installing-snap-on-fedora/6755
```
sudo dnf install snapd

sudo ln -s /var/lib/snapd/snap /snap
```

##### 应用商城
https://snapcraft.io/store

##### 命令
###### 查找应用：
```
$ snap find "media player"
Name  Version  Developer  Notes  Summary
(...)
vlc        3.0.4     videolan✓    -      The ultimate media player.
mpv        0.26.0    casept       -      a free, open source, and cross-platform media player.  
(...)
```
###### 查看应用详情
```
$ snap info vlc
name:      vlc
summary:   The ultimate media player
publisher: VideoLAN✓
contact:   https://www.videolan.org/support/
description: |
  VLC is the VideoLAN project's media player.
  (...)
snap-id: RT9mcUhVsRYrDLG8qnvGiy26NKvv6Qkd
commands:
  - vlc
channels:
  stable:    3.0.0                   (158) 197MB -
  candidate: 3.0.0                   (158) 197MB -
  beta:      3.0.0-5-g407d4ba        (160) 197MB -
  edge:      4.0.0-dev-1218-g201542f (159) 197MB 
```
###### 安装：
```
sudo snap install vlc
```
###### 执行应用：
>在这里可以将该目录添加到环境变量中，这样可以直接执行`redis-desktop-manager.rdm`来启动应用。
```
/snap/bin/redis-desktop-manager.rdm
```
![我用它安装的redis-desktop](https://upload-images.jianshu.io/upload_images/5786888-b6bc384a518e61da.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 列出已安装的应用：
```
[fantj@localhost /]$ snap list
Name                   Version    Rev   Tracking  Publisher   Notes
core                   16-2.35.5  5742  stable    canonical✓  core
redis-desktop-manager  0.9.8+git  156   stable    uglide      -
```
###### 更新应用
```
sudo snap refresh vlc
```
##### 更多命令
请查看官方doc:https://docs.snapcraft.io/getting-started/3876


### 5. alien
>alien是一个用于在各种不同的Linux包格式相互转换的工具，其最常见的用法是将.rpm转换成.deb，或者反过来。

>deb 是 Unix 系统(其实主要是 Linux )下的安装包，基于 tar 包，因此本身会[记录文件](https://baike.baidu.com/item/%E8%AE%B0%E5%BD%95%E6%96%87%E4%BB%B6)的权限(读/写/可执行)以及[所有者](https://baike.baidu.com/item/%E6%89%80%E6%9C%89%E8%80%85)/用户组。

##### 安装
下载地址：http://ftp.de.debian.org/debian/pool/main/a/alien/
源码安装(CentOS推荐)：
```
tar xxxx
perl Makefile.PL
make
make install
```
如果你是Fedora，推荐直接`sudo dnf install alien`

##### 转换格式

```
.deb包转换成.rpm包   alien -r <package.deb>
.rpm包转换成.deb包   alien -d <package.rpm>

eg:
alien -r libbz2-dev_1.0.6-5_i386.deb libbz2-dev-1.0.6-6.i386.rpm generated
```
##### rpm安装
`rpm -ivh xxx`
如果报错则安装依赖：`yum -y install python-libs python-wnck`
