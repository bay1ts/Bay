import static com.bay1ts.bay.core.Bay.*;

/**
 * Created by chenu on 2016/10/18.
 */

public class SessionTest {
    public static void main(String[] args) {
        NSRoute(
                newNameSpace("/a",
                        newNameSpace("/c",
                                NSGet("/d", (req, resp) -> {
                                    req.cookies().forEach((k, v) -> {
                                        System.out.println(k + "--" + v);
                                    });
                                    return "d";
                                })
                        ),
                        NSGet("/hehe", (req, resp) -> {
                            req.cookies().forEach((k, v) -> {
                                System.out.println(k + "--" + v);
                            });
                            System.out.println("----");
                            System.out.println(req.session().id());
                            return "hehe";
                        }),
                        NSPost("xixi", (req, resp) -> {
                            return "xixi";
                        }),
                        NSGet("/b", (req, resp) -> {
                            req.cookies().forEach((k, v) -> {
                                System.out.println(k + "--" + v);
                            });
                            return "b";
                        })
                ));

        listenAndStart();
    }
}
