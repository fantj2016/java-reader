>`render()`函数决定组件显示什么内容，每当state或者prop发生变化，`render`就会被执行，实时的将数据显示在页面上。当父组件的`render()`被运行，其子组件的`render()`都会被运行。效率很高(虚拟DOM(JS对象)，js生成js对象效率很高，但是生成DOM性能很低,所以用虚拟dom来代替真实的DOM，所以react.js中的代码<div>等并不是真实的DOM，而是JSX语法)

JSX->createElement -> 虚拟DOM(JS对象) -> 真实DOM

### 虚拟DOM中的diff算法
>找原始虚拟DOM和新虚拟DOM的差异。同层比对。所以key值应该设置为固定值以提高diff算法效率。

setState函数设置成异步也是为了优化虚拟DOM的比对，如果在很短时间内state进行更改，虚拟DOM只需要做一次变化。
