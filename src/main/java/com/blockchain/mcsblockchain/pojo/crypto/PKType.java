package com.blockchain.mcsblockchain.pojo.crypto;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unisa.dia.gas.jpbc.Element;

import java.io.*;
import java.nio.charset.StandardCharsets;
//公钥类
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PKType implements Serializable {
    private static final long serialVersionUID = /*1528979184671053497L*/1L;


    public transient Element value;
    /*pk的值初始化步骤
    pk.value.set(Cryptography.P);
    pk.value.powZn(this.sk.value);
    */

    public PKType()
    {
        this.value = Cryptography.G2.newElement();
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
        this.value = Cryptography.G1.newElement();

        this.value.setFromBytes(str2);
    }

    public String serialize() throws IOException
    {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(this);
        return byteOut.toString("ISO-8859-1");
    }

    @JsonBackReference
    public Element getValue() {

        return value;
    }

    public void setValue(Element value) {
        this.value = value;
    }

    public PKType deserialize(String str_input) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str_input.getBytes(StandardCharsets.ISO_8859_1));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return (PKType) objIn.readObject();
    }

}
