找最小的和第一个位置做交换，递归下去
```
#include <iostream>

using  namespace std;

void selectionSort(int arr[],int n) {
    for (int i = 0; i < n; ++i) {
        //寻找【i,n】区间的最小值
        int minIndex = i;
        for (int j = i + 1; j < n; ++j) {
            if (arr[j] < arr[minIndex]) minIndex = j;
            swap(arr[i], arr[minIndex]);
        }
    }
}
int main() {
    int a[10] = {10,9,8,7,6,5,4,3,2,1};
    selectionSort(a,10);
    for (int i = 0; i < 10; ++i) {
        cout<<a[i]<<" ";
    }
    cout<<endl;
    return 0;
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-92cb3e82d5ecabde.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
