>有的时候对一种编码信号需要转换成另一种信号以便于发送或达到其他目的。考虑一种字母信号，只有ABCD四个大写字母组成。要求给定一串字母信号，转为数字信号，转化方式为：A转为数值1，B转为数值2，C表示符号$，D表示换行符。给定一串字母信号，求其数字信号。
例如字母信号：
AABBCBACADAB
转化后数字信号为：
1122$21$1
12

>Input
只有一行数据，由一串字符信号组成，长度小于500。

>Output
输出转换后的数字信号

>Sample Input
AABBCBACADAB

>Sample Output
1122$21$1
12

```
import java.util.Scanner;

/**
 * A转为数值1，B转为数值2，C表示符号$，D表示换行符。给定一串字母信号，求其数字信号。
 * Created by Fant.J.
 * 2017/12/7 19:19
 */
public class Main {
    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        String data = scanner.nextLine();
        char []chars = data.toCharArray();
        String result = "";
        for (int i = 0;i<chars.length;i++){
            if (chars[i] == 'A'){
                result += "1";
            }
            if (chars[i] == 'B'){
                result += "2";
            }
            if (chars[i] == 'C'){
                result += "$";
            }
            if (chars[i] == 'D'){
                result += "\n";
            }
        }
        System.out.println(result);
    }
}
```
