### 简介
>ArrayList是我们开发中非常常用的数据存储容器之一，其底层是数组实现的，我们可以在集合中存储任意类型的数据，ArrayList是线程不安全的，非常适合用于对元素进行查找，效率非常高。

### 线程安全性
对ArrayList的操作一般分为两个步骤，改变位置(size)和操作元素(e)。所以这个过程在多线程的环境下是不能保证具有原子性的，因此ArrayList在多线程的环境下是线程不安全的。

### 源码分析

##### 1. 属性分析
```
/**
 * 默认初始化容量
 */
private static final int DEFAULT_CAPACITY = 10;

/**
 * 如果自定义容量为0，则会默认用它来初始化ArrayList。或者用于空数组替换。
 */
private static final Object[] EMPTY_ELEMENTDATA = {};

/**
 * 如果没有自定义容量，则会使用它来初始化ArrayList。或者用于空数组比对。
 */
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

/**
 * 这就是ArrayList底层用到的数组
 
 * 非私有，以简化嵌套类访问
 * transient 在已经实现序列化的类中，不允许某变量序列化
 */
transient Object[] elementData;

/**
 * 实际ArrayList集合大小
 */
private int size;

/**
 * 可分配的最大容量
 */
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
```


---
###### 扩展：什么是序列化

序列化是指：将对象转换成以字节序列的形式来表示，以便用于持久化和传输。

实现方法：实现Serializable接口。

然后用的时候拿出来进行反序列化即可又变成Java对象。

---

##### transient关键字解析
>Java中transient关键字的作用，简单地说，就是让某些被修饰的成员属性变量不被序列化。

有了`transient`关键字声明，则这个变量不会参与序列化操作，即使所在类实现了Serializable接口，反序列化后该变量为空值。

>那么问题来了：ArrayList中数组声明：`transient Object[] elementData;`，事实上我们使用ArrayList在网络传输用的很正常，并没有出现空值。

原来：`ArrayList`在序列化的时候会调用`writeObject()`方法，将`size`和`element`写入`ObjectOutputStream`；反序列化时调用`readObject()`，从`ObjectInputStream`获取`size`和`element`，再恢复到`elementData`。

>那为什么不直接用elementData来序列化，而采用上诉的方式来实现序列化呢？

原因在于`elementData`是一个缓存数组，它通常会预留一些容量，等容量不足时再扩充容量，那么有些空间可能就没有实际存储元素，采用上诉的方式来实现序列化时，就可以保证只序列化实际存储的那些元素，而不是整个数组，从而**节省空间和时间**。

##### 2. 构造方法分析

根据initialCapacity 初始化一个空数组，如果值为0，则初始化一个空数组:
```
/**
 * 根据initialCapacity 初始化一个空数组
 */
public ArrayList(int initialCapacity) {
    if (initialCapacity > 0) {
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        throw new IllegalArgumentException("Illegal Capacity: "+
                                           initialCapacity);
    }
}
```

不带参数初始化，默认容量为10:
```
/**
 * 不带参数初始化，默认容量为10
 */
public ArrayList() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}
```

通过集合做参数的形式初始化：如果集合为空，则初始化为空数组：
```
/**
 * 通过集合做参数的形式初始化
 */
public ArrayList(Collection<? extends E> c) {
    elementData = c.toArray();
    if ((size = elementData.length) != 0) {
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
        // replace with empty array.
        this.elementData = EMPTY_ELEMENTDATA;
    }
}
```

#### 3. 主干方法
###### trimToSize()方法：
>用来最小化实例存储，将容器大小调整为当前元素所占用的容量大小。
```
/**
 * 这个方法用来最小化实例存储。
 */
public void trimToSize() {
    modCount++;
    if (size < elementData.length) {
        elementData = (size == 0)
          ? EMPTY_ELEMENTDATA
          : Arrays.copyOf(elementData, size);
    }
}
```

##### clone()方法
>用来克隆出一个新数组。
```
public Object clone() {
    try {
        ArrayList<?> v = (ArrayList<?>) super.clone();
        v.elementData = Arrays.copyOf(elementData, size);
        v.modCount = 0;
        return v;
    } catch (CloneNotSupportedException e) {
        // this shouldn't happen, since we are Cloneable
        throw new InternalError(e);
    }
}
```
通过调用`Object`的`clone()`方法来得到一个新的`ArrayList`对象，然后将`elementData`复制给该对象并返回。
##### add(E e)方法
>在数组末尾添加元素
```
/**
 * 在数组末尾添加元素
 */
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
```
看到它首先调用了`ensureCapacityInternal()`方法.注意参数是size+1,这是个面试考点。
```
private void ensureCapacityInternal(int minCapacity) {
    ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
}
```
这个方法里又嵌套调用了两个方法:计算容量+确保容量

计算容量：如果elementData是空，则返回默认容量10和size+1的最大值，否则返回size+1
```
private static int calculateCapacity(Object[] elementData, int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        return Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    return minCapacity;
}
```
计算完容量后，进行确保容量可用：(modCount不用理它，它用来计算修改次数)

如果`size+1 > elementData.length`证明数组已经放满，则增加容量，调用`grow()`。
```
private void ensureExplicitCapacity(int minCapacity) {
    modCount++;

    // overflow-conscious code
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}
```
增加容量：默认1.5倍扩容。
1. 获取当前数组长度=>oldCapacity
2. oldCapacity>>1 表示将oldCapacity右移一位(位运算)，相当于除2。再加上1，相当于新容量扩容1.5倍。
3. 如果`newCapacity<minCapacity`，则`newCapacity = minCapacity`。看例子更明白一点：假设size为1,则`minCapacity=size+1=2`,而`elementData.length=1`,`newCapacity=1+1>>1=1`,`1<2`所以如果不处理该情况，扩容将不能正确完成。
4. 如果新容量比最大值还要大，则将新容量赋值为VM要求最大值。
5. 将elementData拷贝到一个新的容量中。


```
private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```
---
##### size+1的问题
>好了，那到这里可以说一下为什么要size+1。

size+1代表的含义是：
1. 如果集合添加元素成功后，集合中的实际元素个数。
2. 为了确保扩容不会出现错误。

假如不加一处理，如果默认size是0，则0+0>>1还是0。
如果size是1，则1+1>>1还是1。有人问:不是默认容量大小是10吗?事实上，jdk1.8版本以后，ArrayList的扩容放在add()方法中。之前放在构造方法中。我用的是1.8版本，所以默认` ArrayList arrayList = new ArrayList();`后，size应该是0.所以,size+1对扩容来讲很必要.
```
public static void main(String[] args) {
    ArrayList arrayList = new ArrayList();
    System.out.println(arrayList.size());
}

输出:0
```
事实上上面的代码是证明不了容量大小的，因为size只会在调用`add()`方法时才会自增。有办法的小伙伴可以在评论区大显神通。

---
##### add(int index, E element)方法
```
public void add(int index, E element) {
    rangeCheckForAdd(index);

    ensureCapacityInternal(size + 1);  // Increments modCount!!
    System.arraycopy(elementData, index, elementData, index + 1,
                     size - index);
    elementData[index] = element;
    size++;
}
```
rangeCheckForAdd()是越界异常检测方法。`ensureCapacityInternal()`之前有讲，着重说一下`System.arrayCopy`方法：
```
public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
代码解释:
　　Object src : 原数组
   int srcPos : 从元数据的起始位置开始
　　Object dest : 目标数组
　　int destPos : 目标数组的开始起始位置
　　int length  : 要copy的数组的长度
```

示例：size为6，我们调用`add(2,element)`方法，则会从index=`2+1=3`的位置开始，将数组元素替换为从index起始位置为`index=2`，长度为`6-2=4`的数据。
>![](https://upload-images.jianshu.io/upload_images/5786888-947cd806b120f0ee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

异常处理：
```
private void rangeCheckForAdd(int index) {
    if (index > size || index < 0)
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
}
```
##### set(int index,E element)方法
```
public E set(int index, E element) {
    rangeCheck(index);

    E oldValue = elementData(index);
    elementData[index] = element;
    return oldValue;
}

E elementData(int index) {
    return (E) elementData[index];
}
```
逻辑很简单，覆盖旧值并返回。

##### indexOf(Object o)方法
>根据Object对象获取数组中的索引值。
```
public int indexOf(Object o) {
    if (o == null) {
        for (int i = 0; i < size; i++)
            if (elementData[i]==null)
                return i;
    } else {
        for (int i = 0; i < size; i++)
            if (o.equals(elementData[i]))
                return i;
    }
    return -1;
}
```
如果o为空，则返回数组中第一个为空的索引；不为空也类似。

注意：通过源码可以看到，该方法是允许传空值进来的。

##### get(int index)方法
>返回指定下标处的元素的值。
```
public E get(int index) {
    rangeCheck(index);

    return elementData(index);
}
```
`rangeCheck(index)`会检测index值是否合法，如果合法则返回索引对应的值。

##### remove(int index)方法
>删除指定下标的元素。
```
public E remove(int index) {
    // 检测index是否合法
    rangeCheck(index);
    // 数据结构修改次数
    modCount++;
    E oldValue = elementData(index);

    // 记住这个算法
    int numMoved = size - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index+1, elementData, index,
                         numMoved);
    elementData[--size] = null; // clear to let GC do its work

    return oldValue;
}
```
这里又碰到了`System.arraycopy()`方法，详情请查阅上文。

大概思路：将该元素后面的元素前移，最后一个元素置空。



### ArrayList优缺点
优点：
1. 因为其底层是数组，所以修改和查询效率高。
2. 可自动扩容(1.5倍)。

缺点：
1. 插入和删除效率不高。
2. 线程不安全。

那面试手写ArrayList应该就不是问题了。
