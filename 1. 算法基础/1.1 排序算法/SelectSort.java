package com.algorithm.leetcode.sort;

import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int [] a = new int[]{1,5,2,9,3,7,0,5};

        for (int i = 0; i < a.length; i++) {
            int index = i;
            for (int j = i+1; j < a.length; j++) {
                if (a[j]<a[index]){
                    index = j;
                }
            }
            int temp = a[i];
            a[i] = a[index];
            a[index] = temp;
        }

        Arrays.stream(a).forEach(System.out::print);
    }
}
