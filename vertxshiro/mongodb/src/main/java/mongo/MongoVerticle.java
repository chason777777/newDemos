package mongo;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

public class MongoVerticle extends AbstractVerticle {

    /**
     * @author : baijun
     * @date : 2019-01-04
     * @description : zookeeper 集群实体，用来获取 zk 客户端
     */
    private ZookeeperClusterManager zookeeperClusterManager;

    public void consumer(AsyncResult<Vertx> rs){
        vertx = rs.result();
        JsonObject config = Vertx.currentContext().config();

        String uri = config.getString("mongo_uri");
        if (uri == null) {
            uri = "mongodb://localhost:27017";
        }
        String db = config.getString("mongo_db");
        if (db == null) {
            db = "sysUser";
        }

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);

        EventBus eventBus = vertx.eventBus();

        // 接受用户新增
        eventBus.consumer("cn.chason678.login.sysUser.insert",message ->{
            JsonObject sysUser = JsonObject.mapFrom(message.body());
            System.out.println("message:" + sysUser.toString());

            // 校验用户名是否已经存在
            mongoClient.find("sysUser",new JsonObject().put("userName",sysUser.getValue("userName")),res ->{
                if (res.result() != null && res.result().size()>0){
                    message.reply(new JsonObject().put("code",402).put("message","userName already exists"));
                    return;
                }else {
                    // 保存用户信息
                    mongoClient.save("sysUser", sysUser, id -> {
                        System.out.println("Inserted id: " + id.result());
                        message.reply(new JsonObject().put("code",200).put("msg","success"));
                    });
                }
            });

        });

        // 接收用户登录
        eventBus.consumer("cn.chason678.login.sysUser.login",message ->{
            JsonObject sysUser = JsonObject.mapFrom(message.body());
            System.out.println("message:" + sysUser.toString());

            // 根据用户名、密码查询用户信息
            mongoClient.find("sysUser",new JsonObject().put("userName",sysUser.getValue("userName")).put("password",sysUser.getValue("password")),res ->{
                if (res.result() != null && res.result().size()>0){
                    message.reply(new JsonObject().put("code",200).put("msg","success")
                            .put("datas",new JsonObject().put("uid",res.result().get(0).getValue("_id")).put("nickName",res.result().get(0).getValue("nickName"))));
                    return;
                }else {
                    // 用户不存在
                    message.reply(new JsonObject().put("code",401).put("message","auth failed"));
                }
            });

        });

        // 修改用户信息
        eventBus.consumer("cn.chason678.login.sysUser.update",message ->{
            JsonObject sysUser = JsonObject.mapFrom(message.body());
            System.out.println("message:" + sysUser.toString());

            JsonObject query = new JsonObject().put("_id",sysUser.getString("uid"));

            // 根据用户名、密码查询用户信息
            mongoClient.updateCollection("sysUser",query,new JsonObject().put("$set",sysUser),res ->{
                if (res.succeeded()){
                    message.reply(new JsonObject().put("code",200).put("message","success"));
                    return;
                }else {
                    // 用户不存在
                    message.reply(new JsonObject().put("code",401).put("message","faild"));
                }
            });

        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        JsonObject json = new JsonObject();
//        json.put("zookeeperHosts","121.201.57.214:2181");
        json.put("zookeeperHosts", "127.0.0.1:2181");
        json.put("sessionTimeout",5000);
        json.put("connectTimeout",10000);
        json.put("rootPath","test");
        JsonObject childJson = new JsonObject();
        childJson.put("initialSleepTime",100);
        childJson.put("intervalTimes",10000);
        childJson.put("maxTimes",5);
        json.put("retry",childJson);

        ClusterManager mgr = new ZookeeperClusterManager(json);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);

        /**
         * @date : 2019-01-04
         * @description : 为 zookeeper 集群赋值
         */
        zookeeperClusterManager = (ZookeeperClusterManager)mgr;
        //集群
        Vertx.clusteredVertx(options, this::consumer);

        startFuture.complete();
    }
}
