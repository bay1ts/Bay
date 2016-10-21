import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.bay1ts.bay.core.Bay.*;


/**
 * Created by chenu on 2016/8/15.
 */
public class Main {
    // TODO: 2016/10/20 复杂路由基本完事了,还差个厉害的数据绑定. 又特么是个大坑.
    // TODO: 2016/10/20 还差 拦截器(过滤器) 这个应该不算难
    // TODO: 2016/10/20 还差个 路由中 any的实现
    public static void main(String[] args) throws Exception {
        staticResources("/statica");
        //慎用第二个静态资源,里面不能出现重复的文件名
        staticResources("/static");
        before("/*",(req,resp)->{
            System.out.println("===============呵呵哒");
            return null;
        });
        get("/hello2", new Action() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "css";
            }
        });
        post("/css", (req, res) -> {
            return "nihao";
        });
        get("/test2/:name", (req, resp) -> {
            return "Hello " + req.params(":name");
        });

        get("/from/*/to/*", (req, resp) -> {
            return "number of splat param " + req.splat().length + "  " + req.splat()[1];
        });

        get("/test1", (req, resp) -> {
            return req.session().id();
        });
        get("test2", (req, resp) -> {
            StringBuilder sb = new StringBuilder();
            req.cookies().forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String s, String s2) {
                    sb.append("key " + s + " value " + s2 + "  \r\n");
                }
            });
            return sb.toString();
        });
        get("/", (req, resp) -> {
            System.out.println("-------------------");
            POJOTest pojoTest = new POJOTest();
            pojoTest.setId(3);
            pojoTest.setAge(23);
            return pojoTest;
        });
        get("/test3", (req, resp) -> {
//            req.cookies().forEach(new BiConsumer<String, String>() {
//                @Override
//                public void accept(String s, String s2) {
//                    System.out.println(s + "   " + s2);
//                }
//            });
            req.cookies().forEach((a,b)-> System.out.println(a+"   "+b));
            System.out.println("-------------------");
            resp.cookie("ni", "hao", 3600);
            return "cookie test";
        });

        get("/hehe/test4", (req, resp) -> {
            System.out.println("useragent");
            System.out.println(req.userAgent());
            System.out.println("uri");
            System.out.println(req.uri());
            System.out.println("url");
            System.out.println(req.url());
            System.out.println("pathinfo");
            System.out.println(req.pathInfo());
            System.out.println("attributes");
            System.out.println(req.attributes());
            System.out.println("attribute('hehe')");
            // TODO: 2016/10/18 bug found
            System.out.println((String) req.attribute("hehe"));
            System.out.println("body");
            System.out.println(req.body());
            System.out.println("bodyas bytes");
            System.out.println(req.bodyAsBytes());
            System.out.println("contentlength");
            //bug found
            System.out.println(req.contentLength());
            System.out.println("contextpath");
            System.out.println(req.contextPath());
            System.out.println("session");
            System.out.println(req.session());
            System.out.println("servletpath");
            System.out.println(req.servletPath());
            System.out.println("scheme");
            System.out.println(req.scheme());
            System.out.println("raw");
            System.out.println(req.raw());
            //maybe bug
            System.out.println("headers");
//            req.headers().forEach(new Consumer<String>() {
//                @Override
//                public void accept(String s) {
//                    System.out.println(s);
//                }
//            });
            req.headers().forEach(System.out::println);
            System.out.println("host");
            System.out.println(req.host());
            //bug
            System.out.println("ip");
            System.out.println(req.ip());
            System.out.println("protocol");
            System.out.println(req.protocol());
            System.out.println("issecure");
            System.out.println(req.isSecure());
            System.out.println("querystring");
            System.out.println(req.queryString());
            System.out.println("queryparams");
            System.out.println(req.queryParams());
            System.out.println("queryparams hehe");
            // TODO: 2016/10/18 实现复杂路由和数据绑定 首要目标 spring mvc的 自动绑定成对象 感觉够呛..
            System.out.println(req.queryParams("nihao"));
            return "req test";
        });

        NSRoute(
                newNameSpace("/hehe",
                        NSGet("/nihao", (req, resp) ->
                                "aa"
                        ),
                        NSGet("/gaga",UserController.serveLoginPage),
                        NSPost("/haha",(req,resp)->{
                            return "bb";
                        }),
                        newNameSpace("/api",
                                NSBefore("/*",(req,resp)->{
                                    System.out.println("adding filter..........");
                                    return "cc";
                                }),
                                NSGet("/root",(req,resp)->{
                                    return "haha";
                                }),
                                newNameSpace("/v2",
                                        NSGet("/one",(req,resp)->{
                                            return "aa";
                                        }))
                        )
                )
        );


        listenAndStart();


    }


}
