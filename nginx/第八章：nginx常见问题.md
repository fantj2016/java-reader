###相同server_name多个虚拟主机优先级访问
优先读取第一个conf文件。
###location匹配优先级
`=`进行普通字符精确匹配，也就是完全匹配**优先级最高**
`^~`表示普通字符匹配，使用前缀匹配**优先级最高**
`~ \~*`表示执行一个真个则匹配**优先级最低**
###try_files使用
按顺序检查文件是否存在,存在即访问
```
location / {
      try_files $uri $uri/  index.html;   
      #先访问$uri 如果宕机（404）再访问$uri/ 再访问index.html
}
```
###nginx的alias和root区别
alias    /opt/app/image/;
root    /opt/app/image/;
同：指定文件在哪个位置（路径）
异：root会根据uri路径来查找。alias不会
例子：
我们访问 192.168.0.1/image/dog.jpg
root设置  会在  /opt/app/image/image/dog.jpg目录去找
alias设置  会在 /opt/app/image/dog.jpg 去找
###用什么方法传递用户的真实IP
如果客户使用多级代理来隐藏自己的ip，我们该如何获取他的真实ip呢。
我们应该在第一级代理处做手脚，我们要求一级代理来请求时，带上初始ip请求头信息
###压测工具ab
建议系统学习下ab工具，不然很多factors都看不懂
```
ab -n 2000 -c 2 http://127.0.0.1/index.html     #2000次请求，每次2并发
```
http_load 也可以,有兴趣者自行百度。
###系统与nginx性能优化
