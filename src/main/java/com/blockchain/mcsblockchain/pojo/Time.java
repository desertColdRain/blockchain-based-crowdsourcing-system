package com.blockchain.mcsblockchain.pojo;

import java.io.*;

public class Time implements Serializable {

        private static final long serialVersionUID =1L;
        double value;

    public Time() {
        this.value = System.currentTimeMillis();
    }

    public String serialize() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objectOS = new ObjectOutputStream(out);
            objectOS.writeObject(this);
            String res = out.toString("ISO-8859-1");
            return res;
        }

        public com.blockchain.mcsblockchain.pojo.Time deserialize(String str) throws IOException, ClassNotFoundException {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            com.blockchain.mcsblockchain.pojo.Time res =(com.blockchain.mcsblockchain.pojo.Time) objIn.readObject();
            return res;
        }

        @Override
        public String toString() {
            return "Time{" +
                    "value=" + value +
                    '}';
        }

}
