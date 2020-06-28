package com.poles.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/23 8:18 下午
 * 1. 我们自定义个handler， 需要继承netty规定好的某一个HandlerAdapter
 * 2. 我们自定义NettyServerHandler，这里继承了ChannelInboundHandlerAdapter，则为一个ChannelInboundHandler
*********************************************************************
*/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /*
     * 读取客户端数据的方法
     *   1. ChannelHandlerContext 上下文对象，包含管道pipeline(里面有很多handler)，通道channel, 地址
     *
     *     通道，里面包含有数据，通常用来读写数据
     *     管道，当数据流过的时候，对数据做一系列的加工，管道就想个工具集合（走不通的地方就等于做不同的处理），通道像个存储箱（不知道对不对，就这么理解吧）
     *
     *   2. Object msg: 客户单发送的数据，object类型
     *
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //1. 耗时业务，提交到taskQueue异步执行
        //如果这里有一个非常耗时的业务，那么就需要异步执行，异步执行就是将业务提交到channel对应的NioEventLoop的taskQueue中。
//        Thread.sleep(10000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端，(>^ω^<)喵222", CharsetUtil.UTF_8));
//        System.out.println("go on ....");
        //解决方案1： 下面就是将耗时部分提交到taskQueue中， 如果额外再提交一个线程也是耗时10秒，那么第二个任务是20秒之后才执行完成，taskQueue是按顺序执行的
        //可以自己读秒，然后观看打印结果，通过断点，在ctx中的taskQueue中就可以看到总过提交了几个任务, pipeline->channel->eventloop->taskQueue
        ctx.channel().eventLoop().execute(()->{
            try {
                Thread.sleep(10 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端，(>^ω^<)喵222\n", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //2. 用户自定义定时任务，提交到scheduleTaskQueue中执行
        ctx.channel().eventLoop().schedule(()->{
            try {
                Thread.sleep(1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端，(>^ω^<)喵333\n", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.SECONDS);   //延时5秒执行，耗时10秒钟


        //3.不耗时业务直接执行
        //打印当前线程信息
        System.out.println("服务器当前线程：" + Thread.currentThread().getName());

        //观察channel与pipeline之间的关系， 通过打断点观察。 channel里包含有pipeline, pipeline里也包含有channel
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();  //本质上是一个双向链表，涉及到出入栈的问题

        //将msg转成字节存储在byteBuf里, ByteBuf是Netty中实现一种比NIO里的byteBuffer更牛逼的存在
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + channel.remoteAddress());
    }

    /*
     *  数据读取完毕， 需要回复一个消息
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将数据写入缓冲区，并将数据刷新到通道里去
        //一般需要将内容先编码，再发送
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello, 客户端~汪汪~~~", CharsetUtil.UTF_8));
    }


    /*
     * 异常处理方法， 需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        ctx.channel().close();
        ctx.close();
    }
}
