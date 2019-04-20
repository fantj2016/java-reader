>罗马数字包含以下七种字符：I， V， X， L，C，D 和 M。
```
字符          数值
I             1
V             5
X             10
L             50
C             100
D             500
M             1000
```
>例如， 罗马数字 2 写做 II ，即为两个并列的 1。12 写做 XII ，即为 X + II 。 27 写做  XXVII, 即为 XX + V + II 。

>通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如 4 不写做 IIII，而是 IV。数字 1 在数字 5 的左边，所表示的数等于大数 5 减小数 1 得到的数值 4 。同样地，数字 9 表示为 IX。这个特殊的规则只适用于以下六种情况：

>I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。
X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。 
C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。
给定一个罗马数字，将其转换成整数。输入确保在 1 到 3999 的范围内。


### 示例 
```
输入: "III"
输出: 3

输入: "IV"
输出: 4

输入: "IX"
输出: 9

输入: "LVIII"
输出: 58
解释: C = 100, L = 50, XXX = 30, III = 3.

输入: "MCMXCIV"
输出: 1994
解释: M = 1000, CM = 900, XC = 90, IV = 4.
```


### 错误代码
```
输入：
"III"
输出：
0
预期：
3
```
```
public int romanToInt(String s) {
    Map<String,Integer> map = new HashMap<>();
    map.put("I",1);
    map.put("V",5);
    map.put("X",10);
    map.put("L",50);
    map.put("C",100);
    map.put("D",500);
    map.put("M",1000);
    int result = 0;
    char[] chars = s.toCharArray();
    for (int i = 0;i<chars.length;i++){
        if (map.containsKey(chars[i])){
            int value = map.get(chars[i]);
                //如果result比拿到的数大 则相加，小则相减
                if (result >= value) {
                    result += value;
                } else {
                    result -= value;
            }

        }
    }
    return result;
}
```
逻辑看起来没有问题，但是有很多小问题：
1. 应该比较相邻的两位数的大小关系。
2. 应该关注是否是最后一位数，如果是最后一位数，后面就没有数可比，直接相加。
3. 没有将chars[i]进行转型，"I"和'I'比较返回false。map直接定义成`<Character,Integer>`更好。

### 答案
```
public int romanToInt(String s) {
    Map<Character,Integer> map = new HashMap<>();
    map.put('I',1);
    map.put('V',5);
    map.put('X',10);
    map.put('L',50);
    map.put('C',100);
    map.put('D',500);
    map.put('M',1000);
    int result = 0;
    char[] chars = s.toCharArray();
    for (int i = 0;i<chars.length;i ++){
        if (map.containsKey(chars[i])){
            int value = map.get(chars[i]);
            if ((i == chars.length - 1) || map.get(chars[i+1]) <= map.get(chars[i])) {
                result += value;
            } else {
                result -= value;
            }
        }
    }
    return result;
}
```
### 总结
1. 需要关注相邻两元素值来做解答时，一定要关注当前的值是否是尾数。
