package com.example.netty.tcp.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/18
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf>{
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println("5555555555555");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("66666666666666");
        System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("7777777777777");
        cause.printStackTrace();
        ctx.close();
    }
}
