package com.blockchain.mcsblockchain.pojo.crypto;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.fasterxml.jackson.annotation.JsonIgnore;
import it.unisa.dia.gas.jpbc.Element;

import java.io.*;
//私钥类
public class SKType implements Serializable {
    private static final long serialVersionUID = /*4878039769420589901L;*/1L;

    public transient Element value;

    public SKType()
    {
        this.value = Cryptography.Zn.newRandomElement();
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();

        if(this.value == null)
            this.value = Cryptography.G2.newElement();

        byte[] str2 = this.value.toBytes();

        out.writeObject(str2);
    }


   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        //invoke default serialization method
        in.defaultReadObject();
        byte[] str2 = (byte[])in.readObject();
        //this.value = Cryptography.G1.newElement();
        this.value=Cryptography.Zn.newElement();
        this.value.setFromBytes(str2);
    }
    public String serialize() throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(this);
        String res = byteOut.toString("ISO-8859-1");
        return res;
    }

    public SKType deserialize(String str_input) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str_input.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        SKType res =(SKType) objIn.readObject();
        return res;
    }
}
