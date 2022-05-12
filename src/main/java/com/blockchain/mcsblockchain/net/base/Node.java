package com.blockchain.mcsblockchain.net.base;

import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.TransactionPool;

import java.io.Serializable;
import java.util.Objects;

public class Node extends org.tio.core.Node implements Serializable {

    private static final long serializableVersionUID=1L;
    private String ip;
    private int port;
    private String createTime;
    private String notes;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", createTime='" + createTime + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    public Node(String ip, int port) {
        super(ip, port);

    }

    public Node(String ip, int port, String createTime, String notes) {
        super(ip, port);

        this.createTime = createTime;
        this.notes = notes;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //if (!super.equals(o)) return false;
        Node node = (Node) o;
        return port == node.port && Objects.equals(ip, node.ip) && Objects.equals(createTime, node.createTime) && Objects.equals(notes, node.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ip, port, createTime, notes);
    }
}
