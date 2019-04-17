有啥不懂的或者出错的可以在下面留言。


###1. 文件上传

```
    //上传路径
    String folder = "C:\\code\\springboot-springsecurity\\security-demo\\src\\main\\java\\com\\laojiao\\xxx\\controller";

    @PostMapping
    public String upload(MultipartFile file) throws IOException {

        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        //创建本地文件
        File localFile = new File(folder,System.currentTimeMillis()+".txt");
        //把传上来的文件写到本地文件
        file.transferTo(localFile);
        //返回localFile文件路径
        return localFile.getAbsolutePath();
    }
```
返回是String就是文件所在路径，一般把它放到数据库中。
我在这里用的是单机上传，如果有需要上传到ftp或者fastdfs等文件服务器，请看下面核心代码。
```
            //利用 MultipartFile 的 transferTo 方法 上传文件 至 path
            file.transferTo(targetFile);
            //上传到 FTP 服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //删除 path 路径下的 文件
            targetFile.delete();
```
最后返回一个文件名。需要下载或者获取文件的时候，给该文件名加上前缀即可。
###2. 文件下载

```
    @GetMapping("/{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response){
        try(InputStream inputStream = new FileInputStream(new File(folder,id+".txt"));
            OutputStream outputStream = response.getOutputStream();) {

            //设置内容类型为下载类型
            response.setContentType("application/x-download");
            //设置请求头 和 文件下载名称
            response.addHeader("Content-Disposition","attachment;filename=test.txt");
            //用 common-io 工具 将输入流拷贝到输出流
            IOUtils.copy(inputStream,outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
```

注意IOUtils 是apache的一个工具包，是需要导入`common-io`依赖包的。或者你手动获取输入输出流转换也可以。

####介绍下我的所有文集：
###### 流行框架
[SpringCloud](https://www.jianshu.com/nb/18726057)
[springboot](https://www.jianshu.com/nb/19053594)
[nginx](https://www.jianshu.com/nb/18436827)
[redis](https://www.jianshu.com/nb/21461220)

######底层实现原理：
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java reflection 反射详解](https://www.jianshu.com/nb/21989596)
[Java并发学习笔录](https://www.jianshu.com/nb/22549959)
[Java Servlet教程](https://www.jianshu.com/nb/22065472)
[jdbc组件详解](https://www.jianshu.com/nb/22774157)
[Java NIO教程](https://www.jianshu.com/nb/21635138)
[Java语言/版本 研究](https://www.jianshu.com/nb/19137666)
