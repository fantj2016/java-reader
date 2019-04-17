>本文已授权"后端技术精选"独家发布。

>Notepad++功能比 Windows 中的 Notepad(记事本)强大，除了可以用来制作一般的纯文字说明文件，也十分适合编写计算机程序代码。Notepad++ 不仅有语法高亮度显示，也有语法折叠功能，并且支持宏以及扩充基本功能的外挂模组。中文版下载地址：链接: https://pan.baidu.com/s/14a3va-9HCMJ_DWNFSEg5Bw 提取码: pn52

### 1. 基本功能
>1.  支持27 种语法高亮度显示
>2. 可自动检测文件类型，根据关键字显示节点
>3. 可打开双窗口，在分窗口中又可打开多个子窗口，允许快捷切换全屏显示模式(F11)，支持鼠标滚轮改变文档显示比例

### 2. 自定义功能
##### 插件扩展
官方插件下载地址：http://docs.notepad-plus-plus.org/index.php/Plugin_Central

安装方式：将下载并解压后的dll文件放到plugin文件下，然后在设置->导入->导入插件。

推荐compare工具，可以对比两个文件的不同(目前官方只有32位的该插件，64位的同学请转移网盘:https://pan.baidu.com/s/1cZGjM7IQEmaiiCILsCmc9A 提取码: 24ct)。
>插件使用截图：![](https://upload-images.jianshu.io/upload_images/5786888-f01987139b7c9e7b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 主题扩展

>方式同上，设置->导入->导入主题。


#### md5工具
>打开方式：工具->md5
![](https://upload-images.jianshu.io/upload_images/5786888-938218d9da9e9901.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 文件夹工作区使用
>打开方式：视图->文件夹工作区
![](https://upload-images.jianshu.io/upload_images/5786888-61e2f6f276d40ba9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 主题更换
>设置->语言格式设置
![](https://upload-images.jianshu.io/upload_images/5786888-5be8efa380668ce7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-6f973986ab97f822.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-c955484388819836.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 语言选择
>建议在使用该文本编辑器处理文件时，先对应选择文件所属语言，notepad++ 会相应的改变格式。选择方式：语言->J->JAVA
![](https://upload-images.jianshu.io/upload_images/5786888-a33a8563be88d6de.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 宏定义
>我觉得打代码没必要使用。它会重复的干一件事情。

#### 运行
>html文件可以直接在 `运行->launch in Chrome` 跳转显示。


#### 缩进参考线
>![](https://upload-images.jianshu.io/upload_images/5786888-0e5f7a8459977e5b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 显示所有隐藏字符
>![](https://upload-images.jianshu.io/upload_images/5786888-bb8a02b6589f50b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 同步滚动
>该模式是在多个文件下执行。可以保持两文件同步滚动。
![](https://upload-images.jianshu.io/upload_images/5786888-8e2ad926346e50d1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 查找替换
>这个最常用了，Ctrl+f，notepad支持全文替换和正则匹配：
![](https://upload-images.jianshu.io/upload_images/5786888-f1f9e28b3f455888.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 列编辑
>多谢名字为`[东方乌云](https://www.jianshu.com/u/500ef428a2d0)
`的这位朋友提醒，列编辑就是一次性操作一纵列，目前流行的IDA都会有该功能。我一般创建实体类的时候经常用到，因为`private String xxx`是经常看到的在实体类中。

效果图:
>![](https://upload-images.jianshu.io/upload_images/5786888-73a1a9ab08eac86b.gif?imageMogr2/auto-orient/strip)

用法：

>![](https://upload-images.jianshu.io/upload_images/5786888-99545e0ba0912de9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我一般是按住alt 然后鼠标按住往下拉一竖列。

额外用法：
>Alt+c (或者编辑->列编辑)触发
![](https://upload-images.jianshu.io/upload_images/5786888-8a0d4f437e28ed1f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
插入数字还有点意思，插入文本直接粘贴其实就可以。




我基本用到的就是这些。虽然不会用它去开发，但是偶然会用到，熟悉一个工具，也许会多一条处理思路，能够高效在视图上方便开发和寻找处理解决问题。

