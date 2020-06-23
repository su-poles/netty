package com.poles.base_concept.nio;

import java.nio.ByteBuffer;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 5:10 下午
*
*********************************************************************
*/
public class BufferUnderflowExceptionDemo {
    public static void main(String[] args) {
        /*
         * ByteBuffer可以按照类型存取，如果类型不正确，则取出的数据也会错误
         * 甚至抛出异常：BufferUnderflowException
         *
         */

        ByteBuffer buffer = ByteBuffer.allocate(1024);

//        //正常存
//        buffer.putInt(100);
//        buffer.putLong(200L);
//        buffer.putChar('好');
//        buffer.putShort((short)12);

//        buffer.flip();

//        //正常取
//        System.out.println(buffer.getInt());
//        System.out.println(buffer.getLong());
//        System.out.println(buffer.getChar());
//        System.out.println(buffer.getShort());


        //异常
        //short 两个字节， int 4个字节， long 8个字节
//        buffer.putShort((short)12);     //存入两个字节
//        System.out.println(buffer.position());  //打印字节长度
//        buffer.flip();
//        System.out.println(buffer.getLong());    //存储2个字节，要取8个字节，当然报异常，下标越界


//        buffer.clear();
        //异常，但不报错
        buffer.putInt(129);     //1000 0001
        buffer.flip();
        System.out.println(buffer.getShort());
        System.out.println(buffer.getShort());

    }
}
