# Shell脚本-控制多主机操作
### 环境介绍
首先要做到各个主机之间无密ssh连接。这样会最大可能方便的控制。


### 脚本
命名为status
```
#!/bin/bash
params=$@
i=166
for((i=166;i<=169;i++));do
        echo -------s$i  $params -------
        ssh s$i    "$params"
done
```
### 测试
```
[root@s166 bin]# status java -version
-------s166 java -version -------
bash: java: command not found
-------s167 java -version -------
bash: java: command not found
-------s168 java -version -------
bash: java: command not found
-------s169 java -version -------
bash: java: command not found
```
What?就给我看这个，它找不到这个命令，那我把它放到`/usr/local/bin`下
```
[root@s166 bin]# which java
/home/fantj/jdk/bin/java
[root@s166 bin]# ln -s /home/fantj/jdk/bin/java
java            javadoc         javah           javapackager    javaws          
javac           javafxpackager  javap           java-rmi.cgi    
[root@s166 bin]# ln -s /home/fantj/jdk/bin/java ./java
[root@s166 bin]# ls
java  jps  status  xcall.sh
[root@s166 bin]# status java -version
-------s166 java -version -------
java version "1.8.0_171"
Java(TM) SE Runtime Environment (build 1.8.0_171-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.171-b11, mixed mode)
-------s167 java -version -------
bash: java: command not found
-------s168 java -version -------
bash: java: command not found
-------s169 java -version -------
bash: java: command not found
```
好了，jps同理，我们只需要把命令创建软连接到`/usr/local/bin`下即可，每个主机都需要设置的。
### 效果
```
[root@s166 bin]# status jps
-------s166 jps -------
12641 Jps
1397 NameNode
1559 SecondaryNameNode
1727 ResourceManager
-------s167 jps -------
1764 DataNode
11610 Jps
1823 NodeManager
-------s168 jps -------
11572 Jps
1815 NodeManager
1756 DataNode
-------s169 jps -------
1813 NodeManager
1754 DataNode
11548 Jps


[root@s166 bin]# status ls ~/.ssh
-------s166 ls /root/.ssh -------
authorized_keys
id_rsa
id_rsa.pub
known_hosts
-------s167 ls /root/.ssh -------
authorized_keys
id_rsa
id_rsa.pub
known_hosts
-------s168 ls /root/.ssh -------
authorized_keys
id_rsa
id_rsa.pub
-------s169 ls /root/.ssh -------
authorized_keys
id_rsa
id_rsa.pub
```
