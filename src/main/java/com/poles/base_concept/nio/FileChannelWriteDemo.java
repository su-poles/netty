package com.poles.base_concept.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 11:07 上午
*
*********************************************************************
*/
public class FileChannelWriteDemo {

    /**
     * 使用ByteBuffer与FileChannel，将"Hello, netty"写入到data/file01.txt中去，如果文件不存在，则创建
     * @author liyanlong
     * @date 2020-06-15 11:08:19
     * @param args
     * @return void
     */
    public static void main(String[] args) throws IOException {

        /*
         * 使用ByteBuffer与FileChannel，将"Hello, netty"写入到data/file01.txt中去，如果文件不存在，则创建
         * 大致原理是：读取字符串，然后先写入缓冲区，然后写入通道（通道是java原生流对象里的一个属性）,然后写入文件
         */
        String str = "hello, netty";

        //1. 创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //2. 将字符串写入缓冲区
        byteBuffer.put(str.getBytes());

        //3. 缓冲区反转，因为之前写入了内容，现在要从缓冲区中读取内容
        byteBuffer.flip();

        //4. 把buffer中的数据，写入通道
        //4.1 创建一个文件输出流，用来获取FileChannel
        FileOutputStream fos = new FileOutputStream("netty/src/main/java/com/poles/nio/data/file01.txt");

        //4.2 通过fos获取对应的FileChannel
        //FileChannel是一个抽象类，这里的真实类型是FileChannelImpl
        FileChannel fileChannel = fos.getChannel();

        //4.3 写入通道
        fileChannel.write(byteBuffer);   //把byteBuffer的内容写入fileChannel

        //5. 关闭流
        fos.close();
    }
}
