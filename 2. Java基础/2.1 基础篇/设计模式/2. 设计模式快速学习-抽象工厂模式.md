>抽象工厂模式（Abstract Factory Pattern）是围绕一个超级工厂创建其他工厂。该超级工厂又称为其他工厂的工厂。这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。

直接上demo.先代码，后介绍。

如果在之前你没有看过工厂模式，请坐下面的时光机传送，不光是思想基础，主要是演变的过程要知道。
[设计模式快速学习（一）工厂模式](https://www.jianshu.com/p/fe0162489036)

###### 1. 编写接口Shape 
Shape.java
```
public interface Shape {
   void draw();
}
```

###### 2. 编写接口Shape的实现类Rectangle
Rectangle.java
```
/**
 * Created by Fant.J.
 */
public class Rectangle implements Shape {
    @Override
    public void describe() {
        System.out.println("我是个长方形");
    }
}
```
###### 3. 编写接口Shape的实现类Circle
Circle .java
```
/**
 * Created by Fant.J.
 */
public class Circle implements Shape {
    @Override
    public void describe() {
        System.out.println("我是个圆形");
    }
}
```
###### 4. 编写接口Color 
Color .java
```
/**
 * Created by Fant.J.
 */
public interface Color {
    void fill();
}
```
###### 5. 编写接口Color 的实现类Red 
Red .java
```
/**
 * Created by Fant.J.
 */
public class Red implements Color {
    @Override
    public void fill() {
        System.out.println("填充了红色");
    }
}
```
###### 6. 编写接口Color 的实现类Green
Green .java
```
/**
 * Created by Fant.J.
 */
public class Green implements Color {
    @Override
    public void fill() {
        System.out.println("填充了绿色");
    }
}
```
###### 7. 为 Color 和 Shape 对象创建抽象类来获取工厂
```
/**
 * Created by Fant.J.
 */
public abstract class AbstractFactory {
    abstract Color getColor(String color);
    abstract <T> T getShape(Class<T> clazz) ;
}

```
###### 8. 扩展(继承)AbstractFactory 工厂类,使其可以生成实体类Shape的对象
ShapeFactory .java
```
/**
 * Created by Fant.J.
 */
public class ShapeFactory extends AbstractFactory {
    @Override
    public Color getColor(String color) {
        return null;
    }


    @Override
    <T> T getShape(Class<T> clazz) {
        T obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
```

###### 9. 扩展(继承)AbstractFactory 工厂类,使其可以生成实体类Color的对象 . 略
###### 10. 创建工厂生成器,用来生成Shape 或 Color 工厂
FactoryProducer .java
```
/**
 * Created by Fant.J.
 */
public class FactoryProducer {
    public static <T> AbstractFactory getFactory(String choice){
        if(choice.equalsIgnoreCase("SHAPE")){
            return new ShapeFactory();
        } else if(choice.equalsIgnoreCase("COLOR")){
            //这里不举例子了，你可以自己 创建一个 ColorFactory
            return null;
        }
        return null;
    }
}
```
###### 11. 测试

```
/**
 * Created by Fant.J.
 */
public class Test {
    public static void main(String[] args) {
        //抽象类不能直接调用，所以我们实例化 只能实例其子类对象
        AbstractFactory shapeFactory = FactoryProducer.getFactory("SHAPE");
        shapeFactory.getShape(Circle.class).describe();
    }
}
```

### 总结
这个实现看起来很复杂，我把大概思路总结一下：
首先，我们可以通过 **工厂生成器**来生成一个抽象工厂类，然后调用抽象工厂类的扩展类(子类)方法。
