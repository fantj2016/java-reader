

### 1. 安装
`yum install mariadb\* -y`

##### 1.1 启动服务
>默认3306端口

`systemctl restart mariadb`

`systemctl enable mariadb`

##### 1.2 登录

`mysql`

查看mysql用户列表：
```
MariaDB [mysql]> select host,user,password from user;
+-----------+------+----------+
| host      | user | password |
+-----------+------+----------+
| localhost | root |          |
| s168      | root |          |
| 127.0.0.1 | root |          |
| ::1       | root |          |
| localhost |      |          |
| s168      |      |          |
+-----------+------+----------+
6 rows in set (0.00 sec)
```

##### 1.3 设置密码

###### 方法一：
`mysqladmin -u root -p password 'redhat'`

没有原密码的话直接回车。此时redhat就是新密码。

###### 方法二：

`mysql_secure_installation`
```
Set root password? [Y/n]    这里直接回车
New password: 填写密码
Re-enter new password: 重复密码
Password updated successfully!
然后一路回车，一路默认就行
```
###### 方法三：
1. `mysql `无登录用户进入cli

2. 然后`set password=password('redhat');`

3. 最后`flush privileges;`刷新配置。


### 2. 配置


##### 2.1 主配置文件
`/etc/my.cnf`

###### 拒绝远程访问配置
>对[mysqld]配置块做修改。
```
单机运行MySQL使用skip-networking关闭MySQL的TCP/IP连接方式

skip-networking

开启该选项后就不能远程访问MySQL
```
###### 优化连接速度
>对[mysqld]配置块做修改。
```
使用skip-name-resolve增加远程连接速度

skip-name-resolve

该选项表示禁用DNS解析，属于官方一个系统上的特殊设定不管，链接的的方式是经过hosts或是IP的模式，他都会对DNS做反查，由于反查解析过慢，就会无法应付过量的查询。
```

###### 指定ip访问
>对[mysqld]配置块做修改。

```
为安全考虑希望指定的IP访问MySQL，可以在配置文件中增加bind-address=IP，前提是关闭skip-networking

bind-address=192.168.1.100

MySQL优化应该按实际情况配置。
```



### 强行修改密码

##### 修改配置文件

1. `/etc/my.cnf`在mysqld下面加`skip-gran-tables`(或者执行命令`mysqld_safe --skip-grant-tables`
两者道理一样。)
2. 重启服务
3. `mysql`直接回车遍可以进入安全模式。
4. `update mysql.user set password=password('redhat') where user='root' and host='localhost'`
5. `flush privileges;`
6. 去掉1中添加的配置。
7. 

# Mysqldump
>


### 1. 做备份


语法：
`mysqldump -u root-p 库  表 > /path/file`

```
[root@s168 ~]# mysqldump -uroot -p mysql user > /home/fantj/sql.bak;
Enter password: 
[root@s168 ~]# cd /home/fantj/
[root@s168 fantj]# ls
download  hadoop  jdk  keepalived  scala  spark  sql.bak

[root@s168 fantj]# cat sql.bak 
-- MySQL dump 10.14  Distrib 5.5.60-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: mysql
-- ------------------------------------------------------
-- Server version	5.5.60-MariaDB
```

###### 备份库中所有表

省略表参数就行：`mysqldump -uroot -p  库 > /path/file`

如果希望sql中附带创建数据库，则需要在库名前外加`-B `属性。

###### 备份表数据(不带数据库语句)
>在mysql cli中完成。

`select * from user into outfile '/home/fantj/sql.data'`也可以给他加分隔符，在该命令后追加`fields terminated by ','`则会以逗号分隔保存。

当前，前提是mysql用户对该目录有写权限：`setfacl -m u:mysql:rwx /home/fantj`


相反，将数据文件导入user表则是：`load data infile '/hoem/fantj/sql.data into table user'`，如果有分隔符，则需要在命令后追加`fields terminated by ','`
### 2. 做还原
>需要进入mysql cli中进行操作。

语法：`source /home/fantj/sql.bak`

```
MariaDB [(none)]> source /home/fantj/sql.bak
Query OK, 0 rows affected (0.00 sec)

Query OK, 0 rows affected (0.00 sec)
```