#### PropTypes
>当父类给子类组件传值时，如果类型不对应，则在console里报错提示。
>![](https://upload-images.jianshu.io/upload_images/5786888-750f5b4994e1aca2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

1. 导包
>  `package.json` 文件中`dependencies`模块添加  `"prop-types": "latest"`
```
import PropTypes from 'prop-types'
```
2. 在导出组件`export default TodoItem;`之前声明传递的参数类型。
```
TodoItem.prototype = {
    content: PropTypes.string,
    delItem: PropTypes.func,
    index: PropTypes.number
}
```
更详细的设置请查看官方文档：https://react.docschina.org/docs/typechecking-with-proptypes.html

如果需要某个参数必须从父组件中传过来，则在类型后面添加`isRequired`,总代码为`content: PropTypes.string.isRequired`,如果该参数不存在，则会在console中报错，但是这样代码的强健性就有问题了，所以react支持对参数设置默认值，如下。

#### defaultProps
>给参数设置默认值

```
// 为属性指定默认值:
Greeting.defaultProps = {
  name: 'Stranger'
};
```


