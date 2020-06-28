package com.poles.netty.unpooled_buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/25 7:08 下午
*
*********************************************************************
*/
public class NettyByteBuf02 {
    public static void main(String[] args) {

        //创建缓冲区，将数据存入缓冲区，并且进行编码
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello, ByteBuf! 娃哈哈！", CharsetUtil.UTF_8);
        //对于"Hello, ByteBuf! 娃哈哈！"来说，使用UTF_16，则：每个字符都是2个字节，中英文都是，共40个字节，加上一个结束符，共42个字节
        //如果是UTF-8, 则一个英文或空格就是1个，中文三个字节，英文加空格16，汉字字符4个，共12个字节，一共就是16 + 12= 28个字节长度，没有其它的了

        //hasArray, 有数据就返回true，没有数据就返回false
        if(byteBuf.hasArray()){

            //获取数据
            byte[] cotent = byteBuf.array();

            //转成字符串，使用编码时候的编码格式
            System.out.println(new String(cotent, CharsetUtil.UTF_8));

            //打印真实类型，及其内容，比如存放长度widx, 容量大小cap
            System.out.println("byteBuf = " + byteBuf);

            System.out.println(byteBuf.arrayOffset());
            System.out.println(byteBuf.readerIndex());  //如果要继续读，则从byteBuf.readerIndex()的位置开始读取，读一个会增加1
            System.out.println(byteBuf.writerIndex());  //如果继续要写，则从byteBuf.writerIndex()的位置开始写入，写一个会增加1
            System.out.println(byteBuf.capacity());

//            System.out.println("读取第一个字节：" + byteBuf.readByte());  //读取一个字节，即获取byteBuf.readerIndex() - 1位置上的字节，打印的是当前字符对应的ascii码， 比如H，就是72
//            System.out.println("reader_index = " + byteBuf.readerIndex());
            System.out.println("读取第一个字节：" + byteBuf.getByte(0)); //这种读取的方式，不会导致readerIndex的变化
            System.out.println("reader_index = " + byteBuf.readerIndex());

            int len = byteBuf.readableBytes(); //可以读取的字节数
            System.out.println("len = " + len);

            //可以通过byteBuf.getByte(i)的方式获取内容，不会导致readerIndex的变化
            for(int i = 0; i < len; i++){
                System.out.print((char)byteBuf.getByte(i));
            }
            System.out.println();

            //按照范围读取
            System.out.println(byteBuf.getCharSequence(0, 4, CharsetUtil.UTF_8));  //从0开始读取，读取4个长度，返回：hell
            System.out.println(byteBuf.getCharSequence(4, 6, CharsetUtil.UTF_8));  //从索引为4的下标开始，读取6个，返回: o, Byt
        }
    }
}
