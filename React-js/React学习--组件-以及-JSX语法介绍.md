### App.js文件分析
```
import React  from 'react';

class App extends React.Component {
  render() {    
    return (
        <div>hello fantj</div>
    );
  }
}

export default App;    # 将App组件导出
```

1. `class App`继承自 `React.Component`，则App就是React的一个组件。

2. `render()`函数决定组件显示什么内容，每当state或者prop发生变化，`render`就会被执行，实时的将数据显示在页面上。当父组件的`render()`被运行，其子组件的`render()`都会被运行。效率很高(虚拟DOM(JS对象)，js生成js对象效率很高，但是生成DOM性能很低)

3. ` export default App；`导出组件

4. 首页`index.js`中引入App组件:`import App from './App';`,所以组件会被显示出来。

>![](https://upload-images.jianshu.io/upload_images/5786888-720c205d8dd978fe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### ES6结构赋值：
```
import  {Component} from 'react'  
等价于
import React from 'react'
const Component = React.Component
```
即上面代码可写成这样：
```
import React ,{ Component,Fragment } from 'react';

class App extends Component {
  render() {
# render函数决定组件显示什么内容，必须被包含在一个大的元素之中。
#如果不希望外层有个div标签，可以使用Fragment占位符来代替。
    return (
        <div>hello fantj</div>  //该div不是html中的div，是jsx语法，需要导入react
        <Fragment><input /></Fragment>#使用Fragment占位符来代替div
    );
  }
}

export default App;
```


### index.js 文件分析
```
import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';

 //<App />是 jsx 语法，使用该语法就必须import react 
ReactDOM.render(<App />, document.getElementById('root')); 

```

1. `ReactDOM`是一个第三方模块，它的render方法可以将一个组件挂载到一个DOM节点上。则在该文件中，组件`<App />`被挂载到`root`节点上了。
>![](https://upload-images.jianshu.io/upload_images/5786888-a69842cdcc6ea031.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### JSX语法

>什么是JSX语法?   
在js语法中，如果要返回一段前端代码，则需要将前端代码打包成字符串，而jsx语法中，不需要将其打包成字符串，直接使用即可。

与此同时，JSX支持自定义创建组件。如:
```
import App from './App';  # 当然，这个开头必须以大写字母开头。
```
`<Fragment>`其实也是组件，是JSX语法
