>实现 strStr() 函数。

>给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。


### 示例

```
输入: haystack = "hello", needle = "ll"
输出: 2

输入: haystack = "aaaaa", needle = "bba"
输出: -1
```

### 答案
```
public int strStr(String haystack, String needle) {
    if (needle.isEmpty()) {
        return 0;
    }
    /**
     * 思路：将needle 看作一个整体，去和 haystack比较
     * 所以，要先拿出needle的第一个字母去找，然后判断第二个字母是否对应，知道结束
     * 如果needle的字符串长度大于 haystack ，直接返回-1
     * 如果needle的部分还没遍历玩，haystack就没值了，得判断，返回-1
     */
    if (needle.length() > haystack.length()) {
        return -1;
    }
    int index = 0;
    char[] needles = needle.toCharArray();
    //如果needle 还有值，则一直循环
        char[] chars = haystack.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == needles[index]) {
                    //拿到这个开始位置
                    int start = j;
                    //如果needle后面 没有字母,直接返回，否则的话index++，然后break继续判断
                    if (index == needle.length() - 1){
                        return start-(needle.length()-1);
                    }
                    index++;
                }
            }
    return -1;
}
```

