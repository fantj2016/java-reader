###什么是JPA
* JPA是用于管理Java EE 和Java SE环境中的持久化，以及对象/关系映射的JAVA API
* 最新规范为"JSR 338:Java Persistence 2.1" https://jcp.org/en/jsr/detail?id=3389
* 实现 :EclipseLink ; Hibernate ; Apache Open JPA

###核心概念
#####实体类
* 实体类中必须用Entity注解(javax.persistence.Entity包)
* 必须有一个无参数的构造方法
* 如果被远程调用，则该类必须实现Serializable接口
* 唯一对象标识符：主键（javax.persistence.id）；复合主键（EmbeddedId和IdClass）
#####关系
* 一对一 @OneToOne
* 一对多 @OneToMany
* 多对一 @ManyToOne
* 多对多 @ManyToMany
#####EntityManager 接口介绍
* 定义用于与持久性上下文进行交互的方法
* 创建和删除持久实体实例，通过实体的主键查找实体
* 允许在实体上进行查询


###什么是spring data JPA
* 是spring data家族一部分
* 对基于JPA的数据访问层的增强

###spring data JPA常用接口
* CurdRepository 增删改查
* PagingAndSortingRepository 分页和排序
#####自定义接口
首先要记住继承Repository接口（或者其子类接口）















