package com.poles.netty.groupchat;

import com.google.errorprone.annotations.Var;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.sound.midi.Soundbank;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/26 9:15 上午
*
*********************************************************************
*/
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有的channel, handler是每个Channel独有的，所有这类定义的管理所有Channel的这个组，必须是static的，即所有channel共享一个组
    //GlobalEventExecutor.INSTANCE 是一个单例，全局的事件执行器
    //如果这里使用list管理，也可以，只不过就用不了netty提供的这个方法的一些API，比如循环推送，都需要自己实现
    private static ChannelGroup channelsGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //对于私聊，可以通过hashMap<userId, Channel>来管理, 即用户userId的内容，要转发给Channel
    //对于开房间聊天，我个人觉得可以使用List<ChannelsGroup>来管理用户会话

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /*
     * handlerAdded 表示连接建立时就触发， 一旦连接，第一个被执行
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //获取当前Channel
        Channel channel = ctx.channel();
        //将该客户端上线的信息推送给其它在线的客户端, 它内部会循环遍历所有在线channel去通知
        channelsGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "(" + currentTime() + ") 加入聊天!!!\n");
        //加入到channel组
        channelsGroup.add(channel);
        System.out.println("channelsGroup size = " + channelsGroup.size());
    }

    /*
     * 表示channel处于活动状态，
     * 也可以在这里提示xx上线，该信息直接在服务器端打印即可，当然也可以给客户端提示上线，不过这个事情已经被added方法做了，就不重复做了
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "(" + currentTime() + ") 上线了~\n");
    }

    /*
     * 表示channel处于非活动状态
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "(" + currentTime() + ") 离线了~\n");
    }

    /*
     * 断开连接，如果触发该方法，channelsGroup自动会做remove(Channel channel)的方法
     */

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelsGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "(" + currentTime() + ") 退出聊天!!!\n");
        //channelsGroup自动会做remove(Channel channel)的方法
        System.out.println("channelsGroup size = " + channelsGroup.size());
    }

    /*
     * 读取数据，并转发内容
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        //遍历channelsGroup, 回送消息，但是要排除自己
        channelsGroup.forEach(ch -> {
            if(channel != ch){
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "：" + msg + "\n\n");
            }else{
                ch.writeAndFlush("我：" + msg + "\n\n");
            }
        });
    }

    /**异常处理*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close(); //关闭通道
    }

    private String currentTime(){
        return format.format(new Date());
    }
}
