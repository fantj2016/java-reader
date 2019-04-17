###一、慢查询说明
   慢查询日志, 是系统记录那些超过指定查询时间的日志，查询时间指的是单个命令占用CPU处理时间。不包括在队列中等待的时间。仅仅指执行该命令需要的时间。
###二、三个命令
* slowlog get [n] 获取慢查询队列
* slowlog len 获取慢查询队列长度
* slowlog reset 清空慢查询队列
###三、两个配置
######1. 两个重要参数
慢查询日志有两个参数：
* slowlog-log-slower-than: 单位微妙，指定redis执行命令的最大时间，超过将记录到慢查询日志中, 
不接受负值，如果设置为0，每条命令都要记录到慢查询日志中.（默认值128）
* slowlog-max-len: 设置慢查询日志长度，如果慢查询日志已经到最大值，如果有新命令需要记录，就将最老那条记录删除.（默认值1000）
![image.png](http://upload-images.jianshu.io/upload_images/5786888-04eb00e95d2af990.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######2. 动态配置
  redis作为一个持久化服务，一般开启之后不会在对它进行重启操作。因此redis支持动态修改配置
命令为`config set slowlog-max-len 1`和`config set slowlog-log-slower-than 1000`
###四、通常配置
  通常slowlog-max-len不要设置过大 默认10ms ,设置为1ms
  通常slowlog-log-slower-than 不要设置过小，通常设置1000
