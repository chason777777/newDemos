package com.example.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/18
 */
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            System.out.println("--------------------start----------------------------");
            System.out.println("fullRequest: " + request.toString());
            System.out.println("url: " + request.uri());
            System.out.println("method: " + request.method());
            System.out.println("headers :" + request.headers().toString());
            System.out.println("content: " + request.content());
            System.out.println("--------------------end------------------------------");
        }else {
            System.out.println("request: " + msg.toString());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
