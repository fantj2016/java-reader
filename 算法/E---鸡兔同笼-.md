>一个笼子里面关了鸡和兔子（鸡有2只脚，兔子有4只脚，没有例外）。已经知道了笼子里面脚的总数a，问笼子里面至少有多少只动物，至多有多少只动物。

>Input
一行，一个正整数a (a < 32768)。

>Output
一行，包含两个正整数，第一个是最少的动物数，第二个是最多的动物数，两个正整数用一个空格分开。 
如果没有满足要求的答案，则输出两个0，中间用一个空格分开。

>Sample Input
20

>Sample Output
5 10

```
import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/5 17:10
 */
public class Main {
    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        //总数肯定是2的倍数
        if (count%2 == 0){
            //如果全是鸡，总数最多  count/2
            int max = count/2;
            //鸡尽可能的少，总数就最少
            int tuzi = count/4;
            int ji = (count%4)/2;
            int min = tuzi+ji;
            System.out.print(min+" "+max);
        }else {
            System.out.println(0+" "+0);
        }
    }
}
```
