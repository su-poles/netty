package com.poles.base_concept.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/22 11:37 上午
*
*********************************************************************
*/
public class NewIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(7001));

        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();

            int readCount = 0;
            while (true) {
                readCount = socketChannel.read(byteBuffer);

                //客户端断开连接
                if(readCount == -1){
                     break;
                }

                byteBuffer.rewind(); //倒带，position = 0 , mark标记作废
            }
        }
    }
}
