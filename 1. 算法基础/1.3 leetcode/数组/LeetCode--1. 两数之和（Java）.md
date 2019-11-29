>给定一个整数数组和一个目标值，找出数组中和为目标值的两个数。
你可以假设每个输入只对应一种答案，且同样的元素不能被重复利用。

### 示例:
```
给定 nums = [2, 7, 11, 15], target = 9

因为 nums[0] + nums[1] = 2 + 7 = 9
所以返回 [0, 1]
```
#### 最初答案：
```
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int result[] =  new int[2];
        for (int i = 0;i<nums.length;i++){
            for (int j = 1;j<nums.length;j++){
                if (i==j){
                    break;
                }
                if (target == nums[i]+nums[j]){
//                    System.out.println("i:"+i+"j:"+j);
                    result[0]=i;
                    result[1]=j;
                }
            }
        }
        return result;
    }
}
```
### 完善答案：
```
    public int[] twoSum(int[] nums, int target) {
        for (int i = 0;i<nums.length;i++){
            for (int j = i+1;j<nums.length;j++){
                if (target == nums[i]+nums[j]){
                    return new int[]{i,j};
                }
            }
        }
        throw new RuntimeException();
    }
```
可以看到主要修改了三处：
1. 用`j=i+1`省去了`i==j`的判断
2. 用`return new int[]{i,j}`省去了好几行代码。
3. 用`throw new RuntimeException();`解决没有返回值报错的问题。


### 思考
暴力法思想很简单，但是时间复杂度高(O(n²))，空间复杂度(O(1))。

还有什么时间方式呢？
哈希法：
```
public int[] twoSum(int [] nums,int target){
    Map<Integer,Integer> map = new HashMap<>();
    for (int i = 0;i<nums.length;i++) {
        map.put(nums[i],i);
        int dv = target - nums[i];
        if (map.containsKey(dv)){
            return new int[]{map.get(dv),i};
        }
    }
    throw new RuntimeException();
}
```
但是我这样的代码是有问题的，我跑了下：
```
输入：
[3,2,4]
6
输出：
[0,0]
预期：
[1,2]
```
看来，我们需要处理第一个数，如果map中不提前放第一个数，则会自动空过第一个数，或者map在if条件后再进行赋值。
```
public int[] twoSum(int [] nums,int target){
    Map<Integer,Integer> map = new HashMap<>();
    for (int i = 0;i<nums.length;i++) {
        int dv = target - nums[i];
        if (map.containsKey(dv)){
            return new int[]{map.get(dv),i};
        }
        map.put(nums[i],i);
    }
    throw new RuntimeException();
}
```
好了，满分通过。可以看到，该方法的时间复杂度和空间复杂度都是O(n).