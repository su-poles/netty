package com.poles.bio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/11 5:39 下午
* 用法： 先去动服务端，然后监听客户端连接，然后客户端连接并发送数据即可：
 *      1. 使用telnet工具： telnet 127.0.0.1 6666
 *      2. 使用nc工具： nc 127.0.0.1 6666
 *      然后继续输入字符即可。
 *      甚至可以传输文件，自己百度即可。
*********************************************************************
*/
public class BIOServer {
    public static void main(String[] args) throws IOException {

        //使用guava的ThreadFactoryBuilder来创建线程池
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("bio-pool-%d").build();

        //普通线程池
        ExecutorService pool = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器已启动 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        while (true){
            //监听客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("监听到客户端连接");
            pool.execute(()-> {
                //重新Runnable里的run方法体
                handler(socket);
            });
        }
    }

    //编写一个方法，用来和客户端通信，需要一个socket对象进行通信
    public static void handler(Socket socket){
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            //因为不知道传入的数据有多大，所以，需要一个循环，不停的读取
            int read = -1;
            while((read = inputStream.read(bytes)) != -1){
                //从0到read这个长度的全部输出
                System.out.print(new String(bytes, 0, read));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("关闭与客户端的连接");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}















