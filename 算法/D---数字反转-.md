>给定一个整数，请将该数各个位上数字反转得到一个新数。新数也应满足整数的常见形式，即除非给定的原数为零，否则反转后得到的新数的最高位数字不应为零（参见样例 2） 。

>Input
输入共 1 行，一个整数 N。

>Output
输出共 1 行，一个整数，表示反转后的新数。

>Sample Input
样例 #1:
123
>样例 #2:
-380

>Sample Output
样例 #1:
321
>样例 #2:
-83

>Hint
-1,000,000,000 ≤ N ≤1,000,000,000。

```
import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/5 16:39
 */
public class Main{
    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        int data = scanner.nextInt();
        int result = 0;
        while (true){
            int n = data%10;
            result = result*10 + n;
            data /= 10; 
            if (data == 0) {break;}  
        }
        System.out.println(result);
        scanner.close();
    }
}
```
