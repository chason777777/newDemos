import io.vertx.core.Vertx;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2020/3/12
 */
public class Test {
    public static io.vertx.redis.client.Redis client;
    public static RedisAPI redis;

    public static void main(String[] args) throws Exception{
//        List<SocketAddress> sas = new ArrayList<SocketAddress>();
//        sas.add(SocketAddress.inetSocketAddress(6381, "47.107.151.220"));
//        sas.add(SocketAddress.inetSocketAddress(6382, "47.107.151.220"));
//        sas.add(SocketAddress.inetSocketAddress(6383, "47.107.151.220"));
//        sas.add(SocketAddress.inetSocketAddress(6391, "47.107.151.220"));
//        sas.add(SocketAddress.inetSocketAddress(6392, "47.107.151.220"));
//        sas.add(SocketAddress.inetSocketAddress(6393, "47.107.151.220"));


        RedisOptions options = new RedisOptions()
                .setType(RedisClientType.CLUSTER)
                .setUseSlave(RedisSlaves.ALWAYS)
//                .setEndpoints(sas)
                .addEndpoint(SocketAddress.inetSocketAddress(6381, "47.107.151.220"))
                .addEndpoint(SocketAddress.inetSocketAddress(6382, "47.107.151.220"))
                .addEndpoint(SocketAddress.inetSocketAddress(6383, "47.107.151.220"))
                .addEndpoint(SocketAddress.inetSocketAddress(6391, "47.107.151.220"))
                .addEndpoint(SocketAddress.inetSocketAddress(6392, "47.107.151.220"))
                .addEndpoint(SocketAddress.inetSocketAddress(6393, "47.107.151.220"))
                ;

        Vertx vertx = Vertx.vertx();
        Redis.createClient(vertx, options).connect(onConnect -> {
            if (onConnect.succeeded()) {
                client = onConnect.result();
                redis = RedisAPI.api(client);
//                redis.get("tat", rs -> {
//                    if (rs.succeeded()){
//                        System.out.println(rs.result().toString());
//                    }else {
//                        System.out.println("55555");
//                    }
//                });
            }else {
                System.out.println("555666");
            }
        });
//        Thread.sleep(5000);
        int index = 0;
        do{
            System.out.println("wait connent:" + index++);
            Thread.sleep(1000);
        }while (redis == null);


        System.out.println("started.............");
        Scanner scanner = new Scanner(System.in);
        while (true){
            String str = scanner.nextLine();
            String[] strs = str.split(":");
            if (strs[0].equals("set")){
                List<String> list = new ArrayList<String>();
                list.add(strs[1]);
                list.add(strs[2]);
                redis.set(list, srs ->{
                    if (srs.succeeded()){
                        System.out.println(srs.result());
                    }else {
                        System.out.println(srs.cause());
                    }
                });
            }else {
                redis.get(strs[1], grs -> {
                   if (grs.succeeded()){
                       System.out.println(grs.result().toString());
                   }else {
                       System.out.println(grs.cause());
                   }
                });
            }
        }
    }
}
