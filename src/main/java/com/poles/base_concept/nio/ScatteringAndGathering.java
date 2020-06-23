package com.poles.base_concept.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 7:35 下午
*
 *  分散 与 聚合
 *  Scattering: 将数据写入Buffer时，可以采用buffer数组，依次写入
 *  Gathering: 从buffer读取数据时，可以采用buffer数组，依次读取
 *
 *  示例的原理：
 *  1. 我们开启一个客户端与服务端网络程序，服务端监听客户端的消息， 使用nc 127.0.0.1 7000 命令来发送消息
 *  2. 首先socketChannle(对比FileInputStream.getChannle()) 里是有消息的，我们通过socketChannel.read()方法将通道里的数据读出来并写入缓冲区（缓冲区是一个数组）
 *  3. 我们不清楚这个数据有多大，而缓冲区（数组）的总大小是8，所以就while(true)不断去读取
 *  4. 读取的数据，我们回显到客户端，怎么回显，就是再从缓冲区读取数据，写入socketChannle
 *
 *  简单理解：(1)从socketChannel读取消息写入缓冲区，(2)又从缓冲区读数据写入socketChannel
 *  (1)中buffer是个数组，就是分散写入，即Scattering
 *  (2)中buffer也是个数字，就是分别读取到一处，即Gathering
 *  (3)回显的意思及时：你在nc程序中，输入什么，nc也会回显什么，telnet效果一样
*********************************************************************
*/
public class ScatteringAndGathering {
    public static void main(String[] args) throws IOException {
        //本示例使用ServerScoketChannel  和 SocketChannel 网络
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
        //绑定端口到socket并启动
        serverSocketChannel.socket().bind(inetSocketAddress);   //启动服务端程序

        //服务器端创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        //创建两个ByteBuffer对象
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //服务器端监听客户端发送来的消息, 一旦得到客户端的连接，则会生成并返回一个socketChannel, 与客户端进行通信
        SocketChannel socketChannel = serverSocketChannel.accept();

        //循环读取客户端发过来的消息
        int byteRead = 0;   //累计读取的字节总数（即读取消息写入缓冲区中的字节总数）
        int byteWrite = 0;  //累计写入的字节总数
        while (true){
            //1. 客户端发送了消息，读取消息写入缓冲区
            long read = socketChannel.read(byteBuffers);//从客户端通道读取消息写入缓存数组中去
            if(read == -1){
                //读取完成，直接退出
                break;
            }

            byteRead += read;
            System.out.println("累计读取字节数byteRead=" + byteRead);
            Arrays.asList(byteBuffers).stream().map(buffer -> "Position=" + buffer.position() + ", Limit=" + buffer.limit())
                    .forEach(System.out::println);

            //2. 从缓冲区中读取数据，写入通道
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.flip());
            long write = socketChannel.write(byteBuffers);
            byteWrite += write;
            System.out.println("累计写入字节数byteWrite=" + byteWrite);

            //清空所有buffer
            Arrays.asList(byteBuffers).forEach(buffer->buffer.clear());  //一直读取，直到结束

            System.out.println("-----------------------------------------------------\n");
        }

        //打印一些信息
        System.out.println("byteRead=" + byteRead + ", writeRead=" + byteWrite);
    }
}
