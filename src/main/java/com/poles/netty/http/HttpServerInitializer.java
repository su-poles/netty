package com.poles.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/24 3:57 下午
*
*********************************************************************
*/
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //获取管道对象
        ChannelPipeline pipeline = ch.pipeline();

        //增加处理器

        //1. 使用一个netty提供的编解码器：这里的HttpServerCodec为Netty提供的编解码器
        pipeline.addLast("myCodec", new HttpServerCodec());

        //2. 使用一个自定义的处理器
        pipeline.addLast("myHandler", new HttpServerHandler());
    }


    /**
     * 自定义的处理器使用一个内部类
     * 1. SimpleChannelInboundHandler 为 ChannelInboundHandlerAdapter 的一个子类
     * 2. HttpObject为客户端与服务器端通讯的一个数据类型，指定为HttpObject, 那么就被封装为该中类型
     */
    static class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if(msg instanceof HttpRequest){

                //浏览器访问：http://localhost:8080, 会请求两次，一次是这个，额外的一次是下载favicon.ico图标
                HttpRequest request = (HttpRequest)msg;
                String uri = request.uri();
                if("/favicon.ico".equals(uri)){
                    System.out.println("请求接口为: " + uri + ", 不做响应！");
                    return;
                }

                System.out.println("msg 类型=" + msg.getClass());
                System.out.println("客户端地址：" + ctx.channel().remoteAddress());

                //回复信息给浏览器，需要整成httpRequest格式的，必须要遵循http协议才行
                ByteBuf content = Unpooled.copiedBuffer("Hello, 浏览器你好~~服务器端向你问好！", CharsetUtil.UTF_16);  //为啥中文乱码需要用这个编码？

                //构造httpResponse
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

                //返回response
                ctx.writeAndFlush(response);
            }
        }
    }
}
