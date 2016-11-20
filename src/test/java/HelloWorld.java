
import com.bay1ts.bay.Config;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import static com.bay1ts.bay.core.Bay.*;

public class HelloWorld {
    public static void main(String[] args) {
        //支持静态文件
        staticResources("/static");
        webSocket("/path", (context -> {
            System.out.println("收到websocket消息");
            context.getChannelHandlerContext().writeAndFlush(context.getTextWebSocketFrame().retain());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.getChannelHandlerContext().writeAndFlush(new TextWebSocketFrame("不知道该做成什么样子,,,,"));
        }));
        Config.builder().port(4566).enableHttps("F:\\Dev\\IDEA_Projects\\NoServletWebFrameWork\\src\\main\\resources\\nginx2.key","F:\\Dev\\IDEA_Projects\\NoServletWebFrameWork\\src\\main\\resources\\nginx2.crt");
        //支持RESTful路由,可使用java8 lambda表达式简化编码
        get("/hello", (req, resp) -> {
                    System.out.println("呵了个呵");
                    return "World";
                }
        );


        //支持基于命名空间的路由
        NSRoute(
                newNameSpace("/start",

                        //支持controller
                        NSGet("/a", Controller.serveA),

                        NSPost("/b", (req, resp) -> {

                                    //支持 类似SpringMVC的 @requestbody 方式
                                    Pojo pojo = req.requestBody(Pojo.class);
                                    if (pojo != null) {

                                        //停止处理,自定义response
                                        halt(500, "something wrong");
                                    }

                                    //内置Gson,可返回
                                    return pojo;
                                }
                        ),

                        //支持命名空间嵌套
                        newNameSpace("/c",

                                //拦截器(/start/c/d/abc   等),request预处理
                                NSBefore("/*", (req, resp) -> {

                                    //可用作验证授权
                                    return null;
                                }),

                                //简单数据绑定
                                NSGet("/d/:name", (req, resp) -> {
                                    return req.params(":name");
                                })
                        )
                )
        );
        //启动服务器.需要写在最后 默认端口5677
        listenAndStart();
    }

}