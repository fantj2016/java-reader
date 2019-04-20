### 题目
给定两个二叉树，编写一个函数来检验它们是否相同。

如果两个树在结构上相同，并且节点具有相同的值，则认为它们是相同的。

### 示例
```
输入:       1         1
          / \       / \
         2   3     2   3

        [1,2,3],   [1,2,3]

输出: true
```
```
输入:      1          1
          /           \
         2             2

        [1,2],     [1,null,2]

输出: false
```
```
输入:       1         1
          / \       / \
         2   1     1   2

        [1,2,1],   [1,1,2]

输出: false
```


### 分析
如果两树都是空，则返回true

如果有一个树是空，另一个不是空，返回false

如果两树都不是空，则比较val，若不等返回false，若相等：分别遍历左支和右支。

### Coding
```
public boolean isSameTree(TreeNode p, TreeNode q) {
    /**
     * 判断两树是否为空
     */
    if (p != null && q!= null){
        if (p.val != q.val){
            return false;
        }else {
            return isSameTree(p.right,q.right)&&isSameTree(p.left,q.left);
        }
    }else {
        if (p == null && q==null){
            return true;
        }else {
            return false;
        }
    }
}
```