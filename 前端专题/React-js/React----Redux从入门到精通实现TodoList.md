>Redux 是 JavaScript 状态容器，提供可预测化的状态管理。通俗的讲，它把所有的数据放到store进行管理，一个组件修改了store的内容，其他的组件就能感知到store的变化，从而实现组件内数据传递。它是一个纯函数，给个固定的输入，就会有固定的输出。

>Redux 由 [Flux](http://facebook.github.io/flux/) 演变而来，但受 [Elm](http://elm-lang.org/guide/architecture) 的启发，避开了 Flux 的复杂性。 不管你有没有使用过它们，只需几分钟就能上手 Redux。


### 工作流程：
`Component` --action-->`Reducer`--handle-->`Store`--notify-->`Component`

### 安装
`npm install --save redux`

### 创建store

##### 1. 创建store
>![](https://upload-images.jianshu.io/upload_images/5786888-18b7b1e27e389f56.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### src/store/index.js
>创建store，并传入reducer。
```
import { createStore } from 'redux';   
import reducer from './reducer'

const store = createStore(reducer);

export default store;
```

1. 引入store依赖：`import { createStore } from 'redux';`
2. 把reducer传值`const store = createStore(reducer);`
3. 导出 store： `export default store;`
###### src/store/reducer.js
>创建reducer。
```
const  defaultState = {
    inputValue: '',
    list: []
}
//state 是整个数据
export default (state = defaultState,action) => {
    return state;
}
```

###### 在TodoList.js中引用
```
import React, { Component } from 'react'
import 'antd/dist/antd.css';
import { Input, List, Button } from 'antd';
import store from './store/'


class TodoList extends Component{

constructor(props){
    super(props);
    this.state = store.getState();
}
    render(){
        return (
            <div>
                <div style={{marginTop: '10px', marginLeft: '10px'}}>
                    <Input value={this.state.inputValue} name='value' style={{width: `300px`, marginRight: '10px' }} />
                    <Button >Default</Button>
                    <List
                        style={{marginTop: '10px', width: '300px'}}
                        bordered
                        dataSource={this.state.list}
                        renderItem={item => (<List.Item >{item}</List.Item>)}
                    />
                </div>
            </div>
        )
    }
}

export default TodoList;
```
1. 引入store:`import store from './store/'`
2. 加入全局变量：
```
constructor(props){
    super(props);
    this.state = store.getState();
}
```
3. 调用数据：`dataSource={this.state.list}`


>效果图：
>![](https://upload-images.jianshu.io/upload_images/5786888-a9a2546f724f301e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### Action 和 Reducer 的编写

##### 1. 安装插件Redux Devtools
注意安装完需要重启chrome才生效。用它来调试redux很方便。而且需要在redux中设置对其支持。
```
import {createStore, compose} from 'redux'
import reducer from "./reducer";

const composeEnhancers =
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({}) : compose;

const store  = createStore(reducer, composeEnhancers());

export default store;
```

>![](https://upload-images.jianshu.io/upload_images/5786888-20f26bce76550ca1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>![查看state数据](https://upload-images.jianshu.io/upload_images/5786888-a086f229f06f99df.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 2. 编写action
1. 首先在`Input`框中添加点击事件
```
onChange={this.handleInputChange}
```
2. 处理该点击事件，并编写action，发送至reducer
```
    handleInputChange(e){
        const  action = {
            type: 'change_input_value',
            value: e.target.value
        }
        // 发送数据给reduce，固定写法
        store.dispatch(action);
    }
```
##### 3. 处理该action，编写reducer
>打开 reducer.js 文件.
```
export default (state = defaultState,action) => {
    console.log(state,action);
    return state;
}
```
当我们在input框里输入内容时，console中就会打印一些信息。

>![](https://upload-images.jianshu.io/upload_images/5786888-af0de28758a5a289.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

那如何对state进行修改呢，reducer只可以接收state，但是不能修改state，我们可以重新new一个state并赋值来达到目的，代码如下：
```
export default (state = defaultState,action) => {
    if (action.type === 'change_input_value') {
        const newState = JSON.parse(JSON.stringify(state));
        newState.inputValue = action.value;
        return newState;
    }
    console.log(state,action);
    return state;
}
```

但是又有一个问题，reducer返回数据给store，页面并不会做同步更新，所以需要写一个store的订阅函数subscribe。
```
    this.handleStoreChange = this.handleStoreChange.bind(this);
    store.subscribe(this.handleStoreChange);
```
```
    handleStoreChange(){
        this.setState(store.getState);
    }
```
其实我们在进行`this.setState(store.getState);`就可以将数据保存到store中。到这里页面和store就可以实现同步更新了。

##### 4. 点击button，使数据加入到store中的list
1. 添加事件
```
<Button onClick={this.handleButtonClick}>提交</Button>

并在构造器中进行绑定:
this.handleButtonClick = this.handleButtonClick.bind(this);
```
编写action：
```
    handleButtonClick(){
        // 创建action
        const action={
            type: 'add_todo_item',
        };
        store.dispatch(action);
    }
```
编写reducer：
```
    if (action.type === 'add_todo_item') {
        const newState = JSON.parse(JSON.stringify(state));
        newState.list.push(newState.inputValue);
        newState.inputValue = '';
        return newState;
    }
```
这样，我们在点击button时，便会将数据存放到store的list中。

##### 5. todolist 的删除实现 
1. 修改遍历list的代码：
```
                    <List
                        style={{marginTop: '10px', width: '300px'}}
                        bordered
                        dataSource={this.state.list}
                        renderItem={(item, index) => (<List.Item onClick={this.handleItemDel.bind(this,index)}>{item}</List.Item>)}
                    />
```
2. 编写action
```
    handleItemDel(index){
        const action = {
            type: 'del_todo_item',
            index: index
        }
        store.dispatch(action);
        alert(index);
    }
```
3. 编写reducer
```
    if (action.type === 'del_todo_item') {
        const newState = JSON.parse(JSON.stringify(state));
        newState.list.splice(action.index,1);
        return newState;
    }
```
这样，基于redux实现的TodoList就实现了。


### React -- action type的拆分
>在java项目中，我们往往用一个常量类来将常量做统一管理，react中也一样。
>![](https://upload-images.jianshu.io/upload_images/5786888-f944e2288df6078d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### actionTypes.js
```
export const CHANGE_INPUT_VALUE = 'change_input_value';
export const ADD_TODO_ITEM = 'add_todo_item';
export const DEL_TODO_ITEM = 'del_todo_item';
```

###### 引入
`import {CHANGE_INPUT_VALUE, ADD_TODO_ITEM, DEL_TODO_ITEM} from './store/actionTypes`

###### 使用
```

    handleButtonClick(){
        // 创建action
        const action={
            type: ADD_TODO_ITEM,
        };
        store.dispatch(action);
    }
    handleItemDel(index){
        const action = {
            type: DEL_TODO_ITEM,
            index: index
        }
        store.dispatch(action);
        alert(index);
    }
```
同理reducer中也一样用常量将字符串代替。

### action统一管理
>我们会发现，action也是一个常量属性，那将action也做一个js来管理岂不是更方便。
![](https://upload-images.jianshu.io/upload_images/5786888-c368ed56604b2efe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### actionCreators.js
```
import { CHANGE_INPUT_VALUE, DEL_TODO_ITEM, ADD_TODO_ITEM} from './actionTypes'

export const getInputChangeAction = (value)=> ({
    type: CHANGE_INPUT_VALUE,
    value: value
});
export const getAddItemAction = (value)=> ({
    type: ADD_TODO_ITEM
});
export const getDelItemAction= (index)=> ({
    type: DEL_TODO_ITEM,
    index: index
});
```
我们将action都封装成一个方法函数，然后将常量的调用也写进creators，在业务的js文件中，就不需要调用和显示常量，而只需要调用creator中的方法即可。
```
    handleItemDel(index){
        const action = getDelItemAction(index);
        store.dispatch(action);
        alert(index);
    }
```


### 拆分UI组件和容器组件
>UI组件(无状态组件)负责渲染，容器组件负责处理逻辑。

用到一下几个知识点：
1. 父子组件传值
2. 子组件如何调用父组件带参数的方法`((index)=>{return method()})`

##### 1. 创建子组件
```
import React , { Component } from 'react';
import { Input, List, Button, message } from 'antd';

class TodoListUI extends Component{
    render(){
        return(
            <div>
                <div style={{marginTop: '10px', marginLeft: '10px'}}>
                    <Input
                        onChange={this.props.handleInputChange}
                        value={this.props.inputValue} name='value' style={{width: `300px`, marginRight: '10px' }} />
                    <Button onClick={this.props.handleButtonClick}>提交</Button>
                    <List
                        style={{marginTop: '10px', width: '300px'}}
                        bordered
                        dataSource={this.props.list}
                        renderItem={(item, index) => (<List.Item onClick={ (index)=>{
                           return this.props.handleItemDel(index)
                        }
                        }>{item}</List.Item>)}
                    />
                </div>
            </div>
        )
    }
}
export default TodoListUI;
```
其中，
1. `onClick={ (index)=>{
                           return this.props.handleItemDel(index)
                        }`是子组件调用父组件中带参数的方法。
2. `this.props.xxx`则是调用父组件中参数。那父组件也需要将参数传给子组件。所以父组件中`render()`写法:
3. 注意父组件中每个方法都在构造器中进行`bind(this)`.
##### 2. 修改父组件render()方法
```
    render(){
        return (
         <TodoListUI
            inputValue={this.state.inputValue}
            list={this.state.list}
            handleInputChange={this.handleInputChange}
            handleButtonClick={this.handleButtonClick}
            handleItemDel={this.handleItemDel}
         />
        )
    }
```
当然不要忘了在父组件中对其引用：`import  TodoListUI  from './TodoListUI'`

##### 3. 用无状态组件对子组件做优化
>无状态组件，实质上就是一个函数，相当于一个仅有render功能的一个子组件。但性能要高于子组件，因为子组件是个类，类就有生命周期，垃圾回收等因素，函数没有，代码如下：

```
import React , { Component } from 'react';
import { Input, List, Button, message } from 'antd';

const TodoListUI = (props)=> {
    return (
        <div>
            <div style={{marginTop: '10px', marginLeft: '10px'}}>
                <Input
                    onChange={props.handleInputChange}
                    value={props.inputValue} name='value' style={{width: `300px`, marginRight: '10px' }} />
                <Button onClick={props.handleButtonClick}>提交</Button>
                <List
                    style={{marginTop: '10px', width: '300px'}}
                    bordered
                    dataSource={props.list}
                    renderItem={(item, index) => (<List.Item onClick={ (index)=>{
                        return props.handleItemDel(index)
                    }
                    }>{item}</List.Item>)}
                />
            </div>
        </div>
    )
};
export default TodoListUI;
```
注意：该函数接收传参`props`,函数体内将`this.props.xxx`替换成`props.xxx`


### Redux中发送异步请求(axios)
>还是和之前一样，我们将这部分代码写到`componentDidMount`这个生命周期函数中。

假设我现在已经有服务`/list`，在TodoList.js中代码为：
##### 1. TodoList.js
```
    componentDidMount(){
         axios.get('/list').then((res)=>{
             const data  = res.data;
             const action = initListAction(data);
             store.dispatch(action);
             console.log(action);
         })
    }
```
##### 2. 往actionCreators.js添加事件管理
```
export const initListAction = (data)=>({
    type: INIT_LIST_ACTION,
    data
});
```
##### 3. 往actionTypes.js中添加常量
```
export const INIT_LIST_ACTION = 'init_list_action';
```
##### 4. 在reducer.js中处理事件
```
    if (action.type === INIT_LIST_ACTION){
        const newState  = JSON.parse(JSON.stringify(state));
        newState.list = action.data;
        return newState;
    }
```
完成。

### 使用Redux-thunk完善异步请求
>redux-thunk 是一个比较流行的 redux 异步 action 中间件。它使得store.dispatch()的调用对象可以是个函数，而不仅限制于对象。
git address ：https://github.com/reduxjs/redux-thunk

如果直接将ajax请求放在TodoList.js 文件中，难免显得该js太臃肿，维护也很不方便，所以thunk 使得 ajax 可以在actionCreators中得以执行。
##### 配置：/store/index.js
>首先要在store模块中进行配置，因为thunk中间件是介于action和reducer间的。
```
import { createStore, applyMiddleware, compose } from 'redux';
import thunk from 'redux-thunk';
import reducer from './reducer'

const composeEnhancers =
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({}) : compose;

const enhancer = composeEnhancers(
    applyMiddleware(thunk)
);

const store = createStore(reducer, enhancer);

export default store;
```
##### 修改1：TodoList.js
>修改componentDidMount 代码为：
```
    componentDidMount(){
        const action = getTodoList();
        store.dispatch(action);
    }
```
`store.dispatch(action);`会触发store调用该函数方法。而且，调用的时候回传参`dispatch`，函数接收到这个参数便可以调用其`dispatch(action)`方法发给reducer处理。

##### 修改2：actionCreators.js
>新增函数方法：
```
export const getTodoList=()=>{
    return (dispatch)=>{
             axios.get('/list').then((res)=>{
                const data  = res.data;
                const action = initListAction(data);
                dispatch(action);
                console.log(action);
         })
    }
}
```
1. 接收`dispatch`参数。
2. 声明`action`事件。
3. 调用`dispatch`让`reducer`处理事件。
4. 导包啥的就不说了。



### 用react-redux再次改写TodoList功能(成熟版)
##### 1. 修改入口文件index.js
```
import React from 'react';
import ReactDOM from 'react-dom';
import TodoList from './TodoList'
import {Provider} from "react-redux";
import store from "./store";

const Test = (
    <Provider store={store}>
        <TodoList/>
    </Provider>
);
ReactDOM.render(Test,document.getElementById('root'));
```
1. `Provider`组件是`react-redux`提供的组件，用来连接`store`和组件，属性`store`表示，该标签内的所有组件都可以使用该`store`.如何连接请看下面：

##### 2. 修改TodoList.js
```
import React  from 'react'
import {connect} from 'react-redux'
import 'antd/dist/antd.css';
import {
    getInputChangeAction,
    getAddItemAction,
    getDelItemAction,
    getTodoList
} from './store/actionCreators'
import {Button, Input, List} from "antd";


const TodoList = (props) => {
    const { inputValue, handleInputChange, handleButtonClick, list, handleItemDel } = props;
    return (
        <div>
            <div style={{marginTop: '10px', marginLeft: '10px'}}>
                <Input
                    onChange={handleInputChange}
                    value={inputValue} name='value' style={{width: `300px`, marginRight: '10px'}}/>
                <Button onClick={handleButtonClick}>提交</Button>
                <List
                    style={{marginTop: '10px', width: '300px'}}
                    bordered
                    dataSource={list}
                    renderItem={(item, index) => (<List.Item onClick={() => {
                        return handleItemDel(index)
                    }}>{item},{index}</List.Item>)}
                />
            </div>
        </div>
    )
};

const mapStateToProps = (state) => {
    return {
        inputValue: state.inputValue,
        list: state.list
    }
};
const mapDispatchToProps = (dispatch) => {
    return {

        handleInputChange(e) {
            const action = getInputChangeAction(e.target.value);
            // 发送数据给reduce
            dispatch(action);
        },
        handleButtonClick() {
            // 创建action
            const action = getAddItemAction();
            dispatch(action);
        },
        handleItemDel(index) {
            const action = getDelItemAction(index);
            dispatch(action);
            // alert(index);
            console.log(index)
        },
        componentDidMount() {
            const action = getTodoList();
            dispatch(action);
        }
    }
};

export default connect(mapStateToProps,mapDispatchToProps)(TodoList);
```
1. 如何接收`store`的参数:`connect(mapStateToProps,mapDispatchToProps)(TodoList);`，将`TodoList`和`store`做连接，然后在`mapStateToProps`方法(将`store`中数据映射成`props`)中进行值传递和声明。
2. 如何接收`dispatch`:同理，在`mapDispatchToProps`方法中进行传递`dispatch`对象和使用。(将`store`的`dispatch`映射到本js文件中)


其它，`actionCreators`和`actionTypes` 和`reducer` 不变，`redux-thunk`不变。


注意：因为我使用了无状态组件，所以没有声明周期，所以`componentDidMount`不会自动执行，所以`ajax`不会执行，`store`初始化为空。
