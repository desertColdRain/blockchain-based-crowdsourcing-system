package com.blockchain.mcsblockchain.net.base;

import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.TransactionPool;

import java.io.Serializable;

public class Node extends org.tio.core.Node implements Serializable {

    private static final long serializableVersionUID=1L;
    private String ip;
    private int port;


    @Override
    public String toString() {
        return "Node{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    public Node(String ip, int port) {
        super(ip, port);

    }

    public Node() {
        super(null,0);
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
}
