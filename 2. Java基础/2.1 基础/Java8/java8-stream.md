####1. 什么是stream
* stream被定义为泛型接口
* stream接口代表数据流
* stream不是一个数据结构，不直接存储数据
* stream通过管道操作数据

####2. 什么是管道
* 管道包括：
  1、数据集：可以是集合、数组等list 、array
  2、过滤器filter
  3、终端操作，如Stream.forEach方法

####3. 什么是过滤器
* stream的过滤器可以匹配数据源，并返回一个stream对象。
```
Stream<Student> stream = list.stream();
stream = stream.filter(p->p.getSex=='男');
```
#####4. stream实战
1. 创建Student实例
```
package com.fantJ.JAVA_8;

/**
 * Created by Fant.J.
 * 2017/12/12 21:46
 */
public class Student {
    private String name;
    private int age;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }

    public Student(String name, String sex, int age) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
}
```
2. 创建Stream_Collection类
```
package com.fantJ.JAVA_8;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Fant.J.
 * 2017/12/12 21:46
 */
public class Stream_Collection {
    public static void main(String[] args) {
        List<Student> list = init();
        //获取stream对象
        Stream<Student> stream = list.stream();
        //遍历集合
        stream.forEach(p-> System.out.println(p.toString()));
    }

    static List<Student> init(){
        List<Student> list = new ArrayList<>();
        Student student = new Student("老焦","男",1);
        list.add(student);
        student = new Student("老王","男",1);
        list.add(student);
        student = new Student("老赵","女",1);
        list.add(student);
        return list;
    }

}
```

![stream.png](http://upload-images.jianshu.io/upload_images/5786888-d331d0d854e92068.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####5. stream filter实战
稍微修改下Stream_Collection这个类的main方法如下，
```
 List<Student> list = init();
        //stream过滤器的实例
        list.stream()
                .filter(p->p.getSex()
                        .equals("女"))
                .forEach(p-> System.out.println(p.toString()));
```
这个过滤器是实现对性别是“女”进行过滤并打印。
![stream filter.png](http://upload-images.jianshu.io/upload_images/5786888-8db387132920dab3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
######6.进阶
![image.png](http://upload-images.jianshu.io/upload_images/5786888-b1058a32a9ba5938.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![image.png](http://upload-images.jianshu.io/upload_images/5786888-f0afb4730a6afb5b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
