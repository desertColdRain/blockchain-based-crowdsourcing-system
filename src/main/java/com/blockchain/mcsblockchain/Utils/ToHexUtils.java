package com.blockchain.mcsblockchain.Utils;

import it.unisa.dia.gas.jpbc.Element;

import java.nio.charset.StandardCharsets;

public class ToHexUtils {
    public static String toHex(Element element){
        String tmp = null;
        StringBuilder sb = new StringBuilder();
        for (byte b : element.toBytes())
        {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();
    }
    public static String toHex(String s){
        String tmp = null;
        StringBuilder sb = new StringBuilder();
        for (byte b : s.getBytes(StandardCharsets.UTF_8))
        {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();
    }
}
