 1.下载并解压

2.移动到/usr目录下   

![image.png](http://upload-images.jianshu.io/upload_images/5786888-421acec5cd263dba.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


2.5 给予jdk所有权限

```chmod 777 -R jdk```

3.修改/etc/profile  文件

在最底部加

```
 export JAVA_HOME=/usr/java/jdk1.7.0_60

 export JRE_HOME=/usr/java/jdk1.7.0_60/jre

 exportCLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib

 export PATH=$PATH:$JAVA_HOME/bin
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-e75432188baced5e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


4.source profile  使文件生效

5.java -version  检测是否安装成功

![image.png](http://upload-images.jianshu.io/upload_images/5786888-7dfe6fc72c52bce0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


*   tomcat 安装
*   1.解压
*   *   unzip或者tar
*   2.移动至/usr
*   3.给予权限
![image.png](http://upload-images.jianshu.io/upload_images/5786888-fefe1f4f2684f4d5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


*   4.执行./startup.sh判断是否安装成功(出现下面的证明成功了)
*   *  ![image.png](http://upload-images.jianshu.io/upload_images/5786888-a889da1dbee5f1da.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


*   5.防火墙和端口问题的解决
*   *   先查看tomcat日志（进入bin目录   ./catalina.sh run）
    *   关闭防火墙   service iptables stop//redhat系列

                                        service ufw stop   //ubuntu       ufw  disable  开机不自动启动 

*   *   查看8080端口占用情况netstat  -apn |grep 8080
    *   ![image.png](http://upload-images.jianshu.io/upload_images/5786888-e5f6d0bb8c74f466.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


    *   ![image.png](http://upload-images.jianshu.io/upload_images/5786888-05883ae56d6705f3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


    *   或者开放8080等端口
    *   yum install -t iptables 
    *   service iptables status
    *   service iptables start
    *   iptables -P INPUT ACCEPT   #这个一定要先做，不然清空后可能会悲剧
    *   iptables -F    #清空默认所有规则 
    *   iptables -X   #清空自定义的所有规则
    *   iptables -Z    #计数器置0
    *   iptables -A INPUT -i lo -j ACCEPT

    *   iptables -A INPUT -p tcp --dport 22 -j ACCEPT

    *   iptables -A INPUT -p tcp --dport 80 -j ACCEPT   开放80端口

    *   iptables -A INPUT -p tcp --dport 8080 -j ACCEPT   开放8080端口

    *   保存重启
    *   service iptables save
    *   service iptables restart
*   如果还不能访问！查看阿狸腾讯安全组端口！！！
*   mysql
*   *   1.查看是否有mysql
    *   rpm -qa |grep mysql
    *   2.普通删除mysql服务
    *   rpm -e mysql
    *   2.5 强力删除mysql服务
    *   rpm -e --nodeps mysql
    *   3.通过yum安装mysql
    *   yum list|grep mysql   查看mysql列表
    *   4.安装mysql一套服务
    *   yum install -y mysql-server mysql mysql-deve
    *   5.查看安装版本
    *   rpm -qi mysql-server
    *   6.启动mysql服务
    *   service mysqld start
    *   7.查看是否是开机启动
    *   chkconfig --list |grep mysqld
    *   7.5如果需要开机启动
    *   chkconfig mysqld on   然后返回第7步查看
    *   8.设置mysql帐号密码
    *   mysqladmin -u root password 'root'
    *   9.配置文件
    *   /etc/my.cnf
    *   10.数据库文件
    *   /var/lib/mysql 
    *   11.日志
    *   /var/log mysql.log
