package com.poles.base_concept.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/22 12:07 下午
*
*********************************************************************
*/
public class NewIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 7001));

        String filename = "netty/data/vedio.mp4";
        FileChannel fileChannel = new FileInputStream(filename).getChannel();

        //准备发送，使用零拷贝
        long startTime = System.currentTimeMillis();

        //在linux下，一个transferTo方法就可以完成传输
        //在windows下，一次调用transfrTo, 只能传入 8 M 内容，所以需要进行分段传输，每次传输时需要记录上一次传输到哪个地方，分段数=文件大小/8M + 1
        //此处没考虑分段的问题
        //参数：从position开始，拷贝fileChannel.size()大小的内容，传输给socketChannel，分段处理时，写好position的值的大小即可
        //transferTo使用了零拷贝技术，参见方法注释：
        /*
         *  This method is potentially much more efficient than a simple loop
         * that reads from this channel and writes to the target channel.  Many
         * operating systems can transfer bytes directly from the filesystem cache
         * to the target channel without actually copying them.
         */
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("发送总字节数： " + transferCount + ", 耗时： " + (System.currentTimeMillis() - startTime));

        fileChannel.close();

    }
    
}
