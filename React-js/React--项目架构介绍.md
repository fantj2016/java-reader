### 整体结构

>![](https://upload-images.jianshu.io/upload_images/5786888-9cc758b281fb9849.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 1.根目录
##### 1.1. package-lock.json

项目依赖的安装包和版本号

##### 1.2. README.md
项目说明文档

##### 1.3. package.json
```
{
  "name": "helloworld",     项目名
  "version": "0.1.0",        版本号
  "private": true,        是否私有
  "dependencies": {        依赖
    "react": "^16.5.2",
    "react-dom": "^16.5.2",
    "react-scripts": "2.0.5"
  },
  "scripts": {        指令：
    "start": "react-scripts start",     (npm start 的实际调用)
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": "react-app"
  },
  "browserslist": [
    ">0.2%",
    "not dead",
    "not ie <= 11",
    "not op_mini all"
  ]
}
```
### 2. node_modules目录
项目依赖的第三方的包和模块。

### 3. public目录
>![](https://upload-images.jianshu.io/upload_images/5786888-d6da16d59fc6b7c3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3.1 favicon.ico
就是网站的icon

##### 3.2 index.html
```
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <link rel="shortcut icon" href="%PUBLIC_URL%/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="theme-color" content="#000000">

    <link rel="manifest" href="%PUBLIC_URL%/manifest.json">
    <title>React App</title>
  </head>
  <body>
    <noscript>
      You need to enable JavaScript to run this app.
    </noscript>
    <div id="root"></div>
  </body>
</html>
```
这就是访问http://localhost:3000/ 所展示的页面。

##### 3.3 manifest.json
>PWA(用来像app一样将网页缓存到本地)的配置
```
{
  "short_name": "React App",      # 快捷方式名字
  "name": "Create React App Sample",
  "icons": [     # 快捷方式的icon
    {
      "src": "favicon.ico",
      "sizes": "64x64 32x32 24x24 16x16",
      "type": "image/x-icon"
    }
  ],
  "start_url": ".",     # 快捷方式网址
  "display": "standalone",
  "theme_color": "#000000",
  "background_color": "#ffffff"
}
```


### 4. src目录
>![](https://upload-images.jianshu.io/upload_images/5786888-a09abe2b9244397c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##### 4.1 index.js 
>整个程序的入口.
```
import React from 'react';    #导入包，依赖在package.json文件中
import ReactDOM from 'react-dom';
import './index.css';   # 引入css，all in js
import App from './App';    #自动将APP.js导入
import * as serviceWorker from './serviceWorker';    #PWA，将网页存储在浏览器中

ReactDOM.render(<App />, document.getElementById('root'));

serviceWorker.unregister();
```

##### 4.2 App.test.js
>自动化测试文件。
