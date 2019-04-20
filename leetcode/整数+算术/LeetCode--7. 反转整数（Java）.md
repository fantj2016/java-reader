>给定一个 32 位有符号整数，将整数中的数字进行反转。

### 示例
```
输入: 123
输出: 321

输入: -123
输出: -321

输入: 120
输出: 21
```


### 注意:
假设我们的环境只能存储 32 位有符号整数，其数值范围是 [−231,  231 − 1]。根据这个假设，如果反转后的整数溢出，则返回 0。


### 答案

```
public int reverse(int x) {
    int temp = 0;
    while (x!=0) {
        int remainder = x % 10;
        x /= 10;
        //判断越界
        if (temp > Integer.MAX_VALUE/10 || (temp == Integer.MAX_VALUE / 10 && remainder > 7)) {
            return 0;
        }
        if (temp < Integer.MIN_VALUE/10 || (temp == Integer.MIN_VALUE / 10 && remainder < -8)) {
            return 0;
        }
        //乘以10+余数
        temp = temp * 10 + remainder;
    }
    return temp;
}
```


### 总结
##### 1. 末位数求法

取个位数或者最后一位数的方法：通过`int remainder = x % 10;`取得最后一位数，然后数本身`x /= 10;`割掉最后一个数。然后通过`while(x!=0)`来迭代。

##### 2. 越界判断
越界分为两种：
1. `temp > Integer.MAX_VALUE/10`,那`temp = temp * 10 + remainder;`肯定会溢出。
2. `temp == Integer.MIN_VALUE / 10`,因为Integer最大值是2147483647，尾数是7，所以，如果余数大于7，则溢出：`remainder > 7`；负数同理。

