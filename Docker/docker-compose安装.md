>参考文档:https://docs.docker.com/compose/install/#install-compose
### 下载
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.23.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

### 授予x权限
```
sudo chmod +x /usr/local/bin/docker-compose
```
### 测试
```
docker-compose --version  


[fantj@lalala docker]$ docker-compose version
docker-compose version 1.23.1, build b02f1306
docker-py version: 3.5.0
CPython version: 3.6.7
OpenSSL version: OpenSSL 1.1.0f  25 May 2017
```
