>在国外，每月的13号和每周的星期5都是不吉利的。特别是当13号那天恰好是星期5时，更不吉利。已知某年的一月一日是星期w，并且这一年一定不是闰年，求出这一年所有13号那天是星期5的月份，按从小到大的顺序输出月份数字。（w=1..7）

>Input
输入有一行，即一月一日星期几（w）。（1 <= w <= 7）

>Output
输出有一到多行，每行一个月份，表示该月的13日是星期五。

>Sample Input
7

>Sample Output
1
10

>Hint
1、3、5、7、8、10、12月各有31天 
4、6、9、11月各有30天 
2月有28天

```
package com.fantJ.ACM;

import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/5 16:02
 */
public class Main{
    public static void main(String []args) {

        //没有加十二月分，因为十一月+30天就可以判断12月13号是否是星期5了
        int day[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30};
        Scanner scanner = new Scanner(System.in);
        int weekday = scanner.nextInt();
        if (weekday>=1 && weekday<=7){
            //先计算一月的13号是否是星期5
            weekday = (weekday+12)%7;
            if (weekday == 5){
                System.out.println(1);
            }
            for (int i = 0;i<day.length;i++){
                weekday = (weekday+day[i])%7;
                if (weekday == 5){
                    System.out.println(i+2);
                }
            }
        }
        scanner.close();
    }
}

```
