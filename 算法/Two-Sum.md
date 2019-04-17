>　　Given an array of integers, find two numbers such that they add up to a specific target number. 
　　The function twoSum should return indices of the two numbers such that they add up to the target, where index1 must be less than index2. Please note that your returned answers (both index1 and index2) are not zero-based. 
　　You may assume that each input would have exactly one solution. 
　　Input: numbers={2, 7, 11, 15}, target=9 
　　Output: index1=1, index2=2 
specific 	具体的
assume   假定

意思是：
给定一个整数数组，找出其中两个数满足相加等于你指定的目标数字。 
这个方法twoSum必须要返回能够相加等于目标数字的两个数的索引，且index1必须要小于index2。请注意一点，你返回的结果（包括index1和index2）都不是基于0开始的。你可以假设每一个输入肯定只有一个结果。

```
public int[] twoSum(int[] numbers, int target) {
    int[] result = new int[2];
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    for (int i = 0; i < numbers.length; i++) {
        if (map.containsKey(target - numbers[i])) {
            result[1] = i + 1;
            result[0] = map.get(target - numbers[i]);
            return result;
        }
        map.put(numbers[i], i + 1);
    }
    return result;
}
```








