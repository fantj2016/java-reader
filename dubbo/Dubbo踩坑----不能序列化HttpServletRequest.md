### 1. 报错
这种错误在同一dubbo端口下个多个相同服务中是很难发现错误具体信息的，报错虽然也是`fail invoke method`但是没有详细信息（你靠猜是猜不出来哪里出问题的），所以最好暂时关闭其它服务，或者申请不同的dubbo端口测试，让错误更准确的暴露出来。

##### 1.1 Post请求报错：

![](https://upload-images.jianshu.io/upload_images/5786888-9d65e26b7375ffdf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 1.2 控制台报错：
```
java.lang.IllegalStateException: 
Serialized class org.springframework.web.multipart.support
.StandardMultipartHttpServletRequest must implement java.io.Serializable
```
### 2. 原因

查找了一下这个类发现它确实是没有序列化
```
public class StandardMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest {...}
```

### 3. 解决办法
之所以报这个错是因为我在web层将HttpServletRequest进行传值到注册的服务：
![](https://upload-images.jianshu.io/upload_images/5786888-bb4be85e7a28ea8f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 所以解决方法也很简单：
1. 将整个业务处理都写在web服务中。（不规范）
2. 将有用信息提取，转换成可序列化的类型。

其实在HttpServletRequest中对我有用的信息是获取到容器的一个路径，所以将其提取，并修改传参。
![](https://upload-images.jianshu.io/upload_images/5786888-fd71a9ff61d19586.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

大功告成。
