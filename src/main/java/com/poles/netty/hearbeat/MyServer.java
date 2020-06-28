package com.poles.netty.hearbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/27 5:59 下午
*
*********************************************************************
*/
public class MyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))  //在bossGroup中增加一个日志处理器，这个处理器是netty自带的
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        /*
                         * 加入一个netty提供的处理空闲状态的处理器
                         * readerIdelTime 表示这段时间如果没有读操作，则会发送一个心跳检测包检测连接是否存在
                         * writeIdelTime  表示这段时间如果没有写操作，则会发送一个心跳检测包检测连接是否存在
                         * allIdelTime 表示这段s时间如果没有读操作和些错误，则会发送一个心跳检测包检测连接是否存在
                         *
                         * 文档：Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
                         * read, write, or both operation for a while.
                         *
                         * 当IdleStateEvent出发h后，就会传递给管道的下一个handler去处理，即调用下一个handler的userEventTriggered方法
                         */
                        pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));

                        //自定义一个handler专门用于处理上面读空闲？写空闲？度写空闲？事件，上面只触发，怎么处理，需要自定义
                        pipeline.addLast(new MyServerHandler());
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(6667).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
