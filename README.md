# Bay

一个非Servlet规范的JavaWeb框架,包括一个基于Netty的高性能服务器.

#找工作.实习.Web后端方向 有意请联系bay1ts@163.com

##介绍
这是一个基于Netty实现的非Servlet规范的Web服务器,由于底层设计经验不足,所以实际上是完成了对Servlet对外接口的一层包装.此框架与Servlet的相同之处大概就这些.就Servlet注册路由的繁琐,本框架尽量简化了这部分体验,详情请参见用法部分.另外本框架支持session,但提供了两种配置方案:1.内存存储.适合小应用,在单机环境部署.2.Redis存储.可以实现简单的横向扩展,适合大型应用.
此框架正在完善之中.希望有兴趣的朋友可以联系我,一起完善这个框架.
##用法
###1.Start
```
import static com.bay1ts.bay.core.Bay.*;

public class HelloWorld {
    public static void main(String[] args) {
        //支持静态文件
        staticResources("/static");


        //支持RESTful路由,可使用java8 lambda表达式简化编码
        get("/hello", (req, resp) -> {
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
                                    if (pojo == null) {

                                        //停止处理,自定义response
                                        halt(500, "something wrong");
                                    }

                                    //内置Gson,可返回
                                    return pojo;
                                },

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
                                                }
                                        )
                                )
                        )
                )
        );
        //启动服务器.需要写在最后 默认端口5677
        listenAndStart();
    }
}
```
