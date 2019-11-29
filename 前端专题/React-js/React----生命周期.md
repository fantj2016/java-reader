>按照周期类别解释。


### 挂载
##### 1. componentWillMount
>在组件即将挂载到页面之前执行。

```
  componentWillMount() {

  }
```
##### 2. render
>进行页面渲染。
##### 3. componentDidMount
>在组件加载完后执行,执行在render完成之后。

一般用来放ajax代码块。render函数会被反复执行，所以放在这里。只执行一次。(ajax也可以放到构造器里)
```
  componentDidMount() {
   
  }
```
### 数据更新
##### 1. shouldComponentUpdate
>在state数据或者props数据更新之前执行。

一般用来判断哪些数据的改变不需要重新渲染页面，过滤请求，提高性能。

##### 2. componentWillUpdate
>在shouldComponentUpdate之后执行，在render之前执行。
##### 3. render
>在确认上两步执行返回true后执行。
##### 4. componentdidupdate
>在render后执行(组件更新完毕)。

### 卸载
##### componentWillUnmount
>在组件移除的时候被调用。

---
---


### 使用场景


### ajax 发送
安装：
`npm add axios`
导包:
`import axios from 'axios'`
Demo
```
    componentDidMount(){
        axios.get("http://www.fantj.top/cpt/list").then(()=>{
            console.log("success");
        })
    }
```

### 模拟(Mock)api
安装charles
