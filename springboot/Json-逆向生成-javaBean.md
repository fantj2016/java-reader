我的开发环境：IDEA+maven+jdk1.8

### 1.下载idea插件GsonFormat
![](https://upload-images.jianshu.io/upload_images/5786888-019f2e246983b246.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2. 添加依赖
该插件支持的json序列化工具：
![](https://upload-images.jianshu.io/upload_images/5786888-a77a8cc1f39551d2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我在这里用阿里的fastJson 依赖，想用gson、jackson的都可以，插件都支持
```
       <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.39</version>
        </dependency>
```

### 3. 设置json序列化反序列化工具类型

##### 3.1 开启gsonFormat
我们在任意的一个java类里，按快捷键`alt+ins`。我们就可以看到GsonFormat。或者直接快捷键`Alt+S`。
![](https://upload-images.jianshu.io/upload_images/5786888-1fc61f26160e238b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
##### 3.2设置
包含一些bean命名的设置，相信大家一看都懂，这里就不多啰嗦。
![](https://upload-images.jianshu.io/upload_images/5786888-9e2e99d0e6eba3f0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 4. 黏贴json串
看到那个框框了吧，对，就把json黏贴到这里。然后有洁癖的人想要格式化一下，看下面的图。
提醒：如果json格式不正确的话，form是不会成功的。
![](https://upload-images.jianshu.io/upload_images/5786888-f854363dd0bb5093.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 5. 解析json
点击OK就行了。
然后会弹出个框框，让你选择需要反序列化生成哪个 javabean。这个就各求所需了。它生成的是static类，我们把他提取出去就行了。


