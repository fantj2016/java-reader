### 删除脚本
```
#!/bin/bash
rm -rf ./ip*
```

### 复制脚本
```
#!/bin/bash
cp ./ip-user-server/target/*.jar ./
cp ./ip-web/target/*.jar ./

```

### 关闭端口进程脚本
```
#!/bin/bash
kill -9 $(lsof -i:8080)

kill -9 $(lsof -i:9000)

```

### 休息一段时间执行下一个指令
```
#!/bin/bash
nohup java -jar user-server.jar &

sleep 10;

nohup java -jar web.jar &

```
