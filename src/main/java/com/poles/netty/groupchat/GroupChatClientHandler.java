package com.poles.netty.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/26 9:54 下午
*
*********************************************************************
*/
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取到服务器端的消息之后，进行相应处理，这里直接打印即可
        System.out.println(msg.trim());
    }
}
