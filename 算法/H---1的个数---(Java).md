>给定一个十进制整数N，求其对应2进制数中1的个数

>Input
第一个整数表示有N组测试数据，其后N行是对应的测试数据，每行为一个整数。

>Output
N行，每行输出对应一个输入。

>Sample Input
4
2
100
1000
66

>Sample Output
1
3
6
2
```
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/7 18:51
 */
public class Main {
    public static void main(String []args){
        List list = new ArrayList();
        String result = "";
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int []data = new int[n];
        for (int  i = 0;i<n;i++){
            data[i] = scanner.nextInt();
        }
        for (int i = 0;i<n;i++){
            if (data[i] == 0){
                result = "0";
                list.add(result);
                result = "";
                break;
            }else {
                while (true){
                    int yushu = data[i]%2;
                    data[i] /= 2;
                    result += yushu;
                    if (data[i] == 0){
                        list.add(result);
                        //初始化result
                        result = "";
                        break;
                    }
                }
            }
        }
        int count = 0;
        for (Object item:list){
            char []chars = item.toString().toCharArray();
            for (int i = 0;i<chars.length;i++){
                if (chars[i] == '1'){
                    count++;
                }
            }
            System.out.println(count);
            count = 0;
        }
    }
}
```
