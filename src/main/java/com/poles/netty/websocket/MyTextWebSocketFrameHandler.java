package com.poles.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    //群组
    private static ChannelGroup channelsGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**消息处理与转发等操作*/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息：" + msg.text());

        Channel channel = ctx.channel();
        //遍历channelsGroup, 回送消息，但是要排除自己
        channelsGroup.forEach(ch -> {
            if(channel != ch){
                ch.writeAndFlush(new TextWebSocketFrame("[客户端]" + channel.remoteAddress() + "：" + msg.text()));
            }else{
                ch.writeAndFlush(new TextWebSocketFrame("我：" + msg.text()));
            }
        });

        //如果只转发给自己：
//      channel.writeAndFlush(new TextWebSocketFrame("服务器回送消息(" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ")：" + msg.text()));

        //如果要转发给所有人：
//        channelsGroup.writeAndFlush(new TextWebSocketFrame("服务器回送消息(" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ")：" + msg.text()));
    }

    /**客户端连接*/
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id表示唯一的值，LongText是唯一的
//        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
//        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());  //这个可能会重复

        //获取当前Channel
        Channel channel = ctx.channel();
        //将该客户端上线的信息推送给其它在线的客户端, 它内部会循环遍历所有在线channel去通知
        channelsGroup.writeAndFlush(new TextWebSocketFrame("[客户端]" + channel.remoteAddress() + "(" + currentTime() + ") 加入聊天!!!\n"));
        //加入到channel组
        channelsGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        channelsGroup.writeAndFlush(new TextWebSocketFrame("[客户端]" + channel.remoteAddress() + "(" + currentTime() + ") 退出聊天!!!"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生" + cause.getMessage());
        ctx.close(); //关闭连接
    }

    //心跳监测
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                default:
            }

            System.out.println(ctx.channel().remoteAddress() + "--" + eventType + "， 时间超时，断开连接--");
//            System.out.println("服务器做各种超时情况下的相应处理...");

            ctx.channel().close();  //一旦出发任何一种，就干掉这个通道
        }
    }

    private String currentTime(){
        return format.format(new Date());
    }
}
