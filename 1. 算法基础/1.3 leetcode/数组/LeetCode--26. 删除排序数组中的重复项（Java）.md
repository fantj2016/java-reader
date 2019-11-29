>给定一个排序数组，你需要在原地删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
不要使用额外的数组空间，你必须在原地修改输入数组并在使用 O(1) 额外空间的条件下完成。

### 示例
```
给定数组 nums = [1,1,2], 
函数应该返回新的长度 2, 并且原数组 nums 的前两个元素被修改为 1, 2。 
你不需要考虑数组中超出新长度后面的元素。

给定 nums = [0,0,1,1,1,2,2,3,3,4],
函数应该返回新的长度 5, 并且原数组 nums 的前五个元素被修改为 0, 1, 2, 3, 4。
你不需要考虑数组中超出新长度后面的元素。
```


### 答案

```
public int removeDuplicates(int[] nums) {
    if (nums == null){
        return 0;
    }
    int index = 0;
    for (int i = 0;i<nums.length ;i++){
        /**
         * index=0 nums[index]表示拿出第一个当临时值
         * 如果当前元素(num[i])和 前面的元素相等，则跳过,如果不相等，把index+1并把当前元素赋值
         */
        if (nums[i] == nums[index]){
            //do nothing
        }else {
            index++;
            nums[index] = nums[i];
        }
    }
    return ++index;
}
```

### 总结

从数组中找重，去重，拿数组的第index(初始值0)个数和后面比较，如果找到重复的，不管它，继续往后找，如果找到值和num[index]不一样的，index+=1，然后将这个不一样的值给num[index]，以此类推，后来的num的前index+1位就绝对是不重复的。