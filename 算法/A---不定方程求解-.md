* problem:
给定正整数a，b，c。求不定方程 ax+by=c 关于未知数x和y的所有非负整数解组数。
* input:
一行，包含三个正整数a，b，c，两个整数之间用单个空格隔开。每个数均不大于1000。
* output:
一个整数，即不定方程的非负整数解组数。
* input demo:
2 3 18
* output demo:
4
---
```
package com.fantJ.ACM;

import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/3 21:37
 */
public class A不定方程求解 {
    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        String data = scanner.nextLine();
        String []datas = data.split(" ");
        Integer a = Integer.valueOf(datas[0]);
        Integer b = Integer.valueOf(datas[1]);
        Integer c = Integer.valueOf(datas[2]);
        int count = 0;
//        System.out.println(c/a);
        //拿到了a、b、c 的值，问题的关键是减少遍历次数
        for (int i = 0;i<= (c/a) ;i++){
            for (int j = 0;j<= (c/b); j++){
                if (a*i+b*j==c){
                    count++;
//                    System.out.println("i="+i+";j="+j);
                }
            }
        }
        System.out.print(count);

    }
}
```
