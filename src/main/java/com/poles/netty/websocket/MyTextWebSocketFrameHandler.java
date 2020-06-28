package com.poles.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/27 6:40 下午
*
*********************************************************************
*/
//因为前后端有数据，交互，设计到出站入站等，需要继承SimpleChannelInboundHandler类， TextWebSocketFrame表示一个文本帧
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**消息处理与转发等操作*/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息：" + msg.text());

        //给浏览器回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器回送消息(" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ")：" + msg.text()));
    }

    /**客户端连接*/
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id表示唯一的值，LongText是唯一的
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());  //这个可能会重复
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生" + cause.getMessage());
        ctx.close(); //关闭连接
    }
}
