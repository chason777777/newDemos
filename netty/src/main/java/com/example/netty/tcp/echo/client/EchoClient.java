package com.example.netty.tcp.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/18
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();
            System.out.println("链接成功");
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{
//        if (args.length != 2) {
//            System.out.println("Usage: " + EchoClientHandler.class.getSimpleName() + "<host> <port>");
//        }
//        String host = args[0];
//        int port = Integer.parseInt(args[1]);
        new EchoClient("127.0.0.1", 1010).start();
    }
}
