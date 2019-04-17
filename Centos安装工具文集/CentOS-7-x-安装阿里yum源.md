
### 1. 执行命令
```
curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
```
该命表示把Centos-7.repo下载到/etc/yum.repos.d/目录下，如果该目录下有CentOS-Base.repo，则会自动覆盖。

### 2. 缓存清理
```
yum clean cache
```

### 3. 生成缓存
```
yum makecache
```
