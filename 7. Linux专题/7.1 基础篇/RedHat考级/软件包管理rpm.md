### 软件包分类
1. 源码包：脚本安装包：安装慢但能看到源码
2. 二进制包（RPM、系统默认包）：安装快但不知道源码

#### rpm包命名规则
```
httpd-2.215-15.e16.centos.1.i686,rpm

httpd   软件包名
2.2.15  软件版本
15      软件发布次数
e16.centos      适合的linux平台
i686        适合的硬件平台
rpm     rpm包扩展名

```

#### rpm包依赖性

树形依赖：a>b>c
环形依赖：a>b>c>a   需要abc同时装
模块依赖：查询网站 www.rpmfind.net


### RPM安装
`rpm -ivh 包全名`

-i  install
-v  显示详细信息verbose
-h  显示进度hash
--nodeps    不检测依赖性

### RPM包查询
`rpm -qa |grep xxx`

### RPM包升级
`rpm -Uvh 包全名`
-U  upgrade升级

### RPM包卸载
`rpm -e 包名`
-e  erase卸载
--nodeps    不检查依赖性


### 查询RPM包是否安装
`rpm -q 包名`
-q  query查询
-a  all所有

### 查询RPM包详细信息
`rpm -qi 包名`
-p      package包信息
-i      information软件信息

### 查询RPM包中文件安装位置
`rpm -ql 包名`

-l      list列表

-p      包信息package

### 查询软件包的依赖性
`rpm -qR 包名`

-R      依赖性requires

### RPM包校验
>可以查看对原生包做的修改

`rpm -V 已安装包名`
-V      verify校验


