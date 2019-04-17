Java的3种Base64加密方法 , 分别是 jdk默认实现的加密方式， 使用cc的加密方式和使用bc的加密方式
```
import java.io.IOException;  
  
import org.apache.commons.codec.binary.Base64;  
  
import sun.misc.BASE64Decoder;  
import sun.misc.BASE64Encoder;  
  
  
  
public class Main {  
  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
  
          
        /*使用jdk默认的base64加解密*/  
        String str ="这是要加密的字符串,使用jdk";  
        str = jdkBase64Encoder(str);  
        System.out.println("加密后的字符串为："+str);  
        str = jdkBase64Decoder(str);  
        if(str!=null)  
        {  
            System.out.println("解密后的字符串："+str);  
        }  
        else  
        {  
            System.out.println("解密失败");  
        }  
        /*使用commons-codec的base64加解密*/  
        str ="这是要加密的字符串，使用CC";  
        str = CCBase64Encoder(str);  
        System.out.println("加密后的字符串为："+str);  
        str=CCBase64Decoder(str);  
        System.out.println("解密后的字符串为："+str);  
        /*使用bcprov的base64加解密*/  
        str = "这是要加密的字符串，使用bc";  
        str = BCBase64Endoer(str);  
        System.out.println("加密后的字符串为："+str);  
        str = BCBase64Decoder(str);  
        System.out.println(str);  
          
    }  
      
      
    /** 
     * 使用jdk的base64 加密字符串 
     * */  
    public static String jdkBase64Encoder(String str)  
    {  
        BASE64Encoder encoder = new BASE64Encoder();  
        String encode = encoder.encode(str.getBytes());  
        return encode;  
    }  
    /** 
     * 使用jdk的base64 解密字符串 
     * 返回为null表示解密失败 
     * */  
    public static String jdkBase64Decoder(String str)  
    {  
        BASE64Decoder decoder = new BASE64Decoder();  
        String decode=null;  
        try {  
            decode = new String( decoder.decodeBuffer(str));  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return decode;  
    }  
      
    /** 
     * 使用commons-codec的base64 加密字符串 
     * */  
    public static String CCBase64Encoder(String str)  
    {  
           
        return new String(Base64.encodeBase64(str.getBytes()));  
    }  
      
    /** 
     * 使用commons-codec的base64 解密字符串 
     * */  
    public static String CCBase64Decoder(String str)  
    {  
        return new String(Base64.decodeBase64(str.getBytes()));  
          
    }  
      
    /** 
     * 使用bcprov的base64加密字符串 
     * */  
    public static String BCBase64Endoer(String str)  
    {  
        byte[] arr =org.bouncycastle.util.encoders.Base64.encode(str.getBytes());  
          
        return new String(arr);  
    }  
      
    /** 
     * 使用bcprov的base64加密字符串 
     * */  
    public static String BCBase64Decoder(String str)  
    {  
        byte[] arr = org.bouncycastle.util.encoders.Base64.decode(str.getBytes());  
          
        return new String(arr);  
    }  
}  
```
