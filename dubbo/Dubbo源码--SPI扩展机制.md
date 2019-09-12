>最好有是AOP、IOC、MVC框架基础和dubbo使用基础再阅读噢。

### 什么是SPI
>spi全称Service Provider Interface， 服务提供接口， 是Java提供的一套用来被第三方实现或者扩展的API。

没有使用过JDK SPI的可以百度一个例子自己跑下，这里只讲源码。

SPI的核心思想是解耦，基于接口、策略模式、配置实现实现类的动态扩展。

经验丰富的开发者肯定用过很多个`Driver`的实现类产品, 比如`oracle.jdbc.driver.OracleDriver`和`oracle.jdbc.OracleDriver`、还有ODBC(连接微软的那个数据库)，以JDBC驱动为例，我们分析一下JDK是如何做到动态扩展的：

在`mysql-connector-java-5.1.44.jar!/META-INF/services/java.sql.Driver`文件中：
```
com.mysql.jdbc.Driver
com.mysql.fabric.jdbc.FabricMySQLDriver
```
这两个都是`java.sql.Driver`的实现类的全限定名，我们看看哪里在加载这个`Driver.class`：
```
    private static void loadInitialDrivers() {
        // 先从JVM启动参数里面获取到jdbc.drivers的值 (-Djdbc.drivers=xxx)
        String drivers;
        try {
            drivers = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("jdbc.drivers");
                }
            });
        } catch (Exception ex) {
            drivers = null;
        }

        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                // JDK提供的加载SPI方式：ServiceLoader
                ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
                Iterator<Driver> driversIterator = loadedDrivers.iterator();
                try{
                    while(driversIterator.hasNext()) {
                        driversIterator.next();
                    }
                } catch(Throwable t) {
                // Do nothing
                }
                return null;
            }
        });

        println("DriverManager.initialize: jdbc.drivers = " + drivers);

        if (drivers == null || drivers.equals("")) {
            return;
        }
        String[] driversList = drivers.split(":");
        println("number of Drivers:" + driversList.length);
        for (String aDriver : driversList) {
            try {
                println("DriverManager.Initialize: loading " + aDriver);
                Class.forName(aDriver, true,
                        ClassLoader.getSystemClassLoader());
            } catch (Exception ex) {
                println("DriverManager.Initialize: load failed: " + ex);
            }
        }
    }
```
我来解释下大概流程：
1. 加载JVM启动属性，拿到driverName
2. 用ServiceLoader加载Driver的所有实现类
2. 根据jvm属性中的driver名字加载类: Class.forName(driverName)

第一步和第三部大家应该都很熟悉。来探究下ServiceLoader的主干源码:
```
#首先它实现了迭代类
public final class ServiceLoader<S> implements Iterable<S>
所以从该对象中拿到的迭代器， 是定制的迭代器，我们再调用迭代器的hasNext方法，事实上调用的是：

        public boolean hasNext() {
            if (acc == null) {
                return hasNextService();
            } else {
                PrivilegedAction<Boolean> action = new PrivilegedAction<Boolean>() {
                    public Boolean run() { return hasNextService(); }
                };
                return AccessController.doPrivileged(action, acc);
            }
        }
        private boolean hasNextService() {
            if (nextName != null) {
                return true;
            }
            if (configs == null) {
                try {
                    // service就是我们传进来的接口类对象
                    // PREFIX 是常量: private static final String PREFIX = "META-INF/services/";
                    String fullName = PREFIX + service.getName();
                    if (loader == null)
                        configs = ClassLoader.getSystemResources(fullName);
                    else
                        configs = loader.getResources(fullName);
                } catch (IOException x) {
                    fail(service, "Error locating configuration files", x);
                }
            }
            while ((pending == null) || !pending.hasNext()) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                // 这里面会打开流去解析每一行， 把实现类全限定名加到names列表并返回其迭代器对象，用pending来接收
                pending = parse(service, configs.nextElement());
            }
            nextName = pending.next();
            return true;
        }
```
由此我们可以逆向推出JDK的SPI使用方法：
1. SPI的配置文件路径:`META-INF/services/`，不能改变。
2.  配置文件名以接口的全限定类名声明
3. 配置文件中每一行是一个实现类的全限定类名

### Java SPI 的问题
>Dubbo为什么不用java的SPI而选择自己再重写一套呢。
1. JDK 标准的 SPI 会一次性实例化扩展点所有实现，如果有扩展实现初始化很耗时，但如果没用上也加载，会很浪费资源。
2. 如果扩展点加载失败，连扩展点的名称都拿不到了。
3. 不支持AOP和IOC。


### Dubbo SPI
>Dubbo 的SPI 扩展从 JDK 标准的 SPI (Service Provider Interface) 扩展点发现机制加强而来。 改进了 JDK 标准的 SPI 的以上问题。

使用在这里不说了，没有用过的同学可以去官方看下：[http://dubbo.apache.org/zh-cn/docs/dev/SPI.html](http://dubbo.apache.org/zh-cn/docs/dev/SPI.html)

```
    public static void main(String[] args) {
        ExtensionLoader<Color> loader = ExtensionLoader.getExtensionLoader(Color.class);
        Color yellow = loader.getExtension("yellow");
        System.out.println(yellow.getColor());
    }
```
这是测试Dubbo SPI的一段代码，可以看到`getExtensionLoader`是dubbo SPI的入口，我们来看看dubbo是如何实现动态扩展的。

`Color yellow = loader.getExtension("yellow");`在这行代码中，经历了从配置的扩展类name到获得该类的实例化的过程，那我们从getExtension方法(类似spirng的getBean())看起。

```
    public T getExtension(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        if ("true".equals(name)) {
            return getDefaultExtension();
        }
        // 根据接口名获取它的持有类， 如果有从缓存中取， 没有就重新new
        final Holder<Object> holder = getOrCreateHolder(name);
        // 获取到实例
        Object instance = holder.get();
        // 双重检锁 获取和创建实例
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }
```
假设现在什么都没有，则会到了createExtension(name);方法， 这个方法创建了扩展类的实例。
```
    private T createExtension(String name) {
      // 根据接口名获取所有实现类
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw findException(name);
        }
        try {
            // 从缓存中获取实例
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            // 缓存(在时候就加载了)中没有， 调用反射创建
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            // inject 依赖注入， 给实例注入需要的依赖对象
            injectExtension(instance);
            // 从缓存拿接口的包装类(也是实现类)
            Set<Class<?>> wrapperClasses = cachedWrapperClasses;
            // 如果包装类不是空， 就遍历通过构造方法反射创建实例
            if (CollectionUtils.isNotEmpty(wrapperClasses)) {
                for (Class<?> wrapperClass : wrapperClasses) {
                    instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
                }
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance (name: " + name + ", class: " +
                    type + ") couldn't be instantiated: " + t.getMessage(), t);
        }
    }
```

createExtension 方法包含了如下的步骤：
1. 通过 `getExtensionClasses `获取所有的实现类
2. 通过反射`clazz.newInstance()`创建扩展对象
3. `injectExtension(instance);`向拓展对象中注入依赖
4. 实例化带接口参数的构造方法，将拓展对象包裹在相应的 Wrapper 对象中

从`wrapperClass.getConstructor(type).newInstance(instance);`代码中可以看到， Wrapper对象一定要有参数为接口类对象的构造方法。

那如何获取所有的扩展类的呢？我们探索一下`getExtensionClasses`方法：

```
    private Map<String, Class<?>> getExtensionClasses() {
        // 从缓存中拿map， 如果没有，就调用loadExtensionClasses
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }
```
这个方法很简单，从缓存中拿map， 如果没有，就调用loadExtensionClasses，看下loadExtensionClasses方法做了啥：
```
    private Map<String, Class<?>> loadExtensionClasses() {
        cacheDefaultExtensionName();

        Map<String, Class<?>> extensionClasses = new HashMap<>();
        loadDirectory(extensionClasses, DUBBO_INTERNAL_DIRECTORY, type.getName());
        loadDirectory(extensionClasses, DUBBO_INTERNAL_DIRECTORY, type.getName().replace("org.apache", "com.alibaba"));
        loadDirectory(extensionClasses, DUBBO_DIRECTORY, type.getName());
        loadDirectory(extensionClasses, DUBBO_DIRECTORY, type.getName().replace("org.apache", "com.alibaba"));
        loadDirectory(extensionClasses, SERVICES_DIRECTORY, type.getName());
        loadDirectory(extensionClasses, SERVICES_DIRECTORY, type.getName().replace("org.apache", "com.alibaba"));
        return extensionClasses;
    }
```
1. 调用cacheDefaultExtensionName拿到默认实现类
2. 调用loadDirectory从几个文件下寻找扩展类配置文件

先来看看cacheDefaultExtensionName是如何拿到默认实现类的：
```
    private void cacheDefaultExtensionName() {
        // 获取SPI上的注解， 如果有设置value值，则取出， 这个值是默认实现类噢
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value();
            if ((value = value.trim()).length() > 0) {
                String[] names = NAME_SEPARATOR.split(value);
                if (names.length > 1) {
                    throw new IllegalStateException("More than 1 default extension name on extension " + type.getName()
                            + ": " + Arrays.toString(names));
                }
                if (names.length == 1) {
                    cachedDefaultName = names[0];
                }
            }
        }
    }
```
这个方法很简单， 就是将SPI注解的value取出来来对应默认的实现类。

```
    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type) {
        String fileName = dir + type;
        try {
            Enumeration<java.net.URL> urls;
            // 阿里封装的获取 classLoader 的方法
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                // jdk的方法， 与java spi一样的噢
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            // 拿到url后遍历调用loadResource方法
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    java.net.URL resourceURL = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceURL);
                }
            }
        } catch (Throwable t) {
            logger.error("Exception occurred when loading extension class (interface: " +
                    type + ", description file: " + fileName + ").", t);
        }
    }
```
再具体下去大家自己看，我给描述下做了什么：
1. 用url获取到文件流
2. 解析每一行， 将`=`号前面的赋值为name，后面的赋值为实现类的全限定路径
3. 反射实例化每一行的实现类， 并调用loadClass方法实现对extensionClasses的最终赋值(它new了一个extensionClasses的map对象直到后面才进行赋值)。

```
    private void loadClass(Map<String, Class<?>> extensionClasses, java.net.URL resourceURL, Class<?> clazz, String name) throws NoSuchMethodException {
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Error occurred when loading extension class (interface: " +
                    type + ", class line: " + clazz.getName() + "), class "
                    + clazz.getName() + " is not subtype of interface.");
        }
        // 如果是Adaptive注解的类， 就把它放到cacheAdaptiveClass缓存中
        if (clazz.isAnnotationPresent(Adaptive.class)) {
            cacheAdaptiveClass(clazz);
        } else if (isWrapperClass(clazz)) {
            cacheWrapperClass(clazz);
        } else {
            clazz.getConstructor();
            if (StringUtils.isEmpty(name)) {
                name = findAnnotationName(clazz);
                if (name.length() == 0) {
                    throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + resourceURL);
                }
            }
            // 分割name， 因为配置文件中name可以有很多个，然后每个name对应赋值一个实现类对象(即使对象都相同)
            String[] names = NAME_SEPARATOR.split(name);
            if (ArrayUtils.isNotEmpty(names)) {
                cacheActivateClass(clazz, names[0]);
                for (String n : names) {
                    cacheName(clazz, n);
                    saveInExtensionClass(extensionClasses, clazz, n);
                }
            }
        }
    }
```
1. 判断是否是Adaptive等注解的类， 就把它放到对应的cacheAdaptiveClass等缓存中
2. 分割name， 因为配置文件中name可以有很多个，然后每个name对应赋值一个实现类对象(即使对象都相同)

好了，到这里扩展类的加载就完成了， 接下来就到它的依赖注入(injectExtension方法)：
```
    private T injectExtension(T instance) {
        try {
            // 你一定很好奇，如果objectFactory是空怎么办，其实再
            if (objectFactory != null) { 
                // 遍历实例所有的方法
                for (Method method : instance.getClass().getMethods()) {
                    // 如果是setter方法
                    if (isSetter(method)) {
                        /**
                         * Check {@link DisableInject} to see if we need auto injection for this property
                         */
                        if (method.getAnnotation(DisableInject.class) != null) {
                            continue;
                        }
                        Class<?> pt = method.getParameterTypes()[0];
                        if (ReflectUtils.isPrimitives(pt)) {
                            continue;
                        }
                        try {
                            // 获取setter方法属性
                            String property = getSetterProperty(method);
                            // 根据第一个参数和类型 从List<ExtensionFactory>中获取实例
                            Object object = objectFactory.getExtension(pt, property);
                            if (object != null) {
                                // 利用反射将获取的依赖注入到当前实例中
                                method.invoke(instance, object);
                            }
                        } catch (Exception e) {
                            logger.error("Failed to inject via method " + method.getName()
                                    + " of interface " + type.getName() + ": " + e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return instance;
    }
```
1. 如果objectFactory是空(证明还没有加载扩展类)，则先加载扩展类到dubbo的IOC。
2. 获取setter方法的参数和类型， 并从dubbo的IOC中拿到依赖对象的实例。
3. 反射将依赖对象注入到当前实例中。

>objectFactory为什么一定不是空呢，因为再调用`ExtensionLoader.getExtensionLoader(Color.class);`方法的时候， dubbo已经对扩展类IOC进行初始化了， 详情看下面。

其实在调用getExtensionLoader的时候， 就把objectFactory实例化了， 而且初始化了IOC：
```
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        ...
        if (loader == null) {
             // 在这里new的对象
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }
    
    private ExtensionLoader(Class<?> type) {
        this.type = type;
        objectFactory = (type == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension());
    }
```
它先后调用了这几个方法:
1. getAdaptiveExtension(从缓存里取，没有就调用下个方法)
2. createAdaptiveExtension(从缓存里取，没有就调用下个方法)
3. getAdaptiveExtensionClass 调用方法4, 然后调用方法5
4. getExtensionClasses这个方法就和前面(`loader.getExtension("yellow");`)的一样了。
5. createAdaptiveExtensionClass 方法，默认使用javassist生成代理字节码，然后用dubbo的Compiler解析成类并返回。

不知不觉中，dubbo的SPI还有自己独特的AOP和IOC源码就看完了。
>你可能纳闷，我没看到AOP啊。

记得解析Wrapper那一块吗， dubbo用构造方法实例化Wrapper再将依赖注入，就完成了类似AOP的功能， 你可以再Wrapper里面定制你的逻辑。


因为篇幅够长了，javassist部分足以拿出来重立一篇来讲，如果你读完感到很茫然，那就再读一遍，读源码，最重要的是主动接收的心态。多调试几遍，加油!

