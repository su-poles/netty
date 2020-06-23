package com.poles.base_concept.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 4:14 下午
*
*********************************************************************
*/
public class FileChannelCopyDemo {
    public static void main(String[] args) throws IOException {
        /*
         * 使用NIO机制完成文件拷贝功能：
         *
         * 读取文件到缓冲区，然后从缓冲区读取内容到新文件
         * 1. 先使用一个FileInputStream获取Channel(通道， 输入流的通道里是有数据的)，然后读取通道里的数据并写入缓冲区(写缓冲区)
         * 2. 创建一个FileOutputStream获取其Channel(输出流的通道里无数据，channel == null), 然后把缓冲区里的数据（读缓冲区）赋值给输入流的Channel
         * 3. 写入文件
         * 4. 关闭流
         */

        //获取读入通道
        FileInputStream fis = new FileInputStream("netty/src/main/java/com/poles/nio/data/file01.txt");
        FileChannel fileInputChannel = fis.getChannel();

        //创建输出流，获取写入通道
        FileOutputStream fos = new FileOutputStream("netty/src/main/java/com/poles/nio/data/file02.txt");
        FileChannel fileOutchannel = fos.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);   //每次读取3个字节

        //如何计算文件多大？ 一个英文字符是1个字节，一个中文字符是3个字节（默认都是utf-8编码的），一个换行符是2个字节

        //读取通道，并写入缓冲区, 因为不知道源文件有多大，所以需要一个循环，来不停的往缓冲区写入数据
        while(true){
            int read = fileInputChannel.read(byteBuffer);  //每次读取的应该是3，也就是read == 3, 最后一次是小于3
            if(read == -1){
                //读取完成，直接退出循环
                break;
            }

            //从缓冲区中读取数据，写入通道中
            byteBuffer.flip();
            fileOutchannel.write(byteBuffer);

            //清空buffer, 复位
            byteBuffer.clear();
        }


        fos.close();
        fis.close();
    }
}
