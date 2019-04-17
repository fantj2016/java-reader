速度方面的比较：StringBuilder >  StringBuffer  >  String
>因为给String添加字符时，JVM 会给String 创建新的对象。重新赋值，所以速度很慢。


* String
  * 操作少量的数据用
* StringBuilder：线程非安全的
  *尽量在单线程下使用，字符串缓冲去被多个线程使用时，JVM不能保证StringBuilder操作安全性。 
* StringBuffer：线程安全的
  * 多线程操作字符串缓冲区 下操作大量数据

