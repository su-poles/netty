package com.poles.base_concept.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/19 7:02 下午
*
*********************************************************************
*/
public class ChatClient {
    //服务器IP
    private final String host = "127.0.0.1";
    //服务器端口
    private final int port = 6667;
    private Selector selector;
    //客户端通道
    private SocketChannel socketChannel;
    //客户端名称
    private String username;

    public ChatClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + "初始化完成...");
    }


    //向服务器发送消息
    public void sendInfo(String message){
        //对消息做个简单封装
        message = username + "说：" + message;

        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //从服务器端读取消息（考虑多通道）
    public void readInfo(){
        try {
            int readChannels = selector.select();   //这里可以阻塞获取，如果有别的工作要做，可以若干秒来看一次，使用while(true){selector.select(2000)}这种
            //如果有可读状态的通道
            if(readChannels > 0){
                //获取有事件发生的SelectionKeys集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                //遍历
                while (iterator.hasNext()) {
                    //得到一个事件
                    SelectionKey key = iterator.next();

                    //如果是可读事件
                    if (key.isReadable()) {

                        //得到SocketChannel
                        SocketChannel socketChannel = (SocketChannel)key.channel();

                        //把消息写入buffer
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer);  //从通道读出来，写入buffer

                        //读取buffer, 转成字符串
                        String result = new String(byteBuffer.array());
                        System.out.println(result.trim());
                    }

                    iterator.remove();
                }
            } else{
//                System.out.println("没有可用的通道...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //启动客户端
        ChatClient chatClient = new ChatClient();

        //启动一个线程, 每隔3秒，从服务器读取数据
        new Thread(()->{
            while (true){
                chatClient.readInfo();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //发送数据到服务器
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.next();
            if(s.getBytes().length > 1024){
                throw new RuntimeException("消息太长，搞不定...");
            }
            chatClient.sendInfo(s);
        }
    }
}
