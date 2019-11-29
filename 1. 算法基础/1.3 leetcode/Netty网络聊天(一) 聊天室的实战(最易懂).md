>之前做过一个IM的项目，里面涉及了基本的聊天功能，所以注意这系列的文章不是练习，不含基础和逐步学习的部分，直接开始实战和思想引导，基础部分需要额外的去补充，我有精力的话可以后续出一系列的文章。

为什么第一篇是聊天室，聊天室是最容易实现的部分。也是IM结构最简单的一部分，其次作单聊和群聊，业务逻辑层层递增，彻底的拿下聊天室的代码，进阶单聊和群聊就很简单了，后续我还会推出直播间的实现。

如果单纯想实现聊天室很简单，但是我尽量会把流程都走全，为了方便理解。

主要由两个功能类实现：初始化类+响应处理类

### 0. 准备工作
添加pom.xml 
```
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.2.Final</version>
        </dependency>
```
### 1. 辅助接口实现
> 辅助接口让服务架构更加清晰，这里有两个接口，一个用来处理Http请求，一个处理Webocket请求。

MyHttpService.java
```
/**
 * 处理 http请求
 */
public interface MyHttpService {
    void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request);
}
```

MyWebSocketService.java
```
/**
 * 处理 WebSocket 请求中的frame
 */
public interface MyWebSocketService {
    void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame);
}
```
那么问题来了，谁来实现这两个类呢，谁来处理这两种请求的分发呢。

下面来看服务响应处理类：WebSocketServerHandler.java


### 2. 请求处理类
>继承SimpleChannelInboundHandler类，实现`channelRead0()`  `handlerAdded()`  `handlerRemoved()`  `exceptionCaught()`等方法，第一个是必选方法，其他方法供我们做一些标记和后续处理。


WebSocketServerHandler.java
```
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private MyHttpService httpService;
    private MyWebSocketService webSocketService;

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public WebSocketServerHandler(MyHttpService httpService, MyWebSocketService webSocketService) {
        super();
        this.httpService = httpService;
        this.webSocketService = webSocketService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            httpService.handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            webSocketService.handleFrame(ctx, (WebSocketFrame) msg);
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        channels.writeAndFlush(new TextWebSocketFrame(ctx.channel() +"上线了"));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        channels.writeAndFlush(new TextWebSocketFrame(ctx.channel() +"下线了"));
    }

    /**
     * 发生异常时处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        channels.remove(ctx.channel());
        ctx.close();
        log.info("异常信息：{}",cause.getMessage());
    }
}
```
1. 创建ChannelGroup,来保存每个已经建立连接的channel，在`handlerAdded()`方法中`channels.add(ctx.channel());`,相应的在`handlerRemoved`方法中`remove`。
2. 在`channelRead0()`方法中，实现了对请求的识别和分别处理。
3. `exceptionCaught()`方法为发生异常时处理。
### 3. 初始化类实现
```
@Slf4j
public class WebSocketServer implements MyHttpService, MyWebSocketService {



    /**
     * 握手用的 变量
     */
    private static final AttributeKey<WebSocketServerHandshaker> ATTR_HAND_SHAKER = AttributeKey.newInstance("ATTR_KEY_CHANNEL_ID");

    private static final int MAX_CONTENT_LENGTH = 65536;

    /**
     * 请求类型常量
     */
    private static final String WEBSOCKET_UPGRADE = "websocket";
    private static final String WEBSOCKET_CONNECTION = "Upgrade";
    private static final String WEBSOCKET_URI_ROOT_PATTERN = "ws://%s:%d";

    /**
     * 用户字段
     */
    private String host;
    private int port;

    /**
     * 保存 所有的连接
     */
    private Map<ChannelId, Channel> channelMap = new HashMap<>();
    private final String WEBSOCKET_URI_ROOT;

    public WebSocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        // 将 ip 和端口 按照格式 赋值给 uri
        WEBSOCKET_URI_ROOT = String.format(WEBSOCKET_URI_ROOT_PATTERN, host, port);
    }

    public void start(){
        // 实例化 nio监听事件池
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 实例化 nio工作线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 启动器
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pl = channel.pipeline();
                // 保存 该channel 到map中
                channelMap.put(channel.id(),channel);
                log.info("new channel {}",channel);
                channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                    log.info("channel close future  {}",channelFuture);
                    //关闭后 从map中移除
                    channelMap.remove(channelFuture.channel().id());
                });
                //添加 http 编解码
                pl.addLast(new HttpServerCodec());
                // 聚合器
                pl.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                // 支持大数据流
                pl.addLast(new ChunkedWriteHandler());
                // 设置 websocket 服务处理方式
                pl.addLast(new WebSocketServerHandler(WebSocketServer.this, WebSocketServer.this));
            }
        });
        /**
         * 实例化完毕后，需要完成端口绑定
         */
        try {
            ChannelFuture channelFuture = bootstrap.bind(host,port).addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture1.isSuccess()){
                    log.info("webSocket started");
                }
            }).sync();
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) channelFuture12 ->
                    log.info("server channel {} closed.", channelFuture12.channel())).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("绑定端口失败");
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        log.info("webSocket shutdown");
    }

    @Override
    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        //判断是不是 socket 请求
        if (isWebSocketUpgrade(request)){
            //如果是webSocket请求
            log.info("请求是webSocket协议");
            // 获取子协议
            String subProtocols = request.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
            //握手工厂 设置 uri+协议+不允许扩展
            WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(WEBSOCKET_URI_ROOT,subProtocols,false);
            // 从工厂中实例化一个 握手请求
            WebSocketServerHandshaker handshaker = handshakerFactory.newHandshaker(request);
            if (handshaker == null){
                //握手失败：不支持的协议
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }else {
                //响应请求:将 握手转交给 channel处理
                handshaker.handshake(ctx.channel(),request);
                //将 channel 与 handshaker 绑定
                ctx.channel().attr(ATTR_HAND_SHAKER).set(handshaker);
            }
            return;
        }else {
            // 不处理 HTTP 请求
            log.info("不处理 HTTP 请求");
        }
    }

    @Override
    public void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        /**
         * text frame handler
         */
        if (frame instanceof TextWebSocketFrame){
            String text = ((TextWebSocketFrame) frame).text();
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(text);
            log.info("receive textWebSocketFrame from channel: {} , 目前一共有{}个在线",ctx.channel(),channelMap.size());
            //发给其它的 channel  (群聊功能)
            for (Channel ch: channelMap.values()){
                if (ch.equals(ctx.channel())){
                    continue;
                }
                //将 text frame 写出
                ch.writeAndFlush(textWebSocketFrame);
                log.info("消息已发送给{}",ch);
                log.info("write text: {} to channel: {}",textWebSocketFrame,ctx.channel());
            }
            return;
        }
        /**
         * ping frame , 回复  pong frame
         */
        if (frame instanceof PingWebSocketFrame){
            log.info("receive pingWebSocket from channel: {}",ctx.channel());
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        /**
         * pong frame, do nothing
         */
        if (frame instanceof PongWebSocketFrame){
            log.info("receive pongWebSocket from channel: {}",ctx.channel());
            return;
        }
        /**
         * close frame, close
         */
        if (frame instanceof CloseWebSocketFrame){
            log.info("receive closeWebSocketFrame from channel: {}", ctx.channel());
            //获取到握手信息
            WebSocketServerHandshaker handshaker = ctx.channel().attr(ATTR_HAND_SHAKER).get();
            if (handshaker == null){
                log.error("channel: {} has no handShaker", ctx.channel());
                return;
            }
            handshaker.close(ctx.channel(),((CloseWebSocketFrame) frame).retain());
            return;
        }
        /**
         * 剩下的都是 二进制 frame ，忽略
         */
        log.warn("receive binary frame , ignore to handle");
    }

    /**
     * 判断是否是 webSocket 请求
     */
    private boolean isWebSocketUpgrade(FullHttpRequest req) {
        HttpHeaders headers = req.headers();
        return req.method().equals(HttpMethod.GET)
                && headers.get(HttpHeaderNames.UPGRADE).contains(WEBSOCKET_UPGRADE)
                && headers.get(HttpHeaderNames.CONNECTION).contains(WEBSOCKET_CONNECTION);
    }
}
```
1. `l.addLast(new WebSocketServerHandler(WebSocketServer.this, WebSocketServer.this));` 添加自己的响应处理。`WebSocketServerHandler`是第二点实现的请求处理类.
2. `private Map<ChannelId, Channel> channelMap = new HashMap<>();`来将ChannelId和CHannel对应保存。方便后来对应获取。
3. `bootstrap.bind(host,port)`也可以替换成仅bind端口。
```
    public ChannelFuture bind(String inetHost, int inetPort) {
        return bind(new InetSocketAddress(inetHost, inetPort));
    }
```
```
    public synchronized InetAddress anyLocalAddress() {
        if (anyLocalAddress == null) {
            anyLocalAddress = new Inet4Address(); // {0x00,0x00,0x00,0x00}
            anyLocalAddress.holder().hostName = "0.0.0.0";
        }
        return anyLocalAddress;
    }
```
它默认会给`0.0.0.0`端口开放服务。
4. `handleHttpRequest`和`handleFrame`是`MyWebSocketService`类的一个实现。
5. 各个细节都有注释，仔细看注释。

### 4. 启动服务
```
public class Main {
    public static void main(String[] args) {
        new WebSocketServer("192.168.1.33",9999).start();
    }
}
```
##### 局域网内如何测试?

我用的是npm 的一个serve 服务来搞局域网。
官网介绍：https://www.npmjs.com/package/serve
我的文章：[React打包注意事项及静态文件服务搭建](https://www.jianshu.com/p/c14c427a6b3b)

>![](https://upload-images.jianshu.io/upload_images/5786888-5ad3c4b9ae94c97e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这下保证你的手机和电脑都在局域网内，就可以访问你自己的群聊了。

### 5. 前端页面
>要送就送一套。
```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <style type="text/css">
        .talk_con{
            width:600px;
            height:500px;
            border:1px solid #666;
            margin:50px auto 0;
            background:#f9f9f9;
        }
        .talk_show{
            width:580px;
            height:420px;
            border:1px solid #666;
            background:#fff;
            margin:10px auto 0;
            overflow:auto;
        }
        .talk_input{
            width:580px;
            margin:10px auto 0;
        }
        .whotalk{
            width:80px;
            height:30px;
            float:left;
            outline:none;
        }
        .talk_word{
            width:420px;
            height:26px;
            padding:0px;
            float:left;
            margin-left:10px;
            outline:none;
            text-indent:10px;
        }
        .talk_sub{
            width:56px;
            height:30px;
            float:left;
            margin-left:10px;
        }
        .atalk{
            margin:10px;
        }
        .atalk span{
            display:inline-block;
            background:#0181cc;
            border-radius:10px;
            color:#fff;
            padding:5px 10px;
        }
        .btalk{
            margin:10px;
            text-align:right;
        }
        .btalk span{
            display:inline-block;
            background:#ef8201;
            border-radius:10px;
            color:#fff;
            padding:5px 10px;
        }
    </style>
    <script type="text/javascript">
        //
        document.onkeydown = function (ev) {
            if (ev && ev.keyCode == 13){
                send();
                clear();
            }
        }
        var socket;
        if (window.WebSocket) {
            socket = new WebSocket("ws://192.168.1.33:9999");
            // socket = new WebSocket("ws://127.0.0.1:9999");
            // socket = new WebSocket("ws://192.168.43.186:9999");
            socket.onmessage = function (ev) {
                atalkAppendIn("接收:"+socket.channel + ":" + ev.data)
            };
            socket.onopen = function () {
                btalkAppendIn("连接已建立");
            }
            socket.onclose = function () {
                btalkAppendIn("连接关闭");
            };
        }else {
            alert("浏览器不支持");
        }
        function send(){
            var message = document.getElementById("talkwords");
            if (!window.WebSocket){
                return
            }
            if (socket.readyState === WebSocket.OPEN){
                socket.send(message.value);
                btalkAppendIn("发送:"+ message.value);
                clear();
            } else {
                alert("WebSocket 建立失败");
            }
        }

        function atalkAppendIn(text) {
            var append = document.getElementById("words");
            append.innerHTML+= '<div class="atalk"><span>'+ text +'</span></div>';
        }

        function btalkAppendIn(text) {
            var append = document.getElementById("words");
            append.innerHTML+= '<div class="btalk"><span>'+ text +'</span></div>';
        }
        function clear () {
            var elementById = document.getElementById("talkwords");
            elementById.value = "";
        }

    </script>
</head>
<body>
<div class="talk_con">
    <div class="talk_show" id="words">
    </div>
    <div class="talk_input">
        <!--<select class="whotalk" id="who">-->
            <!--<option value="0">A说：</option>-->
            <!--<option value="1">B说：</option>-->
        <!--</select>-->
        <input type="text" class="talk_word" id="talkwords">
        <input type="button" onclick="send()" value="发送" class="talk_sub" id="talksub">
    </div>
</div>
</body>
</html>
```
1. `socket = new WebSocket("ws://192.168.1.33:9999");`注意这里ip和port与服务一一对应。
2. `socket.onmessage()`是获取socket信息。`socket.onopen`是创建连接。`socket.onclose`是关闭连接。`socket.send(message.value);`是发送socket信息。

>![页面1](https://upload-images.jianshu.io/upload_images/5786888-fb83081c9c653bbe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>![页面2](https://upload-images.jianshu.io/upload_images/5786888-523fed57c9c9be45.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

控制台输出：
```
15:12:42.443 [nioEventLoopGroup-3-6] INFO com.fantj.springbootjpa.netty.WebSocketServer - receive textWebSocketFrame from channel: [id: 0x0d08c657, L:/192.168.1.33:9999 - R:/192.168.1.33:50440] , 目前一共有2个在线
15:12:42.443 [nioEventLoopGroup-3-6] INFO com.fantj.springbootjpa.netty.WebSocketServer - 消息已发送给[id: 0xacd5c1ad, L:/192.168.1.33:9999 - R:/192.168.1.33:50438]
15:12:42.444 [nioEventLoopGroup-3-5] DEBUG io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder - Encoding WebSocket Frame opCode=1 length=5
15:12:42.443 [nioEventLoopGroup-3-6] INFO com.fantj.springbootjpa.netty.WebSocketServer - write text: TextWebSocketFrame(data: UnpooledUnsafeHeapByteBuf(ridx: 0, widx: 5, cap: 15)) to channel: [id: 0x0d08c657, L:/192.168.1.33:9999 - R:/192.168.1.33:50440]
```