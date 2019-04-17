###一、希尔排序的产生
>希尔排序(Shell Sort)是[插入排序](https://baike.baidu.com/item/%E6%8F%92%E5%85%A5%E6%8E%92%E5%BA%8F)的一种。也称缩小[增量](https://baike.baidu.com/item/%E5%A2%9E%E9%87%8F)排序，是直接插入排序算法的一种更高效的改进版本。希尔排序是非稳定排序算法。该方法因DL．Shell于1959年提出而得名。
希尔排序是把记录按下标的一定增量分组，对每组使用直接插入排序算法排序；随着增量逐渐减少，每组包含的关键词越来越多，当增量减至1时，整个文件恰被分成一组，算法便终止。

###二、希尔排序是基于插入排序的以下两点性质而提出改进方法的：
插入排序在对几乎已经排好序的数据操作时，效率高，即可以达到线性排序的效率。
但插入排序一般来说是低效的，因为插入排序每次只能将数据移动一位。
![希尔排序.png](http://upload-images.jianshu.io/upload_images/5786888-5db6ad4c21dd7e29.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![希尔排序.png](http://upload-images.jianshu.io/upload_images/5786888-8d4cd18d50f123c0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
```
public static void main(String [] args)
{
    int[]a={49,38,65,97,76,13,27,49,78,34,12,64,1};
        System.out.println("排序之前：");
        for(int i=0;i<a.length;i++)
        {
            System.out.print(a[i]+" ");
        }
        //希尔排序
        int d=a.length;
            while(true)
            {
                d=d/2;
                for(int x=0;x<d;x++)
                {
                    for(int i=x+d;i<a.length;i=i+d)
                    {
                        int temp=a[i];
                        int j;
                        for(j=i-d;j>=0&&a[j]>temp;j=j-d)
                        {
                            a[j+d]=a[j];
                        }
                        a[j+d]=temp;
                    }
                }
                if(d==1)
                {
                    break;
                }
            }
            System.out.println();
            System.out.println("排序之后：");
                for(int i=0;i<a.length;i++)
                {
                    System.out.print(a[i]+" ");
                }
    }
```
或者
```
package com.fantj.dataStruct.shellSort;

/**
 * Created by Fant.J.
 * 2017/12/21 20:49
 */
public class ShellSort {
    /**
     * 排序方法
     */
    public static void sort(long []arr){
        //初始化一个间隔
        int h =1;
        //计算最大间隔
        while (h < arr.length/3){
            h = h * 3 + 1;
        }
        while (h > 0){
            //进行插入排序
            long temp = 0;
            for (int i = 1; i< arr.length ;i++){
                temp = arr[i];
                int j = i;
                while (j>h-1 && arr[j-h] >= temp){
                    arr[j] = arr[j-1];
                    j -= h;
                }
                arr[j] = temp;
            }
            //减小间隔
            h = (h -1) / 3;
        }
    }
}

```
