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
        //读取从客户端发过来的StudentPojo.Student类型的数据
        StudentPOJO.Student student = (StudentPOJO.Student) msg;
        System.out.println("客户端发送的数据 id=" + student.getId() + ", 名字=" + student.getName());
    }

    /*
     *  数据读取完毕， 需要回复一个消息
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将数据写入缓冲区，并将数据刷新到通道里去
        //一般需要将内容先编码，再发送
        ctx.writeAndFlush(Unpooled.copiedBuffer("信息读取完成", CharsetUtil.UTF_8));
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
