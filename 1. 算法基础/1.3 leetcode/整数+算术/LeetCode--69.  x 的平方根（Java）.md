>实现 int sqrt(int x) 函数。

>计算并返回 x 的平方根，其中 x 是非负整数。

>由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。


### 示例

```
输入: 4
输出: 2

输入: 8
输出: 2
说明: 8 的平方根是 2.82842..., 
     由于返回类型是整数，小数部分将被舍去。
```

### 分析
1. x非负数判断
2. 返回类为整数，小数部分舍去
3. 二分法查找。


### Coding
```
public int mySqrt(int x) {
    if (x<=0){
        return x;
    }
    int low = 0;
    int high = x;
    int mid = 0;
    while (low<=high){
        mid = (low+high+1)/2;
        if (mid==x/mid){
            return mid;
        }else if (mid > x/mid){
            high = mid-1;
        }else {
            low = mid+1;
        }
    }
    return mid>x/mid?mid-1:mid;
}
```

注意两个点：
1. 用`mid==x/mid`代替`mid*mid==x`防止溢出
2. `mid = (low+high+1)/2`,加一的目的是防止mid为0，导致除数为0。或者定义`high = x/2 +1`。