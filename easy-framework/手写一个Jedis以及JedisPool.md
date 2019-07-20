Redis、Jedis的用途就不介绍了，不了解的可以先去官网:https://www.redis.net.cn/tutorial/3501.html 学习和使用。本文章着重讲解如何手动实现一个类似jedis的工具。

### 1. 源码探索
经过源码的研究，可以发现Jedis的实现是基于Socket，可以从Jedis的set(key,value)方法开始追溯:
```
public void set(String key, String value) {
    this.set(SafeEncoder.encode(key), SafeEncoder.encode(value));
}
public void set(byte[] key, byte[] value) {
    this.sendCommand(Command.SET, new byte[][]{key, value});
}    
public static void sendCommand(RedisOutputStream os, Protocol.Command command, byte[]... args) {
    sendCommand(os, command.raw, args);
}

private static void sendCommand(RedisOutputStream os, byte[] command, byte[]... args) {
    try {
        os.write((byte)42);
        os.writeIntCrLf(args.length + 1);
        os.write((byte)36);
        os.writeIntCrLf(command.length);
        os.write(command);
        os.writeCrLf();
        byte[][] var3 = args;
        int var4 = args.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte[] arg = var3[var5];
            os.write((byte)36);
            os.writeIntCrLf(arg.length);
            os.write(arg);
            os.writeCrLf();
        }

    } catch (IOException var7) {
        throw new JedisConnectionException(var7);
    }
}
```
```
System.out.println((char)42);
System.out.println(((char)36));

控制台：
*
$
```
可以大体的看出他得实现过程。把`sendCommand`方法翻译一下就是：
```
os.write("*".getBytes());
os.write(//数组长度\r\n);
os.write("$".getBytes());
os.write(//命令长度\r\n)
os.write(命令\r\n);
然后进入循环写入: 
    $+参数长度\r\n
    参数\r\n
```
整理一下：就是Redis的通信协议，官方文档：https://redis.io/topics/protocol 。

总结一下就是以下几点：
```
*3   // 数据一共有三个数组
//数组1
$6   //下行为6个长度的字符串
APPEND   
//数组2
$5    // 下行为5个长度的字符串
fantj
//数组3
$3    // 下行为3个长度的字符串
666
```

### 手写Jedis
通过源码可以看到，它在`Connection`类中进行sendCommend，那我们也一样：

#### Connection.java
>负责与Redis的Server端建立连接并获取反馈信息。
```
package com.fantj.jedis.connect;

import com.fantj.jedis.protocol.Protocol;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 连接类
 * 在这里进行创建连接并处理IO请求，用inputStream进行数据回显，
 * 提供OutputStream给协议层，以便让其给服务端发送命令
 */
public class Connection {
    private String host = "localhost";
    private int port = 6379;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public Connection() {
    }

    public Connection(String host) {
        this.host = host;
    }

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public Connection sendCommand(Protocol.Command cmd, byte[]... args) {
        connect();
        Protocol.sendCommand(outputStream, cmd, args);
        return this;
    }

    private void connect() {

        try {
            if (socket == null) {  //IO复用
                socket = new Socket(host, port);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 操作状态的返回
     * 比如：SET 操作成功返回 +OK
     */
    public String getStatus() {
        byte[] bytes = new byte[1024];
        try {
            socket.getInputStream().read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }
}
```
而真正的`sendCommend`实现是在`Protocol`类中实现(Jedis源码也是这样)：

#### Protocol.java
>负责提供RESP协议支持和拼接。
```
package com.fantj.jedis.protocol;

import java.io.IOException;
import java.io.OutputStream;

/**
 * RESP协议
 * 详见； https://redis.io/topics/protocol
 */
public class Protocol {
    // jedis后来将这些常量优化为byte，在os进行写出的时候对其进行char转型
    private static final String DOLLAR_BYTE = "$";
    private static final String ASTERISK_BYTE = "*";
    public static final byte PLUS_BYTE = 43;
    public static final byte MINUS_BYTE = 45;
    public static final byte COLON_BYTE = 58;
    private static final String BLANK_BYTE = "\r\n";

    /**
     * 拼接RESP 并 发送write
     */
    public static void sendCommand(OutputStream os,Protocol.Command cmd, byte[] ... args){
        // 1. 生成协议 *3 $3 SET $3 key $5 value
        StringBuffer stringBuffer = new StringBuffer();
        // 1.1 数组长度 *3
        stringBuffer.append(ASTERISK_BYTE).append(args.length+1).append(BLANK_BYTE);
        // 1.2 命令长度 $3
        stringBuffer.append(DOLLAR_BYTE).append(cmd.name().length()).append(BLANK_BYTE);
        // 1.3 命令 SET / GET
        stringBuffer.append(cmd).append(BLANK_BYTE);
        for (byte[] arg: args){
            // 1.4 key/value 长度
            stringBuffer.append(DOLLAR_BYTE).append(arg.length).append(BLANK_BYTE);
            // 1.5 key/value
            stringBuffer.append(new String(arg)).append(BLANK_BYTE);
        }
        // 写出到服务端
        try {
            os.write(stringBuffer.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 定义一个枚举类 存放命令
     */
    public static  enum Command{
        SET , GET , KEYS, APPEND
    }}
```

#### Client.java
>对外提供API。
```
package com.fantj.jedis.client;

import com.fantj.jedis.connect.Connection;
import com.fantj.jedis.protocol.Protocol;

/**
 * 客户端
 * 给开发人员使用提供API
 */
public class Client {
    private Connection connection;
    public Client(String host, int port){
        connection = new Connection(host,port);
    }

    public String set(String key, String value) {
        connection.sendCommand(Protocol.Command.SET, key.getBytes(), value.getBytes());
        return connection.getStatus();
    }

    public String get(String key) {
        connection.sendCommand(Protocol.Command.GET, key.getBytes());
        return connection.getStatus();
    }


    public void set(String key, String value, String nx, String ex, int i) {
        connection.sendCommand(Protocol.Command.SET, key.getBytes(), value.getBytes(), nx.getBytes(), ex.getBytes(), String.valueOf(i).getBytes());
    }
    public void append(String key, String value){
        connection.sendCommand(Protocol.Command.APPEND, key.getBytes(),value.getBytes());
    }
}
```

#### Jedis.java
>对Client类的进一层封装，留给开发人员使用。

```
package com.fantj.jedis.client;

public class Jedis extends Client{

    public Jedis(String host, int port) {
        super(host, port);
    }

    @Override
    public String set(String key, String value) {
        return super.set(key, value);
    }

    @Override
    public String get(String key) {
        return super.get(key);
    }

    @Override
    public void set(String key, String value, String nx, String ex, int i) {
        super.set(key, value, nx, ex, i);
    }

    @Override
    public void append(String key, String value) {
        super.append(key, value);
    }
}
```


### 测试
```
/**
 * 测试我们自己写的客户端
 */
public class Main {
    private Jedis client = new Jedis("www.xxx.top",6380);
    @Test
    public void set(){
        client.set("fantj","fantj");
        String result = client.get("fantj");
        System.out.println(result);
    }
    @Test
    public void setNx(){
        client.set("fantj","fantj","NX","EX",10000);
        String result = client.get("fantj");
        System.out.println(result);
    }
    @Test
    public void append(){
//        client.append("fantj","-2019");
        String fantj = client.get("fantj");
        System.out.println(fantj);
    }
    @Test
    public void testChar(){
        System.out.println((char)42);
        System.out.println(((char)36));
    }
}
```
测试都可以通过。

### 总结一下原理

假设我执行`append("fantj","666")`这个命令,那客户端的操作过程：

1. RESP协议对命令的分析
```
*3   // 数据一共有三个数组
//数组1
$6   //下行为6个长度的字符串
APPEND   
//数组2
$5    // 下行为5个长度的字符串
fantj
//数组3
$3    // 下行为3个长度的字符串
666
```
2. 然后我们进行拼接
```
StringBuffer sb = new StringBuffer();
// 注意每个类型表示完后都进行换行
sb.append("*").append("3").append("\r\n");
sb.append("$").append("6").append("\r\n");
sb.append("APPEND").append("\r\n");
sb.append("$").append("5").append("\r\n");
sb.append("fantj").append("\r\n");
sb.append("$").append("3").append("\r\n");
sb.append("666").append("\r\n");
```
3. 然后用os进行写出
```
os.write(sb.toString().getBytes());
```

### 连接池的实现

#### Pool<T>.java
```
/**
 * 连接池契约
 */
public interface Pool<T> {
    /**
     * 初始化连接池
     * @param maxTotal 最大连接数
     * @param maxWaitMillis 最大等待时间
     */
    public void init(int maxTotal, long maxWaitMillis);

    /**
     * 获取连接
     * @return 返回jedis对象
     */
    public Jedis getResource() throws Exception;

    /**
     * 释放连接
     */
    public void release(T t);
}
```

#### JedisPool.java
>实现方式与ExcutorThreadPool工作流程类似，注释写的挺全的就不做详细解读了。

```
package com.fantj.jedis.pool;

import com.fantj.jedis.client.Jedis;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JedisPool implements Pool<Jedis>{

    public JedisPool(String url, int port) {
        this.url = url;
        this.port = port;
        init(maxTotal,maxWaitMillis);
    }

    public JedisPool(String url, int port, int maxTotal, long maxWaitMillis) {
        this.url = url;
        this.port = port;
        this.maxTotal = maxTotal;
        this.maxWaitMillis = maxWaitMillis;
    }

    private String url;
    private int port;
    private int maxTotal = 20;
    private long maxWaitMillis = 1000;
    // 空闲的连接queue
    private LinkedBlockingQueue<Jedis> idleWorkQueue = null;
    private Queue<Jedis> activeWorkQueue = null;
    // 当前连接数量
    private AtomicInteger count = new AtomicInteger(0);
    @Override
    public void init(int maxTotal, long maxWaitMillis) {
        maxTotal = maxTotal;
        maxWaitMillis = maxWaitMillis;
        idleWorkQueue = new LinkedBlockingQueue<>(maxTotal);
        activeWorkQueue = new LinkedBlockingQueue<>(maxTotal);
    }

    @Override
    public Jedis getResource() throws Exception {
        Jedis jedis = null;
        // 1. 记录开始时间，检测超时
        long startTime = System.currentTimeMillis();
        while (true){
            // 2. 从空闲队列中获取连接，如果拿到，一式两份存放到活动队列
            jedis = idleWorkQueue.poll();
            if (jedis != null){
                activeWorkQueue.offer(jedis);
                return jedis;
            }
            // 3. 如果失败，判断池是否满，没满则创建
            if (count.get() < maxTotal){
                if (count.incrementAndGet() <= maxTotal){
                    jedis = new Jedis(url,port);
                    activeWorkQueue.offer(jedis);
                    System.out.printf("创建了一个新的连接: %s \r\n", jedis.toString());
                    return jedis;
                }else {
                    count.decrementAndGet();
                }
            }
            // 4. 如果连接池满了，则在超时时间内进行等待
            try {
                jedis = idleWorkQueue.poll(maxWaitMillis-(System.currentTimeMillis()-startTime), TimeUnit.MILLISECONDS);
                if (jedis != null){
                    activeWorkQueue.offer(jedis);
                    return jedis;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 5. poll可能被中断，所以在这里再进行超时判断
            if (maxWaitMillis < (System.currentTimeMillis()-startTime)){
                throw new RuntimeException("JedisPool: jedis connect timeout");
            }
        }
    }

    @Override
    public void release(Jedis jedis) {
        if (activeWorkQueue.remove(jedis)){
            idleWorkQueue.offer(jedis);
        }
    }
}
```


GitHub地址: https://github.com/fantj2016/easy-jedis  