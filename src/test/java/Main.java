import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;
import com.sun.media.sound.SoftTuning;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.bay1ts.bay.core.Bay.*;


/**
 * Created by chenu on 2016/8/15.
 */
public class Main {
    // TODO: 2016/10/14 支持复杂路由(beego)和 可配置的session存储方案
    public static void main(String[] args) throws Exception {
        // TODO: 2016/10/15 我觉着得引入个配置.在哪开始初始化配置啊.不能再listenandstart中.
        staticResources("/statica");
        //慎用第二个静态资源,里面不能出现重复的文件名
        staticResources("/static");

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
        get("/test3",(req,resp)->{
            req.cookies().forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String s, String s2) {
                    System.out.println(s+"   "+s2);
                }
            });
            System.out.println("-------------------");
            resp.cookie("ni","hao",3600);
            return "cookie test" ;
        });

        get("/hehe/test4",(req,resp)->{
            System.out.println("------------------");
            System.out.println(req.userAgent());
            System.out.println(req.uri());
            System.out.println(req.url());
            System.out.println(req.pathInfo());
            System.out.println(req.attributes());
            // TODO: 2016/10/18 bug found
            System.out.println((String) req.attribute("hehe"));
            System.out.println(req.body());
            System.out.println(req.bodyAsBytes());
            System.out.println("=========================");
            //bug found
            System.out.println(req.contentLength());
            System.out.println(req.contextPath());
            System.out.println(req.session());
            System.out.println(req.servletPath());
            System.out.println(req.scheme());
            System.out.println("********************");
            System.out.println(req.raw());
            //maybe bug
            System.out.println("++++++++++++++++++");
            req.headers().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    System.out.println(s);
                }
            });
            System.out.println("++++++++++++++++++");
            System.out.println(req.host());
            //bug
            System.out.println(req.ip());
            System.out.println("=-=-=-=-=-=-=-=-=");
            System.out.println(req.protocol());
            System.out.println(req.isSecure());
            System.out.println(req.queryString());
            System.out.println(req.queryParams());
            System.out.println(req.queryParams("hehe"));
            return "req test";
        });

        listenAndStart();
    }


}
