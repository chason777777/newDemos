package com.example.netty.http;

import com.example.netty.tcp.echo.server.EchoServerHandler;
import com.example.netty.tcp.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/23
 */
public class HttpServer {
    public static void main(String[] args) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8700))
                    .childHandler(new HttpPipelineInitializer(false, null));
            ChannelFuture f = b.bind().sync();
            System.out.println(Server.class + " 启动正在监听： " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
