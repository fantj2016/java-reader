>acl全称是访问控制列表，如果涉及到对某个用户的权限设置，则用ACL更方便快捷。

注意：针对某个用户设置的权限是覆盖other用户权限的。

比如你给jiao用户设置了---权限，系统默认other权限是rw-，但是该文件对jiao用户所执行的权限是---。

#### 查看文件权限
`getfacl xxx`

```

```


#### 设置文件/文件夹权限

对用户设置：

`setfacl -m u:jiao:rw- fantj.file `
```
-m  modify修改文件用户权限
-x  取消用户权限  
```
```
[root@localhost fantj]# getfacl fantj.file 
# file: fantj.file
# owner: root
# group: root
user::rw-
user:jiao:rw-
group::r--
mask::rw-
other::r--
```



对组设置：

与对用户设置一样`setfacl -m g:jiao:rw- fantj.file`
就是把u改成g

#### 取消文件权限
`setfacl -x u:jiao fantj.file `

```
[root@localhost fantj]# setfacl -x u:jiao fantj.file 
[root@localhost fantj]# getfacl fantj.file 
# file: fantj.file
# owner: root
# group: root
user::rw-
group::r--
mask::r--
other::r--
```

对组设置：
与对用户设置一样，将u变成g即可。


#### 设置文件/目录对用户的默认权限
>有的时候，我们需要对某个目录下的所有新建东西给某个用户权限，那我们就需要用这个命令。
`setfacl -m d:u:jiao:rwx  /fantj`

d的意思是default，不管哪个用户在fantj里新创建的文件或者目录，对jiao都是rwx权限

```
[root@localhost fantj]# setfacl -m d:u:jiao:rwx /home/fantj/

[root@localhost fantj]# touch newfile.txt
[root@localhost fantj]# getfacl newfile.txt 
# file: newfile.txt
# owner: root
# group: root
user::rw-
user:jiao:rwx			#effective:rw-
group::r-x			#effective:r--
mask::rw-
other::r--
```
可以看到，user里jiao有rwx权限。

#### 取消文件/目录对用户的默认权限

`setfacl -x d:u:jiao /home/fantj`

注意：该操作不会对已经设置权限的文件有影响，只会对新的文件有影响。