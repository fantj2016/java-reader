
>这个东西是将项目中所有的reducer进行统一管理和解藕，类似父依赖管理。


###  根目录下的store/reducer.js
```
import { combineReducers } from 'redux'
import {reducer as headerReducer } from '../common/header/store/'


const reducer =  combineReducers({
    header: headerReducer
});

export default reducer;
```
可以看该js文件的第二行，引入的是个`/store/index.js`文件，那index.js文件是什么作用呢，他负责将子项目模块的配置文件(const,actionType,reducer)暴露出去，使得其它项目模块中调用方便快捷。那来看看index.js是如何写的。

### header目录下的/store/index.js
```
import reducer from './reducer'

import  * as actionCreators from './actionCreators'

import * as constants from './constants'

export { reducer, constants, actionCreators };
```

###  header目录下的/store/reducer.js
```
const  defaultState = {
    focused: false
};
//state 是整个数据
export default (state = defaultState,action) => {
    if (action.type === 'search_focus') {
        return {
            focused: true
        }
    }
    if (action.type === 'search_blur'){
        return{
            focused:false
        }
    }
    return state;
}
```
