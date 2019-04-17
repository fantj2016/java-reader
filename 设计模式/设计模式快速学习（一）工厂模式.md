>工厂模式（Factory Pattern）是 Java 中最常用的设计模式之一。这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。我们熟悉的Spring 的 bean 工厂等。


直接上demo.先代码，后介绍。

###### 1. 编写接口Shape 
Shape .java
```
/**
 * 一个接口：关于形状
 * Created by Fant.J.
 */
public interface Shape {
    /**
     * 描述方法
     */
    void describe();
}

```
###### 2. 编写实现类
Circle .java
```
/**
 * Created by Fant.J.
 */
public class Circle implements Shape {
    /**
     * 描述方法
     */
    @Override
    public void describe() {
        System.out.println("我是个圆形");
    }
}

```

Rectangle .java
```
/**
 * Created by Fant.J.
 */
public class Rectangle implements Shape {
    /**
     * 描述方法
     */
    @Override
    public void describe() {
        System.out.println("我是一个长方形");
    }
}

```
###### 3. 编写工厂类ShapeFactory 
ShapeFactory .java
```
/**
 * 形状工厂（你可以通过我获取 相应的实例化对象）
 * Created by Fant.J.
 */
public class ShapeFactory {
    public Shape getShape(String type){
        if (type == null){
            return null;
        }
        if ("CIRCLE".equals(type)){
            return new Circle();
        }else if ("RECTANGLE".equals(type)){
            return new Rectangle();
        }
        return null;
    }
}

```


###### 4. 测试
```
/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {

        ShapeFactory shapeFactory = new ShapeFactory();
        //获取  圆形  实例化对象
        Shape circle = shapeFactory.getShape("CIRCLE");
        //调用对象方法
        circle.describe();

        //获取 长方形  实例化对象
        Shape rectangle = shapeFactory.getShape("RECTANGLE");
        //调用方法
        rectangle.describe();

    }
}

```

```
我是个圆形
我是一个长方形
```
###### 5. 进阶

>提问：如果你是一个爱动脑筋的人，你会发现，它和Bean工厂的作用是相似的，但是Bean工厂是这样实现的吗？它会自动去写if语句去创建实例对象吗？

答案肯定是不是的。

当然，我在这里不会像spring 的bean工厂一样，把它的逻辑写出来，因为人家已经是经过很多年的迭代的产品，封装的已经（目不忍视）太厚了，所以我把它的实现原理说明。

可能有些人都想到了，对，就是框架离不开的：[反射机制（传送门）](https://www.jianshu.com/nb/21989596)，不怎么了解的可以很快的看一下我以前的一个文集。


###### 5.1 优化工厂类代码
ShapeFactory.java
```
    /**
     * 反射
     */
    public Object getShape(Class<? extends  Shape> clazz){
        Object object = null;

        try {
            object = Class.forName(clazz.getName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
```

###### 5.2 测试
```
        Object shape = (Rectangle)shapeFactory.getShape(Rectangle.class);
        ((Rectangle) shape).describe();
```


###### 6 新的问题
如果我们忘记去对shapeFactory 做强制类型转换的话，那就是一个完全的object对象，使用不方便，那我们如何能够省略类型转换的这一步骤呢？
###### 6.1 再优化工厂类

```
    /**
     * 反射 + 泛型
     */
    public <T> T getShape(Class<? extends T> clazz){
        T object = null;
        try {
            //实例化，并在这里做 类型转换
            object = (T) Class.forName(clazz.getName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
```

###### 6.2 测试类
```
        Rectangle shape = shapeFactory.getShape(Rectangle.class);
        shape.describe();
```
```
我是一个长方形
```
我们可以看到，事实上也是做了强制类型转换的，只不过在ShapeFactory里做的，我们看不到而已，这种做法会大大方便我们使用。

###### 7. 最后的甜点

经过第五点和第六点的学习，我们想想，spring 框架里 其实只有一个工厂，那就是BeanFactory，ApplicationContext 也需要用它来创建对象。那我们如何去写一个针对多个接口实现一个公共的工厂类呢？

```
    /**
     * 针对多个接口实现一个公共的工厂类
     */
    public <T> Object getObject(Class<T> clazz) {
        if (clazz == null ) {
            return null;
        }
        Object obj  = null;
        try {
            obj = Class.forName(clazz.getName()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
```

###### 8. 总结


>留个思考题，我们在Spring 框架下，@Autowired 装配Bean 具体是完成什么样的操作呢，这可能也是工厂模式最好的总结说明。

希望能帮到大家，谢谢大家！




















