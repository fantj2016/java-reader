>给定一个由整数组成的非空数组所表示的非负整数，在该数的基础上加一。

>最高位数字存放在数组的首位， 数组中每个元素只存储一个数字。

>你可以假设除了整数 0 之外，这个整数不会以零开头。


### 示例
```
输入: [1,2,3]
输出: [1,2,4]
解释: 输入数组表示数字 123。
```
```
输入: [4,3,2,1]
输出: [4,3,2,2]
解释: 输入数组表示数字 4321。
```


### coding-1

```
public int[] plusOne(int[] digits) {
    int n = digits.length;
    for (int i = digits.length - 1; i >= 0; --i) {
        if (digits[i] < 9) {
            ++digits[i];
            return digits;
        }
        digits[i] = 0;
    }
    int[] res = new int[n + 1];
    res[0] = 1;
    return res;
}
```


### coding-2
>这种解法思路很复杂，carry=1表示进位，0表示不进位。
1. 如果不进位，carry初始值是1，for执行一轮后，最后一位数得到加一并且，carry=sum/10 ==0，在第二轮for循环中直接返回digits。
2. 如果进位，carry初始值也是1，for执行一轮后，最后一位数加一后等于10，取余数为0也就是最后一位数变0，然后carry = sum/10 == 1，循环继续。
3. 最后还有一个问题，假如输入数组为{8,9}，第一次循环完carry=1，然后再循环一次就退出了，因为数组长度为2，所以我们需要获取此时的carry，并创建一个新的数组这个数组为{1,0,0,0...}，最后判断carry是0还是1，如果是1证明digits这个数组里每个元素都进位，则返回新数组，如果是0，则证明digits数组首位没有进位，返回digits。

```
public int[] plusOne(int[] digits) {
    if (digits.length == 0) {
        return digits;
    }
    int carry = 1, n = digits.length;
    for (int i = digits.length - 1; i >= 0; --i) {
        if (carry == 0) {
            return digits;
        }
        int sum = digits[i] + carry;
        //取余数
        digits[i] = sum % 10;
        //充值carry，如果没有进位。carry=0，否则carry=1
        carry = sum / 10;
    }
    int[] res = new int[n + 1];
    res[0] = 1;
    return carry == 0 ? digits : res;
}
```