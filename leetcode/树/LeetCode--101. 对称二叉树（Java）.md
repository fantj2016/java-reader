### title
给定一个二叉树，检查它是否是镜像对称的。

### demo

例如，二叉树 [1,2,2,3,4,4,3] 是对称的。

```
    1
   / \
  2   2
 / \ / \
3  4 4  3
```
但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的:
```
    1
   / \
  2   2
   \   \
   3    3
```


### 分析

如果树是空，则返回true

如果有一个分支是空，另一个不是空，返回false

如果两分支都不是空，则比较val，若不等返回false，若相等：遍历左支和右支并比较val。


### Coding
```
public boolean isSymmetric(TreeNode root) {
    /**
     * 将该树分成左右两半，然后遍历比较
     */
    if (root == null){ return true; }
    return isEqual(root.left,root.right);
}
public boolean isEqual(TreeNode leftBranch,TreeNode rightBranch){
    if (leftBranch == null&&rightBranch == null){
        return true;
    }
    if (leftBranch == null || rightBranch ==null){
        return false;
    }
    return leftBranch.val==rightBranch.val
            &&isEqual(leftBranch.left,rightBranch.right)
            &&isEqual(leftBranch.right,rightBranch.left);
}
```