>链表是一种物理[存储单元](https://baike.baidu.com/item/%E5%AD%98%E5%82%A8%E5%8D%95%E5%85%83)
上非连续、非顺序的[存储结构](https://baike.baidu.com/item/%E5%AD%98%E5%82%A8%E7%BB%93%E6%9E%84)，[数据元素](https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E5%85%83%E7%B4%A0)的逻辑顺序是通过链表中的[指针](https://baike.baidu.com/item/%E6%8C%87%E9%92%88)
链接次序实现的。链表由一系列结点（链表中每一个元素称为结点）组成，结点可以在运行时动态生成。每个结点包括两个部分：一个是存储[数据元素](https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E5%85%83%E7%B4%A0)的数据域，另一个是存储下一个结点地址的[指针](https://baike.baidu.com/item/%E6%8C%87%E9%92%88)
域。 相比于[线性表](https://baike.baidu.com/item/%E7%BA%BF%E6%80%A7%E8%A1%A8)[顺序结构](https://baike.baidu.com/item/%E9%A1%BA%E5%BA%8F%E7%BB%93%E6%9E%84)，操作复杂。由于不必须按顺序存储，链表在插入的时候可以达到O(1)的复杂度，比另一种线性表顺序表快得多，但是查找一个节点或者访问特定编号的节点则需要O(n)的时间，而线性表和顺序表相应的时间复杂度分别是O(logn)和O(1)。

```
package com.fantj.dataStruct.listnode;

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
```
package com.fantj.dataStruct.listnode;

/**
 * 链表
 * Created by Fant.J.
 * 2017/12/21 11:26
 */
public class LinkList {
    //头结点
    private Node first;

    public LinkList(){
        first = null;
    }
    /**
     * 插入一个结点，在头结点后进行插入
     */
    public void insertFirst(long value){
        Node node = new Node(value);
        node.next = first;
        first = node;
    }
    /**
     * 删除一个结点，在头结点后进行删除
     */
    public Node deleteFirst(){
        Node temp = first;
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

}
```
源码：[git地址](https://github.com/jiaofanting/Java-dataStruct/tree/master/src/com.fantj.dataStruct/listnode)
