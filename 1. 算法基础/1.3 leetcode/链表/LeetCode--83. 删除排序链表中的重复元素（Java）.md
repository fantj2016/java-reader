
>给定一个排序链表，删除所有重复的元素，使得每个元素只出现一次。

### 示例
```
输入: 1->1->2
输出: 1->2

输入: 1->1->2->3->3
输出: 1->2->3
```


### 分析
1. 很简单的一个链表题。
2. 链表题我们经常会复制一份链表来进行操作，最后返回原链表。因为current链表在经过处理之后，它的当前指针已不在初始位置。

### Coding
```
public ListNode deleteDuplicates(ListNode head) {
    ListNode current = head;
    while (current != null && current.next != null){
        //如果当前val和下一节点的val相等，则将该节点的next指向下下节点,
        //否则的话，将当前指针往后移
        if (current.val == current.next.val){
            current.next = current.next.next;
        }else {
            current = current.next;
        }
    }
    return head;
}
```
