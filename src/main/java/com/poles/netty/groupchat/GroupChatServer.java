package com.poles.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/26 9:07 上午
*
*********************************************************************
*/
public class GroupChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workergroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workergroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //增加两个三个处理器
                        //1. 从客户端发来的消息进行解码
                        ch.pipeline().addLast("decoder", new StringDecoder());
                        //2. 发往客户端的消息进行编码
                        ch.pipeline().addLast("encoder", new StringEncoder());
                        //3. 自定义业务处理器
                        ch.pipeline().addLast(new GroupChatServerHandler());
                    }
                });

        try {

            System.out.println("netty 服务器已经启动...");
            //监听
            ChannelFuture future = serverBootstrap.bind(6667).sync();
            //关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workergroup.shutdownGracefully();
        }

    }
}
