### 1. 删除所有主机上的密钥
rm -rf /.ssh/*

### 2. 在一台主机上生成密钥对

ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa

### 3.  拷贝公钥&无密登录
如果想让A无密码登录B，就把A的公钥传给B，放到用户家目录下的.ssh目录里。我在这里直接用的root用户操作：
`scp id_rsa.pub root@s169:~/.ssh/authorized_keys`
```
[root@s166 .ssh]# scp id_rsa.pub root@s169:~/.ssh/authorized_keys
The authenticity of host 's169 (192.168.27.169)' can't be established.
ECDSA key fingerprint is SHA256:3BePLPmiwUOy025LcIJJNvQJlKUJ3uo9T03op0XC5ws.
ECDSA key fingerprint is MD5:24:ee:b2:a8:cf:df:9d:f7:cc:6c:1f:73:c5:ad:b5:b0.
Are you sure you want to continue connecting (yes/no)? y
Please type 'yes' or 'no': yes
Warning: Permanently added 's169,192.168.27.169' (ECDSA) to the list of known hosts.
root@s169's password: 
id_rsa.pub                                                                                            100%  391   347.1KB/s   00:00    
[root@s166 .ssh]# ssh s169
There was 1 failed login attempt since the last successful login.
Last login: Wed Jul 25 10:27:45 
[root@s169 ~]# 

```
