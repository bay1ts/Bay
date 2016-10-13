import com.bay1ts.bay.Action;
import com.bay1ts.bay.core.Request;
import com.bay1ts.bay.core.Response;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static com.bay1ts.bay.core.Bay.*;


/**
 * Created by chenu on 2016/8/15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        get("/hello2", new Action() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "hehe";
            }
        });

//        Router.addRouter("/shit", new Action() {
//            public FullHttpResponse doAction(FullHttpRequest request, FullHttpResponse response) {
//                return response.replace(Unpooled.copiedBuffer("this is a shit",CharsetUtil.UTF_8));
//            }
//        });
        listenAndStart(8080);
    }


}
