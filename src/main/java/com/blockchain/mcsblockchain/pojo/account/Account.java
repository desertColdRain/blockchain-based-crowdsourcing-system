package com.blockchain.mcsblockchain.pojo.account;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.SKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.pojo.db.RocksDBAccess;
import com.google.common.base.Optional;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.Element;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class Account implements Serializable {

    private static final long serializeVersionUID=1L;
   // private static DBAccess dbAccess;
    private PKType publicKey;           //账户公钥
    private SKType secretKey;           //账号私钥
    private BigDecimal balance;         //账户余额
    private String accountAddr;         //账户地址，即公钥的哈希值
    private int accountType;            //账户类型：0代表miner， 1代表worker
    private double trustValue;          //节点的信任值

    public double getTrustValue() {
        return trustValue;
    }

    public void setTrustValue(double trustValue) {
        this.trustValue = trustValue;
    }

    private String userName;
    private String password;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;

    }

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
        Cryptography.init();
        SKType sk = new SKType();
        PKType pk = new PKType();
        pk.value.set(Cryptography.P);
        pk.value.powZn(sk.value);
        account.setPublicKey(pk);
        account.setSecretKey(sk);
        account.setUserName(null);
        account.setPassword(null);
        account.setAccountAddr(Cryptography.myHash(pk.value.toString().trim()));
        account.setBalance(new BigDecimal("0.0"));
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
        return out.toString("ISO-8859-1");
    }

    public Account deserialize(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return (Account) objIn.readObject();
    }

    @Override
    public String toString() {
        return "Account{\n" +
                "publicKey=" + publicKey.value +
                ",\n secretKey=" + secretKey.value +
                ",\n balance=" + balance +
                ",\n accountAddr='" + accountAddr + '\'' +
                ",\n accountType=" + accountType +
                ",\n userName='" + userName + '\'' +
                ",\n password='" + password + '\'' +
                "\n}";
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, RocksDBException {


        Account account = Account.generateAccount();
        SKType secretKey = account.getSecretKey();
        System.out.println("pk in ten:"+account.getPublicKey().value);
        System.out.println("pk in 16:"+ getHex(account.getPublicKey().value.toBytes()));
        System.out.println("P:"+Cryptography.P);
        //secretKey.value = new BigInteger(52674025744224371277326876720628435011964550627);
        System.out.println(account.getPublicKey().getValue());

        Signature signature = Cryptography.SignAlgorithm(account.publicKey.value.toString(), secretKey);

        System.out.println("signature:"+signature.value);
        //System.out.println(account.getPublicKey().value.toBigInteger());
    }
    public static String getHex(byte[] bytes ){

        StringBuffer sb = new StringBuffer();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex).append(" ");
        }
        return sb.toString();

    }
}
