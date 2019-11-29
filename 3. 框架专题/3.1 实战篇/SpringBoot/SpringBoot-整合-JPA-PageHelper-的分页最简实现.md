>JPA又自己的Pageable来帮助我们实现分页，Mybatis有PageHelper帮我们实现分页，下面直接贴代码。


### 1. 用JPA实现分页

##### 1.1 pom添加依赖
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

```
其实就是JPA的依赖。
##### 1.2 核心实现
```
    /**
     * 查询全部
     */
    @Override
    public ServerResponse selectAll(Integer page,Integer size) {
        Pageable pageable = new PageRequest(page,size,Sort.Direction.DESC,"noticeId");
        Iterator<Notice> all = noticeRepostory.findAll(pageable).iterator();
        List<Notice> list = new ArrayList<Notice>();
        while (all.hasNext()){
           list.add(all.next());
        }
        if (all == null){
            return ServerResponse.createByErrorMessage("查询公告列表失败");
        }
        return ServerResponse.createBySuccess(list);
    }
```
更多详细的请看[spring boot2 整合（二）JPA（特别完整！）](https://www.jianshu.com/p/3b31270a44b1)中分页那部分。
### 2. 用Mybatis实现分页

##### 2.1 pom依赖
```
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.0.0</version>
        </dependency>

```

##### 2.2 核心代码
```
    /**
     * 查询消息列表
     *
     * @param userId
     */
    @Override
    public ServerResponse selectNewsList(Integer userId,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<News> news = newsMapper.selectNewsList(userId);
        PageInfo<News> appsPageInfo = new PageInfo<>(news);

        if (StringUtils.isEmpty(news)){
            return ServerResponse.createByErrorMessage("查询消息列表失败");
        }
        log.info("查询到的消息数目{}",appsPageInfo.getList());
        appsPageInfo.getList().forEach(p-> System.out.println(p.toString()));
        return ServerResponse.createBySuccess(appsPageInfo.getList());
    }
```
我们只需要PageHelper.startPage，然后紧跟着查询并返回一个list对象，然后用list对象创建一个PageInfo对象。

pageInfo有很多属性，比如当前页是多少，有没有下一页，数据一共多少等等，我这里调用它的getList方法来获取到news集合。

更多详细的请看[spring boot2 整合（一）Mybatis （特别完整！）](https://www.jianshu.com/p/c15094bd1965)
中分页那部分。
