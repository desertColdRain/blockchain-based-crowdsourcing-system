package com.blockchain.mcsblockchain.net.Socket;


import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

@Component
public class Client {
    //定义一个Socket对象
    Socket socket = null;
    private String ip;
    private int port;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Client() {
    }

    public void init(String host, int port) {
        try {
            //需要服务器的IP地址和端口号，才能获得正确的Socket对象
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String sendMessage(String message) throws IOException {
        OutputStream os= null;
        try {
            os= socket.getOutputStream();
            do {
                os.write((""+message).getBytes());
                os.flush();
            } while (message.equals("bye"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = socket.getInputStream();

        String ret = "";
        try {
            // 读Sock里面的数据
            InputStream s = socket.getInputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = s.read(buf)) != -1) {
                ret = new String(buf, 0, len);
                return ret;
                //else return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String receive() throws IOException {
        String message = "";
        DataInputStream input = new DataInputStream(socket.getInputStream());
        message = input.readUTF();
        return message;
    }
    public void closeClient() throws IOException {
        socket.close();
    }
    //函数入口
    public static void main(String[] args) throws IOException {
        //需要服务器的正确的IP地址和端口号
        Client clientTest=new Client();
        clientTest.init("127.0.0.1",8888);
        clientTest.sendMessage("sjflskfs");
        ThreadLocal objectThreadLocal = new ThreadLocal();
    }

}
