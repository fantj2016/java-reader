###1.栈
先进后出，后进先出
>栈（stack）又名堆栈，它是一种运算受限的线性表。其限制是仅允许在表的一端进行插入和删除运算。这一端被称为栈顶，相对地，把另一端称为栈底。向一个栈插入新元素又称作进栈、入栈或压栈，它是把新元素放到栈顶元素的上面，使之成为新的栈顶元素；从一个栈删除元素又称作出栈或退栈，它是把栈顶元素删除掉，使其相邻的元素成为新的栈顶元素。
![stack.png](http://upload-images.jianshu.io/upload_images/5786888-2862cf08582e5dd3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
package com.fantj.dataStruct.stack;

/**
 * 栈
 * Created by Fant.J.
 * 2017/12/21 10:20
 */
public class MyStack {
    //底层是一个数组
    private long []arr;
    private int top;
    /**
     * 默认构造方法
     */
    public MyStack(){
        arr = new long[10];
        top = -1;
    }
    /**
     * 带参数的构造方法
     */
    public MyStack(int maxsize){
        arr = new long[maxsize];
        top = -1;
    }
    /**
     * 添加数据
     */
    public void push(int value){
        arr[++top] = value;
    }
    /**
     * 移除pop数据
     */
    public long pop(){
        return arr[top--]; //返回数据并递减
    }
    /**
     * 查看数据
     */
    public long peek(){
        return arr[top];  //返回数据
    }
    /**
     * 判断 是否是空
     */
    public boolean isEmpty(){
        return top == -1;  //top为-1，就是空
    }
    /**
     * 判断 是否满了
     */
    public boolean isFull(){
        return top == arr.length-1;
    }
}

```
###2.队列
先进先出
>队列是一种特殊的[线性表](https://baike.baidu.com/item/%E7%BA%BF%E6%80%A7%E8%A1%A8)，特殊之处在于它只允许在表的前端（front）进行删除操作，而在表的后端（rear）进行插入操作，和栈一样，队列是一种操作受限制的线性表。进行插入操作的端称为队尾，进行删除操作的端称为队头
![queue.png](http://upload-images.jianshu.io/upload_images/5786888-ae5bbca06e3e6400.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
package com.fantj.dataStruct.queue;

/**
 * 队列（先进先出）
 * Created by Fant.J.
 * 2017/12/21 10:44
 */
public class MyQueue {
    //底层使用数组
    private long []arr;
    //有效数据的多少
    private int elements;
    //队头
    private int front;
    //队尾
    private int end;
    /**
     * 默认构造方法
     */
    public MyQueue(){
        arr = new long[10];
        elements = 0;
        front = 0;
        end = -1;
    }
    /**
     * 带参数的构造方法，参数为数组大小
     */
    public MyQueue(int maxsize){
        arr = new long[maxsize];
        elements = 0;
        front = 0;
        end = -1;
    }
    /**
     * 添加数据，从队尾插入
     */
    public void insert(long value){
        arr[++end] = value;
        elements++;
    }
    /**
     * 删除数据，从队尾删除
     */
    public long remove(){
        elements--;
        return arr[front++];
    }
    /**
     * 查看数据，从对头查看
     */
    public long peek(){
        return arr[front];
    }
    /**
     * 判断是否为空
     */
    public boolean isEmpty(){
        return elements == 0;
    }
    /**
     * 判断是否满了
     */
    public boolean isFull(){
        return  elements == arr.length;
    }
}
```
但是普通队列有个问题，就是你在进行一轮存取数据操作后，因为end=arr.length-1;front = arr.lenth-1;所以，会报错越界异常。因此我们会在插入删除前加入判断，让这个队列循环利用。   --循环队列
###2.队列pro --循环队列
>为充分利用向量空间，克服"[假溢出](https://baike.baidu.com/item/%E5%81%87%E6%BA%A2%E5%87%BA)"现象的方法是：将向量空间想象为一个首尾相接的圆环，并称这种向量为循环向量。存储在其中的队列称为循环队列（Circular Queue）。这种循环队列可以以[单链表](https://baike.baidu.com/item/%E5%8D%95%E9%93%BE%E8%A1%A8)的方式来在实际编程应用中来实现。

修改两处：insert和remove
```
/**
     * 添加数据，从队尾插入
     */
    public void insert(long value){
        if (end == arr.length-1){
            end = -1; //如果到达了队列尽头，初始化end
        }
        arr[++end] = value;
        elements++;
    }
    /**
     * 删除数据，从队尾删除
     */
    public long remove(){
        long value = arr[front++];
        if (front == arr.length){
            front=0; //如果到达了队列尽头，初始化front
        }
        elements--;
        return value;
    }
```
源码地址：[git地址](https://github.com/jiaofanting/Java-dataStruct/tree/master/src/com.fantj.dataStruct/queue)

