redhat系列的yum配置文件放在
`/etc/yum.repos.d/xxx.repo`
文件的基本格式
```
[xxx]
name=xxx
baseurl= http://xxxxx    镜像仓库链接
gpgcheck=0      不扫描
```

测试yum是否成功安装
`yum clean all`清除yum缓存
`yum repolist `查看yum源信息
