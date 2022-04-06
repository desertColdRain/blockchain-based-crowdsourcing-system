package com.blockchain.mcsblockchain;

import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.net.client.AppClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
public class test {
    List<List<Integer>> ans = new ArrayList<>();

    @Test
    public void clientTest() throws Exception {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader in = null;
        int port = 9001;
        String str = null;
        try {
            serverSocket = new ServerSocket(port);    //创建服务器套接字
            System.out.println("服务器开启，等待连接。。。");
            clientSocket = serverSocket.accept();// 获得链接
            //接收客户端发送的内容
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                str = in.readLine();//从套接字中读取一行作为数据
                System.out.println("客户端发送的内容为：" + str);
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
