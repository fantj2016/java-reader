>先看两个如果，再看第三点，动手能力强的可以用第三点(命令行)来解决，做一步要知道自己做的意义，比如：不要在错误的远程仓库上浪费精力等。所以检查好自己已有的环境。**这里以gitee为例子，别的牌子路数都是一样的**。

### 1. 如果你已经搞乱了
将项目根目录下的`.git`删掉，然后再跟着做2或者3。
### 2. 如果你用的IDEA
IDEA可以利用插件`gitee`来快速的上传项目到gitee上。(github原生支持，不需要下载插件)
##### 1. 安装插件
>setting->plugins->搜索gitee
![](https://upload-images.jianshu.io/upload_images/5786888-ae53058885760309.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-a06064bcc9112d9e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 2. 使用插件
> VCS -> Import into Version Control -> Share Project on Gitee (or Share Project on GitHub)
![](https://upload-images.jianshu.io/upload_images/5786888-06ef95220fd0011b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

然后就是傻瓜式的操作了。如果有错误，自己解决不了，返回第一点，重新来。如果git密码第一次输入错误后不再提示你输入而是一直用错误的密码，看第三点的第4小点的注意事项(`3.4`)。


### 3. 从头开始（命令行上传）
>请按照自己进行到的步骤，结合自己的实际情况来选则继续下面的步骤。假设我的环境中没有`.git`,且我的远程仓库`https://gitee.com/xxx.git`已创建好。

##### 1. 初始化一个空的git本地仓库
`git init`

##### 2. 声明你的身份
```
git config --global user.name "fantj"
git config --global user.email "8440xxx@qq.com"
```
##### 3. 声明你的远程仓库路径
```
git remote add origin https://gitee.com/xxx/xxx.git (你的远程项目地址)
```
##### 查看远程仓库地址
`git remote -v`
应该要显示出你的远程仓库地址，如果不是对应的地址。先删除后添加。如果是正确的则跳过下面的代码。
```
如果结果是正确的则跳过下面的代码。
git remote rm origin
git remote add origin xxxxx.git
```
##### 查看全局配置信息
`git config --global  --list`
```
E:\workspace\go-xxx>git config --global --list
user.email=84407xxx@qq.com
user.name=fantj
```
##### 4. 检测是否成功连接上远程仓库
执行`git fetch`
```
E:\workspace\go-xxx>git fetch
remote: Enumerating objects: 3, done.
remote: Counting objects: 100% (3/3), done.
remote: Compressing objects: 100% (2/2), done.
remote: Total 3 (delta 0), reused 0 (delta 0)
Unpacking objects: 100% (3/3), done.
From https://gitee.com/xxxx/go-xxxx
 * [new branch]      master     -> origin/master
```
**注意**：
如果出现上述，证明成功连接到远程仓库了，没有出现也没关系，证明你本地没有你的gitee账户信息，随便打个命令`git clone http://gitee.xxxx.git`或者`git pull origin master`就会让你输入密码，注意尽量一次性输正确，否则需要去win10 账户下修改(`控制面板->用户账户->管理凭据->寻找修改你的gitee密码`)。

##### 5. 拉取远程仓库
`git pull origin master`
```
E:\workspace\go-xxxx>git pull origin master
From https://gitee.com/xxx/go-xxxx
 * branch            master     -> FETCH_HEAD
```
如果这步有错请检查你的gitee密码是否正确。

##### 6. 准备上传工作
```
git add .
git commit -m "first commit"
git push origin master
```
```
E:\workspace\go-xxx>git push origin master
Enumerating objects: 83, done.
Counting objects: 100% (83/83), done.
Delta compression using up to 4 threads
Compressing objects: 100% (75/75), done.
Writing objects: 100% (82/82), 4.10 MiB | 1.73 MiB/s, done.
Total 82 (delta 6), reused 0 (delta 0)
remote: Powered by Gitee.com
```
出现上面则为上传成功。
