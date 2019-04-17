### 联合查杀redis所有进程
 `ps -ef|grep redis|grep -v grep|cut -c 9-15|xargs kill -9`

### 修改并重写入文件
`sed 's/26379/26380/g' sentinel.conf > sentinel-26380.conf`

### 过滤注释和空格查看文件
`cat redis.conf |grep -v "#"| grep -v "^$"`

### 快速设置环境变量
`echo -e "export PATH=$(npm prefix -g)/bin:$PATH" >> ~/.bashrc && source ~/.bashrc`
