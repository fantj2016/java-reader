### 1. 父组件向子组件传递数据
>将之前的TodoList做改动，变成多组件形式。
注意：子组件只能使用父组件的值(只读传值)，而不能直接改变父组件中的值。会报错。防止父组件中其他依赖此数据的子组件全部损坏。

###### 父js类 TodoList.js
```
import React, { Component } from 'react'
import './style.css'
import TodoItem from './TodoItem'

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
                <label htmlFor='insertText'>输入内容</label>
                <input id='insertText' className='input' value={this.state.inputValue} onChange={this.handleInputChange.bind(this)}/>
                <button onClick={this.handleButtonClick.bind(this)}>提交</button>
                <ul>
                    {
                        this.state.list.map((item,index)=>{
                            // return <li dangerouslySetInnerHTML={{__html:item}} onClick={this.handleItemDel.bind(this,index)}></li>
                            return (
                                <div><TodoItem content={item}/></div>
                            )
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
注意这几个代码段：
1. `import TodoItem from './TodoItem'`
2. `<div><TodoItem content={item}/></div>`
3. 其中，TodoItem是子组件,`content={item}`是将`item`变量赋值给`content`变量然后传给子组件。那子组件如何接收该参数呢？

###### 子组件js：TodoItem.js
```
import React, { Component } from 'react'

class TodoItem extends Component{
    render(){
        return <div>{this.props.content}</div>
    }
}

export default TodoItem;
```
1. `{this.props.content}`中子组件就可以接收到父组件的值传递。

### 2. 子组件修改父组件的数据(传值)

###### 父组件js：TodoList.js
```
import React, { Component } from 'react'
import './style.css'
import TodoItem from './TodoItem'

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
                <label htmlFor='insertText'>输入内容</label>
                <input id='insertText' className='input' value={this.state.inputValue} onChange={this.handleInputChange.bind(this)}/>
                <button onClick={this.handleButtonClick.bind(this)}>提交</button>
                <ul>
                    {
                        this.state.list.map((item,index)=>{
                            // return <li dangerouslySetInnerHTML={{__html:item}} onClick={this.handleItemDel.bind(this,index)}></li>
                            return (
                                <div>
                                    <TodoItem content={item} index={index} delItem={this.handleItemDel.bind(this)}/>
                                </div>
                            )
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
1. `<TodoItem content={item} index={index} delItem={this.handleItemDel.bind(this)}/>`这段代码用来传递父类方法，`bind(this)`的意思是将父类的`handleItemDel`方法绑定在`TodoItem`子类组件中。注意这点，如果没有`bind`操作，则子类组件拿不到对象。

###### 子类组件js：TodoItem.js

```
import React, { Component } from 'react'

class TodoItem extends Component{
    render(){
        return(
            <div onClick={this.delItem.bind(this)}>
                {this.props.content}
            </div>
        )
    }
    delItem(){
        this.props.delItem(this.props.index)
    }
}
export default TodoItem;
```

在子类中也写了一个方法名来接收父类传递过来的方法，然后bind在子类组件上使用。`onClick={this.delItem.bind(this)}`

该bind方式建议写在构造函数中，这样会加速代码的执行速度。优化后如下：
```
import React, { Component } from 'react'

class TodoItem extends Component{

    constructor(props){
        super(props);
        this.delItem = this.delItem.bind(this);
    }
    render(){
        return(
            <div onClick={this.delItem}>
                {this.props.content}
            </div>
        )
    }
    delItem(){
        this.props.delItem(this.props.index)
    }
}
export default TodoItem;
```

### ES6规范化项目
可以使用ES6的语法再做修改：
```
import React, { Component } from 'react'

class TodoItem extends Component{

    constructor(props){
        super(props);
        this.delItem = this.delItem.bind(this);
    }
    render(){
        const {content} = this.props;
        return(
            <div onClick={this.delItem}>
                {content}
            </div>
        )
    }
    delItem(){
        const {delItem,index} = this.props;
        delItem(index);
    }
}
export default TodoItem;
```

可以看出来是用`const {content} = this.props;`然后直接调用`{content}`,来代替之前的`this.props.content`


###### 2. 函数式编程
```
    handleInputChange(e){
        this.setState({
            inputValue: e.target.value
        })
}
```
将上面的代码修改成下面的：
```
    handleInputChange(e){
        this.setState(()=>({
            inputValue: e.target.value
        }))
    }
```
明面上看着没差别，但其实变成了异步处理。相应的，我们需要在外部新声明变量来获取该异步体系内的信息。不然会报错！如：
>![异步数据操作错误信息](https://upload-images.jianshu.io/upload_images/5786888-b3cad9832966faf4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

所以需要做以下修改:
```
    handleInputChange(e){
        const inputValue = e.target.value;
        this.setState(()=>({
            inputValue: inputValue
        }))
    }
```
其中，`this.setState`方法其实有个默认的参数`preState`，代表上次修改前的值，我们可以直接从该参数中取值(相对安全的做法)。
同理：
```
    handleItemDel(index){
        //获取当前index
        //splice(start: number, deleteCount: number, ...items: T[]): T[];
        this.setState((preState)=>{
            const list = [...preState.list];
            list.splice(index,1);
            return(list)
        });
    }
```

如果发现有key值报错：
>![](https://upload-images.jianshu.io/upload_images/5786888-5ce8635c970424a7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
是因为每个`array`或者`iterator`都应该有一个`key`值：
```
    getTodoItem(){
        return this.state.list.map((item,index)=>{
            // return <li dangerouslySetInnerHTML={{__html:item}} onClick={this.handleItemDel.bind(this,index)}></li>
            return (
                <div  key={index}>
                    <TodoItem content={item} index={index} delItem={this.handleItemDel.bind(this)}/>
                </div>
            )
        })
    }
```
这个`key={index}`要保证唯一，并且放在最外层的标签内。


