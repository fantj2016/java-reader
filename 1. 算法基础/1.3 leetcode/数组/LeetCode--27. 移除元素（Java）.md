>给定一个数组 nums 和一个值 val，你需要原地移除所有数值等于 val 的元素，返回移除后数组的新长度。

>不要使用额外的数组空间，你必须在原地修改输入数组并在使用 O(1) 额外空间的条件下完成。

>元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。


### 示例
```
给定 nums = [3,2,2,3], val = 3,

函数应该返回新的长度 2, 并且 nums 中的前两个元素均为 2。

你不需要考虑数组中超出新长度后面的元素。
```
```
给定 nums = [0,1,2,2,3,0,4,2], val = 2,

函数应该返回新的长度 5, 并且 nums 中的前五个元素为 0, 1, 3, 0, 4。

注意这五个元素可为任意顺序。

你不需要考虑数组中超出新长度后面的元素。
```

### coding

```
class Solution {
    public int removeElement(int[] nums, int val) {
        /**
         * 思路：拿出第index（初始值0）个数和后面比较，
         * 如果nums当前值等于 val ，不做任何操作
         * 如果nums当前值不等于 val，index++;  然后将值赋值给 nums[index]
         */
        int index = 0;
        for (int i = 0;i<nums.length;i++){
            if (nums[i] == val){
                //do nothing
            }else {
                nums[index] = nums[i];
                index++;
            }
        }
        return index;
    }
}
```

### 总结

这个题和26题很像，不同的是这个题需要从数组的第0位开始冲刷数组，而26题是从数组的第1位开始冲刷。

