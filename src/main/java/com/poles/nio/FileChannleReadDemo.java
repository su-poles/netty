package com.poles.nio;

import java.io.FileInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 3:38 下午
*
*********************************************************************
*/
public class FileChannleReadDemo {
    public static void main(String[] args) throws IOException {
        /*
         * 使用ByteBuffer与FileChannel，将data/file01.txt文件中的内容读取出来，然后打印到控制台
         * 大致原理是：创建Java输入流，获取FileChannel对象，然后将内容写入缓冲区， 然后从缓冲区读取内容转成字符串
         */

        FileInputStream fis = new FileInputStream("netty/src/main/java/com/poles/nio/data/file01.txt");

        FileChannel fileChannel = fis.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(fis.available());
        //从通道里读取数据并写入缓冲区
        fileChannel.read(byteBuffer);

        //读取缓冲区
        byteBuffer.flip();

        //将byteBuffer转成字符串，然后打印
//        byte[] content = new byte[byteBuffer.limit()];
//        for(int i = 0; i < content.length; i++){
//            content[i] = byteBuffer.get(i);
//        }
//        System.out.println(new String(content));

        System.out.println(new String(byteBuffer.array()));  //直接返回hb数组

        fis.close();

    }
}
