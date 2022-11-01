### 简介
>HashMap最早出现在JDK1.2中，底层基于散列算法实现。HashMap 允许 null 键和 null 值，是非线程安全类，在多线程环境下可能会存在问题。

1.8版本的HashMap数据结构：
![](https://upload-images.jianshu.io/upload_images/5786888-12173e75fb90ffad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

为什么有的是链表有的是红黑树？
>默认链表长度大于8时转为树

### 结构
>Node是HhaspMap中的一个静态内部类 ：
```
//Node是单向链表，实现了Map.Entry接口
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
    //构造函数
    Node(int hash, K key, V value, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }

    // getter and setter ... toString ...
    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    public final String toString() { return key + "=" + value; }

    public final int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public final V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }

    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Map.Entry) {
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            if (Objects.equals(key, e.getKey()) &&
                Objects.equals(value, e.getValue()))
                return true;
        }
        return false;
    }
}

```
>TreeNode 是红黑树的数据结构。
```
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;
    TreeNode(int hash, K key, V val, Node<K,V> next) {
        super(hash, key, val, next);
    }

    /**
     * Returns root of tree containing this node.
     */
    final TreeNode<K,V> root() {
        for (TreeNode<K,V> r = this, p;;) {
            if ((p = r.parent) == null)
                return r;
            r = p;
        }
    }
```

### 类定义
```
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable 
```

### 变量
```
/**
 * 默认初始容量16(必须是2的幂次方)
 */
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

/**
 * 最大容量，2的30次方
 */
static final int MAXIMUM_CAPACITY = 1 << 30;

/**
 * 默认加载因子，用来计算threshold
 */
static final float DEFAULT_LOAD_FACTOR = 0.75f;

/**
 * 链表转成树的阈值，当桶中链表长度大于8时转成树 
   threshold = capacity * loadFactor
 */
static final int TREEIFY_THRESHOLD = 8;

/**
 * 进行resize操作时，若桶中数量少于6则从树转成链表
 */
static final int UNTREEIFY_THRESHOLD = 6;

/**
 * 桶中结构转化为红黑树对应的table的最小大小

 当需要将解决 hash 冲突的链表转变为红黑树时，
 需要判断下此时数组容量，
 若是由于数组容量太小（小于　MIN_TREEIFY_CAPACITY　）
 导致的 hash 冲突太多，则不进行链表转变为红黑树操作，
 转为利用　resize() 函数对　hashMap 扩容
 */
static final int MIN_TREEIFY_CAPACITY = 64;
/**
 保存Node<K,V>节点的数组
 该表在首次使用时初始化，并根据需要调整大小。 分配时，
 长度始终是2的幂。
 */
transient Node<K,V>[] table;

/**
 * 存放具体元素的集
 */
transient Set<Map.Entry<K,V>> entrySet;

/**
 * 记录 hashMap 当前存储的元素的数量
 */
transient int size;

/**
 * 每次更改map结构的计数器
 */
transient int modCount;

/**
 * 临界值 当实际大小(容量*填充因子)超过临界值时，会进行扩容
 */
int threshold;

/**
 * 负载因子：要调整大小的下一个大小值（容量*加载因子）。
 */
final float loadFactor;

```
### 构造方法
```
/**
 * 传入初始容量大小，使用默认负载因子值 来初始化HashMap对象
 */
public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}

/**
 * 默认容量和负载因子
 */
public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
}
/**
 * 传入初始容量大小和负载因子 来初始化HashMap对象
 */
public HashMap(int initialCapacity, float loadFactor) {
    // 初始容量不能小于0，否则报错
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                                           initialCapacity);
    // 初始容量不能大于最大值，否则为最大值                                       
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    //负载因子不能小于或等于0，不能为非数字    
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +
                                           loadFactor);
    // 初始化负载因子                                       
    this.loadFactor = loadFactor;
    // 初始化threshold大小
    this.threshold = tableSizeFor(initialCapacity);
}

/**
 * 找到大于或等于 cap 的最小2的整数次幂的数。
 */
static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```
##### tableSizeFor方法详解：
>用位运算找到大于或等于 cap 的最小2的整数次幂的数。比如10，则返回16
1. 让cap-1再赋值给n的目的是使得找到的目标值大于或等于原值。例如二进制`0100`,十进制是4,若不减1而直接操作，答案是`0001 0000`十进制是16，明显不符合预期。
2. 对n右移1位：001xx...xxx，再位或：011xx...xxx
3. 对n右移2位：00011...xxx，再位或：01111...xxx
4. 对n右移4位...
5. 对n右移8位...
6. 对n右移16位,因为int最大就`2^32`所以移动1、2、4、8、16位并取位或,会将最高位的1后面的位全变为1。
7. 再让结果n+1，即得到了2的整数次幂的值了。

附带一个实例：
>![](https://upload-images.jianshu.io/upload_images/5786888-fca5aecade86a051.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### loadFactor 负载因子
>对于 HashMap 来说，负载因子是一个很重要的参数，该参数反应了 HashMap 桶数组的使用情况。通过调节负载因子，可使 HashMap 时间和空间复杂度上有不同的表现。

当我们调低负载因子时，HashMap 所能容纳的键值对数量变少。扩容时，重新将键值对存储新的桶数组里，键的键之间产生的碰撞会下降，链表长度变短。此时，HashMap 的增删改查等操作的效率将会变高，这里是典型的拿空间换时间。相反，如果增加负载因子（负载因子可以大于1），HashMap 所能容纳的键值对数量变多，空间利用率高，但碰撞率也高。这意味着链表长度变长，效率也随之降低，这种情况是拿时间换空间。至于负载因子怎么调节，这个看使用场景了。

一般情况下，我们用默认值就可以了。大多数情况下0.75在时间跟空间代价上达到了平衡所以不建议修改。


### 查找
```
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}
// 获取hash值
static final int hash(Object key) {
    int h;
    // 拿到key的hash值后与其五符号右移16位取与
    // 通过这种方式，让高位数据与低位数据进行异或，以此加大低位信息的随机性，变相的让高位数据参与到计算中。
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}

final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; 
    Node<K,V> first, e; 
    int n; K k;
    // 定位键值对所在桶的位置
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        // 判断桶中第一项(数组元素)相等
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        // 桶中不止一个结点
        if ((e = first.next) != null) {
            // 是否是红黑树，是的话调用getTreeNode方法
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            // 不是红黑树的话，在链表中遍历查找    
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```
注意：
1. HashMap的hash算法(`hash()`方法)。
2. `(n - 1) & hash`等价于对 length 取余。

### 添加
```
public V put(K key, V value) {
    // 调用hash(key)方法来计算hash 
    return putVal(hash(key), key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; 
    Node<K,V> p; 
    int n, i;
    // 容量初始化：当table为空，则调用resize()方法来初始化容器
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    //确定元素存放在哪个桶中，桶为空，新生成结点放入桶中
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        // 比较桶中第一个元素(数组中的结点)的hash值相等，key相等
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            //如果键的值以及节点 hash 等于链表中的第一个键值对节点时，则将 e 指向该键值对
            e = p;
        // 如果桶中的引用类型为 TreeNode，则调用红黑树的插入方法
        else if (p instanceof TreeNode)
            // 放入树中
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            //对链表进行遍历，并统计链表长度
            for (int binCount = 0; ; ++binCount) {
                // 到达链表的尾部
                if ((e = p.next) == null) {
                    //在尾部插入新结点
                    p.next = newNode(hash, key, value, null);
                    // 如果结点数量达到阈值，转化为红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                // 判断链表中结点的key值与插入的元素的key值是否相等
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        //判断要插入的键值对是否存在 HashMap 中
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            // onlyIfAbsent 表示是否仅在 oldValue 为 null 的情况下更新键值对的值
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    // 键值对数量超过阈值时，则进行扩容
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```
事实上，`new HashMap();`完成后，如果没有`put`操作，是不会分配存储空间的。
1. 当桶数组 table 为空时，通过扩容的方式初始化 table
2. 查找要插入的键值对是否已经存在，存在的话根据条件判断是否用新值替换旧值
3. 如果不存在，则将键值对链入链表中，并根据链表长度决定是否将链表转为红黑树
4. 判断键值对数量是否大于阈值，大于的话则进行扩容操作


### 扩容机制
>在 HashMap 中，桶数组的长度均是2的幂，阈值大小为桶数组长度与负载因子的乘积。当 HashMap 中的键值对数量超过阈值时，进行扩容。

>HashMap 按当前桶数组长度的2倍进行扩容，阈值也变为原来的2倍（如果计算过程中，阈值溢出归零，则按阈值公式重新计算）。扩容之后，要重新计算键值对的位置，并把它们移动到合适的位置上去。

```

final Node<K,V>[] resize() {
    // 拿到数组桶
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    // 如果数组桶的容量大与0
    if (oldCap > 0) {
        // 如果比最大值还大，则赋值为最大值
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        // 如果扩容后小于最大值 而且 旧数组桶大于初始容量16， 阈值左移1(扩大2倍)
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    // 如果数组桶容量<=0 且 旧阈值 >0
    else if (oldThr > 0) // initial capacity was placed in threshold
        // 新容量=旧阈值
        newCap = oldThr;
    // 如果数组桶容量<=0 且 旧阈值 <=0
    else {               // zero initial threshold signifies using defaults
        // 新容量=默认容量
        newCap = DEFAULT_INITIAL_CAPACITY;
        // 新阈值= 负载因子*默认容量
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    // 如果新阈值为0
    if (newThr == 0) {
        // 重新计算阈值
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    // 更新阈值
    threshold = newThr;
    @SuppressWarnings({"rawtypes","unchecked"})
        // 创建新数组
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    // 覆盖数组桶    
    table = newTab;
    // 如果旧数组桶不是空，则遍历桶数组，并将键值对映射到新的桶数组中
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                // 如果是红黑树
                else if (e instanceof TreeNode)
                    // 重新映射时，需要对红黑树进行拆分
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                else { // preserve order
                    // 如果不是红黑树，则按链表处理
                    Node<K,V> loHead = null, loTail = null;
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    // 遍历链表，并将链表节点按原顺序进行分组
                    do {
                        next = e.next;
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    // 将分组后的链表映射到新桶中
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```
整体步骤：
1. 计算新桶数组的容量 newCap 和新阈值 newThr
2. 根据计算出的 newCap 创建新的桶数组，桶数组 table 也是在这里进行初始化的
3. 将键值对节点重新映射到新的桶数组里。如果节点是 TreeNode 类型，则需要拆分红黑树。如果是普通节点，则节点按原顺序进行分组。

总结起来，一共有三种**扩容方式**：
1. 使用默认构造方法初始化HashMap。从前文可以知道HashMap在一开始初始化的时候会返回一个空的table，并且thershold为0。因此第一次扩容的容量为默认值`DEFAULT_INITIAL_CAPACITY`也就是16。同时`threshold = DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR = 12`。
2. 指定初始容量的构造方法初始化`HashMap`。那么从下面源码可以看到初始容量会等于`threshold`，接着`threshold = 当前的容量（threshold） * DEFAULT_LOAD_FACTOR`。
3. HashMap不是第一次扩容。如果`HashMap`已经扩容过的话，那么每次table的容量以及`threshold`量为原有的两倍。

细心点的人会很好奇，为什么要判断loadFactor为0呢？
>loadFactor小数位为 0，整数位可被2整除且大于等于8时，在某次计算中就可能会导致 newThr 溢出归零。



### 疑问和进阶


#### 1. JDK1.7是基于数组+单链表实现（为什么不用双链表）
首先，用链表是为了解决hash冲突。

单链表能实现为什么要用双链表呢?(双链表需要更大的存储空间)

#### 2. 为什么要用红黑树，而不用平衡二叉树？
>插入效率比平衡二叉树高，查询效率比普通二叉树高。所以选择性能相对折中的红黑树。


#### 3. 重写对象的Equals方法时，要重写hashCode方法，为什么？跟HashMap有什么关系？
equals与hashcode间的关系:
1. 如果两个对象相同（即用equals比较返回true），那么它们的hashCode值一定要相同；
2. 如果两个对象的hashCode相同，它们并不一定相同(即用equals比较返回false)   

因为在 HashMap 的链表结构中遍历判断的时候，特定情况下重写的 equals 方法比较对象是否相等的业务逻辑比较复杂，循环下来更是影响查找效率。所以这里把 hashcode 的判断放在前面，只要 hashcode 不相等就玩儿完，不用再去调用复杂的 equals 了。很多程度地提升 HashMap 的使用效率。

所以重写 hashcode 方法是为了让我们能够正常使用 HashMap 等集合类，因为 HashMap 判断对象是否相等既要比较 hashcode 又要使用 equals 比较。而这样的实现是为了提高 HashMap 的效率。

附上源码图：

![](https://upload-images.jianshu.io/upload_images/5786888-1c513f40c48e6c3c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/5786888-73bbd906f1afe818.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 4. HashMap为什么不直接使用对象的原始hash值呢?
```
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
我们发现，HashMap的哈希值是通过上面的方式获取，而不是通过`key.hashCode()`方法获取。

原因：

>通过移位和异或运算，可以让 hash 变得更复杂，进而影响 hash 的分布性。

#### 5. 既然红黑树那么好，为啥hashmap不直接采用红黑树，而是当大于8个的时候才转换红黑树？
因为红黑树需要进行左旋，右旋操作， 而单链表不需要。

以下都是单链表与红黑树结构对比。

如果元素小于8个，查询成本高，新增成本低。

如果元素大于8个，查询成本低，新增成本高。

至于为什么选数字8，是大佬折中衡量的结果-.-，就像loadFactor默认值0.75一样。

