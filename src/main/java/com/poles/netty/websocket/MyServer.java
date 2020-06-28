package com.poles.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.concurrent.TimeUnit;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/27 6:25 下午
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
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        //由于是基于http的交互，所有需要使用http的编码器和解码器
                        pipeline.addLast("http_codec", new HttpServerCodec());
                        //由于是基于http协议，所以是以块方式读写的，所以需要添加ChunkedWriteHandler处理器
                        pipeline.addLast(new ChunkedWriteHandler());

                        /*
                         * http协议的数据，在传输过程中是分段的，所有需要将多段内容聚合起来
                         * 当浏览器发送大量数据时，会发送多次http请求
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));

                        /*
                         * 对于webSocket，数据是帧（frame)的形式传递的，可以看到WebSocketFrame下面有六个子类
                         * 浏览器请求时使用websocket协议：ws://localhost:8080/hello, 那么这里要对hello进行处理
                         * WebSocketServerProtocolHandler 核心功能是将http协议升级为ws协议，才能保持长连接
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/accept"));

//                        pipeline.addLast(new IdleStateHandler(5, 5, 10, TimeUnit.SECONDS));

                        //自定义handler, 用于处理业务逻辑
                        pipeline.addLast(new MyTextWebSocketFrameHandler());
                    }
                });

        try {
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
