>在进行项目的完善和修改过程中，难免出一些烂记性导致的bug。

##### 25/10错误日志
```
ERROR o.a.c.c.C.[.[localhost].[/].[dispatcherServlet]:181 - Servlet.service() for servlet [dispatcherServlet] in context with 
path [] threw exception [Request processing failed; nested exception is 
org.springframework.data.redis.serializer.SerializationException: Cannot deserialize; nested exception is 
org.springframework.core.serializer.support.SerializationFailedException: Failed to deserialize payload. Is the byte array a 
result of corresponding serialization for DefaultDeserializer?; nested exception is java.io.InvalidClassException: 
com.xxx.Competition; local class incompatible: stream classdesc serialVersionUID = 6538832880805972610, 
local class serialVersionUID = -3999125971536227608] with root cause
```
刚开始没看到`redis`字眼，只看到了`Competition`这个类反序列化失败...导致绕了很大的圈子，干了很多无谓的挣扎。

##### 一瞬间的发现
>再狂改了一顿代码后发现了是`redis`相关的报错，MD啊，以后一定要耐心认真读完每一行报错。

好了，检查`Competition`类的代码，真的什么都忘了，好几周前改的代码，一点都想不起来哪里会出错，然后我去看`git`日志。发现我在之前给该类增加过字段，然后`redis`的数据也忘记做清除，导致`redis`中的数据结构还是旧的，返回的序列号也是之前的，所以`spring`在拿到该数据后不能正确的给予反序列化，导致该报错。

##### 问题解决
>找到问题原因，解决就很简单了，直接将相关的`redis`做清除。

```
redis-cli keys "cpt_*"  |  xargs redis-cli del
```
好了，访问正常。


**好的习惯很重要，一个功能开发不完就先别睡觉、吃饭等...。不然可能会付出双倍的时间去填坑。**
