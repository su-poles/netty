package com.poles.base_concept.nio;

import java.nio.ByteBuffer;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/15 6:13 下午
*
*********************************************************************
*/
public class ReadOnlyBuffer {
    public static void main(String[] args) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        for(int i = 0; i < 64; i++){
            byteBuffer.put((byte)i);
        }

        //开始读取
        byteBuffer.flip();

        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
//        System.out.println(readOnlyBuffer.getClass());      //class java.nio.HeapByteBufferR
        while(readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        readOnlyBuffer.put((byte)20);  //java.nio.ReadOnlyBufferException
    }
}
