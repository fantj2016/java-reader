>流使程序猿可以在抽象层上对集合进行操作。

### 从外部迭代到内部迭代
>什么是外部迭代和内部迭代呢？

个人认为，外和内是相对集合代码而言。

如果迭代的业务执行在应用代码中，称之为外部迭代。

反之，迭代的业务执行在集合代码中，称为内部迭代（函数式编程）。

语言描述可能有点抽象，下面看实例。


#### 1. 外部迭代
>调用itrator方法，产生一个新的Iterator对象，进而控制整个迭代过程。

```
for (Student student:list){
    if (student.getAge()>18){
        result++;
    }
}
```
我们都知道，for其实底层使用的迭代器：
```
Iterator<Student> iterator = list.iterator();
while (iterator.hasNext()){
    Student student = iterator.next();
    if (student.getAge()>18){
        result++;
    }
}
```

上面的迭代方法就是外部迭代。

##### 外部迭代缺点：
1. 很难抽象出复杂操作
2. 本质上讲是串行化操作。

#### 2. 内部迭代
>返回内部迭代中的响应接口：Stream

```
long count = list.stream().filter(student -> student.getAge() > 18).count();
```
整个过程被分解为：过滤和计数。

**要注意**：返回的Stream对象不是一个新集合，而是创建新集合的配方。

##### 2.1 惰性求值和及早求值
>像filter这样值描述Stream，最终不产生新集合的方法叫做**惰性求值**。
>像count这样最终会从Stream产生值的方法叫做**及早求值**。


判断一个操作是惰性操作还是及早求值，只需看它的返回值。如果返回值是Stream，那么是惰性求值；如果返回值是另一个值或者为空，那就是及早求值。这些操作的理想方式就是形成一个惰性求值的链，最后用一个及早求值的操作返回想要的结果。

整个过程跟建造者模式很像，使用一系列的操作后最后调用build方法才返回真正想要的对象。[设计模式快速学习（四）建造者模式](https://www.jianshu.com/p/24dcb48754b5)

那这个过程有什么**好处**呢：可以在集合类上级联多种操作，但迭代只需要进行一次。

#### 3. 常用操作

##### 3.1 collect(toList())   及早求值
>collect(toList())方法由Stream里的值生成一个列表，是一个及早求值操作。

```
List<String> collect = Stream.of("a", "b", "c").collect(Collectors.toList());
```
`Stream.of("a", "b", "c")`首先由列表生成一个Stream对象，然后`collect(Collectors.toList())`生成List对象。

##### 3.2 map
>map可以将一种类型的值转换成另一种类型。

```
List<String> streamMap = Stream.of("a", "b", "c").map(String -> String.toUpperCase()).collect(Collectors.toList());
```
`map(String -> String.toUpperCase())`将返回所有字母的大写字母的Stream对象，`collect(Collectors.toList())`返回List。
##### 3.3 filter过滤器
>遍历并检查其中的元素时，可用filter

```
List<String> collect1 = Stream.of("a", "ab", "abc")
        .filter(value -> value.contains("b"))
        .collect(Collectors.toList());
```

##### 3.4 flatMap
>如果有一个包含了多个集合的对象希望得到所有数字的集合，我们可以用flatMap

```
List<Integer> collect2 = Stream.of(asList(1, 2), asList(3, 4))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
```
`Stream.of(asList(1, 2), asList(3, 4))`将每个集合转换成Stream对象，然后`.flatMap`处理成新的Stream对象。

##### 3.5 max和min
>看名字就知道，最大值和最小值。

```
Student student1 = list.stream()
        .min(Comparator.comparing(student -> student.getAge()))
        .get();
```
java8提供了一个`Comparator`静态方法，可以借助它实现一个方便的比较器。其中`Comparator.comparing(student -> student.getAge()`可以换成`Comparator.comparing(Student::getAge)`成为更纯粹的lambda。`max`同理。

##### 3.6 reduce
>reduce操作可以实现从一组值中生成一个值，在上述例子中用到的count、min、max方法事实上都是reduce操作。

```
Integer reduce = Stream.of(1, 2, 3).reduce(0, (acc, element) -> acc + element);
System.out.println(reduce);

6
```
上面的例子使用reduce求和，0表示起点，acc表示累加器，保存着当前累加结果（每一步都将stream中的元素累加至acc），element是当前元素。


#### 4. 操作整合
1. collect(toList())方法由Stream里的值生成一个列表
2. map可以将一种类型的值转换成另一种类型。
3. 遍历并检查其中的元素时，可用filter
4. 如果有一个包含了多个集合的对象希望得到所有数字的集合，我们可以用flatMap
5. max和min
6. reduce（不常用）

#### 5. 链式操作实战
```
List<Student> students = new ArrayList<>();
students.add(new Student("Fant.J",18));
students.add(new Student("小明",19));
students.add(new Student("小王",20));
students.add(new Student("小李",22));
List<Class> classList = new ArrayList<>();
classList.add(new Class(students,"1601"));
classList.add(new Class(students,"1602"));
```
```
    static class Student{
        private String name;
        private Integer age;
        getter and setter ...and construct ....
    }

    static class Class{
        private List<Student> students;
        private String className;
        getter and setter ...and construct ....
    }
```
这是我们的数据和关系--班级和学生，现在我想要找名字以小开头的学生，用stream链式操作：
```
List<String> list= students.stream()
                            .filter(student -> student.getAge() > 18)
                            .map(Student::getName)
                            .collect(Collectors.toList());
```
```
[小明, 小王, 小李]
```
这是一个简单的students对象的Stream的链式操作实现，那如果我想要在许多个class中查找年龄大于18的对象呢？

#### 6. 实战提升
>在许多个class中查找年龄大于18的名字并返回集合。

原始代码：
```
        List<String> nameList = new ArrayList<>();
        for (Class c:classList){
            for (Student student:c.getStudents()){
                if (student.getAge()>18){
                    String name = student.getName();
                    nameList.add(name);
                }
            }
        }

        System.out.println(nameList);
```
链式流代码：
如果让你去写，你可能会`classList.stream().forEach(aClass -> aClass.getStudents().stream())....`去实现？

我刚开始就是这样无脑干的，后来我缓过神来，想起foreach是一个及早求值操作，而且返回值是void，这样的开头就注定了没有结果，然后仔细想想，flatMap不是用来处理不是一个集合的流吗，好了，就有了下面的代码。
```
List<String> collect = classList.stream()
        .flatMap(aclass -> aclass.getStudents().stream())
        .filter(student -> student.getAge() > 18)
        .map(Student::getName)
        .collect(toList());
```

**原始代码和流链式调用相比，有以下缺点**：
1. 代码可读性差，隐匿了真正的业务逻辑
2. 需要设置无关变量来保存中间结果
3. 效率低，每一步都及早求值生成新集合
4. 难于并行化处理


#### 7. 高阶函数及注意事项
>高阶函数是指接受另外一个函数作为参数，或返回一个函数的函数。如果函数的函数里包含接口或返回一个接口，那么该函数就是高阶函数。

Stream接口中几乎所有的函数都是高阶函数。比如：Comparing  接受一个函数作为参数，然后返回Comparator接口。
```
Student student = list.stream().max(Comparator.comparing(Student::getAge)).get();

public interface Comparator<T> {}
```

foreach方法也是高阶函数：
```
void forEach(Consumer<? super T> action);

public interface Consumer<T> {}
```

除了以上还有一些类似功能的高阶函数外，不建议将lambda表达式传给Stream上的高阶函数，因为大部分的高阶函数这样用会有一些副作用：给变量赋值。局部变量给成员变量赋值，导致很难察觉。

这里拿ActionEvent举例子：

![](https://upload-images.jianshu.io/upload_images/5786888-86f3835c5d9b5273.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
