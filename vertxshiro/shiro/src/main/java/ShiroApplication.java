import io.vertx.core.Vertx;
import shiro.ShiroVerticle;

public class ShiroApplication {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ShiroVerticle(), res ->{
            if (res.succeeded()){
                System.out.println("create ShiroVerticle success");
            }else {
                System.out.println("create ShiroVerticle faild");
            }
        });
    }
}
