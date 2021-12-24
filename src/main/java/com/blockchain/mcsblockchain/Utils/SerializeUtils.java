package com.blockchain.mcsblockchain.Utils;

import java.io.*;

//序列化工具
public class SerializeUtils {

    private static final long serialVersionUID=1L;
    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(out);
        objectOS.writeObject(object);
        byte[] res = out.toByteArray();
        out.close();
        return res;
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object res = objIn.readObject();
        byteIn.close();
        return res;
    }

}
