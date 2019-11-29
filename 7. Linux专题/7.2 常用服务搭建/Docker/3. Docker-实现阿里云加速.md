### 阿里云加速
```shell
[root@FantJ ~]# tee /etc/docker/daemon.json <<-'EOF'
> {
>   "registry-mirrors": ["https://wghlmi3i.mirror.aliyuncs.com"]
> }
> EOF
{
  "registry-mirrors": ["https://wghlmi3i.mirror.aliyuncs.com"]
}
[root@FantJ ~]# cat /etc/docker/daemon.json 
{
  "registry-mirrors": ["https://wghlmi3i.mirror.aliyuncs.com"]
}

```
其实就是添加了一个registry-mirrors的一个键值对。然后重加载该文件，重启服务
``` shell
[root@FantJ ~]# systemctl daemon-reload
[root@FantJ ~]# systemctl restart docker
```
