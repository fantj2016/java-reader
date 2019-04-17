本示例在windows环境下执行：
######安装python运行环境
python下载网站：https://www.python.org/downloads/release/python-353/
安装时把 add path 打上对钩，自动添加环境变量。
然后打开cmd，输入`python`
######安装虚拟环境
然后输入`pip install virtualenv`
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ed64e66e182d9a6f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######安装Django
`pip install -i https://pypi.douban.com/simple/ django`
######创建虚拟环境
`virtualenv pythonEnv`
![image.png](http://upload-images.jianshu.io/upload_images/5786888-0890b66abebbf49f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
```
active.bat 进入虚拟环境
deavtivate.bat  退出虚拟环境
```
######安装wrapper方便管理虚拟环境
`pip install virtualenvwrapper-win`
`workon`查看虚拟环境列表
`mkvirtualenv xxx`创建名为xxx的虚拟环境,并进入该环境
```
workon xxx  进入环境
deactivate  退出环境
```
这样，我们就可以进入虚拟环境后pip安装所需要的依赖了。
如果出错，在https://www.lfd.uci.edu/~gohlke/pythonlibs/这个网站找一些依赖文件。
`pip list`查看pip安装列表
