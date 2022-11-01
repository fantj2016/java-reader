本问翻译自：http://tutorials.jenkov.com/java-servlets/index.html

GZip Servlet过滤器可用于GZip压缩内容从Java Web应用程序发送到浏览器。

### 为什么要压缩

Gzip压缩HTML、js、css等，使得发送给浏览器的数据大小变得更小。提升上传速度，尤其是移动端带宽受限制的情况下，不过它可能带来服务器和浏览器的CPU消耗问题，但是响应速度会得道很大的改善。

### GZip 请求头

浏览器在发送到HTTP服务器（例如Java Web服务器）的请求中包含Accept-Encoding HTTP标头。 Accept-Encoding标头的内容告诉浏览器可以接受哪些内容编码。 如果该标题包含gzip值，则浏览器可以接受GZip压缩内容。 然后服务器可以将GZip压缩发送回浏览器的内容。

如果从服务器发回的内容是GZip压缩的，则服务器会在HTTP响应中包含带有值gzip的Content-Encoding HTTP标头。 这样浏览器就知道内容是GZip压缩的。

### 为什么使用GZip Servlet过滤器？

如果对每一个Servlet请求都设置压缩，那肯定在性能上会有差距，所以Gzip Servlet过滤器 可以让我们对需要压缩的东西进行压缩，没必要压缩的就不去压缩。使性能最大化的提升。

### GZip Servlet滤波器设计
![image.png](http://upload-images.jianshu.io/upload_images/5786888-93e32f829b7f4b77.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如图所示：首先需要一个Servlet过滤器类。 该类映射到web.xml文件中的一组URL。
当一个HTTP请求到达映射到过滤器的Servlet容器时，过滤器会在该请求被Servlet，JSP等处理之前截取请求所针对的目标。 GZip servlet过滤器检查客户端（浏览器）是否可以接受GZip压缩内容。如果可以接受，就对目标做压缩处理，然后将HttpServletResponse对象封装在GZipServletResponseWrapper进行压缩，最后将压缩内容写入HttpServletResponse。

### 实例
The code consists of 3 classes. A GZipServletFilter, a GZipServletResponseWrapper and a GZipServletOutputStream.

The GZipServletOutputStream is what compresses the content written to it. It does so by using a GZIPOutputStream internally, which is a standard Java class.


主要用到三个类：GzipServletFilter、GzipServletResponseWrapper、GzipServletOutputStream。
* GZipServletFilter 用来拦截请求，检查浏览器是否接受压缩。它将在HttpServletResponse传递给过滤器链之前将信息包装在GZipServletResponseWrapper中。
* GZipServletResponseWrapper 用来返回一个输出流给Servlet或者Jsp，可以是GZipServletOutputStream 或者PrintWriter 类型的数据。
* GZipServletOutputStream 是用来压缩并写入的内容的，通过内部使用GZIPOutputStream来实现。

示例：
```
public class GZipServletFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  public void doFilter(ServletRequest request, 
                       ServletResponse response,
                       FilterChain chain) 
  throws IOException, ServletException {

    HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if ( acceptsGZipEncoding(httpRequest) ) {
      httpResponse.addHeader("Content-Encoding", "gzip");
      GZipServletResponseWrapper gzipResponse =
        new GZipServletResponseWrapper(httpResponse);
      
      chain.doFilter(request, gzipResponse);
      gzipResponse.close();
    } else {
      chain.doFilter(request, response);
    }
  }

//判断 请求对象 是否接受压缩
  private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
      String acceptEncoding = 
        httpRequest.getHeader("Accept-Encoding");

      return acceptEncoding != null && 
             acceptEncoding.indexOf("gzip") != -1;
  }
}
```

```
class GZipServletResponseWrapper extends HttpServletResponseWrapper {

  private GZipServletOutputStream gzipOutputStream = null;
  private PrintWriter             printWriter      = null;

  public GZipServletResponseWrapper(HttpServletResponse response)
          throws IOException {
      super(response);
  }

  public void close() throws IOException {

      //PrintWriter.close does not throw exceptions.
      //Hence no try-catch block.
      if (this.printWriter != null) {
          this.printWriter.close();
      }

      if (this.gzipOutputStream != null) {
          this.gzipOutputStream.close();
      }
  }


  /**
   * Flush OutputStream or PrintWriter
   *
   * @throws IOException
   */

  @Override
  public void flushBuffer() throws IOException {

    //PrintWriter.flush() does not throw exception
    if(this.printWriter != null) {
      this.printWriter.flush();
    }

    IOException exception1 = null;
    try{
      if(this.gzipOutputStream != null) {
        this.gzipOutputStream.flush();
      }
    } catch(IOException e) {
        exception1 = e;
    }

    IOException exception2 = null;
    try {
      super.flushBuffer();
    } catch(IOException e){
      exception2 = e;
    }

    if(exception1 != null) throw exception1;
    if(exception2 != null) throw exception2;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (this.printWriter != null) {
      throw new IllegalStateException(
        "PrintWriter obtained already - cannot get OutputStream");
    }
    if (this.gzipOutputStream == null) {
      this.gzipOutputStream = new GZipServletOutputStream(
        getResponse().getOutputStream());
    }
    return this.gzipOutputStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
     if (this.printWriter == null && this.gzipOutputStream != null) {
       throw new IllegalStateException(
         "OutputStream obtained already - cannot get PrintWriter");
     }
     if (this.printWriter == null) {
       this.gzipOutputStream = new GZipServletOutputStream(
         getResponse().getOutputStream());
       this.printWriter      = new PrintWriter(new OutputStreamWriter(
       this.gzipOutputStream, getResponse().getCharacterEncoding()));
     }
     return this.printWriter;
  }


  @Override
  public void setContentLength(int len) {
    //ignore, since content length of zipped content
    //does not match content length of unzipped content.
  }
}
```

```
class GZipServletOutputStream extends ServletOutputStream {
  private GZIPOutputStream    gzipOutputStream = null;

  public GZipServletOutputStream(OutputStream output)
        throws IOException {
    super();
    this.gzipOutputStream = new GZIPOutputStream(output);
  }

  @Override
  public void close() throws IOException {
    this.gzipOutputStream.close();
  }

  @Override
  public void flush() throws IOException {
    this.gzipOutputStream.flush();
  }

  @Override
  public void write(byte b[]) throws IOException {
    this.gzipOutputStream.write(b);
  }

  @Override
  public void write(byte b[], int off, int len) throws IOException {
    this.gzipOutputStream.write(b, off, len);
  }

  @Override
  public void write(int b) throws IOException {
     this.gzipOutputStream.write(b);
  }
}
```
### web.xml 配置

为了激活Gzip Servlet Filter，我们需要配置一些东西。
```
<filter>
  <filter-name>GzipFilter</filter-name>
  <filter-class>com.xxx.GZipServletFilter</filter-class>  #这里填写自己的GzipServletFilter类路径。
</filter>

<filter-mapping>
  <filter-name>GzipFilter</filter-name>
  <url-pattern>*.js</url-pattern>
</filter-mapping>
<filter-mapping>
  <filter-name>GzipFilter</filter-name>
  <url-pattern>*.css</url-pattern>
</filter-mapping>
<filter-mapping>
  <filter-name>GzipFilter</filter-name>
  <url-pattern>*.html</url-pattern>
</filter-mapping>
<filter-mapping>
  <filter-name>GzipFilter</filter-name>
  <url-pattern>*.jsp</url-pattern>
</filter-mapping>
<filter-mapping>
  <filter-name>GzipFilter</filter-name>
  <url-pattern>/</url-pattern>
</filter-mapping>
```













