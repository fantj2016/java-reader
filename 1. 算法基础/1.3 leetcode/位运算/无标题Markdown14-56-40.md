在Excel2003中，用A表示第一列，用B表示第二列，用C表示第三列....用z表示第26列，AA表示第27列，用AB表示第28列.....依次类推，请写一个函数输入字母表 示列号，输出是第几列。（十进制转换为26进制的问题）


思路：写一个函数返回值为整型，如果输入的字符串s为空则返回空，定义并初始化一个最终的返回值sum=0，设置一个行动下标i，当i满足小s.length()时，定义一个临时的整型变量temp=s[i]-'A'用来存放单个位置上的由字符变成的数字。只有当temp是在0~26的范围之间的时候才有sum=sum*26+temp+1.

public class StringToInt {
      public int stringToInt(String str){
          int len=str.length();
          if(len<0)
              throw new RuntimeException("inVaild input!");
          int sum=0;//输入为空的时候，输出0；
          for(int i=0;i<len;i++){
              int temp=str.charAt(i)-'A';
              if(temp<26&&temp>=0)
                  sum=26*sum+temp+1;
          }
          return sum;
      }
      public static void main(String[] args){
          String str="AAZ";
          StringToInt sti=new StringToInt();
          int sum=sti.stringToInt(str);
          System.out.println(sum+" ");
      }
}


---
