>这篇文章只写给主键用uuid并且用jpa的小伙伴。


### 1. 数据实体类
```
@Entity
@Table(name = "ip_user")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class User  implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String userId;
    ...
}
```

注意`@GenericGenerator(name = "jpa-uuid", strategy = "uuid")`  和 ` @GeneratedValue(generator = "jpa-uuid")` 两个注解是生成策略核心注解。



### 2. 数据库字段

![](https://upload-images.jianshu.io/upload_images/5786888-8f482e9373923dab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 3. 执行save方法后
不需要给user.id字段设置值，jpa会自动生成uuid并作为它的主键添加到表中。

![](https://upload-images.jianshu.io/upload_images/5786888-2fd1e50f235e75dd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
