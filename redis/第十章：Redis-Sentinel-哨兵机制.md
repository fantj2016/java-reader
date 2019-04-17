>Redis Sentinel为Redis提供高可用性。实际上，这意味着使用Sentinel可以创建一个Redis部署，在没有人为干预的情况下抵抗某些类型的故障。


### 1. 故障转移步骤
1. 多个sentinel发现并缺人master有问题。
2. 选举出一个sentinel作为领导。
3. 选出一个slave作为master
4. 通知其余slave成为新的master的slave
5. 通知客户端朱从变化
6. 等待老的master复活成为信master的slave

### 2.  为什么要用哨兵
Redis Sentinel是一个分布式系统：

Sentinel本身被设计成运行在多个Sentinel进程合作的配置中。具有多个Sentinel进程协作的优势如下：

1. 当多个Sentinels同意给定的主控器不再可用时，执行故障检测。这降低了误报的可能性。
即使不是所有的Sentinel进程都在工作，Sentinel也能正常工作，从而使系统对故障有效。毕竟，拥有一个本身就是单点故障的故障切换系统是没有意义的。
2. Sentinel，Redis实例（主服务器和从服务器）以及连接到Sentinel和Redis的客户端的总和也是具有特定属性的更大的分布式系统。在这篇文档中，概念将从为了理解Sentinel的基本属性所需的基本信息，到更复杂的信息（这些是可选的），逐步引入，以便理解Sentinel的工作原理。

### 3. 快速开始

#### 3.1 配置哨兵
分别配置`sentinel.conf`和`sentinel-26380.conf`
```
port 26379
protected-mode no
sentinel monitor mymaster 47.xx.xxx.xxx 6379 2    # 127.0.0.1 一定要改成服务器 ip
sentinel auth-pass mymaster root++...   #如果mamaster节点有密码，则需要设置这项
sentinel down-after-milliseconds mymaster 60000
sentinel failover-timeout mymaster 180000
sentinel parallel-syncs mymaster 1


port 26380
sentinel monitor myslave 47.xx.xxx.xxx 6380 4  
sentinel auth-pass myslave root++...   #如果mamaster节点有密码，则需要设置这项
sentinel down-after-milliseconds myslave 10000
sentinel failover-timeout myslave 180000
sentinel parallel-syncs myslave 5
```
#### 3.2 运行哨兵
两种方法：
1. `redis-sentinel /path/to/sentinel.conf`
2. `redis-server /path/to/sentinel.conf --sentinel`


#### 3.3 测试

##### mymaster启动
```
[root@FantJ redis-4.0.9]# redis-sentinel sentinel.conf &
[1] 17859
[root@FantJ redis-4.0.9]# 17859:X 07 Sep 16:44:10.344 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
17859:X 07 Sep 16:44:10.344 # Redis version=4.0.9, bits=64, commit=00000000, modified=0, pid=17859, just started
17859:X 07 Sep 16:44:10.344 # Configuration loaded
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 4.0.9 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in sentinel mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 26379
 |    `-._   `._    /     _.-'    |     PID: 17859
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

17859:X 07 Sep 16:44:10.346 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
17859:X 07 Sep 16:44:10.346 # Sentinel ID is e61d1d9c3a7441d2376cb98399fa9dd479076eef
17859:X 07 Sep 16:44:10.346 # +monitor master mymaster xxx.xx.xx.xx  6379 quorum 2
```
##### myslave启动
```
[root@FantJ sentinel]# redis-sentinel sentinel-26380.conf &
[2] 17873
[root@FantJ sentinel]# 17873:X 07 Sep 16:46:11.401 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
17873:X 07 Sep 16:46:11.401 # Redis version=4.0.9, bits=64, commit=00000000, modified=0, pid=17873, just started
17873:X 07 Sep 16:46:11.401 # Configuration loaded
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 4.0.9 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in sentinel mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 26380
 |    `-._   `._    /     _.-'    |     PID: 17873
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

17873:X 07 Sep 16:46:11.403 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
17873:X 07 Sep 16:46:11.403 # Sentinel ID is 0f375fb9f45af9c6a5402e88cb9296b24da6f067
17873:X 07 Sep 16:46:11.403 # +monitor master myslave xxxxx 6380 quorum 4
```
##### 查看26379的sentinel信息：
```
[root@FantJ sentinel]# redis-cli -p 26379
127.0.0.1:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=sdown,address=47.xx.xx.xxx:6379,slaves=0,sentinels=1
```



