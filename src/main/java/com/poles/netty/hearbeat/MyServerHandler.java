package com.poles.netty.hearbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/27 6:13 下午
*
*********************************************************************
*/
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *
     * @author liyanlong
     * @date 2020-06-27 18:14:21
     * @param ctx 上下文
     * @param evt 事件
     * @return void
     */
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
            }

            System.out.println(ctx.channel().remoteAddress() + "--超时时间--" + eventType);
            System.out.println("服务器做各种超时情况下的相应处理...");

            ctx.channel().close();  //一旦出发任何一种，就干掉这个通道
        }
    }
}
