>![image](http://upload-images.jianshu.io/upload_images/5786888-5e9b46b1e0eca944?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
如上图，有3个方格，每个方格里面都有一个整数a1，a2，a3。已知0 <= a1, a2, a3 <= n，而且a1 + a2是2的倍数，a2 + a3是3的倍数， a1 + a2 + a3是5的倍数。你的任务是找到一组a1，a2，a3，使得a1 + a2 + a3最大。

>Input
一行，包含一个整数n (0 <= n <= 100)。

>Output
一个整数，即a1 + a2 + a3的最大值。

>Sample Input
3

>Sample Output
5
```
import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/5 17:30
 */
public class Main {
    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int result = 0;
        int max = 0;
        for (int a1 =n;a1>=0;a1--){
            for (int a2 = n;a2>=0;a2--){
                for (int a3 = n;a3>=0;a3--){
                    if ((a1+a2)%2==0 && (a2+a3)%3==0 && (a1+a2+a3)%5==0){
                        result = a1+a2+a3;
                        max = max>result?max:result;
                    }
                }
            }
        }
        System.out.println(max);
    }
}
```
