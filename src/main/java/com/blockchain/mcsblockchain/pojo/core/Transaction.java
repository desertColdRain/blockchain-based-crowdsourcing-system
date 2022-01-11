package com.blockchain.mcsblockchain.pojo.core;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.SKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public class Transaction {

    private String transactionHash;  //交易哈希，此交易的摘要
    private Signature senderSign;          //付款人签名
    private PKType senderPk;         //付款人公钥
    private PKType receiverPk;       //收款人公钥
    private int transactionType;        //交易类型: 0代表创世区块    1代表普通交易
   // public Signature receiveSign;   //收款人
    private String content;          //附加信息
    private String receiverAddr;    //收款人地址
    private BigDecimal amount;          //交易金额
    private int blockNum;               //所属区块高度
    private int status;                 //交易状态 0：success  1：failed

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public Transaction() {

    }

    public Transaction(Signature senderSign, PKType senderPk,
                       PKType receiverPk, int transactionType,
                       String content, String receiverAddr,
                       BigDecimal amount, int blockNum) {

        this.senderSign = senderSign;
        this.senderPk = senderPk;
        this.receiverPk = receiverPk;
        this.transactionType = transactionType;
        this.content = content;
        this.receiverAddr = receiverAddr;
        this.amount = amount;
        this.blockNum = blockNum;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Signature getSenderSign() {
        return senderSign;
    }

    public void setSenderSign(Signature senderSign) {
        this.senderSign = senderSign;
    }

    public Signature genSenderSign(SKType sk) throws NoSuchAlgorithmException {
        String msg=    "senderPk=" + senderPk +
                ", receiverPk=" + receiverPk +
                ", transactionType=" + transactionType +
                ", content='" + content + '\'' +
                ", receiverAddr='" + receiverAddr + '\'' +
                ", amount=" + amount +
                ", blockNum=" + blockNum +
                '}';
        return Cryptography.SignAlgorithm(msg, sk);
    }
    public PKType getSenderPk() {
        return senderPk;
    }

    public void setSenderPk(PKType senderPk) {
        this.senderPk = senderPk;
    }

    public PKType getReceiverPk() {
        return receiverPk;
    }

    public void setReceiverPk(PKType receiverPk) {
        this.receiverPk = receiverPk;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionHash='" + transactionHash + '\'' +
                ", senderSignature=" + senderSign +
                ", senderPk=" + senderPk +
                ", receiverPk=" + receiverPk +
                ", transactionType=" + transactionType +
                ", content='" + content + '\'' +
                ", receiverAddr='" + receiverAddr + '\'' +
                ", amount=" + amount +
                ", blockNum=" + blockNum +
                '}';
    }
    public String txHash() throws NoSuchAlgorithmException {

        return Cryptography.myHash("senderSignature=" + senderSign +
                ", senderPk=" + senderPk +
                ", receiverPk=" + receiverPk +
                ", transactionType=" + transactionType +
                ", content='" + content + '\'' +
                ", receiverAddr='" + receiverAddr + '\'' +
                ", amount=" + amount +
                ", blockNum=" + blockNum +
                '}');
    }

}
