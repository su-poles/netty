package com.poles.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/23 8:38 下午
*
*********************************************************************
*/
public class NettyClient {
    public static void main(String[] args) {
        //客户端需要一个事件循环组
        NioEventLoopGroup clientGroup = new NioEventLoopGroup();

        //创建客户端启动对象，主要是这里是Bootstrap不是ServerBootstrap
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(clientGroup)                    //设置线程组
                .channel(NioSocketChannel.class)       //设置客户端实现类
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new ProtobufEncoder());
                        ch.pipeline().addLast(new NettyClienthandler());    //加入自定义的处理器（Handler）
                    }
                });


        System.out.println("客户端已经准备就绪....");


        try {
            //启动客户端，连接服务器端, sync表示异步，不会在这里阻塞，这里涉及到netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            //监听 关闭通道
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }
}
