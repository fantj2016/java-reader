package com.algorithm.leetcode.sort;

import java.util.Arrays;

public class BubbleSort {
    public static void main(String[] args) {
        int [] a = new int[]{1,5,2,9,3,7,0,5};

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j]>a[j+1]){
                    int temp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = temp;
                }
            }
        }
        Arrays.stream(a).forEach(System.out::print);
    }
}
