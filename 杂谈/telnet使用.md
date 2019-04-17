>Telnet是常用的远程登录手段，有两种操作模式：:Telnet命令模式和Telnet会话模式。连接到 Telnet服务器后，Telnet客户端会自动进入Telnet会话模式，此模式最常见。在会话模式下，所有击键将通过网络发送到 Telnet服务器，并可在 Telnet服务器上由在该处运行的任何程序进行处理。Telnet命令模式允许在本地将命令发送到 Telnet客户端服务本身，例如打开到远程主机的连接、关闭到远程主机的连接、显示操作参数、设置终端选项、打印状态信息和退出程序。

步骤1：用win + r 打开cmd

步骤2：在cmd中执行telnet 127.0.0.1 80， 然后可以看到一个黑色的框框

步骤3： 然后按 ctrl + ], 退出， 结果为：
```

欢迎使用 Microsoft Telnet Client

Escape 字符是 'CTRL+]'

Microsoft Telnet>
```
步骤4： 然后用send命令
`send hello world`
