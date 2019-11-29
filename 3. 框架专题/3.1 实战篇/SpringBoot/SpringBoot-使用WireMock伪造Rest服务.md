>WireMock 是基于 HTTP 的模拟器。它具备 HTTP 响应存根、请求验证、代理/拦截、记录和回放功能。
当开发人员的开发进度不一致时，可以依赖 WireMock 构建的接口，模拟不同请求与响应，从而避某一模块的开发进度。

官方文档：http://wiremock.org/docs/running-standalone/


###1. 搭建wireMock单机服务

######1.1 下载jar包
服务jar包下载：http://repo1.maven.org/maven2/com/github/tomakehurst/wiremock-standalone/2.14.0/wiremock-standalone-2.14.0.jar

######1.2 启动jar
`java -jar wiremock-standalone-2.14.0.jar  --port 9000`   
我在这里用9000端口启动
![](https://upload-images.jianshu.io/upload_images/5786888-e1be843e5ebfd162.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

好了，看到上面的图案说明服务就搭建好了。
### 2. 向服务里注册Rest服务

######2.1 导入依赖
```
		<dependency>
			<groupId>com.github.tomakehurst</groupId>
			<artifactId>wiremock</artifactId>
		</dependency>
```

######2.2 写一个简单的模拟Rest
```
/**
 * Created by Fant.J.
 */
public class MockServer {
    public static void main(String[] args) {
        //通过端口连接服务
        WireMock.configureFor(9000);
        //清空之前的配置
        WireMock.removeAllMappings();

        //get请求
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/1"))
                .willReturn(WireMock.aResponse()
                 //body里面写 json
                .withBody("{\"username\":FantJ}")
                 //返回状态码
                .withStatus(200)));

    }
}

```
运行这个main方法。
![](https://upload-images.jianshu.io/upload_images/5786888-143647387324ae33.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
然后访问 http://127.0.0.1:9000/user/1   
![](https://upload-images.jianshu.io/upload_images/5786888-afa9cde1eea7342e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 企业级开发封装

```
/**
 * Created by Fant.J.
 */
public class MockServer {
    public static void main(String[] args) throws IOException {
        //通过端口连接服务
        WireMock.configureFor(9000);
        //清空之前的配置
        WireMock.removeAllMappings();

        //调用 封装方法
        mock("/user/2","user");


    }

    private static void mock(String url, String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource("/wiremock/"+filename+".txt");
        String content = FileUtil.readAsString(resource.getFile());

        //get请求
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(url))
                .willReturn(WireMock.aResponse()
                        //body里面写 json
                        .withBody(content)
                        //返回状态码
                        .withStatus(200)));
    }
}

```
其中，user.txt文件在这里
![](https://upload-images.jianshu.io/upload_images/5786888-7b7dc4394136194c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
文本内容：
![](https://upload-images.jianshu.io/upload_images/5786888-d432b5c8e9fc6efe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


然后我们运行程序，访问http://127.0.0.1:9000/user/2

![](https://upload-images.jianshu.io/upload_images/5786888-c18e0f6ead50a69b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)





####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

######底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)


