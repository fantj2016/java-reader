>先决条件：要使用加密和解密功能，您需要安装在JVM中的全功能JCE（默认情况下不存在）。您可以从Oracle下载“Java加密扩展（JCE）无限强度管辖策略文件”，并按照安装说明进行操作（基本上将JRE lib / security目录中的2个策略文件替换为您下载的那些文件）。

要配置一个对称密钥，只需要设置encrypt.key一个秘密字符串（或者使用一个环境变量ENCRYPT_KEY使其不在纯文本配置文件中）。

```
server: 
  port: 8080
spring:
  cloud:
    config:
      server:
        git:
          uri: https://xxxxx    #自己的git配置仓库
          username:
          password:
encrypt:
  key: cool
```
如果你正在为配置客户端应用程序设置一个远程配置库，它可能包含这样的一个application.yml例如：

* application.yml
```
spring:
  datasource:
    username: dbuser
    password: '{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ'
.properties文件中的加密值不能包含在引号中，否则该值不会被解密：
```

* application.properties
```
spring.datasource.username：dbuser
spring.datasource.password：{cipher} FKSAJDFGYOS8F7GLHAKERGFHLSAJ
```

服务器也暴露/encrypt和/decrypt端点（假设这些将被保护，并且只能被授权代理访问）。如果您正在编辑远程配置文件，则可以使用配置服务器通过发送到/encrypt端点来加密值



最后我们访问 `127.0.0.1:8080/master/laojiao.yml`
即可显示解密后的配置内容
