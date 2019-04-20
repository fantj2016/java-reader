### Title

>给定一个由 '(' 和 ')' 括号组成的字符串 S，我们需要添加最少的括号（ '(' 或是 ')'，可以在任何位置），以使得到的括号字符串有效。

>从形式上讲，只有满足下面几点之一，括号字符串才是有效的：

>它是一个空字符串，或者
它可以被写成 AB （A 与 B 连接）, 其中 A 和 B 都是有效字符串，或者
它可以被写作 (A)，其中 A 是有效字符串。
给定一个括号字符串，返回为使结果字符串有效而必须添加的最少括号数。



### Demo
```
示例 1：

输入："())"
输出：1
```
```
示例 2：

输入："((("
输出：3
```
```
示例 3：

输入："()"
输出：0
```
```
示例 4：

输入："()))(("
输出：4
```
### Analyze
利用堆栈来解决该问题比较简单，如果有匹配到一对括号，则给字串长度减2.


### Coding

```
class Solution {
    public int minAddToMakeValid(String S) {

            if (S.isEmpty()){
                return 0;
            }
            Stack<Character> stack = new Stack<>();
            int result = S.length();
            char [] chars = S.toCharArray();
            for (int i = 0;i<chars.length;i++){
                if (chars[i] == '('){
                    stack.push(')');
                }else if (!stack.isEmpty() && chars[i]==stack.pop()){
                    result -= 2;
                }
            }
            System.out.println(result);
            return result;
        }
}
```