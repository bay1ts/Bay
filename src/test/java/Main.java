import com.bay1ts.bay.core.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;

import static com.bay1ts.bay.core.Bay.*;


/**
 * Created by chenu on 2016/8/15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        staticResources("/static");
        get("/hello2", new Action() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "css";
            }
        });
        post("/css",(req,res)->{
            return "nihao";
        });
        get("/test2/:name",(req,resp)->{
            String a= req.session().attribute("test");
            //支持session啊
            System.out.println(a+"---");
            return "Hello "+req.params(":name");
        });

        get("/from/*/to/*",(req,resp)->{
            req.session().attribute("test","test");
            return "number of splat param "+req.splat().length+"  "+req.splat()[1];
        });

//        Service.addRouter("/shit", new Action() {
//            public FullHttpResponse doAction(FullHttpRequest request, FullHttpResponse response) {
//                return response.replace(Unpooled.copiedBuffer("this is a shit",CharsetUtil.UTF_8));
//            }
//        });
        listenAndStart(8080);
    }


}
