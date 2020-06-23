package com.poles.base_concept.nio_second;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/19 3:08 下午
*
*********************************************************************
*/
public class NIOClient {
    public static void main(String[] args) throws IOException {
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false); //设置为非阻塞

        InetSocketAddress remote = new InetSocketAddress("127.0.0.1", 6666);
        //连接服务器
        if(!socketChannel.connect(remote)){
            while(!socketChannel.finishConnect()){
                System.out.println("因为连接服务需要事件，这里客户端是否阻塞的，这里可以做其它事情...");
            }
        }

        //如果服务器连接成功，则发送数据到服务器
        String sendMessage = "Hello, Server, 你好啊！" + new Date();
        ByteBuffer buffer = ByteBuffer.wrap(sendMessage.getBytes());   //把发送的内容转成字节数组写入buffer里去
        socketChannel.write(buffer);   //将数据写入socketChannel
        System.in.read();  //代码阻塞在这里，否则服务器端会每秒钟都在重复读取数据？why? 多个客户端，如果断开一个就会无限打印，完蛋。
    }
}
