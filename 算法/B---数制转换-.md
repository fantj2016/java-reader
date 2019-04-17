* problem:
求任意两个不同进制非负整数的转换（2进制～16进制），所给整数在long所能表达的范围之内。
不同进制的表示符号为（0，1，...，9，a，b，...，f）或者（0，1，...，9，A，B，...，F）。
* Input:
输入只有一行，包含三个整数a，n，b。a表示其后的n 是a进制整数，b表示欲将a进制整数n转换成b进制整数。 
a，b是十进制整数，2 =< a，b <= 16。
* Output
输出包含一行，该行有一个整数为转换后的b进制数。输出时字母符号全部用大写表示，即（0，1，...，9，A，B，...，F）。
* Sample Input
15 Aab3 7
* Sample Output
210306



我在这里用java原有数制转换工具实现。
```
package com.fantJ.ACM;

import java.util.Scanner;

/**
 * https://vjudge.net/contest/199742#problem/B
 * 2 =< a，b <= 16
 * Created by Fant.J.
 * 2017/12/4 10:13
 */
public class B数制转换 {
    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        String data = scanner.nextLine();
        String[] datas = data.split(" ");
        Integer transitionFrom = Integer.valueOf(datas[0]);
        String character = datas[1];
        Integer transitionTo = Integer.valueOf(datas[2]);
        try {
            if (transitionFrom >= 2 && transitionTo <= 16) {
                String result = Integer.toString(Integer.valueOf(character, transitionFrom), transitionTo);
                Integer resultNum = Integer.valueOf(result);
                System.out.print(resultNum);
            } else {
                System.out.println("非法输入");
            }
        }catch (Exception e){
            throw new RuntimeException("非法输入");
        }
    }
}
```
来看看Integer.valueOf(String s, int radix)源码
```
public static Integer valueOf(String s, int radix) throws NumberFormatException {
        return Integer.valueOf(parseInt(s,radix));
    }
```
parseInt(s,radix)源码
```
public static final int   MIN_VALUE = 0x80000000;
public static final int   MAX_VALUE = 0x7fffffff;
public static final int MIN_RADIX = 2;
public static final int MAX_RADIX = 36;

public static int parseInt(String s, int radix)
                throws NumberFormatException
    {
        /*
         * WARNING: This method may be invoked early during VM initialization
         * before IntegerCache is initialized. Care must be taken to not use
         * the valueOf method.
         */

        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < Character.MIN_RADIX) {
            throw new NumberFormatException("radix " + radix +
                                            " less than Character.MIN_RADIX");
        }

        if (radix > Character.MAX_RADIX) {
            throw new NumberFormatException("radix " + radix +
                                            " greater than Character.MAX_RADIX");
        }

        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+')
                    throw NumberFormatException.forInputString(s);

                if (len == 1) // Cannot have lone "+" or "-"
                    throw NumberFormatException.forInputString(s);
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++),radix);
                if (digit < 0) {
                    throw NumberFormatException.forInputString(s);
                }
                if (result < multmin) {
                    throw NumberFormatException.forInputString(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw NumberFormatException.forInputString(s);
                }
                result -= digit;
            }
        } else {
            throw NumberFormatException.forInputString(s);
        }
        return negative ? result : -result;
    }
```
然后再分析Character.digit方法
emmmm  java用Unicode在处理。

```
int digit(int ch, int radix) {
        int value = -1;
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
            int val = getProperties(ch);
            int kind = val & 0x1F;
            if (kind == Character.DECIMAL_DIGIT_NUMBER) {
                value = ch + ((val & 0x3E0) >> 5) & 0x1F;
            }
            else if ((val & 0xC00) == 0x00000C00) {
                // Java supradecimal digit
                value = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
            }
        }
        return (value < radix) ? value : -1;
    }
```
知识补充：

* 2 to 8：以10010为例，要转成8进制，则从右向左看，每3个为一组，不足的补零，变成010 010，加上权值后为22，即为8进制数！

* 8 to 2：与上面的相反，以27为例，要转为2进制，则每个位作为一组分开，变成2 7，通过权值变换后为010 111(为1的替换为权值，然后相加等于7，则4+2+1，即每个位都是1，故为111)，最后得到的2进制数为010111，去掉左边的0，最终结果是10111。
* 2 to 16: 以101110为例，要转成16进制，类似，从右向左看，每4个为一组，不足的补零，变成0010 1110，加上权值后为2E，有个规律，8进制的各个位<=7，16进制的各个位<=15，也就是说16进制中的数可以是1、2、3、4……9、A、B、C、D、E、F。

* 16 to 2：以EF为例，每个为作为一组分开，变成E F，通过权值变换后为1110 1111，最后得到的2进制是 11101111
* 8 to 16：以27为例，8进制和16进制之间的转换需要用2进制来作为过渡，先转成2进制为010 111，然后从右向左数，将现在的3个一组变为4个一组，不足的补零，变为0001 0111，然后权值变换后为1 7，也就是16进制数 17
