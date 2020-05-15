package com.example.netty.tcp.echo.server;

import com.example.netty.tcp.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/18
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port){
        this.port = port;
    };

    public static void main(String[] args) throws Exception{
//        if (args.length != 1) {
//            System.out.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
//        }
//        int port = Integer.parseInt(args[0]);
        new EchoServer(1010).start();
    }

    public void start()throws Exception{
        EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception{
                            System.out.println("报告");
                            System.out.println("信息：有一客户端链接到本服务端");
                            System.out.println("IP:" + ch.localAddress().getHostName());
                            System.out.println("Port:" + ch.localAddress().getPort());
                            System.out.println("报告完毕");
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();
            System.out.println(Server.class + " 启动正在监听： " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
