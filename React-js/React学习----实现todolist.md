### 构造函数
>最优先执行的函数。写法固定。

```
    constructor(props){
        super(props);    //调用父类的构造函数
    }
```



### Demo1
```
import React, { Component } from 'react'

class TodoList extends Component{
    render(){
        return(
            <div>
                <input value='Fantj'/><button>提交</button>
                <ul>
                    <li>学英语</li>
                    <li>学高数</li>
                </ul>
            </div>
        )
    }
}

export default TodoList;
```
>![](https://upload-images.jianshu.io/upload_images/5786888-007ebdddaa74d5ab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这是个简单的静态网页。如果想把页面做成动态获取，则：
```
import React, { Component } from 'react'

class TodoList extends Component{

    // 构造函数
    constructor(props){
        super(props);
        this.state = {
            inputValue: 'FantJ',
            list: ['hello','world']
        }
    }
    render(){
        return(
            <div>
                <input value={this.state.inputValue}/><button>提交</button>
                <ul>
                    {
                        this.state.list.map((item,index)=>{
                            return <li>{item}</li>
                        })
                    }
                </ul>
            </div>
        )
    }
}

export default TodoList;
```
>![](https://upload-images.jianshu.io/upload_images/5786888-0253b7add6116822.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到，实现了数据的动态获取。js的业务代码需要用`{}`来扩住。其次注意list的遍历方式。

### Demo2:往变量里塞值
>如果需要变更js里的变量，需要做两个步骤：
1. 绑定this对象并传参
2. 调用`this.setState({ xxx: xxx })`方法

```
import React, { Component } from 'react'

class TodoList extends Component{

    // 构造函数
    constructor(props){
        super(props);
        this.state = {
            inputValue: 'FantJ',
            list: ['hello','world']
        }
    }
    render(){
        return(
            <div>
                <input value={this.state.inputValue} />
                <button onClick={this.handleButtonClick.bind(this)}>提交</button>
                <ul>
                    {
                        this.state.list.map((item,index)=>{
                            return <li>{item}</li>
                        })
                    }
                </ul>
            </div>
        )
    }

    handleButtonClick() {
        this.setState({
            list: [...this.state.list,'FantJ']
        })
    }
}

export default TodoList;
```
1. `onClick={this.handleButtonClick.bind(this)`这里我将变量绑定到`handleButtonClick`这个方法中，然后在该方法才能获取到`this.state`这个变量，不然是获取不到的。
2. 在`handleButtonClick`方法中,`list: [...this.state.list,'FantJ']`的意思：`...this.state.list`是将list中的值一一铺开，然后我在后面紧跟`,'FantJ'`即可将该字串加入到list中。
3. 我一旦点击`button`,则就会在list中追加一个字串，并显示在页面。

>![](https://upload-images.jianshu.io/upload_images/5786888-f1c51fe3034b1fd1.gif?imageMogr2/auto-orient/strip)


### Demo3 ：实现TodoList
>在前两个demo的基础上，实现todoList就比较容易理解了。

```
import React, { Component } from 'react'

class TodoList extends Component{


    // 构造函数
    constructor(props){
        super(props);
        this.state = {
            inputValue: '',
            list: []
        }
    }
    render(){
        return(
            <div>
                <input value={this.state.inputValue} onChange={this.handleInputChange.bind(this)}/>
                <button onClick={this.handleButtonClick.bind(this)}>提交</button>
                <ul>
                    {
                        this.state.list.map((item,index)=>{
                            return <li>{item}</li>
                        })
                    }
                </ul>
            </div>
        )
    }
    handleInputChange(e){
        this.setState({
            inputValue: e.target.value
        })
    }
    handleButtonClick() {
        this.setState({
            list: [...this.state.list,this.state.inputValue],
            inputValue: ''
        })
    }
}

export default TodoList;
```
>![](https://upload-images.jianshu.io/upload_images/5786888-f58c2c6ea3646fc4.gif?imageMogr2/auto-orient/strip)


### Demo4:TodoList增加删除功能
```
import React, { Component } from 'react'

class TodoList extends Component{


    // 构造函数
    constructor(props){
        super(props);
        this.state = {
            inputValue: '',
            list: []
        }
    }
    render(){
        return(
            <div>
                <input value={this.state.inputValue} onChange={this.handleInputChange.bind(this)}/>
                <button onClick={this.handleButtonClick.bind(this)}>提交</button>
                <ul>
                    {
                        this.state.list.map((item,index)=>{
                            return <li onClick={this.handleItemDel.bind(this,index)}>{item}</li>
                        })
                    }
                </ul>
            </div>
        )
    }
    handleInputChange(e){
        this.setState({
            inputValue: e.target.value
        })
    }
    handleButtonClick() {
        this.setState({
            list: [...this.state.list,this.state.inputValue],
            inputValue: ''
        })
    }
    handleItemDel(index){
        //获取当前index
        //splice(start: number, deleteCount: number, ...items: T[]): T[];
        const list = [...this.state.list]
        list.splice(index,1);
        this.setState({
            list:list
        })
        console.log(index);
    }

}

export default TodoList;
```
