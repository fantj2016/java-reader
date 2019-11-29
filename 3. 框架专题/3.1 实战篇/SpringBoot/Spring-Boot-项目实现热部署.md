### 方法一：devtools

Pom.xml中直接添加依赖即可：

```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>provided</scope>
      <!--optional我没弄明白，都说必须为true，但我测试true，false，不加都可以-->
      <optional>true</optional>
    </dependency>
```

通过项目主程序入口启动即可，改动以后重新**编译**就好。

### 方法二：springloaded

Pom.xml中直接在spring-boot插件中添加依赖即可：

```
      <plugin>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-maven-plugin</artifactId>
          <dependencies>
            <!-- spring热部署 -->
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>springloaded</artifactId>
              <version>1.2.6.RELEASE</version>
            </dependency>
          </dependencies>
          <configuration>
            <mainClass>cn.springboot.Mainspringboot</mainClass>
          </configuration>
        </plugin>
```
