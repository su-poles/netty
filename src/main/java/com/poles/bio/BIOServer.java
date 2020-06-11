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
 *      1. 使用telnet工具： telnet 127.0.0.1 6666  , 然后通过send命令发送数据，例如：Telnet> send Hello
 *      2. 使用nc工具： nc 127.0.0.1 6666
 *      然后继续输入字符即可。
 *      该工具可以传输文件，自己百度即可。
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
            System.out.println("等待客户端连接...");
            final Socket socket = serverSocket.accept();
            System.out.println("监听到一个新的客户端连接");
            pool.execute(()-> {
                //重新Runnable里的run方法体
                handler(socket);
            });
        }
    }

    //编写一个方法，用来和客户端通信，需要一个socket对象进行通信
    public static void handler(Socket socket){
        String prefix = "当前线程：(ID=" + Thread.currentThread().getId() + ")" + Thread.currentThread().getName();
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            //因为不知道传入的数据有多大，所以，需要一个循环，不停的读取
//            System.out.println("正在等待读取信息...");
//            int read = -1;
//            while((read = inputStream.read(bytes)) != -1){
//                //从0到read这个长度的全部输出
//                System.out.print(prefix + ", 获取信息：" + new String(bytes, 0, read));
//            }

            while (true) {
                System.out.println("正在等待读取信息...");
                int read = inputStream.read(bytes);        //客户端连接到这里时，会进行阻塞监听，等待传输信息
                if(read != -1){
                    //处理信息
                    System.out.print(prefix + ", 获取信息：" + new String(bytes, 0, read));   //从0到read这个长度的全部输出
                }else{
                    //客户端关闭之后，直接退出当前线程
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println(prefix + "关闭与客户端的连接");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}















