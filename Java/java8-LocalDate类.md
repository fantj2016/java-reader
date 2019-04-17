LocalDate类使用ISO日历表示年、月、日。

* LocalDate.now();  获取系统当前日期
* LocalDate.of(int year,int month,int dayOfMonth);
按指定日期创建LocalDate对象
* getYear();  返回日期中的年份
* getMonth();  返回月份
* getDayOfMonth();  返回月份中的日

####1.LocalDate
```
package com.fantJ.JAVA_8;

import java.time.LocalDate;

public class LocalDate_Test {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date.getYear()+" "+date.getMonthValue()+" "+date.getDayOfMonth());
        System.out.println(date.toString());
    }
}

```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-8ef756b788cc22fe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####2.LocalTime
```
public class LocalTime_Test {
    public static void main(String[] args) {
        LocalTime time = LocalTime.now();
        System.out.println(time.getHour()+" "+time.getMinute()+" "+time.getSecond());
        System.out.println(time.toString());
        System.out.println(time.toSecondOfDay());
    }
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-cfaa53942301dc8b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####3.LocalDateTime
```
public class LocalDateTime_Test {
    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime.getYear()+" "+dateTime.getMonthValue()+" "+dateTime.getDayOfMonth()+
                            dateTime.getHour()+" "+dateTime.getMinute()+" "+dateTime.getSecond());
        System.out.println(dateTime.toString());
    }
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-efbe7e1b83b1675c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####4.DateTimeFormatter
用于将字符串解析为日期
```
public class DateTimeFormatter_Test {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2017-12-15:19:15:01",formatter);
        System.out.println(dateTime.toString());
    }
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-cce32fe7a0d45e49.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
####5.ZonedDateTime
```
public class ZonedDateTime_Test {
    public static void main(String[] args) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy:HH:mm:ss");
        String date = zonedDateTime.format(formatter);
        System.out.println(date);
    }
}
```
![image.png](http://upload-images.jianshu.io/upload_images/5786888-7f33df8462a96f27.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
