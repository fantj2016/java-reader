#####JDK: 
java development kit, java开发工具包，针对开发者，里面主要包含了jre, jvm, jdk源码包，以及bin文件夹下用于开发，编译运行的一些指令器。

#####JRE: 
java runtime environment, java运行时环境，针对java用户，也就是拥有可运行的.class文件包（jar或者war）的用户。里面主要包含了jvm和java运行时基本类库（rt.jar）。rt.jar可以简单粗暴地理解为：它就是java源码编译成的jar包（解压出来看一下），用eclipse开发时，当你ctrl点击发现不能跳转到源文件时，需要把rt.jar对应的源码包加进来，而这里的源码包正是jdk文件夹下的src.zip。

######JVM: 
　　就是我们常说的java虚拟机，它是整个java实现跨平台的最核心的部分，所有的java程序会首先被编译为.class的类文件，这种类文件可以在虚拟机上执行。

　　也就是说class并不直接与机器的操作系统相对应，而是经过虚拟机间接与操作系统交互，由虚拟机将程序解释给本地系统执行。

　　只有JVM还不能成class的执行，因为在解释class的时候JVM需要调用解释所需要的类库lib，而jre包含lib类库。

　　JVM屏蔽了与具体操作系统平台相关的信息，使得Java程序只需生成在Java虚拟机上运行的目标代码（字节码），就可以在多种平台上不加修改地运行。

####JDK和JRE区别：
去bin文件夹下你会发现，JDK有javac.exe而JRE里面没有，众所周知javac指令是用来将java文件编译成class文件的，这是你开发去做的事，用户是不会去做的。JDK还有jar.exe, javadoc.exe等等用于开发的可执行指令文件。这也证实了一个是开发环境，一个是运行环境。

####JRE和JVM区别：
有些人觉得，JVM就可以执行class了，其实不然，JVM执行.class还需要JRE下的lib类库的支持，尤其是rt.jar。

![三者关系图](https://upload-images.jianshu.io/upload_images/5786888-47b2b84a598d891b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


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
