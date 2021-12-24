package com.blockchain.mcsblockchain.pojo.core;

import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;

import java.io.*;

public class Task implements Serializable {

    private static final long serialVersionUID = /*8895323869179354222L;*/1L;
    private int taskId;                 //任务id
    private PKType senderPk;            //任务发出者的公钥
    private Signature senderSign;       //任务发送者的签名
    private String ipAddr;              //IP地址
    private TaskContent taskContent;    //任务内容

    public Task() {
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public PKType getSenderPk() {
        return senderPk;
    }

    public void setSenderPk(PKType senderPk) {
        this.senderPk = senderPk;
    }

    public Signature getSenderSign() {
        return senderSign;
    }

    public void setSenderSign(Signature senderSign) {
        this.senderSign = senderSign;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public TaskContent getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(TaskContent taskContent) {
        this.taskContent = taskContent;
    }
    public String serialize() throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(this);
        String res = byteOut.toString("ISO-8859-1");
        return res;
    }

    public Task deserialize(String str_input) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str_input.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Task res =(Task) objIn.readObject();
        return res;
    }
}
