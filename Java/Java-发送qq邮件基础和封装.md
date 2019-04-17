>前文摘自 菜鸟教程 ：http://www.runoob.com/java/java-sending-email.html

使用Java应用程序发送 E-mail 十分简单，但是首先你应该在你的机器上安装 JavaMail API 和Java Activation Framework (JAF) 。

*   您可以从 Java 网站下载最新版本的 [JavaMail](http://www.oracle.com/technetwork/java/javamail/index.html)，打开网页右侧有个 **Downloads** 链接，点击它下载。
*   您可以从 Java 网站下载最新版本的 [JAF（版本 1.1.1）](http://www.oracle.com/technetwork/articles/java/index-135046.html)。

你也可以使用本站提供的下载链接：

*   [JavaMail mail.jar 1.4.5](http://static.runoob.com/download/mail.jar)

*   [JAF（版本 1.1.1） activation.jar](http://static.runoob.com/download/activation.jar)

下载并解压缩这些文件，在新创建的顶层目录中，您会发现这两个应用程序的一些 jar 文件。您需要把 **mail.jar** 和 **activation.jar**文件添加到您的 CLASSPATH 中。

如果你使用第三方邮件服务器如QQ的SMTP服务器，可查看文章底部用户认证完整的实例。

####发送一封简单的 E-mail
下面是一个发送简单E-mail的例子。假设你的localhost已经连接到网络。

如果需要提供用户名和密码给e-mail服务器来达到用户认证的目的，你可以通过如下设置来完成：
```
props.put("mail.smtp.auth", "true");
props.setProperty("mail.user", "myuser");
props.setProperty("mail.password", "mypwd");
```

####需要用户名密码验证邮件发送实例:
你需要在登录QQ邮箱后台在"设置"=》账号中开启POP3/SMTP服务 ，如下图所示：
![](https://upload-images.jianshu.io/upload_images/5786888-3cb816c22419a498.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
我这里已经开启了。需要生成授权码，仔细看说明就行。生成授权码后会给你一串字符，它是密码
SendEmail2.java
```
// 需要用户名密码邮件发送实例
//文件名 SendEmail2.java
//本实例以QQ邮箱为例，你需要在qq后台设置
 
import java.util.Properties;
 
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class SendEmail2
{
   public static void main(String [] args)
   {
      // 收件人电子邮箱
      String to = "xxx@qq.com";
 
      // 发件人电子邮箱
      String from = "xxx@qq.com";
 
      // 指定发送邮件的主机为 smtp.qq.com
      String host = "smtp.qq.com";  //QQ 邮件服务器
 
      // 获取系统属性
      Properties properties = System.getProperties();
 
      // 设置邮件服务器
      properties.setProperty("mail.smtp.host", host);
 
      properties.put("mail.smtp.auth", "true");
      // 获取默认session对象
      Session session = Session.getDefaultInstance(properties,new Authenticator(){
        public PasswordAuthentication getPasswordAuthentication()
        {
         return new PasswordAuthentication("xxx@qq.com", "qq邮箱密码"); //发件人邮件用户名、密码
        }
       });
 
      try{
         // 创建默认的 MimeMessage 对象
         MimeMessage message = new MimeMessage(session);
 
         // Set From: 头部头字段
         message.setFrom(new InternetAddress(from));
 
         // Set To: 头部头字段
         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));
 
         // Set Subject: 头部头字段
         message.setSubject("This is the Subject Line!");
 
         // 设置消息体
         message.setText("This is actual message");
 
         // 发送消息
         Transport.send(message);
         System.out.println("Sent message successfully....from runoob.com");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}
```

###企业级开发封装


##### 实体类
MailEntity .java
```
package com.fantj.myEmail;

/**
 *  邮件实体类
 * Created by Fant.J.
 */
@Data
public class MailEntity implements Serializable {
    //此处填写SMTP服务器
    private String smtpService;
    //设置端口号
    private String smtpPort;
    //设置发送邮箱
    private String fromMailAddress;
    // 设置发送邮箱的STMP口令
    private String fromMailStmpPwd;
    //设置邮件标题
    private String title;
    //设置邮件内容
    private String content;
    //内容格式（默认采用html）
    private String contentType;
    //接受邮件地址集合
    private List<String> list = new ArrayList<>();
}

```
##### enum 类
MailContentTypeEnum .java
```
package com.fantj.myEmail.emailEnum;

/**
 * 自定义的枚举类型，枚举类型包含了邮件内容的类型
 * Created by Fant.J.
 */
public enum MailContentTypeEnum {
    HTML("text/html;charset=UTF-8"), //html格式
    TEXT("text")
    ;
    private String value;

    MailContentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
package com.fantj.myEmail.emailEnum;

/**
 * 自定义的枚举类型，枚举类型包含了邮件内容的类型
 * Created by Fant.J.
 */
public enum MailContentTypeEnum {
    HTML("text/html;charset=UTF-8"), //html格式
    TEXT("text")
    ;
    private String value;

    MailContentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

```
#####邮件发送类
MailSender .java
```
package com.fantj.myEmail;

/**
 * 邮件发送类
 * Created by Fant.J.
 */
public class MailSender {
    //邮件实体
    private static MailEntity mail = new MailEntity();

    /**
     * 设置邮件标题
     * @param title 标题信息
     * @return
     */
    public MailSender title(String title){
        mail.setTitle(title);
        return this;
    }

    /**
     * 设置邮件内容
     * @param content
     * @return
     */
    public MailSender content(String content)
    {
        mail.setContent(content);
        return this;
    }

    /**
     * 设置邮件格式
     * @param typeEnum
     * @return
     */
    public MailSender contentType(MailContentTypeEnum typeEnum)
    {
        mail.setContentType(typeEnum.getValue());
        return this;
    }

    /**
     * 设置请求目标邮件地址
     * @param targets
     * @return
     */
    public MailSender targets(List<String> targets)
    {
        mail.setList(targets);
        return this;
    }

    /**
     * 执行发送邮件
     * @throws Exception 如果发送失败会抛出异常信息
     */
    public void send() throws Exception
    {
        //默认使用html内容发送
        if(mail.getContentType() == null) {
            mail.setContentType(MailContentTypeEnum.HTML.getValue());
        }
        if(mail.getTitle() == null || mail.getTitle().trim().length() == 0)
        {
            throw new Exception("邮件标题没有设置.调用title方法设置");
        }

        if(mail.getContent() == null || mail.getContent().trim().length() == 0)
        {
            throw new Exception("邮件内容没有设置.调用content方法设置");
        }

        if(mail.getList().size() == 0)
        {
            throw new Exception("没有接受者邮箱地址.调用targets方法设置");
        }

        //读取/resource/mail_zh_CN.properties文件内容
        final PropertiesUtil properties = new PropertiesUtil("mail");
        // 创建Properties 类用于记录邮箱的一些属性
        final Properties props = new Properties();
        // 表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        //此处填写SMTP服务器
        props.put("mail.smtp.host", properties.getValue("mail.smtp.service"));
        //设置端口号，QQ邮箱给出了两个端口465/587
        props.put("mail.smtp.port", properties.getValue("mail.smtp.prot"));
        // 设置发送邮箱
        props.put("mail.user", properties.getValue("mail.from.address"));
        // 设置发送邮箱的16位STMP口令
        props.put("mail.password", properties.getValue("mail.from.smtp.pwd"));

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        String nickName = MimeUtility.encodeText(properties.getValue("mail.from.nickname"));
        InternetAddress form = new InternetAddress(nickName + " <" + props.getProperty("mail.user") + ">");
        message.setFrom(form);

        // 设置邮件标题
        message.setSubject(mail.getTitle());
        //html发送邮件
        if(mail.getContentType().equals(MailContentTypeEnum.HTML.getValue())) {
            // 设置邮件的内容体
            message.setContent(mail.getContent(), mail.getContentType());
        }
        //文本发送邮件
        else if(mail.getContentType().equals(MailContentTypeEnum.TEXT.getValue())){
            message.setText(mail.getContent());
        }
        //发送邮箱地址
        List<String> targets = mail.getList();
        for(int i = 0;i < targets.size();i++){
            try {
                // 设置收件人的邮箱
                InternetAddress to = new InternetAddress(targets.get(i));
                message.setRecipient(Message.RecipientType.TO, to);
                // 最后当然就是发送邮件啦
                Transport.send(message);
            }catch (Exception e)
            {
                continue;
            }

        }
    }
}
```
####配置文件的读取工具类
PropertiesUtil .java
```
package com.fantj.myEmail;

/**
 * PropertiesUtil是用于读取*.properties配置文件的工具类
 * Created by Fant.J.
 */
public class PropertiesUtil {
    private final ResourceBundle resource;
    private final String fileName;

    /**
     * 构造函数实例化部分对象，获取文件资源对象
     *
     * @param fileName
     */
    public PropertiesUtil(String fileName)
    {
        this.fileName = fileName;
        this.resource = ResourceBundle.getBundle(this.fileName, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * 根据传入的key获取对象的值 getValue
     *
     * @param key properties文件对应的key
     * @return String 解析后的对应key的值
     */
    public String getValue(String key)
    {
        String message = this.resource.getString(key);
        return message;
    }

    /**
     * 获取properties文件内的所有key值<br>
     * @return
     */
    public Enumeration<String> getKeys(){
        return resource.getKeys();
    }
}

```

####配置文件
mail.properties
```
mail.smtp.service=smtp.qq.com
mail.smtp.prot=587
mail.from.address=844072586@qq.com
mail.from.smtp.pwd=这里填写自己的授权码
mail.from.nickname=这里填写  将名字转换成ascii码放在这里
```

####测试类
MailTest .java
```
package com.fantj.myEmail;


/**
 * Created by Fant.J.
 */
public class MailTest {
    @Test
    public void test() throws Exception {
        for (int i = 0;i<20;i++){
            new MailSender()
                    .title("焦哥给你发送的邮件")
                    .content("你就是傻")
                    .contentType(MailContentTypeEnum.TEXT)
                    .targets(new ArrayList<String>(){{
                        add("xxxxx@qq.com");
                    }})
                    .send();
            Thread.sleep(1000);
            System.out.println("第"+i+"次发送成功!");
        }
    }
}

```
ok了，自己动手试试吧。
