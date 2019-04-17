>antd是蚂蚁金服的一个开源
中文官方文档:https://ant.design/docs/react/introduce-cn

### 安装
`cnpm install antd`

### 引入样式

`import 'antd/dist/antd.css';  // or 'antd/dist/antd.less'`

### 引入input样式
>点击input框：
![](https://upload-images.jianshu.io/upload_images/5786888-34cca3631293f92e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
点击show code
>![](https://upload-images.jianshu.io/upload_images/5786888-e915b65aea2a0595.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

将代码复制到项目中。注意是`<Input>`i是大写的。

```
import React, { Component } from 'react'
import 'antd/dist/antd.css';  // or 'antd/dist/antd.less'
import { Input } from 'antd';

class TodoList extends Component{

    render(){
        return (
            <div>
                <div>
                    <Input name='value' style={{width:300}}/>
                </div>
            </div>
        )
    }
}

export default TodoList;
```
>![效果图](https://upload-images.jianshu.io/upload_images/5786888-71efde8913c35751.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

更多样式设置请查看：https://ant.design/components/input-cn/


### 引入button 和list
```
   render(){
        return (
            <div>
                <div style={{marginTop: '10px', marginLeft: '10px'}}>
                    <Input name='value' style={{width: `300px`, marginRight: '10px' }} />
                    <Button >Default</Button>
                    <List
                        style={{marginTop: '10px', width: '300px'}}
                        bordered
                        dataSource={data}
                        renderItem={item => (<List.Item >{item}</List.Item>)}
                    />
                </div>
            </div>
        )
    }
```
样式网址：https://ant.design/components/list-cn/

