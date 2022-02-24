package com.blockchain.mcsblockchain.Utils;

import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.SKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Locale;

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Cryptography {
    public static int HashSize = 32;

    public static Pairing pairing;
    @SuppressWarnings("rawtypes")
    public static Field G1, G2, GT, Zn;
    public static Element P;

    public static void outputParam()
    {
        System.out.println(HashSize);
        System.out.println(pairing.toString());
        System.out.println(G1.toString());
        System.out.println(G2.toString());
        System.out.println(GT.toString());
        System.out.println(Zn.toString());
        System.out.println(P.toString());

    }
    //加密工具初始化
    @PostConstruct
    public static void init(){
        pairing = PairingFactory.getPairing("src\\main\\resources\\cryptoParams.properties");
        //System.out.println(pairing);
        PairingFactory.getInstance().setUsePBCWhenPossible(true);

        G1 = pairing.getG1();
        G2 = pairing.getG2();
        GT = pairing.getGT();
        Zn = pairing.getZr();
        /*P = G2.newRandomElement();*/
        byte[] byte_value2 = {48,2,-119,-41,-58,-95,-69,66,-114,-101,49,-119,
                -39,18,113,-40,-121,55,-53,124,35,98,94,79,49,59,-60,116,60,32,
                -118,-43,108,59,109,-62,-123,-33,29,63,83,114,95,38,122,-113,19,
                -58,98,40,-70,-97,-16,29,3,115,-33,-72,-109,-73,45,-93,-62,-32,27,
                -5,-106,23,-49,124,3,23,113,61,-25,-16,77,91,16,31,16,23,76,-122,108,
                113,-104,25,40,-116,98,-30,0,-46,55,28,-97,-47,113,123,-12,49,-83,-106,
                85,-8,32,64,52,78,-53,-65,-26,-85,11,77,-89,70,-2,-116,-62,22,98,126,8,
                -34,-64,86};

        P = G2.newElement();
        P.setFromBytes(byte_value2);
        // System.out.println("P:  "+P);

        pairing.toString();
    }
    public Cryptography() {             //无参构造，初始化

    }

    public static byte[] Hash(String msg) throws NoSuchAlgorithmException {
        MessageDigest sha=MessageDigest.getInstance("SHA-256");     //返回实现指定摘要算法的MessageDigest对象
        byte[] msgByte=msg.getBytes(StandardCharsets.UTF_8);
        sha.update(msgByte);                                        //使用指定的byte数组更新摘要
        byte[] res=sha.digest();//通过执行诸如填充之类的最终操作完成哈希计算，在调用此方法之后，摘要被重置
        return res;
    }

    //返回哈希之后的十六进制字符串
    public static String myHash(String msg) throws NoSuchAlgorithmException {
        byte[] res=Hash(msg);
        String hs=" ";
        String temp=" ";
        for(int i=0;i<res.length;i++){
            temp=Integer.toHexString(res[i]&0xFF);
            if(temp.length()==1) hs=hs + "0"+temp;
            else hs=hs+temp;
        }
        return hs.toUpperCase(Locale.ROOT);
    }

    public static Signature SignAlgorithm(String msg, SKType sk) throws NoSuchAlgorithmException {
        Signature sign=new Signature();
        Element temp=G1.newElement();
        byte[] msghash = Hash(msg);
        /*
        setFromHash():Sets this element deterministically
        from the length bytes stored in the source parameter starting from the passed offset.
        */
        temp.setFromHash(msghash,  0,  HashSize);
        sign.value.set(temp);

        sign.value.powZn(sk.value);
        return sign;
    }
    public static boolean SignatureVerify(String msg, PKType pk, Signature sig) throws NoSuchAlgorithmException
    {

        Element temp1 = GT.newElement();
        Element temp2 = GT.newElement();

        Element temp3msg = G1.newElement();
        byte[] msghash = Hash(msg);
        temp3msg.setFromHash(msghash, 0, HashSize);
        //pairing.pairing()  applied the bilinear map
        temp1 = pairing.pairing(temp3msg,  pk.value);
        temp2 = pairing.pairing(sig.value, P);

        return temp1.isEqual(temp2);

    }
}
