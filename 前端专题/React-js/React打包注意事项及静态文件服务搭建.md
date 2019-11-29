### 1. 打包完后index.html的路径问题
>如果不特意配置的话，默认打包完后打开index.html页面是错误的，因为他不是一个完整的页面，所有依赖路径获取的文件(.js||.css等)都not found .

打包前在package.json文件里添加一个配置：`"homepage":".",`，打包之后资源文件路径前面都会加上一个点，然后在执行`npm run build`
```
{
  "name": "xxx",
  "version": "0.1.0",
  "private": true,
  "homepage":".",
  "dependencies": { ....
  }
```
打包过程及执行成功：
```
> xxx@0.1.0 build /home/fantj/Data/WebstormProjects/xxx
> react-scripts build

Creating an optimized production build...
Compiled with warnings.

./src/pages/login/store/actionCreators.js
  Line 2:  'fromJS' is defined but never used  no-unused-vars

Search for the keywords to learn more about each warning.
To ignore, add // eslint-disable-next-line to the line before.

File sizes after gzip:

  86.58 KB  build/static/js/1.37179901.chunk.js
  9.62 KB   build/static/js/main.a132d79f.chunk.js
  763 B     build/static/js/runtime~main.229c360f.js

The project was built assuming it is hosted at the server root.
You can control this with the homepage field in your package.json.
For example, add this to build it for GitHub Pages:

  "homepage" : "http://myname.github.io/myapp",

The build folder is ready to be deployed.
You may serve it with a static server:

  npm install -g serve
  serve -s build

Find out more about deployment here:

  http://bit.ly/CRA-deploy
```

### 2. 静态的服务器搭建
##### 安装serve
`sudo npm install serve`
完事后注意设置node环境变量，不然执行serve时会报错` bash: serve: command not found`,这是因为你本地没有node的环境变量，需要手动添加一下，提示:`npm prefix -g`可以查看node安装路径。
```
[fantj@localhost xxx]$ serve -v
10.1.1
```

##### 启动服务
>By default, serve will listen on 0.0.0.0:5000 and serve the current working directory on that address. 默认启动5000端口而且暴露当前的目录。

###### 单单执行`serve`:
>会将本地目录暴露到服务中。
```
[fantj@localhost ~]$ serve

   ┌──────────────────────────────────────────────────┐
   │                                                  │
   │   Serving!                                       │
   │                                                  │
   │   - Local:            http://localhost:45265     │
   │   - On Your Network:  http://127.0.0.1:45265     │
   │                                                  │
   │   This port was picked because 5000 is in use.   │
   │                                                  │
   │   Copied local address to clipboard!             │
   │                                                  │
   └──────────────────────────────────────────────────┘
```

**访问` http://localhost:45265`**:
![](https://upload-images.jianshu.io/upload_images/5786888-e3c5ea34754bd36e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这是我本地的目录，局域网可以访问～用途自己脑补～
###### 如果需要构建该目录下的指定项目:
`serve -s build` 
```
[fantj@localhost xxx]$ serve -s build/

   ┌───────────────────────────────────────────────┐
   │                                               │
   │   Serving!                                    │
   │                                               │
   │   - Local:            http://localhost:5000   │
   │   - On Your Network:  http://127.0.0.1:5000   │
   │                                               │
   │   Copied local address to clipboard!          │
   │                                               │
   └───────────────────────────────────────────────┘
```
然后访问` http://localhost:5000` 则会跳转到`index.html` 页面。官方是这么介绍的`Rewrite all not-found requests to index.html`,意思是不管index.html是否存在，它都会去映射该文件。

##### 拓展
>其它serve的可选参数。

```
 -v， - version显示当前版本的服务
```
```
 -d， - debug显示调试信息
```
```
  -c， - config指定`serve.json`的自定义路径
```
```
  -n， - no-clipboard不要将本地地址复制到剪贴板
```
```
-l , 指定要监听的URI端口

[fantj@localhost xxx]$ serve -l 55555
   ┌────────────────────────────────────────────────┐
   │                                                │
   │   Serving!                                     │
   │                                                │
   │   - Local:            http://localhost:55555   │
   │   - On Your Network:  http://127.0.0.1:55555   │
   │                                                │
   │   Copied local address to clipboard!           │
   │                                                │
   └────────────────────────────────────────────────┘

```
