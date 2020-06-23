package com.poles.base_concept.zerocopy;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/20 10:13 下午  自己写的代码
*
*********************************************************************
*/
public class TraditionalSocketServer {
    public static void main(String[] args) {
        //监听客户端，连接到客户端之后，读取数据，然后打印到控制台
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //监听客户端
            while(true){
                Socket client = null;
                InputStream is = null;
                DataInputStream dis = null;
                try {
                    client = serverSocket.accept();
                    is = client.getInputStream();
                    dis = new DataInputStream(is);

                    //读取并打印
                    byte[] buffer = new byte[1024];
                    //-1表示客户端退出, read方法里面是System.in.read()， 是个阻塞方法，就阻塞在那里等待读取，读取之后，while循环有进入read方法阻塞读取
                    //如果客户端输入内容大于缓存，由于while是无限读取，所以会每次读取buffer.length个，然后打印
                    while(dis.read(buffer, 0, buffer.length) != -1){

                        //这个地方要稍微注意，如果缓存， 如果假设缓存比较小，需要两次才能存下内容，假设第二次只需要一个自己就可以
                        //第一次buffer可以存满，没问题
                        //第二次buffer往里面存储时，实际上里面是有内容，而且是满的，所以覆盖写入时，第一个字节是正确的，后面的都是多余的
                        //所以每次这个缓存是要清空的，这个这里没处理
                        String result = new String(buffer);
                        System.out.println("输出结果：" + result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        System.out.println("关闭与客户端的连接");
                        if(dis != null){
                            dis.close();
                        }

                        if(is != null){
                            is.close();
                        }

                        if(client != null){
                            client.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}
