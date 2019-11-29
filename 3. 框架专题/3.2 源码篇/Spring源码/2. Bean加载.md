### 0. Bean 加载原理
>加载过程： 通过 `ResourceLoader`和其子类`DefaultResourceLoader`完成资源文件位置定位，实现从类路径，文件系统，url等方式定位功能，完成定位后得到`Resource`对象，再交给`BeanDefinitionReader`，它再委托给`BeanDefinitionParserDelegate`完成bean的解析并得到`BeanDefinition`对象，然后通过`registerBeanDefinition`方法进行注册，IOC容器内ibu维护了一个HashMap来保存该`BeanDefinition`对象，Spring中的`BeanDefinition`其实就是我们用的`JavaBean`。

##### 什么是BeanDefinition对象
>BeanDefinition是一个接口，描述了一个bean实例，它具有属性值，构造函数参数值以及具体实现提供的更多信息。
![](https://upload-images.jianshu.io/upload_images/5786888-e969d5a2f048be21.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


在开始之前需要认真阅读和理解这个过程，有了这个过程，阅读源码难度就小了一半。

大多源码都进行了注释，有的是官方英文注释。


### 1. bean.xml
>一个普通的bean配置文件，这里我要强调的是它里面的格式，因为解析标签的时候会用到。它有`<beans>``<bean>``<import>``<alias>`等标签，下文会对他们进行解析并翻译成BeanDefinition对象。
```
<beans>

  <!-- this definition could be inside one beanRefFactory.xml file -->
  <bean id="a.qualified.name.of.some.sort"
      class="org.springframework.context.support.ClassPathXmlApplicationContext">
    <property name="configLocation" value="org/springframework/web/context/beans1.xml"/>
  </bean>

  <!-- while the following two could be inside another, also on the classpath,
	perhaps coming from another component jar -->
  <bean id="another.qualified.name"
      class="org.springframework.context.support.ClassPathXmlApplicationContext">
    <property name="configLocation" value="org/springframework/web/context/beans1.xml"/>
    <property name="parent" ref="a.qualified.name.of.some.sort"/>
  </bean>

  <alias name="another.qualified.name" alias="a.qualified.name.which.is.an.alias"/>

</beans>
```

### 2. ResourceLoader.java
>加载资源的策略接口(策略模式)。
DefaultResourceLoader is a standalone implementation that is usable outside an ApplicationContext, also used by ResourceEditor

>An ApplicationContext is required to provide this functionality, plus extended ResourcePatternResolver support.
```
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:". */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;
       
        // 返回一个Resource 对象 (明确配置文件位置的对象)
	Resource getResource(String location);

        // 返回ResourceLoader的ClassLoader
	@Nullable
	ClassLoader getClassLoader();
}
```
然后我们看看`DefaultResourceLoader`对于`getResource()`方法的实现。

```
	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");

		for (ProtocolResolver protocolResolver : this.protocolResolvers) {
			Resource resource = protocolResolver.resolve(location, this);
			if (resource != null) {
				return resource;
			}
		}
               // 如果location 以 / 开头
		if (location.startsWith("/")) {
			return getResourceByPath(location);
		}
                // 如果location 以classpath: 开头
		else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		}
		else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
			}
			catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}
```
可以看到，它判断了三种情况：`/` `classpath:` `url格式匹配`， 然后调用相对应的处理方法，我只分析`classpath:`，因为这是最常用的。所以看一看`ClassPathResource`实现：
```
	public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
		Assert.notNull(path, "Path must not be null");
		String pathToUse = StringUtils.cleanPath(path);
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		this.path = pathToUse;
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}
```
看了上面的代码，意味着你配置静态资源文件路径的时候，不用纠结`classpath:`后面用不用写`/`,因为如果写了它会给你过滤掉。

那url如何定位的呢?
>跟踪getResourceByPath(location)方法:
```
	@Override
	protected Resource getResourceByPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		// 这里使用文件系统资源对象来定义bean文件
		return new FileSystemResource(path);
	}
```

好了，很明显...跑偏了，因为我们想要的是xml文件及路径的解析，不过还好，换汤不换药。下文中会涉及到。

##### 触发bean加载
回到正题，我们在使用spring手动加载bean.xml的时候，用到：
```
ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean.xml");
```
那就从ClassPathXmlApplicationContext开始：
### 3. ClassPathXmlApplicationContext.java
>这个类里面只有构造方法(多个)和一个getConfigResources()方法，构造方法最终都统一打到下面这个构造方法中(Spring源码经常这样，适配器模式): 


```
	public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {
	// 动态的确定用哪个加载器去加载 配置文件
		1.super(parent);
	// 告诉读取器 配置文件在哪里， 定位加载配置文件
		2.setConfigLocations(configLocations);
	// 刷新
		if (refresh) {
			// 在创建IOC容器前，如果容器已经存在，则需要把已有的容器摧毁和关闭，以保证refresh
			//之后使用的是新的IOC容器
			3.refresh();
		}
	}
```

**注意:** 这个类非常关键，我认为它定义了一个`xml`加载`bean`的一个`Life Cycle`:
1. `super()` 方法完成类加载器的指定。
2. `setConfigLocations(configLocations);`方法对配置文件进行定位和解析，拿到Resource对象。
3. ` refresh();`方法对标签进行解析拿到BeanDefition对象，在通过校验后将其注册到IOC容器。(主要研究该方法)

我标记的1. 2. 3. 对应后面的方法x, 方便阅读。

先深入了解下`setConfigLocations(configLocations);`方法:
##### 方法2. setConfigLocations()
```
	// 解析Bean定义资源文件的路径，处理多个资源文件字符串数组
	public void setConfigLocations(@Nullable String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				// resolvePath 为同一个类中将字符串解析为路径的方法
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}
```

然后我们继续上面看`ClassPathXmlApplicationContext`的`refresh()`方法:
##### 方法3. refresh()
```
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// 为refresh 准备上下文
			prepareRefresh();

			// 通知子类去刷新 Bean工厂
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// 用该 上下文来 准备bean工厂
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
				invokeBeanFactoryPostProcessors(beanFactory);

				// Register bean processors that intercept bean creation.
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
				initMessageSource();

				// Initialize event multicaster for this context.
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
				onRefresh();

				// Check for listener beans and register them.
				registerListeners();

				// Instantiate all remaining (non-lazy-init) singletons.
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();

				// Reset 'active' flag.
				cancelRefresh(ex);

				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}
```
**注:**下面的方法全都是围绕`refresh()`里深入阅读，该方法套的很深，下面的阅读可能会引起不适。

然后看看`refresh()`方法中的`obtainFreshBeanFactory()`方法:
##### 方法3.1 obtainFreshBeanFactory()
```
	// 调用--刷新bean工厂
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		// 委派模式：父类定义了refreshBeanFactory方法，具体实现调用子类容器
		refreshBeanFactory();
		return getBeanFactory();
	}
```
然后看`obtainFreshBeanFactory()`的 `refreshBeanFactory()`方法
##### 方法3.1.1 refreshBeanFactory()
```
       // 刷新bean工厂
	protected final void refreshBeanFactory() throws BeansException {
		// 如果存在容器，就先销毁并关闭
		if (hasBeanFactory()) {
			destroyBeans();
			closeBeanFactory();
		}
		try {
			// 创建IOC容器
			DefaultListableBeanFactory beanFactory = createBeanFactory();
			beanFactory.setSerializationId(getId());
			// 对容器进行初始化
			customizeBeanFactory(beanFactory);
			// 调用载入Bean定义的方法，(使用了委派模式)
			loadBeanDefinitions(beanFactory);
			synchronized (this.beanFactoryMonitor) {
				this.beanFactory = beanFactory;
			}
		}
		catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}
```
然后再跟进`refreshBeanFactory() `的`loadBeanDefinitions()`方法：

##### 方法3.1.1.1 loadBeanDefinitions()

>通过 XmlBeanDefinitionReader 加载 BeanDefinition

```
	// 通过 XmlBeanDefinitionReader 加载 BeanDefinition
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// Create a new XmlBeanDefinitionReader for the given BeanFactory.
		// 为beanFactory 创建一个新的 XmlBeanDefinitionReader
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// Configure the bean definition reader with this context's
		// resource loading environment.
		beanDefinitionReader.setEnvironment(this.getEnvironment());
		// 为 Bean读取器设置Spring资源加载器 (因为祖父类是ResourceLoader的子类，所以也是ResourceLoader)
		beanDefinitionReader.setResourceLoader(this);
		//  为 Bean读取器设置SAX xml解析器DOM4J
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

		// Allow a subclass to provide custom initialization of the reader,
		// then proceed with actually loading the bean definitions.
		// 初始化 BeanDefinition读取器
		initBeanDefinitionReader(beanDefinitionReader);
		// 真正加载 bean定义
		loadBeanDefinitions(beanDefinitionReader);
	}
```
再跟进`loadBeanDefinitions(DefaultListableBeanFactory beanFactory)`方法中的`loadBeanDefinitions(XmlBeanDefinitionReader reader)`方法:

##### 方法3.1.1.1.1 loadBeanDefinitions()
>XMLBean读取器加载BeanDefinition 资源

```
	// XMLBean读取器加载Bean 定义资源
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		// 获取Bean定义资源的定位
		Resource[] configResources = getConfigResources();
		if (configResources != null) {
			// XMLBean读取器调用其父类 AbstractBeanDefinitionReader 读取定位的Bean定义资源
			reader.loadBeanDefinitions(configResources);
		}
		// 如果子类中获取的bean定义资源定位为空，
		// 则获取 FileSystemXmlApplicationContext构造方法中 setConfigLocations 方法设置的资源
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			// XMLBean读取器调用其父类 AbstractBeanDefinitionReader 读取定位的Bean定义资源
			reader.loadBeanDefinitions(configLocations);
		}
	}
```
```
	@Override
	public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
		Assert.notNull(resources, "Resource array must not be null");
		int count = 0;
		//
		for (Resource resource : resources) {
			count += loadBeanDefinitions(resource);
		}
		return count;
	}
```
再跟下去`loadBeanDefinitions()`: 这只是一个抽象方法，找到`XmlBeanDefinitionReader`子类的实现：
```
	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource));
	}
```
再深入`loadBeanDefinitions`:

>通过明确的xml文件加载bean
```
    // 通过明确的xml文件加载bean
	public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Loading XML bean definitions from " + encodedResource);
		}

		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		if (currentResources == null) {
			currentResources = new HashSet<>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		if (!currentResources.add(encodedResource)) {
			throw new BeanDefinitionStoreException(
					"Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		try {
			// 将资源文件转为InputStream的IO流
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				// 从流中获取 xml解析资源
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					// 设置编码
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				// 具体的读取过程
				return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
			}
			finally {
				inputStream.close();
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), ex);
		}
		finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}
```
再深入到`doLoadBeanDefinitions()`:
>真正开始加载 BeanDefinitions 
```
	protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

		try {
			// 将xml 文件转换为DOM对象
			Document doc = doLoadDocument(inputSource, resource);
			// 对bean定义解析的过程，该过程会用到 Spring的bean配置规则
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + count + " bean definitions from " + resource);
			}
			return count;
		}
        ...  ...  ..
}
```
`doLoadDocument()`方法将流进行解析，返回一个Document对象:`return builder.parse(inputSource);`为了避免扰乱思路，这里的深入自己去完成。

还需要再深入到:`registerBeanDefinitions()`
>注册 BeanDefinitions 

```
	public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		// 得到容器中注册的bean数量
		int countBefore = getRegistry().getBeanDefinitionCount();
		// 解析过程入口，这里使用了委派模式
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
		// 统计解析的bean数量
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}
```
再深入`registerBeanDefinitions()`方法（该方法是委派模式的结果）:
```
	@Override
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		// 获得XML描述符
		this.readerContext = readerContext;
		doRegisterBeanDefinitions(doc.getDocumentElement());
	}
```
再深入`doRegisterBeanDefinitions(doc.getDocumentElement());`：
>真正开始注册 BeanDefinitions :

```
protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createDelegate(getReaderContext(), root, parent);

		if (this.delegate.isDefaultNamespace(root)) {
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				// We cannot use Profiles.of(...) since profile expressions are not supported
				// in XML config. See SPR-12458 for details.
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}

		// 在bean解析定义之前，进行自定义解析，看是否是用户自定义标签
		preProcessXml(root);
		// 开始进行解析bean定义的document对象
		parseBeanDefinitions(root, this.delegate);
		// 解析bean定义之后，进行自定义的解析，增加解析过程的可扩展性
		postProcessXml(root);

		this.delegate = parent;
	}
```
接下来看`parseBeanDefinitions(root, this.delegate);`:
>document的根元素开始进行解析翻译成BeanDefinitions

```
	// 从document的根元素开始进行解析翻译成BeanDefinitions
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		// bean定义的document对象使用了spring默认的xml命名空间
		if (delegate.isDefaultNamespace(root)) {
			// 获取bean定义的document对象根元素的所有字节点
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				// 获得document节点是xml元素节点
				if (node instanceof Element) {
					Element ele = (Element) node;
					// bean定义的document的元素节点使用的是spring默认的xml命名空间
					if (delegate.isDefaultNamespace(ele)) {
						// 使用spring的bean规则解析元素 节点
						parseDefaultElement(ele, delegate);
					}
					else {
						// 没有使用spring默认的xml命名空间，则使用用户自定义的解析规则解析元素节点
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {
			delegate.parseCustomElement(root);
		}
	}

	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		// 解析 <import> 标签元素，并进行导入解析
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		// alias
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		// bean
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}
		// beans
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
			// recurse
			doRegisterBeanDefinitions(ele);
		}
	}
```
` importBeanDefinitionResource(ele);``processAliasRegistration(ele);``processBeanDefinition(ele, delegate);`这三个方法里分别展示了标签解析的详细过程。
这下看到了，它其实使用DOM4J来解析`import` `bean` `alias`等标签，然后递归标签内部直到拿到所有属性并封装到BeanDefition对象中。比如说`processBeanDefinition`方法:
>给我一个element 解析成 BeanDefinition

```
	// 给我一个element 解析成 BeanDefinition
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		// 真正解析过程
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				// 注册： 将db注册到ioc，委托模式
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}
```
继续深入`registerBeanDefinition():`
>注册BeanDefinitions 到 bean 工厂

```
	// 注册BeanDefinitions 到 bean 工厂
	// definitionHolder ： bean定义，包含了 name和aliases
	// registry： 注册到的bean工厂
	public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		// Register bean definition under primary name.
		String beanName = definitionHolder.getBeanName();
		// 真正注册
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

		// Register aliases for bean name, if any.
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}
```
再深入`registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());`
>注册BeanDefinitions 到IOC容器

**注意**:该方法所在类是接口，我们查看的是`DefaultListableBeanFactory.java`所实现的该方法。
```
	// 实现BeanDefinitionRegistry接口,注册BeanDefinitions 
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {

		Assert.hasText(beanName, "Bean name must not be empty");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");

		// 校验是否是 AbstractBeanDefinition)
		if (beanDefinition instanceof AbstractBeanDefinition) {
			try {
				// 标记 beanDefinition 生效
				((AbstractBeanDefinition) beanDefinition).validate();
			}
			catch (BeanDefinitionValidationException ex) {
				throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
						"Validation of bean definition failed", ex);
			}
		}

		// 判断beanDefinitionMap 里是否已经有这个bean
		BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
		//如果没有这个bean
		if (existingDefinition != null) {
			//如果不允许bd 覆盖已注册的bean， 就抛出异常
			if (!isAllowBeanDefinitionOverriding()) {
				throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
			}
			// 如果允许覆盖， 则同名的bean， 注册的覆盖先注册的
			else if (existingDefinition.getRole() < beanDefinition.getRole()) {
				// e.g. was ROLE_APPLICATION, now overriding with ROLE_SUPPORT or ROLE_INFRASTRUCTURE
				if (logger.isInfoEnabled()) {
					logger.info("Overriding user-defined bean definition for bean '" + beanName +
							"' with a framework-generated bean definition: replacing [" +
							existingDefinition + "] with [" + beanDefinition + "]");
				}
			}
			else if (!beanDefinition.equals(existingDefinition)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Overriding bean definition for bean '" + beanName +
							"' with a different definition: replacing [" + existingDefinition +
							"] with [" + beanDefinition + "]");
				}
			}
			else {
				if (logger.isTraceEnabled()) {
					logger.trace("Overriding bean definition for bean '" + beanName +
							"' with an equivalent definition: replacing [" + existingDefinition +
							"] with [" + beanDefinition + "]");
				}
			}
			// 注册到容器，beanDefinitionMap 就是个容器
			this.beanDefinitionMap.put(beanName, beanDefinition);
		}
		else {
			if (hasBeanCreationStarted()) {
				// Cannot modify startup-time collection elements anymore (for stable iteration)
				synchronized (this.beanDefinitionMap) {
					this.beanDefinitionMap.put(beanName, beanDefinition);
					List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
					updatedDefinitions.addAll(this.beanDefinitionNames);
					updatedDefinitions.add(beanName);
					this.beanDefinitionNames = updatedDefinitions;
					if (this.manualSingletonNames.contains(beanName)) {
						Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
						updatedSingletons.remove(beanName);
						this.manualSingletonNames = updatedSingletons;
					}
				}
			}
			else {
				// Still in startup registration phase
				this.beanDefinitionMap.put(beanName, beanDefinition);
				this.beanDefinitionNames.add(beanName);
				this.manualSingletonNames.remove(beanName);
			}
			this.frozenBeanDefinitionNames = null;
		}

		if (existingDefinition != null || containsSingleton(beanName)) {
			resetBeanDefinition(beanName);
		}
	}
```
这个方法中对所需要加载的bean进行校验，没有问题的话就`put`到`beanDefinitionMap`中，`beanDefinitionMap`其实就是IOC.这样我们的Bean就被加载到IOC容器中了。

