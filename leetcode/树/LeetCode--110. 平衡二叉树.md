### title
给定一个二叉树，判断它是否是高度平衡的二叉树。

本题中，一棵高度平衡二叉树定义为：

一个二叉树每个节点 的左右两个子树的高度差的绝对值不超过1。

### Demo
```
给定二叉树 [3,9,20,null,null,15,7]

    3
   / \
  9  20
    /  \
   15   7
   
返回 true 。
```

```
给定二叉树 [1,2,2,3,3,null,null,4,4]

       1
      / \
     2   2
    / \
   3   3
  / \
 4   4
返回 false 。
```


### Coding
```
public class 平衡二叉树 {
    class Solution {
        public boolean isBalanced(TreeNode root) {
            if (root == null){
                return true;
            }
            //判断当前节点的  左支最大深度 和 右支最大深度
            if (Math.abs(maxDepth(root.left) - maxDepth(root.right)) > 1){
                return false;
            }
            //遍历调用每一个节点
            return isBalanced(root.left) && isBalanced(root.right);
        }

        /**
         * 计算节点的最大深度
         */
        public int maxDepth(TreeNode root) {
            if (root == null){
                return 0;
            }
            int a = maxDepth(root.left)+1;
            int b = maxDepth(root.right)+1;
            return a>b?a:b;
        }
    }
}

```