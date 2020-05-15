import io.vertx.core.Vertx;
import mongo.MongoVerticle;

public class MongodbApplication {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MongoVerticle(), res -> {
            if (res.succeeded()){
                System.out.println("create MongoVerticle success");
            }else {
                System.out.println("create MongoVerticle faild");
            }
        });
    }
}
