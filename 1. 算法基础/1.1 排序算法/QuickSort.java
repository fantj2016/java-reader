package com.algorithm.leetcode.sort;

import java.util.Arrays;

public class QuickSort {
    public static void main(String[] args) {
        int [] a = new int[]{1,5,2,9,3,7,0,5};
        a = quick(a,0,a.length-1);
        Arrays.stream(a).forEach(System.out::print);
    }

    private static int[] quick(int[] a, int low, int high) {
        int start  = low;
        int end = high;
        int baseKey = a[start];
        while (start<end){
            while (start<end && baseKey<=a[end]){
                end--;
            }
            if (baseKey>a[end]){
                int temp = a[end];
                a[end] = a[start];
                a[start] = temp;
            }
            while (start<end && baseKey >= a[start]){
                start++;
            }
            if (baseKey<a[start]){
                int temp = a[start];
                a[start] = a[end];
                a[end] = temp;
            }
        }
        if (start>low) quick(a,low,start);
        if (end<high) quick(a,end+1,high);
        return a;
    }

}
