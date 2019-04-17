>所有异常的根基类。
Exception 是Throwable类的一个主要子类。

**Error**类和**Exception**类的父类都是throwable类，他们的区别是：
         -- Error类一般是指与*虚拟机*相关的问题，如*系统崩溃*，*虚拟机错误*，*内存空间不足*，*方法调用栈溢*等。对于这类错误的导致的**应用程序中断**，仅靠程序**本身无法恢复和和预防**，遇到这样的错误，建议让程序终止。

-- Exception类表示程序**可以处理的异常，可以捕获且可能恢复**。遇到这类异常，应该尽可能处理异常，使程序恢复运行，而不应该随意终止异常。

Exception类又分为运行时异常（Runtime Exception）和受检查的异常(Checked Exception )，运行时异常;ArithmaticException,IllegalArgumentException，编译能通过，但是一运行就终止了，程序不会处理运行时异常，出现这类异常，程序会终止。而受检查的异常，要么用try。。。catch捕获，要么用throws字句声明抛出，交给它的父类处理，否则编译不会通过。
