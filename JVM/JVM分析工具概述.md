### JPS
>java process status 查看进程
###### jps
```
C:\Users\84407>jps
13680 Jps
14704 JConsole
10148 Launcher
8788
10844 ThreadConsole
```
###### jps -q
仅仅显示VM 标示，不显示jar,class, main参数等信息
```
C:\Users\84407>jps -q
18324
15336
19144
19816
```
###### jps -m
输出主函数传入的参数
```
C:\Users\84407>jps -m
15336 ThreadConsole
19144
19816 Launcher C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/annotations.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/httpclient-4.5.2.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/slf4j-api-1.7.10.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/log4j.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/snappy-in-java-0.5.1.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/util.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/jna.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/idea_rt.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/jps-model.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/jna-platform.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/oro-2.0.8.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/aether-dependency-resolver.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/httpcore-4.4.5.jar;C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/lib/netty-all-4.1.13.Final.jar;C:/Program Files/
19148 Jps -m
```
IntelliJ IDEA的pid：19816，参数就是后面那一大堆

###### jps  -l
输出应用程序主类完整package名称或jar完整名称
```
C:\Users\84407>jps -l
15336 com.jvm.jconsole.ThreadConsole
19144
19816 org.jetbrains.jps.cmdline.Launcher
9704 jdk.jcmd/sun.tools.jps.Jps
```
###### jps  -v
 列出jvm参数
```
15336 ThreadConsole -javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\lib\idea_rt.jar=61866:C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\bin -Dfile.encoding=UTF-8
19144  -Xms128m -Xmx750m -XX:ReservedCodeCacheSize=240m -XX:+UseConcMarkSweepGC -XX:SoftRefLRUPolicyMSPerMB=50 -ea -Dsun.io.useCanonCaches=false -Djava.net.preferIPv4Stack=true -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -Dide.no.platform.update=true -Djb.vmOptionsFile=C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\bin\idea64.exe.vmoptions -Didea.jre.check=true -Dide.native.launcher=true -Didea.paths.selector=IntelliJIdea2018.1 -XX:ErrorFile=C:\Users\84407\java_error_in_idea_%p.log -XX:HeapDumpPath=C:\Users\84407\java_error_in_idea.hprof
19816 Launcher -Xmx700m -Djava.awt.headless=true -Djava.endorsed.dirs="" -Djdt.compiler.useSingleThread=true -Dpreload.project.path=D:/workspace/java-nio-and-netty-spring-demo -Dpreload.config.path=C:/Users/84407/.IntelliJIdea2018.1/config/options -Dcompile.parallel=false -Drebuild.on.dependency.change=true -Djava.net.preferIPv4Stack=true -Dio.netty.initialSeedUniquifier=-6623792082140627021 -Dfile.encoding=GBK -Duser.language=zh -Duser.country=CN -Didea.paths.selector=IntelliJIdea2018.1 -Didea.home.path=C:\Program Files\JetBrains\IntelliJ IDEA 2018.1 -Didea.config.path=C:\Users\84407\.IntelliJIdea2018.1\config -Didea.plugins.path=C:\Users\84407\.IntelliJIdea2018.1\config\plugins -Djps.log.dir=C:/Users/84407/.IntelliJIdea2018.1/system/log/build-log -Djps.fallback.jdk.home=C:/Program Files/JetBrains/IntelliJ IDEA 2018.1/jre64 -Djps.fallback.jdk.version=1.8.0_152-release -Dio.netty.noUnsafe=true -Djava.io.tmpdir=C:/Users/84407/.IntelliJIdea2018.1/system/compile-server/java-nio-and-netty-spring-demo_64d579b4/_temp_ -Djps.backw
15052 Jps -Denv.class.path=.;C:\Program Files\Java\jdk-10\lib;C:\Program Files\Java\jre-10\lib; -Dapplication.home=C:\Program Files\Java\jdk-10 -Xms8m -Djdk.module.main=jdk.jcmd
```

详情请参考：https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jps.html
### Jstat
类装载、内存、垃圾收集、jit编译信息


M：表示元空间，不再虚拟机中，而是使用本地内存。


### Jinfo
>实时查看和调整虚拟机的各项参数

###### jinfo -flags <pid>
查看jvm的参数
```
C:\Users\84407>jinfo -flags 15336
VM Flags:
-XX:CICompilerCount=3 -XX:InitialHeapSize=201326592 -XX:MaxHeapSize=3196059648 -XX:MaxNewSize=1065353216 -XX:MinHeapDeltaBytes=524288 -XX:NewSize=67108864 -XX:OldSize=134217728 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
```
###### jinfo -sysprops <pid>
查看java系统参数
```
...
user.name=84407
java.vm.specification.version=1.8
sun.java.command=com.jvm.jconsole.ThreadConsole
java.home=C\:\\Program Files\\Java\\jdk1.8.0_144\\jre
sun.arch.data.model=64
user.language=zh
java.specification.vendor=Oracle Corporation
awt.toolkit=sun.awt.windows.WToolkit
java.vm.info=mixed mode
java.version=1.8.0_144
java.ext.dirs=C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\ext;C\:\\WINDOWS\\Sun\\Java\\lib\\ext
sun.boot.class.path=C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\resources.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\rt.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\sunrsasign.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\jsse.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\jce.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\charsets.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\lib\\jfr.jar;C\:\\Program Files\\Java\\jdk1.8.0_144\\jre\\classes
java.vendor=Oracle Corporation
file.separator=\\
java.vendor.url.bug=http\://bugreport.sun.com/bugreport/
sun.io.unicode.encoding=UnicodeLittle
sun.cpu.endian=little
sun.desktop=windows
sun.cpu.isalist=amd64
...
```

###### jinfo -flag -PrintGC <pid>
表示禁用GC日志的打印,注意是减号
###### jinfo -flag +PrintGC <pid>
表示启用GC日志的打印，注意是加号
###### jinfo -flag PrintGC <pid>
查看是否开启了GC日志的打印，注意没有加减号

详情请参考：https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jinfo.html#BCGEBFDD

---

### Jmap
>	打印JVM堆内对象情况,查询Java堆和永久代的详细信息，使用率，使用大小

###### jmap -finalizerinfo
打印正等候回收的对象的信息。
###### jmap -heap
打印heap的概要信息，GC使用的算法，heap的配置及wise heap的使用情况
######  jmap -clstats
打印classload的信息。包含每个classloader的名字、活泼性、地址、父classloader和加载的class数量
###### jmap   -histo[:live] <pid>
打印堆的直方图

###### jmap -clstats 
打印classload的信息。包含每个classloader的名字、活泼性、地址、父classloader和加载的class数量

###### jmap -dump:format=b,file=d:\a.bin pid
生成堆信息，并存到d:\a.bin这个文件

###### jmap -histo pid | more 
查看类和实例数量和详情：


### Jhat （一般不用，内存占用较高）
jvm heap analysis tool
jvm堆分析工具

###### jhat d:\a.bin
分析堆信息文件，然后默认开7000端口，以网页的形式显示

###### oql 
object query language，对象查询语言


### Jstack
>打印线程堆栈信息

###### jstack <pid>
查看某个Java进程内的线程堆栈信息
###### jstack -l <pid>
在发生死锁时可以用jstack -l pid来观察锁持有情况
```
"Finalizer" #3 daemon prio=8 os_prio=1 tid=0x00000000033b9000 nid=0x268c in Object.wait() [0x000000001b7be000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x0000000780808ec8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143)
        - locked <0x0000000780808ec8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164)
        at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)

   Locked ownable synchronizers:
        - None
```
###### jstack -m <pid>
不仅会输出Java堆栈信息，还会输出C/C++堆栈信息（比如Native方法）


### Jconsole
###### 内存监控
![](https://upload-images.jianshu.io/upload_images/5786888-92d0fb91244cba5c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 线程监控
用下面这段代码来测试：
```
public class ThreadConsole {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.next();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    //空转
                }
            }
        },"while thread").start();
        scanner.next();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object object = new Object();
                synchronized (object){
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
```
![第一次输入前](https://upload-images.jianshu.io/upload_images/5786888-47094cb6023335da.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![第一次输入后&第二次输入前](https://upload-images.jianshu.io/upload_images/5786888-bf44fc72909f365f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![第二次输入后](https://upload-images.jianshu.io/upload_images/5786888-8586f48776d73cbc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 检查死锁
```
/**
 * 模拟死锁并用jconsole进行检测
 *
 * Created by Fant.J.
 */
public class DeadLock {
    public static void main(String[] args) {
        Object o1 = new Object();
        Object o2 = new Object();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (o1){
                    try {
                        System.out.println("获取到o1的锁");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (o2){
                        System.out.println("获取到o2的锁");
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (o2){
                    try {
                        System.out.println("获取到o2的锁");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (o1){
                        System.out.println("获取到o1的锁");
                    }
                }
            }
        }).start();
    }
}

```
![线程1](https://upload-images.jianshu.io/upload_images/5786888-02768d6ae8f84e12.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![线程0](https://upload-images.jianshu.io/upload_images/5786888-ad6e8cc62188092c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![死锁检查](https://upload-images.jianshu.io/upload_images/5786888-c2f651f7d3bd612c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![死锁查看](https://upload-images.jianshu.io/upload_images/5786888-4676b1b41bd70c4b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### visualVm
下载地址：https://visualvm.github.io/download.html
使用一看就懂，不做演示







