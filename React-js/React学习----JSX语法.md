### 1. 导入CSS到组件中
#### style.css
```
.input{
    border: 1px solid red;
}
```

#### TodoList.js
```
import './style.css'    导入

#用class也可以(console会报错)，但是为了和jsx区分，
#建议用className来引入样式。
<input className='input' />  
```
>![](https://upload-images.jianshu.io/upload_images/5786888-8194ff827009e8b8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2. 组件对h5标签的支持
>一般不做声明的话，为了安全组件只会把字符串当字符串来解析(默认进行转义)，但是有时候需要解析字符串中的H5标签，则需要用到该功能。

```
#在要显示的标签中加:
dangerouslySetInnerHTML={{__html:item}}
```
其中上面代码的 `item` 是js文件中的变量。


### 3. 组件对id锚点的支持方式

```
<label htmlFor='insertText'>输入内容</label>
<input id='insertText' className='input' >
```
>![](https://upload-images.jianshu.io/upload_images/5786888-db4884f388bbf2d0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

当我们点击`输入内容`这个`label`标签时，光标会自动进入`input`框。
