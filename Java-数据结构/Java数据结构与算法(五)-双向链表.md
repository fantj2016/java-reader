* 什么是双向链表
每个结点除了保存了xui下一个结点的引用，同时还保存这对前一个节点的引用。
* 从头部进行哈如
要对链表进行判断，如果为空则这是尾结点为信添加的结点。如果不为空，还需要设置投结点的前一个结点为心田的结点。
* 从尾部进行插入
如果链表为空，则直接设置头结点为新添加的结点，否则设置尾结点的后一个结点为新添加的结点。同时设置新添加的结点的前一个结点为尾结点。
* 从头部进行删除
判断头结点是否有下一个结点，如果没有则设置为结点为null。否则设置头结点的下一个结点的previous为null。
* 从尾部进行删除
如果头结点后没有其他结点，则设置尾结点为null。否则设置尾结点前一个结点的next为null。设置尾结点为其前一个结点。
* 删除方法
不需要再使用一个0时的指针域。
```
package com.fantj.dataStruct.doublelistnode;

/**
 * 双向链表,比双端链表多了一个头结点的指向
 * Created by Fant.J.
 * 2017/12/21 19:49
 */
public class DoubleLinkList {
    //头结点
    private Node first;
    //尾结点
    private Node last;

    public DoubleLinkList(){
        first = null;
    }
    /**
     * 插入一个结点，在头结点后进行插入
     */
    public void insertFirst(long value){
        Node node = new Node(value);
        //如果是第一次插入
        if (isEmpty()){
            last = node;
        }else {
            first.previous = node;
        }
        node.next = first;
        first = node;
    }
    /**
     * 插入一个结点，从尾结点进行插入
     */
    public void insertLast(long value){
        Node node = new Node(value);
        if (isEmpty()){
            first = node;
        }else {
            last.next = node;
            node.previous = last;
        }
        last = node;
    }
    /**
     * 删除一个结点，在头结点后进行删除
     */
    public Node deleteFirst(){
        Node temp = first;
        if (first.next == null){
            last = null;
        }else {
            first.next.previous = null;
        }
        first = temp.next;
        return temp;
    }
    /**
     * 显示方法
     */
    public void display(){
        Node current = first;
        while (current != null){
            current.display();   //打印结点
            current = current.next;
        }
    }
    /**
     * 查找方法
     */
    public Node find(long value){
        Node current = first;
        while (current.data != value){
            if (current.next == null){
                return null;
            }
            current = current.next;//继续往下找
        }
        return current;
    }
    /**
     * 删除方法,根据数据域来进行删除
     */
    public Node delete(long value){
        Node current = first;
        Node previous = first;//表示前一个结点
        while (current.data != value){
            if (current.next == null){
                return null;
            }
            previous = current; //提取出当前结点作为前一个结点（用该结点的next指向删除结点的后一个结点）
            current = current.next; //继续往下找
        }
        if (current == first){
            first = first.next;
        }else {
            previous.next = current.next;
        }
        return current;
    }
    /**
     * 判断是否为空
     */
    public boolean isEmpty(){
        return (first == null);
    }
}
```
```
package com.fantj.dataStruct.doublelistnode;

/**
 * 链表结构，链结点
 * Created by Fant.J.
 * 2017/12/19 22:19
 */
public class Node {
    //数据域
    public long data;
    //结点域(指针域)
    public Node next;
    public Node previous;


    public Node(long value){
        this.data = value;
    }

    /**
     * 显示方法
     */
    public void display(){
        System.out.print(data+" ");
    }
}
```
查看源码：[git地址](https://github.com/jiaofanting/Java-dataStruct/tree/master/src/com.fantj.dataStruct/doublelistnode)
