package com.poles.base_concept.zerocopy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

public class OldIOClient {

    public static void main(String[] args) throws Exception {
        //客户端连接
        Socket socket = new Socket("localhost", 7001);
        //输出流
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        //输入流
        String fileName = "netty/data/vedio.mp4";
        InputStream inputStream = new FileInputStream(fileName);


        byte[] buffer = new byte[4096];
        long readCount;
        long total = 0;

        long startTime = System.currentTimeMillis();

        while ((readCount = inputStream.read(buffer)) >= 0) {
            total += readCount;
            dataOutputStream.write(buffer);   //通过buffer, 将字节内容输入流传输到输出流中
        }

        System.out.println("发送总字节数： " + total + ", 耗时： " + (System.currentTimeMillis() - startTime));

        dataOutputStream.close();
        socket.close();
        inputStream.close();
    }
}
