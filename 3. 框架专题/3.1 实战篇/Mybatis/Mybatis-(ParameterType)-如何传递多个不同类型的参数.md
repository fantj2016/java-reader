>偶然碰到一个需要给xml传一个String类型和一个Integer类型的需求，当时心想用map感觉有点太浪费，所以专门研究了下各种方式。

### 方法一：不需要写parameterType参数

```
public List<XXXBean> getXXXBeanList(String xxId, String xxCode);  
```
```xml
<select id="getXXXBeanList" resultType="XXBean">

　　select t.* from tableName where id = #{0} and name = #{1}  

</select>  
```
由于是多参数那么就不能使用parameterType， 改用#｛index｝是第几个就用第几个的索引，索引从0开始


### 方法二：基于注解(最简单)
```
public List<XXXBean> getXXXBeanList(@Param("id")String id, @Param("code")String code);  
```
```xml
<select id="getXXXBeanList" resultType="XXBean">

　　select t.* from tableName where id = #{id} and name = #{code}  

</select>  
```
由于是多参数那么就不能使用parameterType， 这里用@Param来指定哪一个


### 方法三：Map封装
```
public List<XXXBean> getXXXBeanList(HashMap map);  
```

```xml
<select id="getXXXBeanList" parameterType="hashmap" resultType="XXBean">

　　select 字段... from XXX where id=#{xxId} code = #{xxCode}  

</select>  
```

其中hashmap是mybatis自己配置好的直接使用就行。map中key的名字是那个就在#{}使用那个，map如何封装就不用了我说了吧。

### 方法四：List封装
```
public List<XXXBean> getXXXBeanList(List<String> list);  

```
```xml
<select id="getXXXBeanList" resultType="XXBean">
　　select 字段... from XXX where id in
　　<foreach item="item" index="index" collection="list" open="(" separator="," close=")">  
　　　　#{item}  
　　</foreach>  
</select> 
```

### 总结
传递list和map在资源消耗上肯定远大于方法一和方法二，但是有一些特殊的情形需要传递list，比如你需要传递一个id集合并批量对id进行sql操作然后再返回等等。所以都需要了解。
