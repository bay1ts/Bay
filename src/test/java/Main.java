import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;

import java.util.function.BiConsumer;

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
            pojoTest.setName("bay");
            return pojoTest;
        });

        listenAndStart();
    }


}
