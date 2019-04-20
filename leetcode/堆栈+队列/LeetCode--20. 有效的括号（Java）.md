>给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。

>有效字符串需满足：

>左括号必须用相同类型的右括号闭合。
左括号必须以正确的顺序闭合。
注意空字符串可被认为是有效字符串。



### 示例
```
输入: "()"
输出: true

输入: "()[]{}"
输出: true

输入: "(]"
输出: false

输入: "([)]"
输出: false

输入: "{[]}"
输出: true
```

### Coding
```
public boolean isValid(String s) {
    if (s.isEmpty()){
        return true;
    }
    Stack<Character> stack = new Stack<>();
    char[] chars = s.toCharArray();
    for (int i = 0;i<chars.length;i++){
        if (chars[i] == '{'){
            stack.push('}');
        }else if (chars[i] == '['){
            stack.push(']');
        }else if (chars[i] == '('){
            stack.push(')');
        }else if (stack.isEmpty() || chars[i]!=stack.pop()){
            return false;
        }
    }
    return stack.isEmpty();
}
```

### 总结
1. 分析题目很明显是栈问题，所以如何利用好push和pop是一个技术突破口。
2. 当for循环完，也就是stack该pop的已经pop，如果stack为空，则证明括号一一对应，则返回true