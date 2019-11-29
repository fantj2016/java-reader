>判断一个整数是否是回文数。回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。

### 示例 
```
输入: 121
输出: true

输入: -121
输出: false
解释: 从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。

输入: 10
输出: false
解释: 从右向左读, 为 01 。因此它不是一个回文数。
```


### 答案

```
public boolean isPalindrome(int x) {
    if (x<0){
        return false;
    }
    int temp = 0;
    int copy = x;
    while (x!=0){
        int remainder = x%10;
        x /= 10;
        temp = temp *10 +remainder;
    }
    return copy == temp;
}
```
上面代码虽然满分通过，但是还有许多需要改进的部分。

1. 个位数（而且不等于0），应该直接返回flase
2. 算法不是最优，可以将x分为两半，反转后半部分，然后直接和前半部分做比较即可。
3. 我还复制了一次x，占用空间多


### 完善后的代码
```
public boolean isPalindrome(int x) {
    if (x<0 || (x!=0 && x%10==0)){
        return false;
    }
    int temp = 0;
    while (x>temp){
        int remainder = x%10;
        x /= 10;
        temp = temp *10 +remainder;
    }
    return x == temp/10 || x ==temp;
}
```
### 总结

为了将x分为两半，请注意while的条件是`x>temp`,但是这样会出问题的，比如`x=121`,会出现`x=1`,`temp=12`；那`return x ==temp/10`就够了吗?如果`x=11`,则`x=1,temp=1`,此时`return x ==temp/10`是行不通的，所以成了`return x == temp/10 || x ==temp;`。

