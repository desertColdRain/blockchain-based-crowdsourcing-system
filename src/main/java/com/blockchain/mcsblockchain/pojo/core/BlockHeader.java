package com.blockchain.mcsblockchain.pojo.core;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.Utils.Time;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class BlockHeader implements Serializable {

    private static final long serializeVersionUID=1L;

    private int index;                              //区块号
    private Time timeStamp;                         //区块创建的时间戳
    private String hash;                            //区块头的哈希值
    private String preHash;                         //上一个区块的哈希值
    private String generatorAddress;                //区块创建这的账户地址，即公钥的哈希值
    private BigInteger difficulty;                  //难度指标
    private Long nonce;                             //pow问题的答案

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public BlockHeader() {
        this.timeStamp=new Time();
    }
    public BlockHeader(Integer index, String previousHash) {
        this.index = index;
        this.timeStamp = new Time();
        this.preHash = previousHash;
    }
    public BlockHeader(int index, String preHash,String address) {
        this.index = index;
        this.preHash = preHash;
        this.timeStamp=new Time();
        this.generatorAddress=address;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Time getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Time timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public String getGeneratorAddress() {
        return generatorAddress;
    }

    public void setGeneratorAddress(String generatorAddress) {
        this.generatorAddress = generatorAddress;
    }
    public String serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(out);
        objectOS.writeObject(this);
        String res = out.toString("ISO-8859-1");
        return res;
    }

    @Override
    public String toString() {
        return "BlockHeader{" +
                "index=" + index +
                ", timeStamp=" + timeStamp +
                ", hash='" + hash + '\'' +
                ", preHash='" + preHash + '\'' +
                ", generatorAddress='" + generatorAddress + '\'' +
                '}';
    }

    public BigInteger getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(BigInteger difficulty) {
        this.difficulty = difficulty;
    }

    public BlockHeader deserialize(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        BlockHeader res =(BlockHeader) objIn.readObject();
        return res;
    }
    public String headerHash() throws NoSuchAlgorithmException {
        return Cryptography.myHash("BlockHeader{" +
                "index=" + index +
                ", timeStamp=" + this.timeStamp +
                ", preHash='" + preHash + '\'' +
                ", generatorAddress='" + generatorAddress + '\'' +
                '}');

    }
}
