>给定两个二进制字符串，返回他们的和（用二进制表示）。

>输入为非空字符串且只包含数字 1 和 0。


### 示例
```
输入: a = "11", b = "1"
输出: "100"
```
```
输入: a = "1010", b = "1011"
输出: "10101"
```

### coding
>该代码可读性强于下面的解法，但是它似乎只能用于计算二进制。

```
public String addBinary(String a, String b) {
    int i = a.length() - 1,
            j = b.length() - 1,
            sum = 0;
    StringBuilder sb = new StringBuilder();
    while (i >= 0 || j >= 0) {
        int m = i >= 0 ? a.charAt(i) - '0' : 0;
        int n = j >= 0 ? b.charAt(j) - '0' : 0;
        sum += m + n;
        if(sum==2){
            sb.insert(0, "0");
            sum=1;
        }else if(sum==3){
            sb.insert(0, "1");
            sum=1;
        }else{
            sb.insert(0, String.valueOf(sum));
            sum=0;
        }
        i--;
        j--;
    }
    if (sum == 1) {
        sb.insert(0, "1");
    }
    return sb.toString();
}
```

### coding-2
>模拟加法的运算法则，从最低位加到最高位。记得使用StringBuilder来减少字符串操作的开销。
```
public class Solution {
    public String addBinary(String a, String b) {
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        StringBuilder sb = new StringBuilder();
        while(i >=0 || j >=0){
            int m = i >= 0 ? a.charAt(i) - '0' : 0;
            int n = j >= 0 ? b.charAt(j) - '0' : 0;
            int sum = m + n + carry;
            carry = sum / 2;
            sb.insert(0, String.valueOf(sum % 2));
            i--;
            j--;
        }
        if(carry != 0) sb.insert(0, '1');
        return sb.toString();
    }
}
```
如果不是二进制相加，而是十六进制相加呢？只要把算法中的除2和余2换成16，并添加相应的十六进制字母就行了。