>React特点：声明式开发(减少DOM代码量)，可与其他框架并存，组件化，单向数据流，视图层框架，函数式编程。


### 1. 下载node.js
https://nodejs.org/zh-cn/download/

一路next就好。
更换国内镜像：
```
npm install -g cnpm --registry=https://registry.npm.taobao.org
```
则以后使用`cnpm`代替`npm`即可。cnpm介绍：https://github.com/cnpm/cnpm
```
C:\Users\DELL>npm -v
6.4.1

C:\Users\DELL>node -v
v8.12.0
```
### 2. 安装create-react-app
```
C:\Users\DELL>npm  install -g create-react-app
C:\Users\DELL\AppData\Roaming\npm\create-react-app -> C:\Users\DELL\AppData\Roaming\npm\node_modules\create-react-app\index.js
+ create-react-app@2.0.4
added 63 packages from 20 contributors in 16.052s
```
注意：mac 下需要`sudo chmod -R 777 /usr/local/lib`修改文件权限，否则会报错。
### 3. 创建一个项目
```
C:\myReactWorkspace>create-react-app helloworld

Creating a new React app in C:\myReactWorkspace\helloworld.
......
Success! Created helloworld at C:\myReactWorkspace\helloworld
Inside that directory, you can run several commands:

  npm start
    Starts the development server.

  npm run build
    Bundles the app into static files for production.

  npm test
    Starts the test runner.

  npm run eject
    Removes this tool and copies build dependencies, configuration files
    and scripts into the app directory. If you do this, you can’t go back!

We suggest that you begin by typing:

  cd helloworld
  npm start

Happy hacking!
```

### 4. 启动项目
```
C:\myReactWorkspace>cd helloworld

C:\myReactWorkspace\helloworld>npm start

> helloworld@0.1.0 start C:\myReactWorkspace\helloworld
> react-scripts start
Starting the development server...
Compiled successfully!

You can now view helloworld in the browser.

  Local:            http://localhost:3000/
  On Your Network:  http://192.168.0.165:3000/

Note that the development build is not optimized.
To create a production build, use npm run build.

```

>![成功截图](https://upload-images.jianshu.io/upload_images/5786888-d1f3793735075643.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
到这里脚手架就搭建完成了。
