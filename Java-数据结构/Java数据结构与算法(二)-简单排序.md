##### 1.冒泡排序
>冒泡排序（Bubble Sort），是一种计算机科学领域的较简单的排序算法。它重复地走访过要排序的数列，一次比较两个元素，如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成。这个算法的名字由来是因为越大的元素会经由交换慢慢“浮”到数列的顶端。
##### 2.选择排序
>选择排序（Selection sort）是一种简单直观的排序算法。它的工作原理是每一次从待排序的数据元素中选出最小（或最大）的一个元素，存放在序列的起始位置，直到全部待排序的数据元素排完。 选择排序是不稳定的排序方法。

* 每一轮遍历，都去寻找一个最小值，然后把当前的位置与最小值交换。
这样下来得到的是一组 从小到大的排序。
##### 3.插入排序
>插入排序基本操作就是将一个数据插入到已经排好序的有序数据中，从而得到一个新的、个数加一的有序数据，算法适用于少量数据的排序，时间复杂度为O(n^2)。是稳定的排序方法。插入排序的基本思想是：每步将一个待排序的纪录，按其关键码值的大小插入前面已经排序的文件中适当位置上，直到全部插入完为止。

* 先拿出第二个数(然后依次往后拿)，如果发现前面有比它小的数字，该元素后移。
可以看下动态图先做了解
[点击查看排序动态图模拟网](http://www.atool.org/sort.php)
也特别感谢这位作者
### 1.冒泡排序
略，很常见的排序
```
package com.fantj.dataStruct.simplesort;

/**
 * 冒泡排序
 * Created by Fant.J.
 * 2017/12/20 19:43
 */
public class BubbleSort {
    //小值往前排
    public static void sort(long [] arr){
        long tmp = 0;
        for (int i = 0;i < arr.length;i++){
            for (int j = arr.length-1;j>i;j--){
                if (arr[j]<arr[j-1]){
                    tmp = arr[j];
                    arr[j] = arr[j-1];
                    arr[j-1] = tmp;
                }
            }
        }
    }
}
```

### 2.选择排序
每一轮遍历，都去寻找一个最小值，然后把当前的位置与最小值交换。
这样下来得到的是一组 从小到大的排序。
```
package com.fantj.dataStruct.simplesort;

/**
 * 选择排序
 * Created by Fant.J.
 * 2017/12/20 19:56
 */
public class SelectionSort {
    public static void sort(long []arr){

        int k = 0;
        long temp = 0;
        for (int i = 0;i<arr.length-1;i++){
            k = i;
            for (int j = i;j<arr.length;j++){
                if (arr[j] < arr[k]){
                    //拿到最小值对应的index
                    k = j;
                }
            }
            //将k(最小值)和i(当前值)交换
            temp = arr[i];
            arr[i] = arr[k];
            arr[k] = temp;
        }
    }
}
```
### 3.插入排序
拿出第二个数，如果发现前面有比它小的数字，交换它们。
```
package com.fantj.dataStruct.simplesort;

/**
 * Created by Fant.J.
 * 2017/12/20 20:56
 */
public class InsertSort {
    public static void sort(long [] arr){
        int i, j;
        long target;
        //假定第一个元素被放到了正确的位置上
        //这样，仅需遍历1 - n-1
        for (i = 1; i < arr.length; i++)
        {
            j = i;
            target = arr[i];
            //如果当前值小于前面的值，就交换它们
            while (j > 0 && target < arr[j - 1])
            {
                arr[j] = arr[j - 1];
                j--;
            }
            arr[j] = target;
        }
    }
}

```
[git地址](https://github.com/jiaofanting/Java-dataStruct/tree/master/src/com.fantj.dataStruct/simplesort)
