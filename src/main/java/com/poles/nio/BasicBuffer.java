package com.poles.nio;

import java.nio.IntBuffer;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/12 4:17 下午
*
 *   Buffer类的常用方法:
 *   public final int capacity()  //返回此缓冲区的容量
 *   public final int position()  //返回此缓冲区的位置
 *   public final Buffer position(int newPosition) //设置此缓冲区的位置
 *   public final int limit() //返回此缓冲区的限制
 *   public final Buffer limit(int newLimit) //设置此缓冲区的限制
 *   public fina Buffer mark() //在此缓冲区的位置设置标记
 *   public final Buffer reset() //将此缓冲区的位置重置为以前标记的位置
 *   public fianl Buffer clear() //清除此缓冲区，即将各个标记恢复到初始状态，但是数据并没有真正的擦除
 *   public final Buffer flip() //反转此缓冲区
 *   public final Buffer rewind() //重绕此缓冲区
 *   public final int remainig() //返回当前位置与限制之间的元素数
 *   public final boolean hasRemaining() //返回当前位置与限制之间是否有元素
 *   public abstract boolean isReadOnly()  //此缓冲区是否为只读缓冲区
 *
 *
 *   public abstract boolean hasArray(); //此缓冲区是否具有可访问的底层实现数组
 *   public abstract Object array(); //返回此缓冲区的底层实现数组
 *   public abstract int arrayOffset(); //返回此缓冲区的底层实现数组中的第一个元素的偏移量
 *   public abstract boolean isDirect();  //此缓冲区是否为直接缓冲区
 *
 *
 *   ByteBuffer类的常用方法：
 *   public static ByteBuffer allocateDirect(int capacity) //创建直接缓冲区
 *   public static ByteBuffer allocate(int capacity) //设置缓冲区的初始容量
 *   public static ByteBuffer wrap(byte[] array) //把一个数组放到缓冲区中使用
 *   public static ByteBuffer wrap(byte[] array, int offset, int length)  构造初始化位置offset和上界length的缓冲区
 *   public abstract byte get(); 从当前位置position上get，get之后，position+=1
 *   public abstract byte get(int index); 获取索引位置index上的元素
 *   public abstract ByteBuffer put(byte b); 当前位置上put一个元素，put之后，position+=1
 *   public abstract ByteBuffer put(int index, byte b); 从index索引位置上put一个元素
*********************************************************************
*/
public class BasicBuffer {
    public static void main(String[] args) {

        //创建一个Buffer, 可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //存放数据
        for(int i = 0; i < intBuffer.capacity(); i++){
            intBuffer.put(i);
        }

        //读取切换，将写模式改成读模式
        intBuffer.flip();

//        intBuffer.position(1);  //从第二个位置开始读取元素
//        intBuffer.limit(3);  //最大只能读取到第3个元素

        //读取数据
        while(intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
