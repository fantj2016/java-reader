>总体来说，面试官喜欢问一些题目简洁又清晰明了的经典算法，也不希望你浪费很长时间在上面，速度越快越好。我们甚至可以通过在理解的基础上记忆来达到目的。算法系列我将用心收集做IT要求你滚瓜烂熟的算法。


### Title

输入一个链表，输出该链表中倒数第k个结点。


### Coding

```
public class Solution {
    public ListNode FindKthToTail(ListNode head,int k) {
        // 倒数第k个就是正数第 length-k+1 个
        if (head == null || k < 0){
            return null;
        }
        ListNode prototype = head;
        int length = 1;
        while (prototype.next != null){
            prototype = prototype.next;
            length++;
        }
        if (length < k){
            return null;
        }
        for (int i = 0; i< length-k; i++){
            head = head.next;
        }
        return head;
    }
}
```
如果你能想出这个答案，你这一个题只能得至多一半的分。


面试官不要这个答案，那最优解是什么呢?
>思路：当前链表为A，复制一个链表为B，让A先走k个节点，然后B走，当A的next节点为空，则当前的B所指的元素就是倒数第K个节点。

```
public class Solution {
    public ListNode FindKthToTail(ListNode head,int k) {
        ListNode p, q;
        p = q = head;
        int i = 0;
        for(; p!=null; i++){
            if(i>=k){
                q = q.next;
            }
            p = p.next;
        }
        return i<k?null:q;
    }
}
```