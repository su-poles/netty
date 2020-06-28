package com.poles.netty.simple;

import io.netty.util.NettyRuntime;
import org.junit.jupiter.api.Test;

/**
*********************************************************************
* 
* @author poles
* @date 2020/6/24 10:24 上午
*
*********************************************************************
*/
public class CodeTest {

    @Test
    public void getCpuCores(){
        /*获取cpu 最大可同时执行的线程数, 我的mac本这里是四核8线程*/
        int coreNums = NettyRuntime.availableProcessors();
        System.out.println(coreNums);

        /*获取cpu 最大可同时执行的线程数*/
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println(Runtime.getRuntime().maxMemory());

        /* Mac下命令查看 */
        // CPU型号：   sysctl -n machdep.cpu.thread_count
        // CPU核心数： sysctl -n machdep.cpu.core_count
        // CPU线程数： sysctl -n machdep.cpu.thread_count
    }
}
