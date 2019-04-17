### 1. DTO的使用

如果你的controller代码写成这样
```
    @RequestMapping("/user")
    public List query(@RequestParam String username,
                      @RequestParam String password,
                      @RequestParam int age){
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }
```
那你就需要了解一下什么是DTO了。

用DTO后的代码
```
    @RequestMapping("/user")
    public List query(UserQueryCondition condition){

        System.out.println(ReflectionToStringBuilder.toString(condition, ToStringStyle.DEFAULT_STYLE));
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }
```

###2. 如何使用PageAble设置默认分页属性

你是不是还是在方法体里声明Pageable对象固定属性呢？
更优雅的在这里：
`@PageableDefault(page = 2,size = 7,sort = "username,asc")Pageable pageable`

###3. 如何再@RequestMapping注解上写正则

`@RequestMapping("/user/{id:\\d+}")`   id只能是数字

###4. @JsonView注解自定义返回内容

比如User类有两个属性，一个username一个password。
我们想在controller返回里，返回User实体的时候不返回password属性。
######4.1 设置视图
首先需要在实体类里声明两个接口
```
    public interface UserSimpleView{};
    public interface UserDetailView extends UserSimpleView{};
```
然后，在一定要显示的字段的get方法上添加`@JsonView(UserSimpleView.class)`注解。
在不一定要显示的字段的get方法上添加`@JsonView(UserDetailView .class)`注解。
User.java  完整代码
```
public class User {

//    jsonView 设置视图
    public interface UserSimpleView{};
    public interface UserDetailView extends UserSimpleView{};

    private String useranme;
    private String password;

    @JsonView(UserSimpleView.class)
    public String getUseranme() {
        return useranme;
    }

    public void setUseranme(String useranme) {
        this.useranme = useranme;
    }

    @JsonView(UserDetailView.class)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```
注意getUseranme方法上的注解和getPassword上注解的不同。
######4.2 将实体类的get方法上的注解和Controller里相对应
如果controller只想返回username字段，则
```
    @RequestMapping("/user/{id:\\d+}")
    @JsonView(User.UserSimpleView.class)
    public User getInfo(@PathVariable int id){
        User user = new User();
        user.setUseranme("FantJ");
        return user;
    }
```
如果想返回全部的User属性信息，则

```
    @RequestMapping("/user/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public User getInfo(@PathVariable int id){
        User user = new User();
        user.setUseranme("FantJ");
        return user;
    }
```
上面这个controller方法，我们看到` @JsonView(User.UserDetailView.class)`所以它会。如果变成返回全部的User属性信息`@JsonView(User.UserSimpleView.class)`，它就只返回username字段信息。因为User类和Controller类中@JsonView注解一一对应。

### 5. 判断某个字段不为空
我们都知道，post方法，需要用@RequestBody接收实体类信息。如果我们再方法里判断某个属性是否为空然后再抛错，必然增加代码量，不美观。所以我们可以配合几个注解来达到我们的要求。
######5.1 首先在实体类字段上添加注解@NotBlank
```
    @NotBlank  //不为空的注解
    private String password;
```
######5.2 在Controller里的@RequestBody前加注解@Valid
```
@PostMapping("/user")
    public User create(@Valid @RequestBody User user){}
```
但是光这两个注解作用下，如果密码出现了空值，程序会直接报错，我们希望程序可以正常运行，然后把报错信息打印出来就可以，于是我们还需要加一个类。BindingResult
######5.3 添加BindingResult参数
```
    @PostMapping("/user")
    public User create(@Valid @RequestBody User user, BindingResult errors){}
```
那如何获取错误信息呢？看下面的完整代码。
######5.4 完整代码
```
    @PostMapping("/user")
    public User create(@Valid @RequestBody User user, BindingResult errors){
        user.setId("1");
        //打印错误信息
        if (errors.hasErrors()){
            errors.getAllErrors().stream().forEach(p-> System.out.println(p.getDefaultMessage()));
        }
        System.out.println(user.getId());
        System.out.println(user.getUseranme());
        System.out.println(user.getPassword());
        return user;
    }
```
但是你一看控制台打印信息你会发现`may not be empty`，你都不知道是什么字段为空报错的，我们我们把字段信息打印出来。但是又显得代码很长。所以我们可以用@NotBlank的message属性来自定义message。
除了@NotBlank外，还有一些类似常用的注解。
* @NotNull   值不能为空
* @NotEmpty  字符串不能为空，集合不能为空
* @Range(min=,max=)   数字必须大于min小鱼max
* @Max(value=)   设置最大值同理还有 @Min(value=)   
* @Email   字符必须是Email类型
* @Length(min= ,max= )  字符串长度设置
* @URL   字符串是url

###6 自定义注解简便开发


####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

######底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)
