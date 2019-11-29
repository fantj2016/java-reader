### Title
给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现了三次。找出那个只出现了一次的元素。

>说明：

你的算法应该具有线性时间复杂度。 你可以不使用额外

空间来实现吗？

### Demo
```
示例 1:

输入: [2,2,3,2]
输出: 3
示例 2:

输入: [0,1,0,1,0,1,99]
输出: 99
```

### Analyze

要做到保证线性时间复杂度和不开销新空间。根据题意对数组排序更加方便解答。然后依次走三个长度来判断是否满足，注意一点：头部和尾部。

### Coding

```
public int singleNumber(int[] nums) {
    Arrays.sort(nums);
    if (nums.length <= 1){
        return nums[0];
    }
    if (nums[0] != nums[1]){
        return nums[0];
    }
    if (nums[nums.length-1] != nums[nums.length-2]){
        return nums[nums.length-1];
    }
    for (int i = 3; i<nums.length-3;i+=3){
        if (nums[i] != nums[i+1]){
            return nums[i];
        }
    }
    return 0;
}
```