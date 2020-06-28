package com.poles.netty.unpooled_buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/25 7:08 下午
*
*********************************************************************
*/
public class NettyByteBuf01 {
    public static void main(String[] args) {

        //创建一个ByteBuf，底层相当于new byte[10]
        //在NIO的Buffer中，读写需要通过flip转换
        //但是在Netty中的ByteBuf不需要flip转换，为什么呢？ 因为其维护了两个索引：readerIndex、writerIndex
        ByteBuf buffer = Unpooled.buffer(10);

        //写入
        for(int i = 0; i < 10; i++){
            buffer.writeByte(i);
        }

        //读取
        for(int i = 0; i < buffer.capacity(); i++){
            System.out.println(buffer.getByte(i));
        }
    }
}
