>解决使用immutable带来的调用store数据繁琐。

### 在reducer中引用

```
import { combineReducers } from 'redux-immutable'
```
redux本身就有一个combineReducers，这个`combineReducers`是对其的一个替换。


### 在index.js中修改store调用
```
const  initMapStateToProps =(state)=>{
    return{
        //因为 combineReducers 对其进行统一管理，所以需要在 变量前面加 header(combineReducre的一个reducer)
        //也可以使用 focused: state.getIn(['header','focused']) 
        focused: state.get("header").get("focused")
    }
};
```
