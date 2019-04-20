>给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。

### 示例

```
输入: [-2,1,-3,4,-1,2,1,-5,4],
输出: 6
解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
```

### 进阶
如果你已经实现复杂度为 O(n) 的解法，尝试使用更为精妙的分治法求解。


### coding-1
```
public int maxSubArray(int[] nums) {
    if (nums == null){
        return 0;
    }
    if (nums.length == 1){
        return nums[0];
    }
    int max = -1000;
    for (int i = 0 ;i < nums.length; i ++){
        //每一次遍历完需要初始化一次
        int temp = 0;
        for (int j = i;j<nums.length;j++){
            temp += nums[j];
            if (temp > max){
                max = temp;
            }
        }
    }
    return max;
}
```
为什么我要设置max初始值是-1000呢，因为如果给的数组是{-1,-2}，max=0的话，返回值就是0。
当然这样是不正规的，应该设置为Integer.MIN_VALUE。

暴力法思想很简单，不做解释。


### coding-2（分治法）
```
public int maxSubArray(int[] nums) {
    //将nums数组分割
    return subSum(nums,0,nums.length-1);
}
public static int subSum(int a[],int left,int right){
    //如果nums长度为1
    if (left == right){
        if (a[left]>0) {
            return a[left];
        }else {
            return a[left];
        }
    }
    //如果nums长度不为1
    int center = (left+right)/2;
    int maxLeftSum = subSum(a,left,center);
    int maxRightSum = subSum(a,center+1,right);

    int leftSum = 0;
    int maxTempLeftSum = -1000;
    for (int i = center;i>=left;i--){
        leftSum += a[i];
        if (leftSum>maxTempLeftSum){
            maxTempLeftSum = leftSum;
        }
    }
    int rightSum = 0;
    int maxTempRightSum = -1000;
    for (int i = center+1;i<=right;i++){
        rightSum += a[i];
        if (rightSum>maxTempRightSum){
            maxTempRightSum = rightSum;
        }
    }
    int temp = maxLeftSum>maxRightSum?maxLeftSum:maxRightSum;
    int result = temp>(maxTempLeftSum+maxTempRightSum)?temp:(maxTempLeftSum+maxTempRightSum);
    return result;
}
```
注意分治法的思想，先将数组无止境的分割，然后将两段最大值拼接，为了不漏掉分割边界的值，所以前半部分i--遍历，后半部分i++遍历。


