>redhat系统中，如果你想要更新yum仓库，它会提示让你注册才能更新，因为centos和redhat基本相同，所以我把yum这一套全换成centos的。

### 1. 删除已安装的yum包
```
cd /etc/rpm/

rm -rf ./m*
```
### 2. 下载centos的yum包
```
   22  wget https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/yum-metadata-parser-1.1.4-10.el7.x86_64.rpm
   24  wget https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/yum-utils-1.1.31-40.el7.noarch.rpm
   25  wget https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/yum-3.4.3-158.el7.centos.noarch.rpm
   26  wget https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/yum-plugin-fastestmirror-1.1.31-45.el7.noarch.rpm
   28  wget https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/python-urlgrabber-3.10-8.el7.noarch.rpm

```
如果出现404错误，请去https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/搜索关键词查询最新rpm包的链接。
### 3. 强制安装rpm包
--force --nodeps为忽略依赖检测的强制安装
```
rpm -ivh python-urlgrabber-3.10-8.el7.noarch.rpm --force --nodeps

rpm -ivh yum* --force --nodeps
```
### 4. 查看yum包是否安装好
```shell
[root@localhost yum.repos.d]# rpm -qa |grep yum
yum-metadata-parser-1.1.4-10.el7.x86_64
yum-utils-1.1.31-45.el7.noarch
yum-3.4.3-158.el7.centos.noarch
yum-plugin-fastestmirror-1.1.31-45.el7.noarch
```
### 5. 修改repo

下载源文件
```shell
curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
```
需要把Centos-7.repo文件中的$releasever全部替换为7
```
   67  cd /etc/yum.repos.d/
   68  ls
   69  vim CentOS-Base.repo
````
在vim中执行`:%s/$releasever/7/g`快速替换。保存退出。

### 6. 清空重载yum
```
yum clean all
yum update
# 最后查看一下yum列表
yum list
```
