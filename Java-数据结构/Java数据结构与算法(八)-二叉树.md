###一、为什么要使用树
* 有序数组插入、删除数据慢。
* 链表查找数据慢
* 树可以解决这两个问题
###二、相关术语
* 树的结点：包含一个数据元素及若干指向子树的分支；
* 孩子结点：结点的子树的根称为该结点的孩子；
* 双亲结点：B 结点是A 结点的孩子，则A结点是B 结点的双亲；
* 兄弟结点：同一双亲的孩子结点； 堂兄结点：同一层上结点；
* 祖先结点: 从根到该结点的所经分支上的所有结点子孙结点：以某结点为根的子树中* 任一结点都称为该结点的子孙
* 结点层：根结点的层定义为1；根的孩子为第二层结点，依此类推；
* 树的深度：树中最大的结点层
* 结点的度：结点子树的个数
* 树的度： 树中最大的结点度。
* 叶子结点：也叫终端结点，是度为 0 的结点；
* 分枝结点：度不为0的结点；
* 有序树：子树有序的树，如：家族树；
* 无序树：不考虑子树的顺序；
###三、基本操作
1. 插入结点
      - 从根结点开始查找一个相应的结点，这个结点成为新插入结点的父节点，当父节点找到后，通过判断新结点的值比父节点的值的大小来决定是连接到左子结点还是右子结点
2. 查找结点
      - 从根结点开始查找，如果查找的结点值比当前的结点的值小，则继续查找其左子树，否则查找其右子树。
3. 遍历二叉树
      *  遍历树是根据一个特定的顺序访问树的没一个节点，根据顺序的不通氛围前序、中序、后序三中遍历。
      1. 前序
          1. 访问跟节点
          2. 前序遍历左子树
          3.遍历右子树
      2. 中序
          1. 遍历左子树
          2. 访问跟节点
          3.遍历右子树
      3. 后序
          1. 遍历左子树
          2. 遍历右子树
          3.访问跟节点
4. 删除二叉树节点
*  删除是最复杂的，在删除之前首先要查找要删的节点。找到节点后，这个要删除的节点可能会有三中情况需要考虑。
1.  该节点是叶子节点，没有子节点
    -  要删除叶子节点，只需要改变该节点的父节点的引用值，将指向该节点的引用设置为null就可以了。
2.  该节点有一个子节点
    -  改变父节点的引用，将其直接指向要删除节点的子节点。
3.  该节点右两个子节点
    -  要删除右两个子节点的节点，就需要使用他的中序后继来替代该节点。

###代码实现
```
package com.fantj.dataStruct.tree;

/**
 * 二叉树结点
 * Created by Fant.J.
 * 2017/12/22 16:09
 */
public class Node {
    //数据项
    public long data;
    //左子结点
    public Node leftChild;
    //右子结点
    public Node rightChild;
    //构造方法
    public Node(long data){
        this.data = data;
    }
}
```
```
package com.fantj.dataStruct.tree;

/**
 * 二叉树
 * Created by Fant.J.
 * 2017/12/22 16:11
 */
public class Tree {
    //根结点
    public Node root;
    /** 插入结点 */
    public void insert(long value){
        //封装结点
        Node newNode = new Node(value);
        //引用当前结点
        Node current = root;
        //引用父节点
        Node parent;
        //如果root为null，也就是第一次插入的时候
        if (root == null){
            root = newNode;
            return;
        }else {
            while (true){
                //父节点指向当前结点
                parent = current;
                //如果当前指向的结点数据比插入的要大，则向左走
                if (current.data > value){
                    current = current.leftChild;
                    if (current == null){
                        parent.leftChild = newNode;
                        return;
                    }
                }else {
                    //生成一个右子节点，并且赋值为 newNode
                    current = current.rightChild;
                    if (current == null){
                        parent.rightChild = newNode;
                        return;
                    }
                }
            }
        }

    }
    /* 查找节点 **/
    public Node find(long value){
        //引用当前节点，从根节点开始
        Node current = root;
        //循环，只要查找值不等于当前节点值
        while (current.data != value){
            //进行比较，比较查找值和当前节点的大小
            if (current.data > value){
                current = current.leftChild;
            }else {
                current = current.rightChild;
            }
            //如果是空，则退出
            if (current == null){
                return null;
            }
        }
        return current;
    }
    /* 前序遍历 **/
    public void frontOrder(Node localNode){
        if (localNode != null){
            //访问根节点
            System.out.print(localNode.data+",");
            //前序遍历左子树
            frontOrder(localNode.leftChild);
            //前序遍历右子树
            frontOrder(localNode.rightChild);
        }
    }
    /* 中序遍历 **/
    public void inOrder(Node localNode){
        if (localNode != null){
            //中序遍历左子树
            inOrder(localNode.leftChild);
            //访问根节点
            System.out.print(localNode.data+",");
            //中序遍历右子树
            inOrder(localNode.rightChild);
        }
    }
    /* 后序遍历 **/
    public void afterOrder(Node localNode){
        if (localNode != null){
            //后序遍历左子树
            afterOrder(localNode.leftChild);
            //后序遍历右子树
            afterOrder(localNode.rightChild);
            //访问根节点
            System.out.print(localNode.data+",");
        }
    }

    /* 删除结点 **/
}
```
这里我把删除节点的操作单独拿出来*（因为比较复杂）
```
    /* 删除结点 **/
    public boolean delete(long value){
        //引用当前节点，从根节点开始
        Node current = root;
        //引用当前节点的父节点
        Node parent = root;
        //是否右左子节点
        boolean isLeftChild = true;
        while (current.data != value){
            parent = current;
            //进行比较，比较value和当前节点
            if (current.data > value){
                current = current.leftChild;
                isLeftChild = true;
            }else {
                current = current.rightChild;
                isLeftChild = false;
            }
            //如果查找不到
            if (current ==null){
                return false;
            }
        }
        //删除叶子节点，也就是该节点没有子节点
        if (current.leftChild == null && current.rightChild == null){
            if (current == root){
                root = null;
            }
            //如果是左子节点
            if (isLeftChild){
                parent.leftChild = null;
            }else {
                parent.rightChild = null;
            }
        }else if (current.rightChild == null){
            if (current == root){
                root = current.leftChild;
            }else if (isLeftChild){
                parent.leftChild = current.leftChild;
            }else {
                parent.rightChild = current.leftChild;
            }
        }else if(current.leftChild == null){
            if (root == current){
                root = current.rightChild;
            }
            if (isLeftChild){
                parent.leftChild = current.rightChild;
            }else {
                parent.rightChild = current.rightChild;
            }
        }else {
            //获取中序后继节点
            Node succeed = getSucceed(current);
            if (current == root){
                root = succeed;
            }else if (isLeftChild){
                parent.leftChild = succeed;
            }
            succeed.leftChild = current.leftChild;
        }
        return true;
    }
    /* 找到后继(succeed)节点 , 后继是按照中序先找右子树，然后找左节点**/
    public Node getSucceed(Node delNode){
        Node succeed = delNode;
        Node succeedParent = delNode;
        Node current = delNode.rightChild;
        while (current != null){
            succeedParent = succeed;
            succeed = current;
            current = current.leftChild;
        }
        if (succeed != delNode.rightChild){
            succeedParent.leftChild = succeed.rightChild;
            succeed.rightChild = delNode.rightChild;
        }
        return succeed;
    }
```
