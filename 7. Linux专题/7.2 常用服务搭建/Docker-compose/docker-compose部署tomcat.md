### docker-compose.yml
```
version: '3'
services:
  tomcat:
    restart: always
    image: tomcat
    container_name: tomcat
    ports:
      - 8080:8080
    volumes:
      - /home/fantj/app/tomcat/webapps:/usr/local/tomcat/webapps/
    environment:
      TZ: Asia/Shanghai
```
