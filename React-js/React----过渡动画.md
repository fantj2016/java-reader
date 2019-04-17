
### 1. 用css实现过渡动画

```
import React , {Component,Fragment} from  'react';
import './style.css'


class App extends Component {

    constructor(props){
        super(props);
        this.state = {
            show: true
        }
        this.handlerButton = this.handlerButton.bind(this);
    }
    render(){
        return (
            <Fragment>
                <div className={this.state.show? 'show': 'hide'}>hello</div>
                <button onClick={this.handlerButton}>button</button>
            </Fragment>
        )
    }
    handlerButton(){
       this.setState({
           show: !this.state.show
       })
    }
}
export default App;

```

1. 给hello设置了一个`class="show"`和`class="hiden"`
2. 给button设置点击事件来改变hello的className，使得它点一次变一次。
3. 引入相关css:`style.css`。
```
.show{
    opacity: 1;
    transition: all 1s ease-in;
}

.hide{
    opacity: 0;
    transition: all 1s ease-out;
}
```


### 2.  用`react-transition-group`框架实现过渡动画
安装：
``
