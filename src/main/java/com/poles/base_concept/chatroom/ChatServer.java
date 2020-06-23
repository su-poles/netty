package com.poles.base_concept.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/19 5:30 下午
*
*********************************************************************
*/
public class ChatServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final int port = 6667;

    public ChatServer(){
        //初始化属性
        try {
            selector = Selector.open();                                            //获取选择器
            serverSocketChannel = ServerSocketChannel.open();                      //获取通道
            serverSocketChannel.socket().bind(new InetSocketAddress(port));   //绑定监听端口
            serverSocketChannel.configureBlocking(false);                           //设置非阻塞模式
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);        //将通道注册到选择器里
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 事件监听与处理
     */
    public void listen(){
        while(true){
            System.out.println("aaaaaa");
            //每两秒钟检查一下，看看是否有事件发生
            try {
                int count = selector.select(2000);
//                int count = selector.select(); //阻塞监听
                if(count > 0){   //表示有事件发生，发生的事件个数为count
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();   //发生事件的集合
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        //1.取一个事件
                        SelectionKey selectionKey = iterator.next();
                        //删除事件，防止重复处理
                        iterator.remove();

                        //2.处理事件
                        //2.1 如果是连接事件
                        if(selectionKey.isAcceptable()){
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);     //设置非阻塞
                            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));   //这1024是客户端传入的消息最大值

                            //客户端IP
                            SocketAddress remoteAddress = socketChannel.getRemoteAddress();
                            System.out.println("有新用户上线，IP地址：" + remoteAddress.toString());

                        }

                        //通道发送read事件，次数通道处于可读状态
                        if(selectionKey.isReadable()){
                            //服务器接收客户端消息，并实现转发（转发时，要排除自己）
                            readData(selectionKey);
                        }
                    }
                }else{
                    //如果等待过程中需要处理某些事情，可以在这里做，前面监听使用select(2000) 或者selectNow
//                    System.out.println("服务器等待中...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端从通道读取消息，写入buffer, 然后从buffer中读取消息内容，打印在客户端
     */
    private void readData(SelectionKey key){
        SocketChannel socketChannel = null;

        try {
            socketChannel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            int readCount = socketChannel.read(byteBuffer);  //从通道读取数据，写入buffer
            if(readCount > 0){   //如果读取的内容不为空
                //读一下缓冲区的内容
                String msg = new String(byteBuffer.array());
                //打印消息
                System.out.println("from 客户端" + msg);

                //转发消息，转发给其它用户
                List<SocketChannel> exclusion = new ArrayList<>();
                exclusion.add(socketChannel);   //排除当前线程

                //转发
                broadCast(msg, exclusion);
            }

            //客户端已关闭，断开了连接
            if(readCount == -1){
                System.out.println(socketChannel.getRemoteAddress() + "已经离线...");
                key.cancel();  //从selector中取消注册
                socketChannel.close();  //关闭通道
            }

        }catch (IOException e){
            try {
                //如果发生异常，表示客户端已经离线
                System.out.println(socketChannel.getRemoteAddress() + "已经离线...");
                key.cancel();  //从selector中取消注册
                socketChannel.close();  //关闭通道
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 广播消息，让其他用户都收到，但是要排除自己
     * exclusion 为排除的用户，比如不让某些人看到我发送的消息
     */
    private void broadCast(String msg, List<SocketChannel> exclusion) throws IOException {
        Set<SelectionKey> keys = selector.keys();   //获取所有注册到选择器上的通道
        for(SelectionKey key : keys){

            //通过key取出对应的SocketChannel
            SelectableChannel channel = key.channel();

            //判断是客户端
            if(channel instanceof SocketChannel){
                SocketChannel dest = (SocketChannel)channel;
                if(exclusion.contains(dest)){
                    continue;
                }

                //转发消息，即：将消息包装成byteBuffer，写入客户端通道
                dest.write(ByteBuffer.wrap(msg.getBytes()));
            }
        }
    }


    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.listen();
    }

}
