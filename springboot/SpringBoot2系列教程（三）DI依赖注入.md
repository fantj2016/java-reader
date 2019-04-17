我们用Spring Framework技术来定义bean及其注入的依赖关系。
一般，我们用@ComponentScan 来装载Bean  和用 @Autowired 来装载构造方法。

如果在启动类上有@ComponentScan注解。@Component，@Service，@Repository，@Controller等组件都会自动注册为Bean。

以下示例显示了@Service使用构造函数注入来获取所需RiskAssessorbean的Bean：
```
package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseAccountService implements AccountService {

	private final RiskAssessor riskAssessor;

	@Autowired
	public DatabaseAccountService(RiskAssessor riskAssessor) {
		this.riskAssessor = riskAssessor;
	}

	// ...

}
```
如果只有一个构造方法，则可以省略@Autowired注解
```
@Service
public class DatabaseAccountService implements AccountService {

	private final RiskAssessor riskAssessor;

	public DatabaseAccountService(RiskAssessor riskAssessor) {
		this.riskAssessor = riskAssessor;
	}

	// ...

}
```
