>Azkaban是由Linkedin开源的一个批量工作流任务调度器。用于在一个工作流内以一个特定的顺序运行一组工作和流程。Azkaban定义了一种KV文件格式来建立任务之间的依赖关系，并提供一个易于使用的web用户界面维护和跟踪你的工作流。

### 1. 安装

##### 准备工作
`azkaban-web-server-2.5.0.tar.gz`
`azkaban-executor-server-2.5.0.tar.gz`
`azkaban-sql-script-2.5.0.tar.gz`

其中，`azkaban-web-server-2.5.0.tar.gz`是服务器，`azkaban-executor-server-2.5.0.tar.gz`是执行服务器，`azkaban-sql-script-2.5.0.tar.gz`是执行的sql脚本。


### 2. mysql创建表
>分别把他们解压安装后。我们还需要在mysql中创建数据库，然后运行azkaban提供的sql脚本来创建azkaban所需要的表。

```
mysql -uroot -p
mysql> create database azkaban;
mysql> use azkaban;
Database changed
mysql> source /home/fantj/azkaban/azkaban-2.5.0/create-all-sql-2.5.0.sql;
mysql> show tables;
+------------------------+
| Tables_in_azkaban      |
+------------------------+
| active_executing_flows |
| active_sla             |
| execution_flows        |
| execution_jobs         |
| execution_logs         |
| project_events         |
| project_files          |
| project_flows          |
| project_permissions    |
| project_properties     |
| project_versions       |
| projects               |
| properties             |
| schedules              |
| triggers               |
+------------------------+
15 rows in set (0.00 sec)
```
### 3. 创建SSL配置
##### 1. 执行命令`keytool -keystore keystore -alias jetty -genkey -keyalg RSA`会在当前目录生成一个`keystore`证书文件，当然执行该命令需要你填写一些信息，比如你的姓名+工作单位等。按照提示填写即可。

##### 2. 然后把 keystore 考贝到 azkaban web服务器bin目录中

### 4. 配置时区
```
[root@s166 azkaban]# tzselect
Please identify a location so that time zone rules can be set correctly.
Please select a continent or ocean.
 1) Africa
 2) Americas
 3) Antarctica
 4) Arctic Ocean
 5) Asia
 6) Atlantic Ocean
 7) Australia
 8) Europe
 9) Indian Ocean
10) Pacific Ocean
11) none - I want to specify the time zone using the Posix TZ format.
#? 5
Please select a country.
 1) Afghanistan		  18) Israel		    35) Palestine
 2) Armenia		  19) Japan		    36) Philippines
 3) Azerbaijan		  20) Jordan		    37) Qatar
 4) Bahrain		  21) Kazakhstan	    38) Russia
 5) Bangladesh		  22) Korea (North)	    39) Saudi Arabia
 6) Bhutan		  23) Korea (South)	    40) Singapore
 7) Brunei		  24) Kuwait		    41) Sri Lanka
 8) Cambodia		  25) Kyrgyzstan	    42) Syria
 9) China		  26) Laos		    43) Taiwan
10) Cyprus		  27) Lebanon		    44) Tajikistan
11) East Timor		  28) Macau		    45) Thailand
12) Georgia		  29) Malaysia		    46) Turkmenistan
13) Hong Kong		  30) Mongolia		    47) United Arab Emirates
14) India		  31) Myanmar (Burma)	    48) Uzbekistan
15) Indonesia		  32) Nepal		    49) Vietnam
16) Iran		  33) Oman		    50) Yemen
17) Iraq		  34) Pakistan
#? 9
Please select one of the following time zone regions.
1) Beijing Time
2) Xinjiang Time
#? 1

The following information has been given:

	China
	Beijing Time

Therefore TZ='Asia/Shanghai' will be used.
Local time is now:	Sat Jul 28 18:29:58 CST 2018.
Universal Time is now:	Sat Jul 28 10:29:58 UTC 2018.
Is the above information OK?
1) Yes
2) No
#? 1

You can make this change permanent for yourself by appending the line
	TZ='Asia/Shanghai'; export TZ
to the file '.profile' in your home directory; then log out and log in again.

Here is that TZ value again, this time on standard output so that you
can use the /usr/bin/tzselect command in shell scripts:
Asia/Shanghai
```
这个配置需要给集群的每个主机设置，因为任务调度离不开准确的时间。我们也可以直接把相关文件拷贝到别的主机作覆盖。
```
cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
```
```
[root@s166 azkaban]# scp /usr/share/zoneinfo/Asia/Shanghai  root@s168:/etc/localtime
Shanghai                                                                                              100%  388   500.8KB/s   00:00    
[root@s166 azkaban]# scp /usr/share/zoneinfo/Asia/Shanghai  root@s169:/etc/localtime
Shanghai   
```

### 5. 修改配置

##### 5.1 修改服务端配置
###### 5.1.1 `/webserver/conf`目录下的`azkaban.properties`(我之前将服务端的解压文件改名为webserver)
```
#Azkaban Personalization Settings
azkaban.name=Test
azkaban.label=My Local Azkaban
azkaban.color=#FF3601
azkaban.default.servlet.path=/index
web.resource.dir=web/
default.timezone.id=Asia/Shanghai

#Azkaban UserManager class
user.manager.class=azkaban.user.XmlUserManager
user.manager.xml.file=conf/azkaban-users.xml

#Loader for projects
executor.global.properties=conf/global.properties
azkaban.project.dir=projects

database.type=mysql
mysql.port=3306
mysql.host=localhost
mysql.database=azkaban
mysql.user=root
mysql.password=root
mysql.numconnections=100

# Velocity dev mode
velocity.dev.mode=false

# Azkaban Jetty server properties.
jetty.maxThreads=25
jetty.ssl.port=8443
jetty.port=8081
jetty.keystore=keystore
jetty.password=jiaoroot
jetty.keypassword=jiaoroot
jetty.truststore=keystore
jetty.trustpassword=jiaoroot

# Azkaban Executor settings
executor.port=12321

# mail settings
mail.sender=844072586@qq.com
mail.host=smtp.qq.com
job.failure.email=
job.success.email=

lockdown.create.projects=false

cache.directory=cache
```
主要修改时区+mysql配置+SSL密码和文件路径+邮箱配置。不贴注释了，一看就懂。


###### 5.1.2. 修改`/conf/`目录下的`azkaban-users.xml`
```
<azkaban-users>
        <user username="azkaban" password="azkaban" roles="admin" groups="azkaban" />
        <user username="metrics" password="metrics" roles="metrics"/>
        <user username="admin" password="admin" roles="admin">
        
        <role name="admin" permissions="ADMIN" />
        <role name="metrics" permissions="METRICS"/>
</azkaban-users>
```
##### 5.2 执行服务器配置
修改`/executor/conf`目录下的`azkaban.properties`
```
#Azkaban
default.timezone.id=Asia/Shanghai

# Azkaban JobTypes Plugins
azkaban.jobtype.plugin.dir=plugins/jobtypes

#Loader for projects
executor.global.properties=conf/global.properties
azkaban.project.dir=projects

database.type=mysql
mysql.port=3306
mysql.host=localhost
mysql.database=azkaban
mysql.user=root
mysql.password=root
mysql.numconnections=100

# Azkaban Executor settings
executor.maxThreads=50
executor.port=12321
executor.flow.threads=30
```

### 6. 执行
##### 6.1 启动web服务器
在`webserver/bin`目录下，执行`[root@s166 webserver]# nohup bin/azkaban-web-start.sh 1>/tmp/azstd.out 2>/tmp/azerr.out &`启动服务。

小技巧：先别记着用nohup执行，不然报错不能够及时的反馈，应该在尝试执行通过后再去尝试用nohup来执行。`[root@s166 executor]# bin/azkaban-executor-start.sh`

我大概见到的一些报错是：
1. /bin/目录下没有keystore文件------需要把它复制到bin下
2. 找不到各种配置文件-----   我在配置文件中将这些文件配置成绝对路径。
##### 6.2 启动执行服务器
在`/executor/bin/`目录下执行`[root@s166 webserver]# bin/azkaban-web-start.sh 
`

##### 6.3 浏览器访问`https://s166:8443/`
![](https://upload-images.jianshu.io/upload_images/5786888-59d2973d0279d974.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果你看到这样的画面，证明你错了，没有在根目录下执行，而是习惯性的在bin目录下执行启动文件，所以它的很多css都加载不到。

![这才是正确的打开方式](https://upload-images.jianshu.io/upload_images/5786888-d5f658ac0378a33e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

用设置的账号密码登录。
![](https://upload-images.jianshu.io/upload_images/5786888-886dbced00517610.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 7. Azkaban实战
###### 7.1 单一job示例
1. 创建job描述文件
```
vim command.job

#command.job
type=command                                                    
command=echo fantj666
```
2. 将job资源文件打包成zip文件
zip command.job

3. 通过azkaban的web管理平台创建project并上传job压缩包
首先创建project
![创建工程](https://upload-images.jianshu.io/upload_images/5786888-324ed400a42fa942.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![上传文件](https://upload-images.jianshu.io/upload_images/5786888-98d49ae08853eec7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![执行日志](https://upload-images.jianshu.io/upload_images/5786888-b86414f532e482ba.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 7.2 多job工作流flow
1. 创建有依赖关系的多个job描述
第一个job：foo.job
```
# foo.job
type=command
command=echo foo
```
第二个job：bar.job依赖foo.job
```
# bar.job
type=command
dependencies=foo
command=echo bar
```
2. 将所有job资源文件打到一个zip包中
3. 上传zip包并启动
4. 查看job log
![job list](https://upload-images.jianshu.io/upload_images/5786888-1a55a737bb4e2c91.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![foo job log](https://upload-images.jianshu.io/upload_images/5786888-1ae8b79becbcdf52.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![bar job log](https://upload-images.jianshu.io/upload_images/5786888-cca333b487bd2cbd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 7.3 操作hadoop
1. `vim fs.job`
```
# fs.job
type=command
command=/home/fantj/hadoop/bin/hadoop fs -lsr /
```
2. 打包成zip上传
3. 启动job并查看lob
![](https://upload-images.jianshu.io/upload_images/5786888-26ea3191e6640569.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 7.4 操作hive

hive脚本`test.sql`
```
use default;
drop table aztest;
create table aztest(id int,name string,age int) row format delimited fields terminated by ',' ;
load data inpath '/aztest/hiveinput' into table aztest;
create table azres as select * from aztest;
insert overwrite directory '/aztest/hiveoutput' select count(1) from aztest; 
```

job文件`hivef.job`
```
# hivef.job
type=command
command=/home/fantj/hive/bin/hive -f 'test.sql'
```

打zip包-上传-执行-查log

![](https://upload-images.jianshu.io/upload_images/5786888-eadfcb7548abeb16.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
