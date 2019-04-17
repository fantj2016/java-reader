### vue实例详解
```
    var app = new Vue({
        el: '#app',
        components: {
            TodoItem: TodoItem
        },
        data: {
            list: [],
            inputValue: ''
        },
        methods: {
            handleBtnClick: function () {
                //获取data中 inputValue 的值
                // alert(this.inputValue)
                this.list.push(this.inputValue)
                //利用双向绑定 来清空input框内容
                this.inputValue=''
            },
            handleItemDelete: function (index) {
                // this.list.pop();
                // alert(index)
                //删除list中第index个元素
                this.list.splice(index,1)
            }
        }
    })
```
其中，el表示vue接管的domId，components表示需要加载的子组件，data是数据集和js一样，methods是方法。

### 数据双向绑定
`<input type="text" v-model="inputValue">`
什么是双向绑定呢？
就是我修改vue中的inputValue数据，input框会自动同步修改inputValue的值。这个会在下面详解。


### 浏览器console玩vue
假设我是`var app = new Vue({...})`，即：实例对象名称是app
###### 查看实例对象信息
![vue实例对象信息](https://upload-images.jianshu.io/upload_images/5786888-54276202649c8104.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###### 查看该实例接管的dom信息
![查看该实例接管的dom](https://upload-images.jianshu.io/upload_images/5786888-0aec7d4e3a8ed5db.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###### 数据双向绑定demo
![数据双向绑定demo](https://upload-images.jianshu.io/upload_images/5786888-63579cb9c9b36aac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 

### 模板语法
##### 插值
###### 1. v-text和v-html的区别
```
插入文本：
<span>Message: {{ msg }}</span>
<span v-text="xxx"></span>
<span v-once>这个将不会改变: {{ msg }}</span>

插入html：
<p>Using mustaches: {{ rawHtml }}</p>
<p>Using v-html directive: <span v-html="rawHtml"></span></p>

```
注意：v-html标签的绑定会去解析html标签。而不是直接把字符串放上去。
这些标签的本质都是js表达式，所以我们可以`v-text=name + 'fantj'`等等

更多的语法请查看https://cn.vuejs.org/v2/guide/syntax.html

### 计算属性和侦听器
**特点和原因：**都带有内置缓存(不用每次都执行计算)。如果用methods去代替，每次数据的改变都需要去重新渲染页面，耗时耗性能。所以我们需要对容易发生改变的属性进行计算属性和侦听器的维护，大大提升性能。
###### 计算属性例子：
```
<div id="example">
  <p>Original message: "{{ message }}"</p>
  <p>Computed reversed message: "{{ reversedMessage }}"</p>
</div>
```
```
var vm = new Vue({
  el: '#example',
  data: {
    message: 'Hello'
  },
  computed: {
    // 计算属性的 getter
    reversedMessage: function () {
      // `this` 指向 vm 实例
      return this.message.split('').reverse().join('')
    }
  }
})
```
输出：
```
Original message: "Hello"

Computed reversed message: "olleH"
```
###### 侦听器例子：
```
<div id="demo">{{ fullName }}</div>
```
```
var vm = new Vue({
  el: '#demo',
  data: {
    firstName: 'Foo',
    lastName: 'Bar',
    fullName: 'Foo Bar'
  },
  watch: {
    firstName: function (val) {
      this.fullName = val + ' ' + this.lastName
    },
    lastName: function (val) {
      this.fullName = this.firstName + ' ' + val
    }
  }
})
```
watch会监听所要求监听的data的改变。它使得谁修改只改谁，不需要对整个页面进行渲染。


### class的对象绑定
```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Vue中的样式绑定</title>
    <script src="../vue.js"></script>
    <style>
        .activated{
            color: red;
        }
    </style>
</head>
<body>
<div id="app">
    <div @click="handleTextClick" :class="{activated: isActivited}">hello world</div>
</div>

    <script>
        var app = new Vue({
            el: '#app',
            data: {
                isActivited: false
            },
            methods: {
                handleTextClick: function () {
                    this.isActivited = !this.isActivited
                }
            },
        })
    </script>
</body>
</html>
```
hello world有个点击事件handleTextClick和class activated，这个class需要判断isActivited的真假值，handleTextClick方法里将isActivited的值取反，style里activated设置color为red，所以效果就是点击helloworld 黑字变红字(class有值)，再点击红字变黑字(class无值)

###### 数组语法和绑定style
方法类似，按需查找
https://cn.vuejs.org/v2/guide/class-and-style.html

### 条件渲染
###### v-if和 v-show
决定整个标签是否存在
v-if 整个dom真正删除
v-show只是display:none（假删除）
###### v-else-if 和 v-else
略
###### key
当给标签加key值的时候，key值不同(唯一标识)vue就不会尝试去复用一个标签。

### 列表渲染
###### 实例一：v-for 渲染 list
```
<ul id="example-2">
  <li v-for="(item, index) in items">
    {{ parentMessage }} - {{ index }} - {{ item.message }}
  </li>
</ul>
```
```
var example2 = new Vue({
  el: '#example-2',
  data: {
    parentMessage: 'Parent',
    items: [
      { message: 'Foo' },
      { message: 'Bar' }
    ]
  }
})
```
```
Parent-0-Foo
Parent-1-Bar
```
可以用of替代in，原理是一样的
###### 实例二：v-for渲染key:value
```
new Vue({
  el: '#v-for-object',
  data: {
    object: {
      firstName: 'John',
      lastName: 'Doe',
      age: 30
    }
  }
})
```
```
<div v-for="(value, key, index) in object">
  {{ index }}. {{ key }}: {{ value }}
</div>
```
```
0.firstName: 'John',
1l.astName: 'Doe',
2.age: 30
```
建议尽可能在使用 v-for 时提供 key，除非遍历输出的 DOM 内容非常简单，或者是刻意依赖默认行为以获取性能上的提升。
###### template占位符
如果不想用div去包裹太多的东西，我们可以用template代替div，并且前端渲染会忽略该标签，我们就可以在这个标签里控制我们的东西。

### 组件使用技巧
###### 1. 组件的声明
我们在使用table标签时候，如果自己把<tr><td>..等写到组件的template里，我们需要声明一下tr的组件名，否则渲染失败。
比如：
```
<table>
  <tbody>
    <tr>
      <td>
      <td>
```
改成
```
<table>
  <tbody>
    <tr   is="row">

Vue.componet('row',template:'<tr><td>...')
```
这样它就会去寻找row这个组件来代替<tr>
###### 2. 子组件中使用data
子组件中使用data必须是一个有返回值的函数。
```
Vue.component('...',
  {
    data: function() {
      return{
          content: 'Fant.J'
      }
    },
  template: '<td>...'
  }
)
```

###### 3. ref的使用
ref如果在div标签，它会获取整个dom代码
ref如果用在组件上，它会获取到组件的引用。

### Vue组件
##### 父组件给子组件传值
```
v-bind:content="item"
```
v-bind可以省略




##### 子组件给父组件传值
```
this.$emit("delete",this.index)

在js中触发delete标签，即下面

@delete="handleItemDelete"

在@delete后，触发handleItemDelete方法

            handleItemDelete: function (index) {
                // this.list.pop();
                // alert(index)
                //删除list中第index个元素
                this.list.splice(index,1)
            }
```

#####  

### 组件参数Prop验证
```
Vue.component('my-component', {
  props: {
    // 基础的类型检查 (`null` 匹配任何类型)
    propA: Number,
    // 多个可能的类型
    propB: [String, Number],
    // 必填的字符串
    propC: {
      type: String,
      required: true
    },
    // 带有默认值的数字
    propD: {
      type: Number,
      default: 100
    },
    // 带有默认值的对象
    propE: {
      type: Object,
      // 对象或数组且一定会从一个工厂函数返回默认值
      default: function () {
        return { message: 'hello' }
      }
    },
    // 自定义验证函数
    propF: {
      validator: function (value) {
        // 这个值必须匹配下列字符串中的一个
        return ['success', 'warning', 'danger'].indexOf(value) !== -1
      }
    }
  }
})
```
###### Prop和非Prop
Prop和非Prop取决于是否在子组件里用`Props:{}`去接收父组件传过来的参数。

Prop：数据相关元素不会显示在dom
非Prop：与Prop相反，并且子组件获取不到传递值

### 插槽 slot
https://cn.vuejs.org/v2/guide/components-slots.html


