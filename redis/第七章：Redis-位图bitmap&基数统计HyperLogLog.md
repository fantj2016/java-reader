###1. 什么是位图
  redis可以直接对数据进行位操作。
![获取hello二进制的第0位.png](http://upload-images.jianshu.io/upload_images/5786888-2f9d8bff67103715.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###2. 实例
1. setbit key offset value #给位图指定索引设置值
    上面我们给hello赋值为world，那么我们现在把它的二进制第0位改成1，再进行get hello
  ![setbit .png](http://upload-images.jianshu.io/upload_images/5786888-431b1d4f1aea4dbd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
2. getbit key offset #获取第offset位的二进制
3. bitcount key [start end] #获取位图指定范围中（start 到end）1的个数
4. bitop op destkey key [key...] #做多个Bitmap的and(交集)、or(并集)、not(非)、xor(异或)操作并将结果保存在destkey中
5. bitpos key targetBit [start] [end] #计算位图指定范围(start)到（end）的位置

###1. 什么是Hyperloglog
  极小空间完成独立数量统计。本质是个string。千万级别的存储只会消耗极少的内存(几Mb)，但是错误率比较高(0.81%)
###2. 三个命令
* pfadd key element [element... ] # 向hyperloglog添加元素
* pfcount key [key...]  #计算hyperloglog 的独立总数
* pfmerge destkey sourcekey [sourcekey...]  #合并多个hyperloglog
###3. 实例
![image.png](http://upload-images.jianshu.io/upload_images/5786888-ebab061df9f77811.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
