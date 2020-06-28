package com.poles.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashSet;
import java.util.Set;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/23 3:15 下午
*
*********************************************************************
*/
public class NettyServer {
    public static void main(String[] args){
        Set<SocketChannel> set = new HashSet<>();

        /*
         * 1. 创建两个线程组：BossGroup 和 WorkerGroup，
         * 2. BossGrop只处理连接请求，WorkerGroup做真正的与客户端的业务处理，
         * 3. 两个group都是无限循环
         * 4. bossGroup和wokerGroup 含有的子线程（NioEventLoop)的个数，默认都是最大可以同时执行线程数 * 2（本机Mac本四核8线程，也就是16个线程）
         * 当客户端有连接时，则轮询的方式分配16个NioEventLoop去接收处理请求
         * 如果这么写：NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
         * 那么bossGroup里就只有一个线程了，可以debug一下，看看child有几个就知道了。
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();


            //创建服务器端启动的一个助手，用来配置参数。 注意是ServerBootstrap 不是 Bootstrap.
            ServerBootstrap bootstrap = new ServerBootstrap();

            /*
             * 使用链式编程设置参数， 下面凡是child的，指的就是workerGroup的设置
             * 其实就是设置通道、设置选项、设置处理器，主从都设置，主就是bossGroup, 从就是WorkerGroup（需要了解Reactor主从模型）
             * 服务器的通道就是NioServerSocketChannel来实现
             * 客户端的通道就是SocketChannel来实现
             */
            bootstrap.group(bossGroup, workerGroup)                                  //设置线程组：bossGroup 与 WorkerGroup两个线程组
                    .channel(NioServerSocketChannel.class)                          //设置通道类型：使用NioServerScoketChannel 作为服务的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128)                    //设置线程队列等待连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)            //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {         //设置一个通道处理器，这里通过匿名对象初始化一个处理对象

                        //给管道pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //来自客户端的通道，这里可以将通道加入一个集合
                            set.add(ch);

                            ch.pipeline().addLast(new NettyServerHandler());    //通过通道（客户端连接的通道）获取管道，然后将处理器追加到管道的最后面
                        }
                    });

            System.out.println("服务器已经准备好了......");

        try{
            //绑定一个端口号，并且同步后生成一个ChannelFuture对象
            //启动服务器， sync表示异步，而不会阻塞在这里
            ChannelFuture cf = bootstrap.bind(6668).sync();

            //给cf注册监听器，监听我们关系的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("服务器监听端口 6668 成功！");
                    }else{
                        System.out.println("服务器监听端口 6668 失败！");
                    }
                }
            });

            //对关闭通道进行监听
            //当关闭通道有消息时，进行关闭处理
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
