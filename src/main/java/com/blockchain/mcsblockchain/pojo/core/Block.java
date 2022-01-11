package com.blockchain.mcsblockchain.pojo.core;

import java.io.*;

///区块链中区块的结构
 public class Block implements Serializable {

    private static final long serialVersionUID=1L;

    private BlockBody body;
    private BlockHeader header;
    //确认数
    private int confirmNum = 0;

    public int getConfirmNum() {
        return confirmNum;
    }

    public void setConfirmNum(int confirmNum) {
        this.confirmNum = confirmNum;
    }

    public Block() {
    }

    public Block(BlockBody body, BlockHeader header) {
        this.body = body;
        this.header = header;
    }

    public BlockBody getBody() {
        return body;
    }

    public void setBody(BlockBody body) {
        this.body = body;
    }

    public BlockHeader getHeader() {
        return header;
    }

    public void setHeader(BlockHeader header) {
        this.header = header;
    }
    /*
    the space for other parameters of block

     */

    @Override
    public String toString() {
        return "Block{" +
                "body=" + body +
                ", header=" + header +
                '}';
    }

    //serialize() 序列化 deserialize()反序列化
     public String serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(out);
        objectOS.writeObject(this);
        String res = out.toString("ISO-8859-1");
        return res;
    }

    public Block deserialize(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Block res =(Block) objIn.readObject();
        return res;
    }

}
