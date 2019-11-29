以前写SSM项目的时候，项目结构是webapp，所以在代码里./就是相对路径。
但是在resources目录下，这样的写法是

###resources目录项目  
######1.   **./**表示什么
![image.png](http://upload-images.jianshu.io/upload_images/5786888-4db3fd4ef957b8f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![image.png](http://upload-images.jianshu.io/upload_images/5786888-405e749be51de90a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
实验表明, 在resources目录结构下`./`表示项目源码根目录

######2.  **request.getServletContext().getContextPath()**表示什么

 ![image.png](http://upload-images.jianshu.io/upload_images/5786888-6c359cfccc91e8e7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-f220a5455d435350.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)这里我们可以看出它输出的是空，那么我们用这个路径，下载个东西看下它会在哪里![image.png](http://upload-images.jianshu.io/upload_images/5786888-c32f2c20bba98073.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-74472d80fbdc7b62.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
它放在了磁盘根目录下了(我的项目在C盘的code目录里)

######3. WebUtils.getRealPath(servletContext, path)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-dd24a99fc5531cc9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image.png](http://upload-images.jianshu.io/upload_images/5786888-34bd3e9b3dc0b25d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
会发现有个'点'目录，所以我们path里面就别要点了![image.png](http://upload-images.jianshu.io/upload_images/5786888-9a074d10739d9cfb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image.png](http://upload-images.jianshu.io/upload_images/5786888-3bf2d84335d433fb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######4. request.getServletContext().getRealPath("");
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ad10b0545ed93eea.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image.png](http://upload-images.jianshu.io/upload_images/5786888-7d2da3e698c7d3be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7225bb08b4da9120.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image.png](http://upload-images.jianshu.io/upload_images/5786888-23ee8f0d44fc8837.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



######5. 自定义目录
在application.yml里添加
```
web:
  upload-path: ./admin-server/src/main/resources/static/download/
...
spring:
  resources:
    static-locations: classpath:/resources/,classpath:/static/,file:${web.upload-path}
```
在项目里
```
@Value("${web.upload-path}")
    private String path;
```
就可以做指定目录下载上传了。





