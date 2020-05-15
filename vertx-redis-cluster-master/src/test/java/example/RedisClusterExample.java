package example;

import io.vertx.core.AbstractVerticle;
import io.vertx.redis.*;
import io.vertx.redis.HostAndPort;
import util.Runner;

/**
 * Created by Caijt on 2017/1/20.
 */
public class RedisClusterExample extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.run(RedisClusterExample.class);
    }

    public void start() {
        RedisClusterOptions options = new RedisClusterOptions()
                .addNode(new HostAndPort("47.107.151.220", 6381))
                .addNode(new HostAndPort("47.107.151.220", 6382))
                .addNode(new HostAndPort("47.107.151.220", 6383))
                .addNode(new HostAndPort("47.107.151.220", 6391))
                .addNode(new HostAndPort("47.107.151.220", 6392))
                .addNode(new HostAndPort("47.107.151.220", 6393));

        RedisCluster redisCluster = RedisCluster.create(vertx, options);

        redisCluster.get("name", ar -> {
            System.out.println("result:" + ar.result());
            System.out.println("result:" + ar.cause());
        });
    }
}
