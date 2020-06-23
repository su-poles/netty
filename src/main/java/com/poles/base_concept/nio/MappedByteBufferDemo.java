package com.poles.base_concept.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 6:47 下午
*
*********************************************************************
*/
public class MappedByteBufferDemo {
    public static void main(String[] args) throws IOException {
        /*
         *  MappedByteBuffer 可以让文件直接在内存（堆外内存）中进行修改， 内存与文件之间的同步则由NIO来完成
         *
         *  hello.txt的原始内容：hello, netty
         */
        RandomAccessFile randomAccessFile = new RandomAccessFile("netty/src/main/java/com/poles/nio/data/hello.txt", "rw");

        //获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();


        /*
         * 参数1:使用的是读写模式, 参数2：可以直接修改的起始位置  参数3：映射到内存的大小；
         * 即，文件的5个字节的内容映射到内存中，可以在内存中直接修改
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        //修改
        mappedByteBuffer.put(0, (byte)'H');
        mappedByteBuffer.put(3, (byte)'9');

//        mappedByteBuffer.put(5, (byte) 'L');  //这里会报错，所以这里说明了5指的是映射到内存的大小，而不是索引位置：java.lang.IndexOutOfBoundsException

        randomAccessFile.close();
        System.out.println("文件已修改，需要刷新查看~~");
    }
}
