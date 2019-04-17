###1. 什么是流水线
  其实就是批量查询（来减少网络用时）
###2. 客户端实现
```
 @Test
    /** pipeline 批量操作 */
    public void pipelineLearn(){
        Jedis jedis = new Jedis("192.168.218.129",6379);
        for (int i = 0;i<10;i++){
            Pipeline pipeline = jedis.pipelined();
            for (int j = i*10;i<(i+1)*10;j++){
                pipeline.hset("hashkey"+j,"field"+j,"value"+j);
            }
            pipeline.syncAndReturnAll();
        }
    }
```
###3. 两个注意
* 注意批量数据量大小要合适
* pipeline每次只能作用在一个Redis节点
