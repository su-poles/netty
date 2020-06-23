package com.poles.base_concept.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 4:55 下午
*
*********************************************************************
*/
public class TransferFromDemo {
    public static void main(String[] args) throws IOException {
        //直接使用通道的transferFrom方法进行拷贝
        FileInputStream fis = new FileInputStream("netty/src/main/java/com/poles/nio/data/img1.jpeg");
        FileOutputStream fos = new FileOutputStream("netty/src/main/java/com/poles/nio/data/img1_copy.jpeg");

        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        //把inChannel中的数据，直接赋值给outChannel
        outChannel.transferFrom(inChannel, 0, inChannel.size());

        //关闭通道和流
        outChannel.close(); inChannel.close();
        fos.close(); fis.close();
    }
}
