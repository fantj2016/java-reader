>IP地址总是由4个0-255的数字以"."隔开的形式来显示给用户，例如192.168.0.1。在计算机中，一个IP地址用4字节来依次存储其从右到左的4个数字部分，每个字节（8比特）以2进制的形式存储相应的IP地址数字，请你实现一个从IP地址的显示格式到计算机存储格式的转换。

>Input
每行输入一个IP地址，如果输入为-1，结束输入

>Output
每行输出一个IP地址在计算机存储中以二进制表示的4字节内容

>Sample Input
192.168.0.1
255.255.0.0
1.0.0.1
-1

>Sample Output
11000000101010000000000000000001
11111111111111110000000000000000
00000001000000000000000000000001
```
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Fant.J.
 * 2017/12/6 19:20
 */
public class Main {
    private static List list = new ArrayList();
    private static String ipAddr = null;
    private static String result = "";
    private static int count = 1;

    public static void main(String []args){
        Scanner scanner = new Scanner(System.in);
        ipAddr = scanner.nextLine();
        while (! ipAddr.contains("-1")){

            String[] datas = ipAddr.split("\\.");
            int data1 = Integer.valueOf(datas[0]);
            int data2 = Integer.valueOf(datas[1]);
            int data3 = Integer.valueOf(datas[2]);
            int data4 = Integer.valueOf(datas[3]);
            pincou(data4);
            pincou(data3);
            pincou(data2);
            pincou(data1);
           // System.out.println(result.length());
            list.add(result);
            //初始化 三个参数
            result="";
            count = 1;
            ipAddr = scanner.nextLine();
        }
        for (Object item:list){
            System.out.println(item);
        }
    }
    public static void pincou(int data){
        while (true){
            int yushu = data%2;
            result = yushu+result;
            data /= 2;
            if (data==0){
                //判断下是否够8个字节
                int judge = result.length();
                if (judge<8*count){
                    for (int i = 0;i<8*count-judge;i++){
                        result = 0+result;
                    }
                }
                count++;
                break;
            }
        }
    }


}
```
做的过程中有个逻辑错误，找了好长时间分享给大家。
就是在获取result长度当作for循环条件的时候。我起初是用result.lenth() 获取。导致字节数不够8(不满足我的要求)就运行下面程序。之后我先把这个长度赋值给变量judge，然后在做循环。希望大家以后用循环，切忌把动态改变的东西当作条件来判断
![image.png](http://upload-images.jianshu.io/upload_images/5786888-3e69db69a4ad79e2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
