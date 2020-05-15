package shiro;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.apache.commons.lang3.StringUtils;
import util.SHA256Util;
import io.vertx.ext.auth.User;

public class ShiroVerticle extends AbstractVerticle{
    public static final String PW_PATTERN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
    //    private static Logger logger = LoggerFactory.getLogger(SysUserVerticle.class);

    public void uploadApi(AsyncResult<Vertx> res) {
        vertx = res.result();

        // shiro
        JsonObject config = new JsonObject().put("properties_path", "classpath:test-auth.properties");
        AuthProvider provider = ShiroAuth.create(vertx, ShiroAuthRealmType.PROPERTIES, config);

        // 创建路由对象
        Router router = Router.router(vertx);

        // 配置消息体处理器，限值消息体大小(消息体字符长度)
        router.route().handler(BodyHandler.create().setBodyLimit(1000L));
        // 跨域配置,设置请求域Origin的范围*
        router.route().handler(CorsHandler.create("*"));
        // 配置返回数据类型
        router.route("/*").handler(ResponseContentTypeHandler.create());
        // 全局拦截器(处理参数，token校验等)
//        router.post("/*").handler(routingContext -> {routingContext.next();});
        router.post("/*").handler(routingContext -> {
            String username = routingContext.request().getHeader("username");
            String password = routingContext.request().getHeader("password");
            if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
                routingContext.fail(444);//非法参数
                return;
            }
            JsonObject authInfo = new JsonObject().put("username", username).put("password", password);
            provider.authenticate(authInfo, rs -> {
                if (res.succeeded()) {
                    User user = rs.result();
                    user.isAuthorised("newsletter:edit:13", rus -> {
                        if (res.succeeded()) {
                            boolean hasPermission = rus.result();
                            if (hasPermission) {
                                routingContext.next();
                                return;
                            }else {
                                routingContext.fail(333);//非法参数
                                return;
                            }
                        } else {
                            routingContext.fail(222);//非法参数
                            return;
                    }
                    });

                } else {
                    routingContext.fail(111);//非法参数
                }
            });
        });




        // 异常处理
        router.route("/*").failureHandler(failureRoutingContext -> {
            int statusCode = failureRoutingContext.statusCode();//获取错误状态码
            failureRoutingContext.response().putHeader("content-type", "application/json");
            JsonObject result = new JsonObject();
            if (statusCode >= 0) {//判断是否出现异常
                switch (statusCode) {//全局异常处理
                    case 404:
                        failureRoutingContext.response().setStatusCode(404);
                        result.put("code",404);
                        result.put("msg","找不到资源");
                        break;
                    case 500:
                        failureRoutingContext.response().setStatusCode(500);
                        result.put("code",500);
                        result.put("msg","服务器处理异常");
                        break;
                    case 413:
                        failureRoutingContext.response().setStatusCode(413);
                        result.put("code",413);
                        result.put("msg","body数据过大");
                        break;
                    default:
                        failureRoutingContext.response().setStatusCode(500);
                        result.put("code",999);
                        result.put("msg","未知异常");
                        break;
                }
                failureRoutingContext.response().end(JsonObject.mapFrom(result).toString());
            }
        }).handler(TimeoutHandler.create(2000, 509));// 超时处理，返回错误码

        // 添加用户
        router.post("/user/insert").blockingHandler(routingContext -> {
            JsonObject params = routingContext.getBodyAsJson();

            if (null != params && StringUtils.isNotBlank(params.getString("userName")) && StringUtils.isNotBlank(params.getString("password"))) {
                // 校验密码格式
                if (!params.getString("password").matches(PW_PATTERN)) {
                    routingContext.response().end(new JsonObject().put("code", 402).put("message", "Password format error").toString());
                    return;
                }

                // 添加用户
                vertx.eventBus().send("cn.chason678.login.sysUser.insert",
                        new JsonObject().put("userName", params.getString("userName")).put("password", SHA256Util.getSHA256Str(params.getString("password"))),
                        (AsyncResult<Message<JsonObject>> as) -> {
                            if(as.failed()){
                                System.out.println(as.cause());
                                routingContext.fail(501);
                            }else {
                                routingContext.response().end(as.result().body().toString());
                            }
                        }
                );
            } else {
                routingContext.fail(402);
            }


        });

        // 登录
        router.post("/user/login").blockingHandler(routingContext -> {
            JsonObject params = routingContext.getBodyAsJson();
            if (null != params && StringUtils.isNotBlank(params.getString("userName")) && StringUtils.isNotBlank(params.getString("password"))) {
                // 校验密码格式
                if (!params.getString("password").matches(PW_PATTERN)) {
                    routingContext.response().end(new JsonObject().put("code", 402).put("message", "Password format error").toString());
                    return;
                }

                // 用户登录
                vertx.eventBus().send("cn.chason678.login.sysUser.login",
                        new JsonObject().put("userName", params.getString("userName")).put("password", SHA256Util.getSHA256Str(params.getString("password"))),
                        (AsyncResult<Message<JsonObject>> as) -> {
                            System.out.println(as.result().body());
                            routingContext.response().end(as.result().body().toString());
                        }
                );
            } else {
                routingContext.fail(402);
            }
        });

        // 修改用户信息
        router.post("/user/update").blockingHandler(routingContext -> {
            JsonObject params = routingContext.getBodyAsJson();
            if(StringUtils.isNotBlank(params.getString("password"))){
                params.put("password",SHA256Util.getSHA256Str(params.getString("password")));
            }

            if (null != params && StringUtils.isNotBlank(params.getString("uid"))) {
                // 校验邮箱长度
                if (params.getJsonArray("emailList") != null && params.getJsonArray("emailList").size() > 3) {
                    routingContext.response().end(new JsonObject().put("code", 405).put("msg", "The emailList more than 3").toString());
                    routingContext.fail(405);
                    return;
                }

                vertx.eventBus().send("cn.chason678.login.sysUser.update", params, (AsyncResult<Message<JsonObject>> as) -> {
                            System.out.println(as.result().body());
                            routingContext.response().end(as.result().body().toString());
                        }
                );
            } else {
                routingContext.fail(402);
            }
        });

        // 创建HttpServer
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept);
        server.listen(8887);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        JsonObject json = new JsonObject();
//        json.put("zookeeperHosts", "121.201.57.214:2181");
        json.put("zookeeperHosts", "127.0.0.1:2181");
        json.put("sessionTimeout", 5000);
        json.put("connectTimeout", 3000);
        json.put("rootPath", "test");
        JsonObject childJson = new JsonObject();
        childJson.put("initialSleepTime", 100);
        childJson.put("intervalTimes", 10000);
        childJson.put("maxTimes", 5);
        json.put("retry", childJson);

        ClusterManager mgr = new ZookeeperClusterManager(json);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);

        //集群
        Vertx.clusteredVertx(options, res->{
            if(res.failed()){
                System.out.println("fail -> "+ res.cause().getMessage());
            }else {
                this.uploadApi(res);
            }
        });
//        Vertx.clusteredVertx(options, this:: uploadApi);

        startFuture.complete();
    }
}
