>reducer中我们总需要去返回一个新store对象，但是有时候会忘记，所以用immutable来帮助我们解决该困难。


### 1. 引入至reducer
```
import { fromJS } from 'immutable'
```
### 2. fromJS包裹数据
```
const  defaultState = fromJS({
    focused: false
});
```
### 3. 修改reducer中的state返回方法
```
export default (state = defaultState,action) => {
    if (action.type === SEARCH_FOCUS) {
        return state.set("focused",true)
    }
    if (action.type === SEARCH_BLUR){
        return state.set("focused",false)
    }
    return state;
}
```
其中，`state`因为有`fromJS`的包裹，它调用的`set()`方法其实是返回一个新的对象，而不是原对象本身。
### 3. 修改index.js中的获取store中的值方法
```
const  initMapStateToProps =(state)=>{
    return{
        //因为 combineReducers 对其进行统一管理，所以需要在 变量前面加 header(combineReducre的一个reducer)
        focused: state.header.get("focused")
    }
};
```
将之前的直接调用`focused: state.header.focused`改为`focused: state.header.get("focused")`。
