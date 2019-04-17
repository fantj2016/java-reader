工作中可能用Set比较少，但是如果用的时候，出的一些问题很让人摸不着头脑，然后我就看了一下Set的底层实现，大吃一惊。
###看一个问题
```
        Map map = new HashMap();
        map.put(1,"a");
        map.put(12,"ab");
        map.put(123,"abc");

        Set set1 = map.keySet();
        Set set2 = map.keySet();
        Set set3 = map.keySet();
        set1.remove(1);
        set1.forEach(p-> System.out.println(p.toString()));
        set2.forEach(p-> System.out.println(p.toString()));
        set3.forEach(p-> System.out.println(p.toString()));
```
然后我的运行结果是
```
123
12
----------------
123
12
----------------
123
12
```
为什么我在set1里面执行remove(1);其它的两个set对象为什么也删掉了第一个元素呢？
为什么会受到我前面操作的影响呢。
###分析底层实现
##### 1.最简单的实践
我们大概能猜出问题的所在，就是set1其实调用的还是map对象。那怎样才具有说服力呢。
学过反射的应该都清楚，我们可以看下set1它到底是什么类型的对象。
```
        Class classes = set1.getClass();
        System.out.println(classes.getTypeName());
```
控制台打印：
```
java.util.HashMap$KeySet
```
是不是眼前一亮，wtf竟然是个Map类型。我不是明明给它实例化了个Set对象吗。好了，这个现象成功吸引了我的兴趣。于是
###### 找底层实现
我们找到这个KeySet方法
```
   public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new AbstractSet<K>() {
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private Iterator<Entry<K,V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public K next() {
                            return i.next().getKey();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractMap.this.size();
                }

                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                public void clear() {
                    AbstractMap.this.clear();
                }

                public boolean contains(Object k) {
                    return AbstractMap.this.containsKey(k);
                }
            };
            keySet = ks;
        }
        return ks;
    }
```
我们可以看到，有一个成员内部类AbstractSet<K>()，里面有两部分，一部分是new 一个迭代器(内部类)，一部分是调用AbstractMap对象(外部类)。外部类对象调用的内部类的构造函数，反编译的话，会看出传入了外部类对象的引用进去。所以它最终的类型应该是AbstractMap，（Map的派生类）。

所以，眼看是实例化了一个Set对象，其实底层还是调用的map对象。
