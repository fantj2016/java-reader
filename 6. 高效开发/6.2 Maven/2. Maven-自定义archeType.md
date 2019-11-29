### 1. 什么是archeType
>我们在创建maven项目的时候，你会发现有这么多的apache提供的模板。
![](https://upload-images.jianshu.io/upload_images/5786888-28015d714b9b0dd4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
或者使用`mvn archetype:generate`命令来快速创建maven项目，也会有很多个选项，让你选择模板序号。那每个模板之间有什么区别呢？

每个模板里其实就是附带不同的依赖和插件。一般在公司私服里都会有属于本公司的一套archeType模板，里面有着调试好的项目用到的依赖包和版本号。


### 2. 创建archetype
>假如自己已经有了一个maven项目，想给该项目创建一个archeType模板。

cd 到项目根目录下执行(pom.xml同级目录)。
```
mvn archetype:create-from-project 
```
此时会在项目target下生成这些文件:

![](https://upload-images.jianshu.io/upload_images/5786888-9241f3a6dd41bf1f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 3. 生成archetype模板
```
先  cd target/generated-sources/archetype/

然后执行 mvn install 
```
执行成功后，执行`crawl`命令，在本地仓库的根目录生成`archetype-catalog.xml`骨架配置文件:
```
mvn archetype:crawl
```

![](https://upload-images.jianshu.io/upload_images/5786888-4a7098c1dbc7d9e3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

来看一看它里面的内容:
```
[fantj@lalala repository]$ cat archetype-catalog.xml 
<?xml version="1.0" encoding="UTF-8"?>
<archetype-catalog xsi:schemaLocation="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-catalog/1.0.0 http://maven.apache.org/xsd/archetype-catalog-1.0.0.xsd"
    xmlns="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-catalog/1.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <archetypes>
    <archetype>
      <groupId>com.fantj</groupId>
      <artifactId>my-self-defind-archtype-archetype</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <description>my-self-defind-archtype</description>
    </archetype>
  </archetypes>
</archetype-catalog>
```

### 4. 使用archetype模板
>执行` mvn archetype:generate -DarchetypeCatalog=local`从本地archeType模板中创建项目。
```
 mvn archetype:generate -DarchetypeCatalog=local
```
然后会让你选择模板序号和`groupId``artifactId``version`和`package`信息：
```
Choose archetype:
1: local -> com.fantj:my-self-defind-archtype-archetype (my-self-defind-archtype)
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): : 1
Define value for property 'groupId': com.fantj
Define value for property 'artifactId': my-self-defind-archetype-test
Define value for property 'version' 1.0-SNAPSHOT: : 
Define value for property 'package' com.fantj: : 
Confirm properties configuration:
groupId: com.fantj
artifactId: my-self-defind-archetype-test
version: 1.0-SNAPSHOT
package: com.fantj
 Y: : y
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: my-self-defind-archtype-archetype:0.0.1-SNAPSHOT
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: com.fantj
[INFO] Parameter: artifactId, Value: my-self-defind-archetype-test
[INFO] Parameter: version, Value: 1.0-SNAPSHOT
[INFO] Parameter: package, Value: com.fantj
[INFO] Parameter: packageInPathFormat, Value: com/fantj
[INFO] Parameter: package, Value: com.fantj
[INFO] Parameter: version, Value: 1.0-SNAPSHOT
[INFO] Parameter: groupId, Value: com.fantj
[INFO] Parameter: artifactId, Value: my-self-defind-archetype-test
[INFO] Project created from Archetype in dir: /home/fantj/IdeaProjects/maven-tutorial/my-self-defind-archetype-test
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```
项目创建成功!


##### 当然，也可以使用IDEA来帮我们用图形界面使用archeType模板创建项目：
![图1](https://upload-images.jianshu.io/upload_images/5786888-bd72cbdacbfde795.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![图2](https://upload-images.jianshu.io/upload_images/5786888-2bf06239b2ed19a0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![图3](https://upload-images.jianshu.io/upload_images/5786888-3abddbe58df544cb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

后面的就与创建普通项目相同了，不做演示。
