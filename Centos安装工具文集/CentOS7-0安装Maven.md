1. wget http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
2. tar -zxvf apache-maven-3.3.9-bin.tar.gz
3. mv apache-maven-3.3.9 maven
4. vim /etc/profile
```
#添加环境变量
     M2_HOME=/opt/tyrone/maven （注意这里是maven的安装路径）
     export PATH=${M2_HOME}/bin:${PATH}
```
5. source /etc/profile
6. mvn -v
   - 如果报错说是没有javac，那就安装下jdk。https://www.jianshu.com/p/89cd77509a4b
  - 没报错就会显示maven版本

