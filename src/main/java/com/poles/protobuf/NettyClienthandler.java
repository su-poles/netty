package com.poles.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/23 9:18 下午
*
*********************************************************************
*/
public class NettyClienthandler extends ChannelInboundHandlerAdapter {

    /*
     * 通道就绪时，会执行该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //创建一个Student对象，传输到服务器

        StudentPOJO.Student student = StudentPOJO.Student.newBuilder().setId(1).setName("张三").build();

        //通过上下文发送消息，flush会让写入的消息强行写入通道
        ctx.writeAndFlush(student);
    }

    /*
     * 当通道有读取事件时，就会触发
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;

        System.out.println("服务器回复的消息：" + buf.toString(CharsetUtil.UTF_8));

        System.out.println("服务器的地址：" + ctx.channel().remoteAddress());
    }

    /*
    * 异常处理
     */

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //关闭上下文，实际上就是关闭通道
        ctx.close();
    }
}
