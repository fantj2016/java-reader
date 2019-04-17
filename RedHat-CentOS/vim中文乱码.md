###1.安装依赖库
`yum install cjkun* wqy* -y`
###2.修改vim配置
`vim /etc/vimrc`
在文件头部添加
```
   set fileencodings=ucs-bom,utf-8,latin1
   set termencoding=utf-8
   set fileformats=unix
   set encoding=prc
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-13c370b47a8f36df.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
