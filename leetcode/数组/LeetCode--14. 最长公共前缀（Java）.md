>编写一个函数来查找字符串数组中的最长公共前缀。如果不存在公共前缀，返回空字符串 ""。

### 示例
```
输入: ["flower","flow","flight"]
输出: "fl"

输入: ["dog","racecar","car"]
输出: ""
解释: 输入不存在公共前缀。
```

### 错误代码
```
public String longestCommonPrefix(String[] strs) {
    int index = 0;
    //拿出第一个字符串和后面的比较，有不同直接返回
    for (int i = 0;i<strs[0].length();i++){
        char current = strs[0].charAt(index);
        for (String str:strs){
            if (current!=str.charAt(i)){
                return str.substring(0,index);
            }
        }
        index++;
    }
    return strs[0].substring(0, index);
}
```
```
执行错误信息：
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 0
	at Solution.longestCommonPrefix(Solution.java:5)
	at __DriverSolution__.__helper__(__Driver__.java:4)
	at __Driver__.main(__Driver__.java:48)

最后执行的输入：[]
```

说明没有判断空数组。
加上空数组判断后：
```
执行错误信息：
Exception in thread "main" java.lang.StringIndexOutOfBoundsException: String index out of range: 1
	at java.lang.String.charAt(String.java:615)
	at Solution.longestCommonPrefix(Solution.java:11)
	at __DriverSolution__.__helper__(__Driver__.java:4)
	at __Driver__.main(__Driver__.java:48)
最后执行的输入：
["aa","a"]
```
说明没有考虑后面字符串长度小于前面的情况,所以添加判断如果当前字符串的长度等于i，则返回0到i的数据：`str.length()== i`，当然，这个判断需要加载char比较之前，因为一旦字符长度不够，编译就会报错下标越界。


### Coding
```
public String longestCommonPrefix(String[] strs) {
    if (strs.length == 0){
        return "";
    }
    int index = 0;
    //拿出第一个字符串和后面的比较，有不同直接返回
    for (int i = 0;i<strs[0].length();i++){
        char current = strs[0].charAt(index);
        for (String str:strs){
            if (str.length()== i || current!=str.charAt(i)){
                return str.substring(0,index);
            }
        }
        index++;
    }
    return strs[0].substring(0, index);
}
```

### 总结
像这种比较数据间相同元素的多少，我们就拿数组的第一个元素当模版，和后面的比较做判断然后得出结论。

