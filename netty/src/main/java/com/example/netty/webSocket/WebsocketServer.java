package com.example.netty.webSocket;

import com.example.netty.http.HttpPipelineInitializer;
import com.example.netty.tcp.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/24
 */
public class WebsocketServer {
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
