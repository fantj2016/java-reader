>上一章介绍了Bean的加载过程(IOC初始化过程)，加载完成后，紧接着就要用到它的依赖注入(IOC 依赖注入)。

那什么是依赖注入呢?
>所谓依赖注入，就是由IOC容器在运行期间，动态地将某种依赖关系注入到对象之中。再完成IOC容器初始化之后，也就是所谓的Bean加载完成后，我们需要对这些Bean进行调用和获取，这个过程就叫依赖注入。


那什么时候会触发依赖注入呢?

>1. 通过getBean()方法获取Bean对象。
>2. 给Bean配置了懒加载，ApplicationContext启动完成后调用getBean()来实例化对象。
>> 现在计算机性能已经足够，不是特殊要求下尽量别做懒加载，这样的话可以减少web运行时的调用时间开销。

好了，介绍完这些就开始我们的DI之旅。
### 1. BeanFactory
>通过Spring获取Bean的最根本的接口。
![](https://upload-images.jianshu.io/upload_images/5786888-b4a5f7890898f6fa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

```
// 如果myJndiObject时FactoryBean， 则 &myJndiObject 将返回工厂而不是返回实例。
String FACTORY_BEAN_PREFIX = "&";
// 获取bean实例
Object getBean(String name) throws BeansException;
// 判断一个bean是否时单例
boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
// 判断一个bean是否是原型
boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
// 检查bean的name和type是否匹配
boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException;
// 获取bean类型
Class<?> getType(String name) throws NoSuchBeanDefinitionException;
// 获取bean别名
String[] getAliases(String name);
```
getBean()方法有很多重载方法，上面只总结了一个。这个方法是DI的入口方法，接下来会从这个方法开始往下研究。

### 2. AbstractBeanFactory
>从名字也能看出，这是BeanFactory的抽象实现类。
```
	public Object getBean(String name) throws BeansException {
		return doGetBean(name, null, null, false);
	}
```
doGetBean()方法也是该类中的方法。
```
	// 依赖注入 从这里开始发生
	private <T> T doGetBean(
			final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly)
			throws BeansException {

		// 根据指定名字获取被管理Bean的名称
		// 如果是别名， 则转换为真正的bean名
		final String beanName = transformedBeanName(name);
		Object bean;

		// Eagerly check singleton cache for manually registered singletons.
		// 先从缓存中取单例 bean
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null && args == null) {
			if (logger.isDebugEnabled()) {
				// 如果有，则直接返回该bean
				if (isSingletonCurrentlyInCreation(beanName)) {
					logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
							"' that is not fully initialized yet - a consequence of a circular reference");
				}
				else {
					logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
				}
			}
			//获取 bean 的实例对象
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}

		else {
			// Fail if we're already creating this bean instance:
			// We're assumably within a circular reference.
			// 如果不是单例对象， 而且 缓存中有原型模式bean， 就抛异常
			if (isPrototypeCurrentlyInCreation(beanName)) {
				throw new BeanCurrentlyInCreationException(beanName);
			}

			// 检查 BeanDefinition 是否再当前的factory中， 如果不在则委托父类容器取查找
			// Check if bean definition exists in this factory.
			BeanFactory parentBeanFactory = getParentBeanFactory();
			if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
				// Not found -> check parent.
				String nameToLookup = originalBeanName(name);
				if (args != null) {
					// Delegation to parent with explicit args.
					// 委托父类容器取找(名字+参数)
					return (T) parentBeanFactory.getBean(nameToLookup, args);
				}
				else {
					// 委托父类容器取找(名称+类型)
					// No args -> delegate to standard getBean method.
					return parentBeanFactory.getBean(nameToLookup, requiredType);
				}
			}

			if (!typeCheckOnly) {
				// 标记 bean 被创建
				markBeanAsCreated(beanName);
			}

			// 根据bean名称获取 父类的 beanDefinition， 合并继承公共属性
			final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
			checkMergedBeanDefinition(mbd, beanName, args);

			// Guarantee initialization of beans that the current bean depends on.
			// 获取当前bean 所有依赖Bean 的集合
			String[] dependsOn = mbd.getDependsOn();
			if (dependsOn != null) {
				for (String dependsOnBean : dependsOn) {
					// 递归调用， 获取当前Bean的依赖Bean
					getBean(dependsOnBean);
					// 把依赖Bean注册给当前的Bean
					registerDependentBean(dependsOnBean, beanName);
				}
			}

			// Create bean instance.
			// 创建bean 实例
			if (mbd.isSingleton()) {
				// 创建 bean 实例对象， 并且注册给所依赖的对象
				sharedInstance = getSingleton(beanName, new ObjectFactory() {
					public Object getObject() throws BeansException {
						try {
							// 创建一个指定bean 实例对象
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							// Explicitly remove instance from singleton cache: It might have been put there
							// eagerly by the creation process, to allow for circular reference resolution.
							// Also remove any beans that received a temporary reference to the bean.
							// 清除该单例
							destroySingleton(beanName);
							throw ex;
						}
					}
				});
				bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
			}

			else if (mbd.isPrototype()) {
				// It's a prototype -> create a new instance.
				Object prototypeInstance = null;
				try {
					beforePrototypeCreation(beanName);
					prototypeInstance = createBean(beanName, mbd, args);
				}
				finally {
					afterPrototypeCreation(beanName);
				}
				bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
			}

			// 如果创建的bean 不是单例也不是原型， 则根据声明周期选择实例化bean的方法
			// 如 request session 等不同范围的实例
			else {
				String scopeName = mbd.getScope();
				final Scope scope = this.scopes.get(scopeName);
				// 如果 scope 是空， 则抛异常
				if (scope == null) {
					throw new IllegalStateException("No Scope registered for scope '" + scopeName + "'");
				}
				// 否则
				try {
					// 获取一个指定了scope的bean实例
					Object scopedInstance = scope.get(beanName, new ObjectFactory() {
						public Object getObject() throws BeansException {
							beforePrototypeCreation(beanName);
							try {
								return createBean(beanName, mbd, args);
							}
							finally {
								afterPrototypeCreation(beanName);
							}
						}
					});
					bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
				}
				catch (IllegalStateException ex) {
					throw new BeanCreationException(beanName,
							"Scope '" + scopeName + "' is not active for the current thread; " +
							"consider defining a scoped proxy for this bean if you intend to refer to it from a singleton",
							ex);
				}
			}
		}

		// Check if required type matches the type of the actual bean instance.
		// 检查是否需要类型检测
		if (requiredType != null && bean != null && !requiredType.isAssignableFrom(bean.getClass())) {
			throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
		}
		return (T) bean;
	}
```
总结以下它都做了什么事情:
1. 根据传来的`bean`的`name`(有可能是别名)来获取真正的`bean`名称:`beanName`。
2. 根据`beanName`获取单例实例，如果有直接获取到`bean实例`并返回，DI完成。
3. 如果根据`beanName`没有获得到单例实例：
3.1 判断是不是原型实例，如果是，则抛出创建失败异常，如果不是，下一步。
3.2 检查`BeanDefinition` 是否在当前的容器中，如果不在那可能在父类容器中，所以委托父类容器查找，如果还没有，则再上一级容器...递归查找。
3.3 检查这个实例是否是为了类型检查而获取，而不是用来使用，如果是，标记这个bean已经被创建，如果不是，下一步。
3.4 根据`beanName`获取`父类的BeanDefinition`，并检查该对象类类型，比如不能是抽象类等。
3.5 根据`beanName`获取所有该`bean`依赖的`Bean集合`，如果该集合有值，则遍历DI(递归调用`getBean()`)该`bean集合`里的bean，并把bean注册给当前的bean(维护了一个`map`来存放关系)。
3.6 如果3.4中获取的`BeanDefinition`是单例，则根据该单例对象和`beanName`和`args`创建一个实例对象；否则，判断`BeanDefinition`是否是原型，如果是则根据`beanName,`该对象,`args`创建一个实例；否则拿到3.4获取的`BeanDefinition`对象的生命周期`Scope`,然后根据`scope`来创建实例对象，参数`(beanName,bd,args)`。
3.7 检查是否需要类型检测
3.8 返回`3.1-3.7 `生成的实例。

##### 然后我们再看看 createBean()方法的实现。
```
	protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object[] args)
			throws BeanCreationException;
```
### 3. AbstractAutowireCapableBeanFactory.java


```
	// 创建bean 实例
	@Override
	protected Object createBean(final String beanName, final RootBeanDefinition mbd, final Object[] args)
			throws BeanCreationException {

		if (logger.isDebugEnabled()) {
			logger.debug("Creating instance of bean '" + beanName + "'");
		}
		// Make sure bean class is actually resolved at this point.
		// 解析和确定 bean 可以实例化
		resolveBeanClass(mbd, beanName);

		// Prepare method overrides.
		// 准备方法覆盖
		try {
			mbd.prepareMethodOverrides();
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanDefinitionStoreException(mbd.getResourceDescription(),
					beanName, "Validation of method overrides failed", ex);
		}

		try {
			// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
			// 给 Bean处理器 一个机会， 返回一个目标bean实例
			Object bean = resolveBeforeInstantiation(beanName, mbd);
			if (bean != null) {
				return bean;
			}
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"BeanPostProcessor before instantiation of bean failed", ex);
		}

		Object beanInstance = doCreateBean(beanName, mbd, args);
		if (logger.isDebugEnabled()) {
			logger.debug("Finished creating instance of bean '" + beanName + "'");
		}
		return beanInstance;
	}
```
总结以下它都做了什么:
1. 确定`beanName`和`RootBeanDefinition`可以被实例化。
2. 执行方法覆盖
3. 看`BeanPostProcessors`能否再解析之前获取到bean，如果能则直接返回，否则下一步。
4. 调用`doCreateBean()`方法，获取`bean`实例.

`doCreateBean()`方法也是该类中的。

```
	// 真正创建bean实例
	protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
		// Instantiat|e the bean.
		// 封装bean
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			// 如果是单例模式的bean，从容器中获取同名bean
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		// 如果没有同名bean， 则创建bean实例
		if (instanceWrapper == null) {
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		// 如果有同名bean， 则获取到封装实例
		final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);
		// 获取实例化对象类型
		Class beanType = (instanceWrapper != null ? instanceWrapper.getWrappedClass() : null);

		// Allow post-processors to modify the merged bean definition.
		// 调用后置处理器
		synchronized (mbd.postProcessingLock) {
			if (!mbd.postProcessed) {
				applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
				mbd.postProcessed = true;
			}
		}

		// Eagerly cache singletons to be able to resolve circular references
		// even when triggered by lifecycle interfaces like BeanFactoryAware.
		boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
		if (earlySingletonExposure) {
			if (logger.isDebugEnabled()) {
				logger.debug("Eagerly caching bean '" + beanName +
						"' to allow for resolving potential circular references");
			}
			addSingletonFactory(beanName, new ObjectFactory() {
				public Object getObject() throws BeansException {
					return getEarlyBeanReference(beanName, mbd, bean);
				}
			});
		}

		// Initialize the bean instance.
		// bean对象初始化， 依赖注入开始，exposedObject就是完成后的bean
		Object exposedObject = bean;
		try {
			// 将bean 实例封装， 并且 bean 定义中配置的属性值赋值给实例对象
			populateBean(beanName, mbd, instanceWrapper);
			// 初始化 bean对象
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}
		catch (Throwable ex) {
			if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
				throw (BeanCreationException) ex;
			}
			else {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
			}
		}

		// 如果指定名称bean已经注册单例模式
		if (earlySingletonExposure) {
			Object earlySingletonReference = getSingleton(beanName, false);
			if (earlySingletonReference != null) {
				if (exposedObject == bean) {
					// 如果两个对象相等， bean初始化完成
					exposedObject = earlySingletonReference;
				}
				// 如果不相等， 则找出当前bean的依赖bean
				else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<String>(dependentBeans.length);
					for (String dependentBean : dependentBeans) {
						// 检查依赖bean （是否继承接口，是否是父子关系。。）
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
					if (!actualDependentBeans.isEmpty()) {
						throw new BeanCurrentlyInCreationException(beanName,
								"Bean with name '" + beanName + "' has been injected into other beans [" +
								StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
								"] in its raw version as part of a circular reference, but has eventually been " +
								"wrapped. This means that said other beans do not use the final version of the " +
								"bean. This is often the result of over-eager type matching - consider using " +
								"'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
					}
				}
			}
		}

		// Register bean as disposable.
		// 注册完成依赖注入的bean
		try {
			registerDisposableBeanIfNecessary(beanName, bean, mbd);
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
		}

		return exposedObject;
	}
```
同样，总结以下它干的事情：
1. 根据`beanName`获取`beanWrapper`对象。如果`beanWrapper`对象是空，则调用`createBeanInstance()`方法创建`bean`实例。否则，下一步
2. 通过`beanWrapper`对象获取bean实例和`class`类型。
3. 允许 `postProcessors` 调整组合`BeanDefinition`。
4. 如果`RootBeanDefinition`是单例并且允许循环引用并且`beanName`正在进行单例创建，将`beanName`添加到单例工厂。
5. 调用`populateBean()`方法给bean的属性值赋值，然后初始化bean对象并返回创建的bean实例，如果抛异常，则下一步。
6. 如果该`beanName`对象已经注册单例模式，则从单例中获取，并判断获取到的bean实例(`B`)与`BeanWrapper`中的bean实例(`A`)是同一个实例，如果是，则返回`A`或者`B`，如果不是，则递归找出它的依赖`bean`。
7. 返回`1-6`产生的`bean`实例。

我们首次获取bean实例的时候，bean工厂是肯定没有的，所以我们首次获取到的BeanWrapper应该是空对象，但是它调用了`createBeanInstance()`方法后，可以看到spring是很确定它能拿到对象，那么我们看看这个方法的实现。它仍然是这个类中的方法。

```
	protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
		// Make sure bean class is actually resolved at this point.
		// 确保bean可实例化(不能是抽象类等)
		Class beanClass = resolveBeanClass(mbd, beanName);

		// 如果这个bean 不是public 修饰符或者不被允许公共访问， 抛出异常
		if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
					"Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
		}

		if (mbd.getFactoryMethodName() != null)  {
			// 通过工厂方法实例化
			return instantiateUsingFactoryMethod(beanName, mbd, args);
		}

		// Shortcut when re-creating the same bean...
		// 是否有构造器
		if (mbd.resolvedConstructorOrFactoryMethod != null && args == null) {
			if (mbd.constructorArgumentsResolved) {
				return autowireConstructor(beanName, mbd, null, null);
			}
			else {
				return instantiateBean(beanName, mbd);
			}
		}

		// Need to determine the constructor...
		// 需要确认构造器
		Constructor[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
		if (ctors != null ||
				mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR ||
				mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args))  {
			// 自动装配，调用匹配的构造方法进行实例化
			return autowireConstructor(beanName, mbd, ctors, args);
		}

		// No special handling: simply use no-arg constructor.
		// 使用默认无参构造
		return instantiateBean(beanName, mbd);
	}
```
这个类用来创建`Bean`实例，然后返回`BeanWrapper`对象。注释写的很详细了。其中有个`instantiateBean()`方法，当没有参数和构造方法的时候，就会调用该方法来实例化bean。

```
	// 使用默认无参构造方法实例化bean
	protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
		try {
			Object beanInstance;
			final BeanFactory parent = this;
			// 获取JDK安全管理
			if (System.getSecurityManager() != null) {
				// 根据实例化策略实例化对象
				beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
	
					public Object run() {
						return getInstantiationStrategy().instantiate(mbd, beanName, parent);
					}
				}, getAccessControlContext());
			}
			else {
				beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
			}
			// 对实例化对象进行封装
			BeanWrapper bw = new BeanWrapperImpl(beanInstance);
			initBeanWrapper(bw);
			return bw;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
		}
	}
```
这个方法是使用默认无参构造方法实例化bean的，它的核心代码是`getInstantiationStrategy().instantiate(mbd, beanName, parent);`,因为它，我们可以得到一个bean实例对象，然后封装成`BeanWrapper`并返回。

### 4. SimpleInstantiationStrategy.java
>用于BeanFactory的简单对象实例化策略。不支持方法注入，尽管它提供了子类的hook来覆盖以添加方法注入支持，例如通过重写方法。

```
    // 使用初始化策略 实例化bean
	public Object instantiate(
			RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

		// Don't override the class with CGLIB if no overrides.
		// 如果beanDefinition 中没有方法覆盖， 就用jdk，否则用cglib
		if (beanDefinition.getMethodOverrides().isEmpty()) {
			// 获取对象的构造方法和工厂方法
			Constructor constructorToUse = (Constructor) beanDefinition.resolvedConstructorOrFactoryMethod;
                      
			if (constructorToUse == null) {
				// 如果 没有构造方法和工厂方法， 使用JDK反射， 判断实例化的bean是不是接口
				final Class clazz = beanDefinition.getBeanClass();
				if (clazz.isInterface()) {
					throw new BeanInstantiationException(clazz, "Specified class is an interface");
				}
				try {
					if (System.getSecurityManager() != null) {
						// 使用反射获取bean构造方法
						constructorToUse = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor>() {
							public Constructor run() throws Exception {
								return clazz.getDeclaredConstructor((Class[]) null);
							}
						});
					} else {
						constructorToUse =	clazz.getDeclaredConstructor((Class[]) null);
					}
					beanDefinition.resolvedConstructorOrFactoryMethod = constructorToUse;
				}
				catch (Exception ex) {
					throw new BeanInstantiationException(clazz, "No default constructor found", ex);
				}
			}
			// 使用beanUtils实例化   构造方法.newInstance(arg) 来实例化
			return BeanUtils.instantiateClass(constructorToUse);
		}
		else {
			//如果 有覆盖或者重写， 则用CGLIB来实例化对象
			// Must generate CGLIB subclass.
			return instantiateWithMethodInjection(beanDefinition, beanName, owner);
		}
	}
```
总结它的步骤：
1. 如果BeanDefinition的覆盖方法不为空，则交给CGLIB来实例化对象，否则获取构造方法和工厂方法，下一步。
2. 如果没有构造方法和工厂方法，则使用JDK反射，判断实例化的bean是不是接口，如果是，抛出异常，如果不是，则使用反射来获取bean的构造方法，最后，用`构造器.newInstance()`的方法(`BeanUtils.instantiateClass()`方法底层实现)来实例化并返回。

那cglib是如何实例化呢，我们来看下`instantiateWithMethodInjection(beanDefinition, beanName, owner);`方法源码：
```
	@Override
	protected Object instantiateWithMethodInjection(
			RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

		// Must generate CGLIB subclass.
		return new CglibSubclassCreator(beanDefinition, owner).instantiate(null, null);
	}
```

然后再跟进`CglibSubclassCreator(beanDefinition, owner).instantiate(null, null);`方法:

```
		// 使用cglib 来进行bean实例化
		public Object instantiate(Constructor ctor, Object[] args) {
			// cglib
			Enhancer enhancer = new Enhancer();
			// bean本身作为基类
			enhancer.setSuperclass(this.beanDefinition.getBeanClass());
			enhancer.setCallbackFilter(new CallbackFilterImpl());
			enhancer.setCallbacks(new Callback[] {
					NoOp.INSTANCE,
					new LookupOverrideMethodInterceptor(),
					new ReplaceOverrideMethodInterceptor()
			});

			// 生成实例对象
			return (ctor == null) ? 
					enhancer.create() : 
					enhancer.create(ctor.getParameterTypes(), args);
		}
```
从上面代码可以看到，这就是CGLIB动态代理中创建代理的过程代码，不熟悉的可以往前翻`彻底搞懂动态代理`章节的内容。


好了，到了这里，Spring就完成了bean实例的创建，但是此时就能拿着这个实例去使用吗，显然是不可以，因为属性还没有被赋入，下一章再继续介绍如何将属性依赖关系注入到Bean实例对象。
