### Title
>给定一个非负整数数组 A， A 中一半整数是奇数，一半整数是偶数。

>对数组进行排序，以便当 A[i] 为奇数时，i 也是奇数；当 A[i] 为偶数时， i 也是偶数。

>你可以返回任何满足上述条件的数组作为答案。

提示：

2 <= A.length <= 20000
A.length % 2 == 0
0 <= A[i] <= 1000

### Demo
```
输入：[4,2,5,7]
输出：[4,5,2,7]
解释：[4,7,2,5]，[2,5,4,7]，[2,7,4,5] 也会被接受。
```

### Analyze

最笨但是最简单的做法，新new一个数组，遍历A数组将偶数放到B[0..2..4]中，以此类推，奇数同理。

### Coding

```
class Solution {
    public int[] sortArrayByParityII(int[] A) {
            Arrays.sort(A);
            int [] B = new int[A.length];
            //偶数个数
            int oushu = A.length /2;
            int oushuindex = 0;
            int jishuindex = 1;
            for (int i=0;i<A.length;i++){
                if (A[i] % 2 == 0){
                    for (int j = 0;j< oushu;j++){
                        B[oushuindex] = A[i];
                        if (oushuindex+2 <= A.length){
                            oushuindex += 2;
                        }
                        break;
                    }
                }else {
                    for (int j = 0;j< oushu;j++){
                        B[jishuindex] = A[i];
                        if (jishuindex+2 <= A.length){
                            jishuindex += 2;
                        }
                        break;
                    }
                }
            }
            return B;
        }
}
```