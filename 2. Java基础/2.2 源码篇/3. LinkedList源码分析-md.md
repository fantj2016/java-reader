### 概述
>LinkedList是一种可以在任何位置进行高效地插入和移除操作的有序序列，它是基于双向链表实现的，是线程不安全的，允许元素为null的双向链表。

>![](https://upload-images.jianshu.io/upload_images/5786888-74a8749205ec5183.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 源码分析

#### 1. 变量
```
/**
 * 集合元素数量
 **/
transient int size = 0;

/**
 * 指向第一个节点的指针
 * Invariant: (first == null && last == null) ||
 *            (first.prev == null && first.item != null)
 */
transient Node<E> first;

/**
 * 指向最后一个节点的指针
 * Invariant: (first == null && last == null) ||
 *            (last.next == null && last.item != null)
 */
transient Node<E> last;

```

#### 2. 构造方法
```
/**
 * 无参构造方法
 */
public LinkedList() {
}

/**
 * 将集合c所有元素插入链表中
 */
public LinkedList(Collection<? extends E> c) {
    this();
    addAll(c);
}

```

#### 3. Node节点
```
private static class Node<E> {
    // 值
    E item;
    // 后继
    Node<E> next;
    // 前驱
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```
因为一个Node既有prev也有next,所以证明它是一个双向链表。

#### 4. 添加元素
##### addAll(Collection<? extends E> c)
>将集合c添加到链表，如果不传index，则默认是添加到尾部。如果调用`addAll(int index, Collection<? extends E> c)`方法，则添加到index后面。
```
/**
 * 将集合添加到链尾
 */
public boolean addAll(Collection<? extends E> c) {
    return addAll(size, c);
}

/** 
 * 
 */
public boolean addAll(int index, Collection<? extends E> c) {
    checkPositionIndex(index);

    // 拿到目标集合数组
    Object[] a = c.toArray();
    //新增元素的数量
    int numNew = a.length;
    //如果新增元素数量为0，则不增加，并返回false
    if (numNew == 0)
        return false;
    
    //定义index节点的前置节点，后置节点
    Node<E> pred, succ;
    // 判断是否是链表尾部，如果是：在链表尾部追加数据
    //尾部的后置节点一定是null，前置节点是队尾
    if (index == size) {
        succ = null;
        pred = last;
    } else {
        // 如果不在链表末端(而在中间部位)
        // 取出index节点，并作为后继节点
        succ = node(index);
        // index节点的前节点 作为前驱节点
        pred = succ.prev;
    }

    // 链表批量增加，是靠for循环遍历原数组，依次执行插入节点操作
    for (Object o : a) {
        @SuppressWarnings("unchecked") 
        // 类型转换
        E e = (E) o;
        // 前置节点为pred，后置节点为null，当前节点值为e的节点newNode
        Node<E> newNode = new Node<>(pred, e, null);
        // 如果前置节点为空， 则newNode为头节点，否则为pred的next节点
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        pred = newNode;
    }

    // 循环结束后，如果后置节点是null，说明此时是在队尾追加的
    if (succ == null) {
        // 设置尾节点
        last = pred;
    } else {
    //否则是在队中插入的节点 ，更新前置节点 后置节点
        pred.next = succ;
        succ.prev = pred;
    }

    // 修改数量size
    size += numNew;
    //修改modCount
    modCount++;
    return true;
}

/**
  * 取出index节点
  */ 
Node<E> node(int index) {
    // assert isElementIndex(index);

    // 如果index 小于 size/2,则从头部开始找
    if (index < (size >> 1)) {
        // 把头节点赋值给x
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            // x=x的下一个节点
            x = x.next;
        return x;
    } else {
        // 如果index 大与等于 size/2，则从后面开始找
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}


// 检测index位置是否合法
private void checkPositionIndex(int index) {
    if (!isPositionIndex(index))
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}

// 检测index位置是否合法
private boolean isPositionIndex(int index) {
    return index >= 0 && index <= size;
}    
```
假设我们要在index=2处添加{1,2}到链表中，图解如下：

第一步：拿到index=2的前驱节点 prev=ele1                                                 
                        
第二步：遍历集合prev.next=newNode，并实时更新prev节点以便下一次
        遍历:prev=newNode                 
                                         
 第三步：将index=2的节点ele2接上:prev.next=ele2,ele2.prev=prev               
>![](https://upload-images.jianshu.io/upload_images/5786888-862e3c00b8299d9c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

注意`node(index)`方法:寻找处于index的节点，有一个小优化，结点在前半段则从头开始遍历，在后半段则从尾开始遍历，这样就保证了只需要遍历最多一半结点就可以找到指定索引的结点。


##### addFirst(E e)方法
>将e元素添加到链表并设置其为头节点(first)。
```
public void addFirst(E e) {
    linkFirst(e);
}

//将e链接成列表的第一个元素
private void linkFirst(E e) {

    final Node<E> f = first;
    // 前驱为空，值为e，后继为f
    final Node<E> newNode = new Node<>(null, e, f);
    first = newNode;
    //若f为空，则表明列表中还没有元素，last也应该指向newNode
    if (f == null)
        last = newNode;
    else
    //否则，前first的前驱指向newNode
        f.prev = newNode;
    size++;
    modCount++;
}
```
1. 拿到first节点命名为f
2. 新创建一个节点newNode设置其next节点为f节点
3. 将newNode赋值给first
4. 若f为空，则表明列表中还没有元素，last也应该指向newNode；否则，前first的前驱指向newNode。
5. 图解如下：
>![](https://upload-images.jianshu.io/upload_images/5786888-59f6d97ad2ce9e7b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>![](https://upload-images.jianshu.io/upload_images/5786888-f28d7f17902ccabc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### addLast(E e)方法
>将e元素添加到链表并设置其为尾节点(last)。
```
public void addLast(E e) {
    linkLast(e);
}
/**
 * 将e链接成列表的last元素
 */
void linkLast(E e) {
    final Node<E> l = last;
    // 前驱为前last，值为e，后继为null
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    //最后一个节点为空，说明列表中无元素
    if (l == null)
        //first同样指向此节点
        first = newNode;
    else
        //否则，前last的后继指向当前节点
        l.next = newNode;
    size++;
    modCount++;
}
```
过程与`linkFirst()`方法类似，这里略过。
#### add(E e)方法
>在尾部追加元素e。
```
public boolean add(E e) {
    linkLast(e);
    return true;
}

void linkLast(E e) {
    final Node<E> l = last;
    // 前驱为前last，值为e，后继为null
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    //最后一个节点为空，说明列表中无元素
    if (l == null)
        //first同样指向此节点
        first = newNode;
    else
        //否则，前last的后继指向当前节点
        l.next = newNode;
    size++;
    modCount++;
}
```
##### add(int index, E element)方法
>在链表的index处添加元素element.
```
public void add(int index, E element) {
    checkPositionIndex(index);

    if (index == size)
        linkLast(element);
    else
        linkBefore(element, node(index));
}
/**
 * 在succ节点前增加元素e(succ不能为空)
 */
void linkBefore(E e, Node<E> succ) {
    // assert succ != null;
    // 拿到succ的前驱
    final Node<E> pred = succ.prev;
    // 新new节点：前驱为pred，值为e，后继为succ
    final Node<E> newNode = new Node<>(pred, e, succ);
    // 将succ的前驱指向当前节点
    succ.prev = newNode;
    // pred为空，说明此时succ为首节点
    if (pred == null)
        // 指向当前节点
        first = newNode;
    else
        // 否则，将succ之前的前驱的后继指向当前节点
        pred.next = newNode;
    size++;
    modCount++;
}
```
`linkLast`方法上文有讲。
`linkBefore(E e, Node<E> succ)`方法步骤：
1. 拿到succ的前驱节点
2. 新new节点：前驱为pred，值为e，后继为succ : `Node<>(pred, e, succ);`
3. 将succ的前驱指向当前节点
4. pred为空，说明此时succ为首节点,first指向当前节点;否则，将succ之前的前驱的后继指向当前节点

#### 5. 获取/查询元素
##### get(int index)方法
>根据索引获取链表中的元素。

```
public E get(int index) {
    checkElementIndex(index);
    return node(index).item;
}

// 检测index合法性
private void checkElementIndex(int index) {
    if (!isElementIndex(index))
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}

// 根据index 获取元素
Node<E> node(int index) {
    // assert isElementIndex(index);

    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}
```
node方法上文有详细讲解，这里不做介绍。
##### getFirst()方法
>获取头节点。
```
public E getFirst() {
    final Node<E> f = first;
    if (f == null)
        throw new NoSuchElementException();
    return f.item;
}
```

##### getLast()方法
>获取尾节点。
```
public E getLast() {
    final Node<E> l = last;
    if (l == null)
        throw new NoSuchElementException();
    return l.item;
}
```

#### 6. 删除元素

##### remove(Object o)
>根据Object对象删除元素。
```
public boolean remove(Object o) {
    // 如果o是空
    if (o == null) {
        // 遍历链表查找 item==null 并执行unlink(x)方法删除
        for (Node<E> x = first; x != null; x = x.next) {
            if (x.item == null) {
                unlink(x);
                return true;
            }
        }
    } else {
        for (Node<E> x = first; x != null; x = x.next) {
            if (o.equals(x.item)) {
                unlink(x);
                return true;
            }
        }
    }
    return false;
}

E unlink(Node<E> x) {
    // assert x != null;
    // 保存x的元素值
    final E element = x.item;
    //保存x的后继
    final Node<E> next = x.next;
    //保存x的前驱
    final Node<E> prev = x.prev;

    //如果前驱为null，说明x为首节点，first指向x的后继
    if (prev == null) {
        first = next;
    } else {
        //x的前驱的后继指向x的后继，即略过了x
        prev.next = next;
        // x.prev已无用处，置空引用
        x.prev = null;
    }

    // 后继为null，说明x为尾节点
    if (next == null) {
        // last指向x的前驱
        last = prev;
    } else {
        // x的后继的前驱指向x的前驱，即略过了x
        next.prev = prev;
        // x.next已无用处，置空引用
        x.next = null;
    }
    // 引用置空
    x.item = null;
    size--;
    modCount++;
    // 返回所删除的节点的元素值
    return element;
}

```
1. 遍历链表查找 item==null 并执行unlink(x)方法删除
2. 如果前驱为null，说明x为首节点，first指向x的后继,x的前驱的后继指向x的后继，即略过了x.
3. 如果后继为null，说明x为尾节点，last指向x的前驱；否则x的后继的前驱指向x的前驱，即略过了x，置空x.next
4. 引用置空：`x.item = null`
5. 图解:
![](https://upload-images.jianshu.io/upload_images/5786888-d6c2cf76ecf59fe6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



##### remove(int index)方法
>根据链表的索引删除元素。
```
public E remove(int index) {
    checkElementIndex(index);
    //node(index)会返回index对应的元素
    return unlink(node(index));
}

E unlink(Node<E> x)  方法上文有详解。
```

##### removeFirst()方法
>删除头节点。
```
public E removeFirst() {
    final Node<E> f = first;
    if (f == null)
        throw new NoSuchElementException();
    return unlinkFirst(f);
}

private E unlinkFirst(Node<E> f) {
    // assert f == first && f != null;
    //取出首节点中的元素
    final E element = f.item;
    //取出首节点中的后继
    final Node<E> next = f.next;
    f.item = null;
    f.next = null; // help GC
    // first指向前first的后继，也就是列表中的2号位
    first = next;
    //如果此时2号位为空，那么列表中此时已无节点
    if (next == null)
        //last指向null
        last = null;
    else
        // 首节点无前驱 
        next.prev = null;
    size--;
    modCount++;
    return element;
}
```
原理与添加头节点类似。

##### removeLast()方法
>删除尾节点(last)
```
public E removeLast() {
    final Node<E> l = last;
    if (l == null)
        throw new NoSuchElementException();
    return unlinkLast(l);
}

private E unlinkLast(Node<E> l) {
    // assert l == last && l != null;
    // 取出尾节点中的元素
    final E element = l.item;
    // 取出尾节点中的后继
    final Node<E> prev = l.prev;
    l.item = null;
    l.prev = null; // help GC
    // last指向前last的前驱，也就是列表中的倒数2号位
    last = prev;
    // 如果此时倒数2号位为空，那么列表中已无节点
    if (prev == null)
        // first指向null
        first = null;
    else
        // 尾节点无后继
        prev.next = null;
    size--;
    modCount++;
    // 返回尾节点保存的元素值
    return element;
}
```

#### 7. 修改元素
>修改元素比较简单，先找到index对应节点，然后对值进行修改。
```
public E set(int index, E element) {
    checkElementIndex(index);
    // 获取到需要修改元素的节点
    Node<E> x = node(index);
    // 保存之前的值
    E oldVal = x.item;
    // 执行修改
    x.item = element;
    // 返回旧值
    return oldVal;
}
```


#### 8. 与ArrayList的对比

优点：
1. 不需要扩容和预留空间,空间效率高
2. 增删效率高

缺点：
1. 随机访问时间效率低
2. 改查效率低
