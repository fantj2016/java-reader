

### 实验1：简单认识
随便写一个简单的程序
```
public class Test {
    public static void main(String[] args) {
        int a = 0,b=2;
        try {
            a = b/0;
        }catch (Exception e){
            a = 1;
        }
    }
}
```
看一下字节码指令过程：
```
 public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=4, args_size=1
         0: iconst_0        push 0
         1: istore_1        pop并保存到局部变量1
         2: iconst_2       push 2
         3: istore_2       pop并保存到局部变量2
         4: iload_2        从局部变量里拿出并push
         5: iconst_0        push 0 
         6: idiv          栈顶两数相除
         7: istore_1
         8: goto          14
        11: astore_3
        12: iconst_1
        13: istore_1
        14: return
      Exception table:
         from    to  target type
             4     8    11   Class java/lang/Exception
      LineNumberTable:
        line 8: 0
        line 10: 4
        line 13: 8
        line 11: 11
        line 12: 12
        line 14: 14
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
           12       2     3     e   Ljava/lang/Exception;
            0      15     0  args   [Ljava/lang/String;
            2      13     1     a   I
            4      11     2     b   I
      StackMapTable: number_of_entries = 2
        frame_type = 255 /* full_frame */
          offset_delta = 11
          locals = [ class "[Ljava/lang/String;", int, int ]
          stack = [ class java/lang/Exception ]
        frame_type = 2 /* same */
}
```
可以看到有个异常表：
```
      Exception table:
         from    to  target type
             4     8    11   Class java/lang/Exception
```
`from`表示`try catch`的开始地址
`to`表示`try catch`的结束地址
`target`表示异常的处理起始位
`type`表示异常类名称

代码运行时出错时，会先判断出错位置是否在`from - to`的范围，如果是，则从`target`标志位往下执行，如果没有出错，直接`goto` 到` return`。可以看出，如果代码不出错的话，性能几乎是不受影响的，和正常的代码执行是一样的。

>那异常处理耗时是什么个概念呢？

### 实验二：异常处理耗时测试
```
public class Test {
    public static void main(String[] args) {
        int a = 0,b=2;
        long startTime = System.nanoTime();
        for (int i = 10; i>0;i--){
            try {
                a = b/i;
            }catch (Exception e){
                a = 1;
            }finally {

            }
        }
        long runTime = System.nanoTime()-startTime;
        System.out.println(runTime);
    }
}
```
我只需要把`i>0`改成`i>=0`，程序遍会进行一次异常处理，因为除数不能为0.

我在修改之前(无异常运行)，运行的结果是`1133`
修改之后(会出现除数为0异常)，运行结果是`44177`

当然，这个结果和cpu的算力有关，多次运行结果相差无几。

**所以，可以看出一旦程序进入到catch里，是非常耗资源的。**

>那try  catch 在for循环外面或者里面，哪个更好呢？

### 实验三：for循环在try里面

```
public class Test {
    public static void main(String[] args) {
        int a = 0,b=2;
        long startTime = System.nanoTime();
        try {
            for (int i = 10; i>=0;i--){
                    a = b/i;
            }
        }catch (Exception e){
            a = 1;
        }finally {
            long runTime = System.nanoTime()-startTime;
            System.out.println(runTime);
        }
    }
}
```
运行多次的控制台输出:
```
46820     48708 54749  47953   46820  45310
```
```
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=5, args_size=1
         0: iconst_0
         1: istore_1
         2: iconst_2
         3: istore_2
         4: bipush        10
         6: istore_3
         7: iload_3
         8: iflt          21
        11: iload_2
        12: iload_3
        13: idiv
        14: istore_1
        15: iinc          3, -1
        18: goto          7
        21: goto          35
        24: astore_3
        25: iconst_1
        26: istore_1
        27: goto          35
        30: astore        4
        32: aload         4
        34: athrow
        35: return
      Exception table:
         from    to  target type
             4    21    24   Class java/lang/Exception
             4    21    30   any
            24    27    30   any
            30    32    30   any
```

### 实验四：try在for循环外面
```
public class Test {
    public static void main(String[] args) {
        int a = 0,b=2;
        long startTime = System.nanoTime();
        for (int i = 10; i>=0;i--){
            try {
                a = b/i;
            }catch (Exception e){
                a = 1;
            }finally {

            }
        }
                long runTime = System.nanoTime()-startTime;
                System.out.println(runTime);
    }
}
```
控制台打印：
```
42289  47953  49463  45688  45310
```
```
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=6, args_size=1
         0: iconst_0
         1: istore_1
         2: iconst_2
         3: istore_2
         4: bipush        10
         6: istore_3
         7: iload_3
         8: iflt          36
        11: iload_2
        12: iload_3
        13: idiv
        14: istore_1
        15: goto          30
        18: astore        4
        20: iconst_1
        21: istore_1
        22: goto          30
        25: astore        5
        27: aload         5
        29: athrow
        30: iinc          3, -1
        33: goto          7
        36: return
      Exception table:
         from    to  target type
            11    15    18   Class java/lang/Exception
            11    15    25   any
            18    22    25   any
            25    27    25   any
```

**综合实验三和实验四，我们发现无论从运行时长还是从字节码指令的角度看，它两的性能可以说是一样的。并没有你感觉到的for循环里放try代码会冗余、资源消耗加倍的问题。**

但是从运行逻辑来看，两个是有点不同的，实验三中，因为for在try catch里，所以jvm在编译的时候，把异常处理放在for循环后面才进行。即：`第24-27行`；实验四中，异常处理是在for循环内部的，即：`第18-22行`。大同小异。

以上仅个人测试观点，如果有误请在下方留言，谢谢！
