欢迎大家先看 [如何用 SpringBoot 优雅的写代码](https://www.jianshu.com/p/8338a153be6b)

在本教程里，注重开发技巧和注意事项。希望能帮到大家。

### 1. Set 集合的使用

我们有时候在处理业务的时候，会有对象不能重复的需求，java程序猿可能首先想到的就是set集合了，但是Set集合原生支持的对象只有java的一些基本类型String、Integer等，我们想要加入我们的对象类(数据类型)，就需要重写两个方法。`hashcode`和 `equals`方法。


### 2. "空"的判断

java判断空值方法很多，但是都略有不同，我在这里介绍两个工具类。

###### StringUtils
isEmpty方法
```
StringUtils.isEmpty(null) = true
StringUtils.isEmpty("") = true
StringUtils.isEmpty(" ") = false //注意在 StringUtils 中空格作非空处理
StringUtils.isEmpty("  ") = false
StringUtils.isEmpty("aaa") = false
StringUtils.isEmpty(" aaa") = false
```
isBlank方法
```
       StringUtils.isBlank(null) = true
　　StringUtils.isBlank("") = true
　　StringUtils.isBlank(" ") = true   //注意这里的区别
　　StringUtils.isBlank("\t \n \f \r") = true  //对于制表符、换行符、换页符和回车符
　　StringUtils.isBlank("\b") = false  //"\b"为单词边界符
　　StringUtils.isBlank("bob") = false
　　StringUtils.isBlank(" bob ") = false
```

同理，isNotEmpty 和isNotBlank 方法就不贴了。

甜点：
不区分大小写：`StringUtils.containsIgnoreCase("china", 'a');// true`
判断两个字符串是否相等，有非空处理:`StringUtils.equals(null, null) = true`、`StringUtils.equals(null, "abc") = false`
###### CollectionUtils
CollectionUtils.isEmpty 一般用于判断数组集合是否为空。
甜点：
CollectionUtils.isEqualCollection(listA,listB) 可用于判断两个集合是否相等（内容相等）。
### 3. 
