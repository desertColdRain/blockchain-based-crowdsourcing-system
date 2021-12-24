package com.blockchain.mcsblockchain.pojo.account;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.SKType;

import java.io.*;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public class Account implements Serializable {

    private static final long serializeVersionUID=1L;
    private PKType publicKey;           //账户公钥
    private SKType secretKey;           //账号私钥
    private BigDecimal balance;         //账户余额
    private String accountAddr;         //账户地址，即公钥的哈希值
    private int accountType;            //账户类型：0代表miner， 1代表worker

    public void setPublicKey(PKType publicKey) {
        this.publicKey = publicKey;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public PKType getPublicKey() {
        return publicKey;
    }

    public Account() throws NoSuchAlgorithmException {

    }
    public static Account generateAccount() throws NoSuchAlgorithmException {   //新建一个账户
        Account account = new Account();
        SKType sk = new SKType();
        PKType pk = new PKType();
        pk.value.set(Cryptography.P);
        pk.value.powZn(sk.value);
        account.setPublicKey(pk);
        account.setSecretKey(sk);
        account.setAccountAddr(Cryptography.myHash(pk.value.toString()));
        account.setBalance(new BigDecimal(0.0));
        return account;
    }


    public SKType getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SKType secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountAddr() {
        return accountAddr;
    }

    public void setAccountAddr(String accountAddr) {
        this.accountAddr = accountAddr;
    }

    public String serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(out);
        objectOS.writeObject(this);
        String res = out.toString("ISO-8859-1");
        return res;
    }

    public Account deserialize(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Account res =(Account) objIn.readObject();
        return res;
    }
    @Override
    public String toString() {
        return "Account{" +
                "publicKey=" + publicKey.value +
                ", secretKey=" + secretKey.value +
                ", balance=" + balance +
                ",accountAddr='" + accountAddr + '\'' +
                '}';
    }
}
