package com.example.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/24
 */
public class HttpPipelineInitializer extends ChannelInitializer<Channel>{
    private final boolean client;
    private final SslContext sslContext;

    public HttpPipelineInitializer(boolean client, SslContext sslContext) {
        this.client = client;
        this.sslContext = sslContext;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        pipeline.addFirst("ssl", new SslHandler(sslEngine));

        if (client) {
//            pipeline.addLast("decoder", new HttpResponseDecoder());
//            pipeline.addLast("encoder", new HttpRequestEncoder());
            pipeline.addLast("codec", new HttpClientCodec());
            FileInputStream in = new FileInputStream("");
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
//            pipeline.addLast("decoder", new HttpRequestDecoder());
//            pipeline.addLast("encoder", new HttpResponseEncoder());
            pipeline.addLast("httpHandler", new HttpServerHandler());
        }
        pipeline.addLast("aggregator" , new HttpObjectAggregator(512 * 1024));

    }
}
