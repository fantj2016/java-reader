### uml是什么
>**Unified Modeling Language (UML)**又称统一建模语言或[标准建模语言](https://baike.baidu.com/item/%E6%A0%87%E5%87%86%E5%BB%BA%E6%A8%A1%E8%AF%AD%E8%A8%80)，是始于1997年一个[OMG](https://baike.baidu.com/item/OMG/3041465)标准，它是一个支持模型化和软件系统开发的图形化语言，为软件开发的所有阶段提供模型化和可视化支持，包括由需求分析到规格，到构造和配置。 面向对象的分析与设计(OOA&D，OOAD)方法的发展在80年代末至90年代中出现了一个高潮，[UML](https://baike.baidu.com/item/UML)是这个高潮的产物。它不仅统一了Booch、Rumbaugh和Jacobson的表示方法，而且对其作了进一步的发展，并最终统一为大众所接受的[标准建模语言](https://baike.baidu.com/item/%E6%A0%87%E5%87%86%E5%BB%BA%E6%A8%A1%E8%AF%AD%E8%A8%80)。

>国内的大多文章千篇一律，涉及的也不全面，仅仅是常用的几个，不一定满足所有的生产实践。


### 设计前准备
那接下来我们直接介绍用法，此次我是借用一个在线uml设计网站：https://www.processon.com，它不止可以设计uml，别的我在这篇文章不做介绍。

### UML类表示法
>类是属性和方法的封装，每个属性都有一个类型，每个方法都有一个签名。

![](https://upload-images.jianshu.io/upload_images/5786888-11efcf0c0f6350a2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 类可见度
>可以看到属性和方法前有一些符号标记`+ - ...`，他们代表什么呢?
```
+: 表示public权限
-: 表示private权限
#: 表示protected权限
~: 表示包权限
下划线: 表示静态static属性
```
