![image.png](http://upload-images.jianshu.io/upload_images/5786888-52ff674d6cabb3a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


// 1. 记录一个进入方法的时间，因为会有等待超时的情况


// 2. 从空闲集合中获取一个redis连接对象 ;如果 return


// 3. 判断连接池数量是否满了，; 如果没满的就构建一个新的redis连接，并且放到繁忙集合中 return


// 4. 等待等待其他线程是否连接 ; 如果等到了一个被释放的连接，放到繁忙集合中，就return


// 5. 超过过了maxWait，抛出等待超时的异常
