package com.poles.base_concept.nio_second;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/19 2:46 下午
*
*********************************************************************
*/
public class NIOServer {
    public static void main(String[] args) throws IOException {
        //1. 创建ServerSocketChannle
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //1.1. 监听一个端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //1.2. 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //2.获取一个selector对象
        Selector selector = Selector.open();    //selector是个抽象类，其真实类型为：WindowsSelectorImpl

        //2.1 将serverSocketChannel注册到selector上去，并设置关心的事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("注册到selector的通道个数：" + selector.keys().size());
        System.out.println("有事件发生的通道个数：" + selector.selectedKeys().size());

        //等待客户端连接
        while(true){
            //select是个阻塞方法，select(1000)表示阻塞1秒，==0表示没有任何事件发生
            //即每1秒查看一次是否有事件发生
            if(selector.select(1000) == 0){
//                System.out.println("服务器等待了1秒，未发现连接" + new Date());
                continue;
            }

            //如果 >0 表示有事件发生
            //获取到有事件发生的所有selectionKey的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //挨个处理
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                //根据key，对发生的事件进行响应

                //OP_ACCEPT事件，要生成一个SocketChannel
                if(key.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();  //客户端都已经通知要连接了，所以这个地方就不会阻塞（事件驱动）
                    socketChannel.configureBlocking(false);
                    System.out.println("客户端连接成功，生成一个socketChannel对象，hash=" + socketChannel.hashCode());
                    //将这个socketChannel也注册到selector上去, 关注事件为读事件，并关联一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("客户端连接后：注册到selector的通道个数：" + selector.keys().size());
                    System.out.println("有事件发生的通道个数：" + selector.selectedKeys().size());
                }

                //OP_READ事件
                if(key.isReadable()){
                    //通过channel方法，获取SocketChannel
                    SocketChannel channel = (SocketChannel)key.channel();
                    //获取byteBuffer对象
                    ByteBuffer byteBuffer = (ByteBuffer)key.attachment();
                    //读取数据
                    channel.read(byteBuffer);  //从客户端通道读取数据，写入缓冲区
                    System.out.println("from 客户端：("+(new Date())+")" + new String(byteBuffer.array()));
                }

//                selectionKeys.remove(key);
                iterator.remove();
            }

        }

    }
}
